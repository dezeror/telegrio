# HOOKS.md — Spectra ⇄ Nagram: карта точек интеграции

Версия снапшота: Nagram clone `/home/z/my-project/Nagram` (read-only), путь кода — `TMessagesProj/src/main/java/...`.
Все точки вставки проверены через `rg` в снапшоте; номера строк указаны как ориентир (≈, они дрейфуют между версиями).

## Как работает оверлей

Spectra — это overlay-модуль поверх форка Nagram. Собственные классы живут в пакете `org.telegram.spectra` (`spectra-mod/overlay/java/...`) и добавляются к сборке как дополнительные исходники; код хоста (Nagram) патчится точечными вставками в существующие файлы. Никакой рефлексии для приватных полей там, где есть прямой доступ, не требуется — хост собирается одной Gradle-конфигурацией, поэтому оверлей может импортировать `NekoConfig`, `SharedConfig`, `TLRPC` и т.д.

**Как применять патчи:** каждая вставка ниже оформлена маркерами

```java
// SPECTRA: START — <фича> (опциональный комментарий)
... добавленный код ...
// SPECTRA: END
```

Правила:
- вставка должна быть целиком между маркерами `// SPECTRA: START` и `// SPECTRA: END` — это позволяет скрипту `scripts/apply-hooks.py` повторно накладывать патчи после rebase;
- для вызова Spectra-классов в сниппетах используются полные имена (`org.telegram.spectra.SpectraConfig.isHideTyping()`), чтобы не трогать секцию импортов хоста; при желании импорты можно добавить обычным способом;
- все методы SpectraConfig — статические, внутренне защищены try/catch, поэтому вставка не может уронить приложение даже при отсутствии SharedPreferences;
- если точка не найдена в снапшоте, раздел помечается «точка не найдена» и содержит готовый `rg`-запрос для поиска в новой версии.

Смежные справочники: `SpectraGlassPanel` (стекло-панели), `SpectraThemeUtils.apply()` / `SpectraPalette.apply()` (тема), `SpectraDeletedStore` (anti-delete / edit history), `SpectraExifCleaner.clean(File)` (EXIF).

---

### 1. B7 Anti-delete (сохранение удалённых сообщений) — `org/telegram/messenger/MessagesController.java`

Точка вставки: `MessagesController.processUpdateArray(...)`, главный поток, блок `if (deletedMessagesFinal != null)` (≈ строка 21510) — до того как `obj.deleted = true` и уйдёт `NotificationCenter.messagesDeleted`.

```java
if (deletedMessagesFinal != null) {
    // SPECTRA: START — B7 anti-delete: save text before messages vanish
    for (int a = 0, size = deletedMessagesFinal.size(); a < size; a++) {
        long dlgId = deletedMessagesFinal.keyAt(a);
        ArrayList<Integer> mids = deletedMessagesFinal.valueAt(a);
        if (mids == null) continue;
        ArrayList<MessageObject> objs = dialogId == 0
                ? null : dialogMessage.get(dlgId);
        for (int mid : mids) {
            MessageObject obj = null;
            if (objs != null) {
                for (MessageObject o : objs) {
                    if (o != null && o.getId() == mid) { obj = o; break; }
                }
            }
            if (obj == null) obj = dialogMessagesByIds.get(mid);
            if (obj != null && obj.messageText != null) {
                org.telegram.spectra.SpectraDeletedStore
                        .rememberDeleted(dlgId, mid, obj.messageText.toString());
            }
        }
    }
    // SPECTRA: END
    for (int a = 0, size = deletedMessagesFinal.size(); a < size; a++) {
    ...
```

Примечание: `SpectraDeletedStore.rememberDeleted` сам проверяет `SpectraConfig.isAntiDelete()` и пишет текст в per-account SharedPreferences (`spectra_deleted_<acc>`). Хук обязан стоять **до** `obj.deleted = true`, иначе текст уже недоступен. Имена `dialogMessage`/`dialogMessagesByIds` — это поля MessagesController, доступные в этом scope.

### 2. B8 Edit history (сохранение исходного текста при правках) — `org/telegram/messenger/MessagesController.java`

Точка вставки: `processUpdateArray(...)`, главный поток, блок `if (editingMessagesFinal != null)`, внутри `if (oldObject.getId() == newMessage.getId())` (≈ строка 21342) — ровно перед заменой `oldObjects.set(i, newMessage)`.

```java
if (oldObject.getId() == newMessage.getId()) {
    // SPECTRA: START — B8 edit history: remember original text
    if (oldObject.messageText != null && newMessage.messageText != null
            && !oldObject.messageText.toString()
                    .contentEquals(newMessage.messageText)) {
        org.telegram.spectra.SpectraDeletedStore
                .rememberOriginal(dialogId, oldObject.getId(),
                        oldObject.messageText.toString());
    }
    // SPECTRA: END
    oldObjects.set(i, newMessage);
```

Примечание: здесь видны и старый, и новый объект сообщения — единственное место, где можно зафиксировать исходный текст без рефлексии. Метода `processEditedMessage` в снапшоте нет; его роль выполняет `getNotificationsController().processEditedMessages(...)` (≈20238), но оно уже после подмены объектов. `rememberOriginal` дедуплицирует записи по `mid`.

### 3. B2 Hide typing (не отправлять «набирает…») — `org/telegram/messenger/MessagesController.java`

Точка вставки: `MessagesController.sendTyping(long dialogId, long threadMsgId, int action, String emojicon, int classGuid)` (≈ строка 11654), сразу после существующего guard'а Nagram.

```java
public boolean sendTyping(long dialogId, long threadMsgId, int action, String emojicon, int classGuid) {
    if (NekoConfig.disableChatAction.Bool()) return false;
    // SPECTRA: START — B2 hide typing
    if (org.telegram.spectra.SpectraConfig.isHideTyping()) {
        return false;
    }
    // SPECTRA: END
    if (action < 0 || action >= sendingTypings.length || dialogId == 0) {
```

Примечание: `sendTyping` — единственная точка, через которую ChatActivity/Threads шлют «typing»; early return полностью выключает отправку `TLSendMessageAction`, но не влияет на входящие статусы.

### 4. A17 Hide stories (скрыть ленту историй) — `org/telegram/ui/DialogsActivity.java`

Точка вставки: `DialogsActivity.createView(...)`, сразу после `contentView.addView(dialogStoriesCell, ...)` (≈ строка 5718; сам `dialogStoriesCell` создаётся в анонимном классе ≈5293).

```java
contentView.addView(dialogStoriesCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, DialogStoriesCell.HEIGHT_IN_DP));
// SPECTRA: START — A17 hide stories
if (org.telegram.spectra.SpectraConfig.isHideStories()) {
    dialogStoriesCell.setVisibility(View.GONE);
    storiesEnabled = false; // disables hints + story items logic (field exists, ≈9062)
}
// SPECTRA: END
```

Примечание: `storiesEnabled` — публичное поле DialogsActivity, инициализируется из `NaConfig.disableStories`; принудительное `false` убирает и подсказки, и логику сторис в списке диалогов. Высоту layout-а панель почти не занимает, т.к. `View.GONE` исключает её из измерения.

### 5. A27 Bubble radius + A28 Disable tails — `org/telegram/ui/ActionBar/MessageDrawable.java`

Точка вставки: `MessageDrawable.draw(Canvas canvas, Paint paintToUse)`, блок вычисления `rad`/`nearRad` (≈ строки 572–586). В этом коде «хвост» — это асимметричный угол: `nearRad = dp(Math.min(6, bubbleRadius))` против полного `rad`.

```java
} else if (currentType == TYPE_PREVIEW) {
    rad = dp(6);
    nearRad = dp(6);
} else {
    // SPECTRA: START — A27 custom bubble radius + A28 no tails
    int spectraRad = dp(org.telegram.spectra.SpectraConfig.getBubbleRadius());
    rad = spectraRad;
    nearRad = org.telegram.spectra.SpectraConfig.isDisableTails()
            ? spectraRad // all corners equal => no tail wedge
            : dp(Math.min(6, org.telegram.spectra.SpectraConfig.getBubbleRadius()));
    // SPECTRA: END
}
int smallRad = dp(6);
```

Примечание: тот же `dp(SharedConfig.bubbleRadius)` дублируется в `getBackgroundDrawable()` (≈330) и `getShadowDrawable()` (≈455) того же файла — для полного покрытия замените и там (одна строка каждая). Для layout-инсетов текста дополнительно используется блок `canChangeRadius` в `ChatMessageCell.java` ≈11100–11108; без него текст может не совпадать с новым радиусом при сгруппированных сообщениях.

### 6. A32 Hide group avatars — `org/telegram/ui/Cells/ChatMessageCell.java`

Точка вставки: `ChatMessageCell.updateMessage...` — присваивание `isAvatarVisible` (≈ строка 7018; поле объявлено публично ≈1568).

```java
isAvatarVisible = needDrawAvatar() && (currentPosition == null || currentPosition.edge);
boolean drawAvatar = needDrawAvatar();
// SPECTRA: START — A32 hide group avatars
if (org.telegram.spectra.SpectraConfig.isHideGroupAvatars()) {
    isAvatarVisible = false;
    drawAvatar = false;
}
// SPECTRA: END
if (messageObject.customAvatarDrawable != null || messageObject.forceAvatar || messageObject.messageOwner.guestchat_via_from != null) {
```

Примечание: локальная переменная `drawAvatar` дальше управляет шириной бабла (`- dp(... + drawAvatar ? 52 : 0)`), поэтому глушим оба флага — иначе слева останется пустая колонка. Ширину бабла это не ломает: все расчёты уже ветвятся по `drawAvatar`.

### 7. A35 Hide quick reactions (полоса реакций под баблом) — `org/telegram/ui/Cells/ChatMessageCell.java`

Точка вставки: установка `reactionsLayoutInBubble` (≈ строки 7290–7303), условие `messageObject.shouldDrawReactions() && ...`.

```java
if (messageObject.shouldDrawReactions() && !messageObject.isExpiredStory() && (currentPosition == null || ((currentPosition.flags & MessageObject.POSITION_FLAG_BOTTOM) != 0))) {
// SPECTRA: START — A35 hide quick reactions
if (org.telegram.spectra.SpectraConfig.isHideQuickReactions()) {
    reactionsLayoutInBubble.setMessage(null, false, false, resourcesProvider);
} else
// SPECTRA: END
if (currentPosition != null) {
    MessageObject primaryMessage = groupedMessages.findPrimaryMessageObject();
```

Примечание: `setMessage(null, ...)` — штатный «пустой» путь этого layout (используется самим кодом ниже, ≈7302), поэтому отрисовка, тап-таргеты и расчёт высоты остаются консистентными. Реакции в превью (`reactionsLayoutInBubble.isSmall`) глушатся той же веткой.

### 8. A41 Capsule input bar (стеклянная капсула ввода) — `org/telegram/ui/Components/ChatActivityEnterView.java`

Точка вставки: `ChatActivityEnterView.<init>`, сразу после `textFieldContainer.addView(frameLayout, ...)` (≈ строка 2750), где `frameLayout = messageEditTextContainer` (создан ≈2709).

```java
FrameLayout capsuleGlass = null;
// SPECTRA: START — A41 capsule glass input bar
if (org.telegram.spectra.SpectraConfig.isLiquidGlass()
        && org.telegram.spectra.SpectraConfig.isCapsuleInputBar()) {
    capsuleGlass = new org.telegram.spectra.SpectraGlassPanel(context);
    capsuleGlass.setCornerRadius(AndroidUtilities.dp(26));
    // captureRoot цепляется позже, в ChatActivity.createView, когда contentView в дереве:
    // ((SpectraGlassPanel) capsuleGlass).attachCaptureRoot(contentView);
    capsuleGlass.addView(frameLayout, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM));
    textFieldContainer.addView(capsuleGlass, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM, 0, 0, DEFAULT_HEIGHT, 0));
} else
// SPECTRA: END
{
    textFieldContainer.addView(frameLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM, 0, 0, DEFAULT_HEIGHT, 0));
}
```

Примечание: капсула — это замена контейнера на `SpectraGlassPanel` с радиусом 26dp; attach-кнопка, emoji-кнопка и поле добавляются в `messageEditTextContainer` позже (≈2877 и далее), поэтому порядок вставок сохраняется. `attachCaptureRoot` требует корень, уже лежащий в дереве, поэтому в проде переехать в `createView()` чата после `contentView` (см. хук 24 для примера).

### 9. A42 Hide record button (кружок записи) — `org/telegram/ui/Components/ChatActivityEnterView.java`

Точка вставки: `ChatActivityEnterView.createRecordCircle()` (≈ строки 4618–4626).

```java
private void createRecordCircle() {
    createControlsView();
    if (recordCircle != null) {
        return;
    }
    recordCircle = new RecordCircle(getContext());
    // SPECTRA: START — A42 hide record button
    if (org.telegram.spectra.SpectraConfig.isHideRecordButton()) {
        recordCircle.setVisibility(GONE);
        return; // do not even add to layout
    }
    // SPECTRA: END
    recordCircle.setVisibility(GONE);
    sizeNotifierLayout.addView(recordCircle, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM));
}
```

Примечание: `RecordCircle` создаётся лениво и штатно стартует в `GONE`; ранний `return` до `addView` гарантированно убирает кнопку записи из иерархии (клик по «+»/attach её всё равно не покажет, т.к. view нет). Учтите, что подавно пропадает и запись кружочков — это и есть цель фичи.

### 10. A43 Hide attach extras (кнопка скрепки) — `org/telegram/ui/Components/ChatActivityEnterView.java`

Точка вставки: создание `attachButton` (≈ строки 2877–2896), сразу после `updateFieldRight(1)`.

```java
attachButton.setOnClickListener(v -> {
    if (adjustPanLayoutHelper != null && adjustPanLayoutHelper.animationInProgress() || attachLayoutPaddingAlpha == 0f) {
        return;
    }
    delegate.didPressAttachButton();
});
attachButton.setContentDescription(getString(R.string.AccDescrAttachButton));
// SPECTRA: START — A43 hide attach button
if (org.telegram.spectra.SpectraConfig.isHideAttachExtras()) {
    attachButton.setVisibility(GONE);
    attachButton.setClickable(false);
}
// SPECTRA: END
updateFieldRight(1);
```

Примечание: `updateFieldRight` пересчитывает отступ поля; при GONE-кнопке Telegram сам подожмёт текст (кнопка исключается из измерения). Если нужен вариант «скрепка остаётся, но меню урезано» — фильтруйте пункты в `didPressAttachButton`-меню ChatActivity, точка та же.

### 11. B12 Strip EXIF перед отправкой фото — `org/telegram/messenger/SendMessagesHelper.java`

Точка вставки: `SendMessagesHelper.prepareSendingMedia(...)` (полный overload ≈ строка 10922), сразу после резолва `tempPath` (≈ строки 10992–10997).

```java
String originalPath = info.path;
String tempPath = info.path;
if (tempPath == null && info.uri != null) {
    tempPath = AndroidUtilities.getPath(info.uri);
    originalPath = info.uri.toString();
}
// SPECTRA: START — B12 strip EXIF from photos before send
if (tempPath != null && info.ttl <= 0 && !tempPath.endsWith(".gif")
        && !tempPath.endsWith(".webp")) {
    try {
        org.telegram.spectra.SpectraExifCleaner.clean(new java.io.File(tempPath));
    } catch (Throwable ignore) {}
}
// SPECTRA: END

boolean isWebP = false;
```

Примечание: `prepareSendingPhoto` (≈10286) — тонкая обёртка над `prepareSendingMedia`, поэтому хук в media-пути покрывает и PhotoViewer (≈17765–17767), и галерею, и drag&drop. `SpectraExifCleaner.clean` чистит GPS/Make/Model/DateTime и т.д. in place; GIF/WebP исключены, чтобы не ломать анимацию.

### 12. B13 Anti-phishing (подтверждение подозрительных ссылок) — `org/telegram/ui/ChatActivity.java`

Точка вставки: `ChatActivity.processExternalUrl(int type, String url, CharacterStyle span, ChatMessageCell cell, boolean forceAlert, boolean forceNoIV)` (≈ строка 38023) — все ссылки чата (LinkSpan/URLSpanNoUnderline/превью) проходят через неё.

```java
private void processExternalUrl(int type, String url, CharacterStyle span, ChatMessageCell cell, boolean forceAlert, boolean forceNoIV) {
    // SPECTRA: START — B13 anti-phishing confirm
    if (org.telegram.spectra.SpectraUtils.isSuspiciousUrl(url)
            && getParentActivity() != null) {
        AlertDialog.Builder b = new AlertDialog.Builder(getParentActivity(), getResourceProvider());
        b.setTitle("Spectra");
        b.setMessage("Suspicious link:\n" + url);
        b.setPositiveButton(R.string.Open, (d, w) ->
                Browser.openUrl(getParentActivity(), Uri.parse(url), true));
        b.setNegativeButton(R.string.Cancel, null);
        showDialog(b.create());
        return;
    }
    // SPECTRA: END
    try {
        String host = AndroidUtilities.getHostAuthority(url);
```

Примечание: `SpectraUtils.isSuspiciousUrl` сам проверяет `isAntiPhishing()` и ловит `http://`, punycode (`xn--`), короткие сервисы и raw-IP хосты. Вставка блокирует **любой** тип открытия (в т.ч. Instant View); после подтверждения открываем через штатный `Browser.openUrl`, а для полного IV-ретрая продублируйте тело `processExternalUrl` в helper и вызывайте его из `setPositiveButton`.

### 13. B15 Fake PIN (фиктивный код блокировки) — `org/telegram/ui/Components/PasscodeView.java`

Точка вставки: `PasscodeView.processDone(boolean fingerprint)` (≈ строка 1112), перед проверкой `PasscodeHelper.checkPasscode / SharedConfig.checkPasscode` (≈1128).

```java
if (password.length() == 0) {
    onPasscodeError();
    return;
}
// SPECTRA: START — B15 fake pin: decoy unlock
if (org.telegram.spectra.SpectraConfig.isFakePin()
        && password.equals(org.telegram.spectra.SpectraConfig.getFakePinValue())) {
    // minimal guard: behave like a wrong code (no real content is shown)
    passwordEditText.setText("");
    passwordEditText2.eraseAllCharacters(true);
    onPasscodeError();
    if (backgroundDrawable instanceof MotionBackgroundDrawable) {
        rotateBackgroundBack((MotionBackgroundDrawable) backgroundDrawable);
    }
    return;
}
// SPECTRA: END
if (!PasscodeHelper.checkPasscode((Activity) getContext(), password) && !SharedConfig.checkPasscode(password)) {
```

Примечание: минимальный гард — фейковый код «не открывает» чаты, но и не увеличивает счётчик неудачных попыток (`increaseBadPasscodeTries` не вызывается). Расширенный вариант — презентовать пустую учётную запись (декой-фрагмент) — требует отдельного фрагмента-обёртки и в этот снапшот не входит. Тот же патч применим к `PasscodeActivity` (TYPE_ENTER_CODE_TO_MANAGE_SETTINGS, ≈1067).

### 14. B28 Empty messages (отправка «пустых» сообщений) — `org/telegram/messenger/SendMessagesHelper.java`

Точка вставки: `SendMessagesHelper.sendMessage(SendMessageParams)` (≈ строка 4451), сразу после `String message = sendMessageParams.message;` (≈4454).

```java
String message = sendMessageParams.message;
String caption = sendMessageParams.caption;
// SPECTRA: START — B28 allow empty messages
if (org.telegram.spectra.SpectraConfig.isEmptyMessages()
        && TextUtils.isEmpty(message) && TextUtils.isEmpty(caption)) {
    message = org.telegram.spectra.SpectraUtils.INVISIBLE_CHAR;
}
// SPECTRA: END
```

Примечание: `INVISIBLE_CHAR` — word-joiner `\u2060`; сервер Telegram принимает его как непустой текст, пользователь видит «пустую» строку. Вставка стоит **после** объявления `caption` (≈4455), поэтому конфликта с распаковкой параметров нет; ниже по методу переменная `message` уже используется как есть, а `TextUtils` импортирован в файле.

### 15. B29 Inline calculator (=25*48 → 1200) — `org/telegram/messenger/SendMessagesHelper.java`

Точка вставки: тот же `sendMessage(SendMessageParams)` (≈4451), сразу после блока B28.

```java
// SPECTRA: START — B29 inline calculator
if (message != null && message.startsWith("=")) {
    String evaluated = org.telegram.spectra.SpectraUtils.tryEvaluate(message);
    if (evaluated != null) {
        message = evaluated;
    }
}
// SPECTRA: END
// SPECTRA: START — B27 text snippets
if (message != null) {
    message = org.telegram.spectra.SpectraUtils.applySnippets(message);
}
// SPECTRA: END
```

Примечание: `tryEvaluate` возвращает `null` для неарифметических строк (`=abc`, `=2+?`), поэтому рисков замены нет; он сам проверяет `isInlineCalc()`. `applySnippets` — п.15 соседний хук B27 (раскрытие `{":sm": "..."}`), тот же send-путь; оба стоят до `searchLinks`-обработки, чтобы URL-детект видел уже подставленный текст.

### 16. B27 Text snippets — см. хук 15 (тот же файл/метод)

Точка вставки: `SendMessagesHelper.sendMessage(SendMessageParams)` — блок `// SPECTRA: START — B27 text snippets` из сниппета выше.

```java
// (входит в сниппет хука 15)
// SPECTRA: START — B27 text snippets
if (message != null) {
    message = org.telegram.spectra.SpectraUtils.applySnippets(message);
}
// SPECTRA: END
```

Примечание: сниппеты хранятся в `SpectraConfig.getSnippetsData()` (JSON `{"ключ":"подстановка"}`); `applySnippets` идемпотентен и делает простые `String.replace`, регулярки не используются.

### 17. B22/B23 Forward no-quote / no-caption — `org/telegram/messenger/SendMessagesHelper.java`

Точка вставки: `sendMessage(ArrayList<MessageObject> messages, long peer, boolean forwardFromMyName, boolean hideCaption, ...)` (≈ строки 2236–2252), сразу после проверки `messages == null || messages.isEmpty()`.

```java
if (messages == null || messages.isEmpty()) {
    return 0;
}
// SPECTRA: START — B22/B23 forward defaults
if (org.telegram.spectra.SpectraConfig.isForwardNoQuote()) {
    forwardFromMyName = true;   // builds TL_messages_forwardMessages with drop_author
}
if (org.telegram.spectra.SpectraConfig.isForwardNoCaption()) {
    hideCaption = true;
}
// SPECTRA: END
int sendResult = 0;
```

Примечание: `forwardFromMyName` дальше присваивается `req.drop_author` (≈2754, `TL_messages_forwardMessages`), а `hideCaption` гасит подписи медиа в скопированных объектах — т.е. патч работает и для сети, и для локальной пересылки. Параметры метода не final, переприсваивание легально; рекурсивный вызов через `ensurePaidMessageConfirmation` (≈2277) сохранит изменённые значения.

### 18. B45 Registration date в шапке профиля — `org/telegram/ui/ProfileActivity.java`

Точка вставки: `ProfileActivity.updateProfileData(boolean reload)` (≈ строка 11847), сразу после построения `newString2` (закрывающая скобка if/else-цепочки ≈11995).

```java
// ... после if/else цепочки, перед hasCustomPhoto:
// SPECTRA: START — B45 show estimated registration date
if (user != null && !isBot && org.telegram.spectra.SpectraConfig.isShowRegDate()) {
    long ts = org.telegram.spectra.SpectraUtils.estimateRegDateUnix(user.id);
    if (ts > 0 && !TextUtils.isEmpty(newString2)) {
        newString2 = newString2 + " · " + LocaleController.formatDateTime(ts, false);
    }
}
// SPECTRA: END
hasCustomPhoto = user.photo != null && user.photo.personal;
```

Примечание: `estimateRegDateUnix` извлекает высокую 32-битную часть id (эвристика работает для новых аккаунтов; возвращает 0 для старых — строка просто не добавляется). `LocaleController.formatDateTime(long, boolean)` существует в снапшоте (LocaleController.java ≈2592).

### 19. B46 Force high refresh rate — `org/telegram/ui/LaunchActivity.java`

Точка вставки: `LaunchActivity.onCreate(Bundle)` (≈ строка 436), сразу после `setContentView(frameLayout)` (≈511).

```java
setContentView(frameLayout);
// SPECTRA: START — B46 force high refresh rate
if (org.telegram.spectra.SpectraConfig.isForceHighRefreshRate()
        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    try {
        Display display = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                ? getDisplay() : getWindowManager().getDefaultDisplay();
        if (display != null) {
            Display.Mode best = null;
            for (Display.Mode m : display.getSupportedModes()) {
                if (best == null || m.getRefreshRate() > best.getRefreshRate()) best = m;
            }
            if (best != null) {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.preferredDisplayModeId = best.getModeId();
                getWindow().setAttributes(lp);
            }
        }
    } catch (Throwable ignore) {}
}
// SPECTRA: END
```

Примечание: в снапшоте уже есть `AndroidUtilities.setPreferredMaxRefreshRate` (≈2756) и `RefreshRateController` (org/telegram/messenger/utils/RefreshRateController.java), но они динамически понижают герцовку; `preferredDisplayModeId` жёстко фиксирует максимальный режим и не конфликтует с ними (побеждает более специфичный modeId). Нужен импорт `android.view.Display` — либо используйте полное имя.

### 20. B34 Video note flash (вспышка при записи кружочка) — `org/telegram/ui/Components/InstantCameraView.java`

Точка вставки: `InstantCameraView.updateFlash()` (≈ строка 452) — обе ветки: camera2 (≈464) и legacy camera1 (≈474).

```java
private void updateFlash() {
    final boolean shouldFrontFlash = flashing && recording && isFrontface;
    // ...
    // SPECTRA: START — B34 torch while recording round video
    final boolean spectraForceTorch = org.telegram.spectra.SpectraConfig.isVideoNoteFlash()
            && recording && !isFrontface;
    // SPECTRA: END
    if (useCamera2) {
        if (camera2Sessions[1] != null) {
            camera2Sessions[1].setFlash((flashing || spectraForceTorch) && !isFrontface && recording);
        }
    } else {
        if (cameraSession != null) {
            cameraSession.setTorchEnabled((flashing || spectraForceTorch) && !isFrontface && recording);
        }
    }
```

Примечание: `updateFlash` вызывается при старте записи (≈1876) и на каждый переключатель вспышки, поэтому факел включается/выключается вместе с записью кружочка. Для фронталки существующий `flashViews.flashIn/flashOut` (белая заливка) уже работает — расширяйте `shouldFrontFlash` аналогично, если нужно.

### 21. B25 Jump to start (долгий тап по «вниз» → в начало чата) — `org/telegram/ui/ChatActivity.java`

Точка вставки: `ChatActivity.onSideControlButtonOnLongClick(int buttonId, View view)` (≈ строка 49981) — обработчик long-press кнопок сайд-панели; кнопка «вниз» = `ChatActivitySideControlsButtonsLayout.BUTTON_PAGE_DOWN`.

```java
private boolean onSideControlButtonOnLongClick(int buttonId, View view) {
    // SPECTRA: START — B25 jump to chat start on long-press of "down" button
    if (buttonId == ChatActivitySideControlsButtonsLayout.BUTTON_PAGE_DOWN
            && org.telegram.spectra.SpectraConfig.isJumpToStart()
            && chatAdapter != null && chatLayoutManager != null) {
        int startRow = Math.max(1, chatAdapter.messagesStartRow);
        chatLayoutManager.scrollToPositionWithOffset(startRow, 0);
        chatListView.invalidate();
        return true;
    }
    // SPECTRA: END
    final Runnable onRead;
```

Примечание: `chatAdapter.messagesStartRow` и `chatLayoutManager.scrollToPositionWithOffset` — реальные API снапшота (используются в ChatActivity ≈1882). Скролл к нулевой строке триггерит догрузку старой истории штатной логикой (`checkScrollForLoad`). Обычный клик (`onPageDownClicked`, ≈11274) сохраняет исходное поведение.

### 22. B26 Search user messages (долгий тап по аватару) — `org/telegram/ui/Components/ChatAvatarContainer.java`

Точка вставки: конструктор `ChatAvatarContainer`, рядом с `avatarImageView.setOnClickListener(...)` (≈ строки 274–278).

```java
avatarImageView.setOnClickListener(v -> {
    if (!onAvatarClick()) {
        openProfile(true);
    }
});
// SPECTRA: START — B26 search messages of this user
if (parentFragment != null && parentFragment.getCurrentUser() != null
        && org.telegram.spectra.SpectraConfig.isSearchUserMessages()) {
    avatarImageView.setOnLongClickListener(v -> {
        TLRPC.Chat chat = parentFragment.getCurrentChat();
        if (chat == null) return false;
        Bundle args = new Bundle();
        args.putLong("chat_id", chat.id);
        args.putLong("search_from_user_id", parentFragment.getCurrentUser().id);
        parentFragment.presentFragment(new ChatActivity(args));
        return true;
    });
}
// SPECTRA: END
```

Примечание: аргумент `"search_from_user_id"` — реальный протокол ChatActivity (читается ≈10138, пример использования в `StatisticActivity` ≈3535–3538). Работает только в личных чатах (uid > 0); для групп используйте `from_id`-фильтр через `openSearchWithText` — точка та же.

### 23. A20 Drawer enabled — точка не найдена в снапшоте

Точка вставки: **не найдена**. Классический drawer (боковое меню) в этом снапшоте удалён: `DrawerLayoutContainer` (`org/telegram/ui/ActionBar/DrawerLayoutContainer.java`) не содержит `openDrawer/closeDrawer` и `onInterceptTouchEvent` только проверяет анимацию переходов; единственное упоминание `closeDrawer` в `LaunchActivity` (≈9351) закомментировано. Поиск: `rg "openDrawer|closeDrawer|drawerLayoutAdapter" TMessagesProj/src/main/java` — совпадений в активном коде нет.

Примечание: если в будущей версии форка drawer вернётся, хук ставится guard'ом `if (!SpectraConfig.isDrawerEnabled()) return;` в его open-метод; сейчас же пункт «Spectra Settings» добавляется в `NekoSettingsActivity` (хук 29).

### 24. A24 Hide «All chats» tab — `org/telegram/ui/Components/FilterTabsView.java`

Точка вставки: `FilterTabsView.onMeasure(...)` (≈ строка 1686) — рядом с существующим флагом Nagram `NekoConfig.hideAllTab`.

```java
Tab firstTab = findDefaultTab();
// SPECTRA: START — A24 hide All chats tab
boolean spectraHideAllChats = NekoConfig.hideAllTab.Bool()
        || org.telegram.spectra.SpectraConfig.isHideAllChatsTab();
// SPECTRA: END
if (firstTab != null && !spectraHideAllChats)  {
    firstTab.setTitle(LocaleController.getString(R.string.FilterAllChats), null, false);
```

Примечание: тот же `NekoConfig.hideAllTab.Bool()` повторяется в `selectTabWithId` (≈1783, `toggleAllTabs`), `notifyTabCounterChanged` (≈1899) и `checkTabWidths` (≈1863) — замените условие `!NekoConfig.hideAllTab.Bool()` на `!spectraHideAllChats` (или добавьте `|| SpectraConfig.isHideAllChatsTab()`) во всех четырёх местах, иначе таб скрыт визуально, но счётчик/скролл будут ссылаться на него.

### 25. A15 Auto-hide tab bar при скролле — `org/telegram/ui/DialogsActivity.java`

Точка вставки: `onScrolled` внутреннего `RecyclerView.OnScrollListener` (≈ строка 4633), рядом с `hideFloatingButton(goingDown)` (≈4674).

```java
if (changed && scrollUpdated && (goingDown || scrollingManually)) {
    hideFloatingButton(goingDown);
}
// SPECTRA: START — A15 auto-hide filter tabs on scroll
if (filterTabsView != null && filterTabsView.getVisibility() == View.VISIBLE
        && org.telegram.spectra.SpectraConfig.isAutoHideTabBar()
        && !filterTabsView.isEditing()) {
    float target = goingDown ? filterTabsView.getMeasuredHeight() : 0f;
    filterTabsView.animate().translationY(target)
            .setDuration(180).setInterpolator(CubicBezierInterpolator.DEFAULT)
            .start();
}
// SPECTRA: END
prevPosition = firstVisiblePosition;
```

Примечание: `isEditing()` исключает анимацию во время редактирования фолдеров (метод существует, ≈1371), `CubicBezierInterpolator.DEFAULT` уже импортирован в DialogsActivity. Сдвиг через `translationY` не ломает layout-вычисления `tabsYOffset` (≈1065), т.к. измеренная высота сохраняется.

### 26. A13 Floating tab bar (стеклянная панель фолдеров) — `org/telegram/ui/DialogsActivity.java`

Точка вставки: `DialogsActivity.createView(...)`, строка `contentView.addView(filterTabsView, ...)` (≈5286; создание ≈3664).

```java
contentView.addView(filterTabsView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 36 + 7 + 7, Gravity.TOP, 4, 0, 4, 0));
// SPECTRA: START — A13 floating glass tab bar
if (org.telegram.spectra.SpectraConfig.isLiquidGlass()
        && org.telegram.spectra.SpectraConfig.isFloatingTabBar()) {
    org.telegram.spectra.SpectraGlassPanel glassPanel =
            new org.telegram.spectra.SpectraGlassPanel(context);
    glassPanel.setCornerRadius(AndroidUtilities.dp(18));
    glassPanel.attachCaptureRoot(contentView);
    contentView.removeView(filterTabsView);
    filterTabsView.setTranslationY(0);
    glassPanel.addView(filterTabsView, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP));
    contentView.addView(glassPanel, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP, 4, 0, 4, 0));
}
// SPECTRA: END
```

Примечание: `attachCaptureRoot(contentView)` заставляет панель захватывать и размывать содержимое списка диалогов под собой (см. `SpectraGlassPanel.attachCaptureRoot`), радиус/альфа/неон берутся из `SpectraConfig` в `applyConfig()`. Внутри `FilterTabsView` уже рисуется rounded-clip (`clipPath`, ≈1657–1671), поэтому капсула не конфликтует с его собственным клипом.

### 27. A41 (glass) Стеклянные диалоги — `org/telegram/ui/ActionBar/BaseFragment.java` (advanced)

Точка вставки: `BaseFragment.showDialog(Dialog dialog, boolean allowInTransition, OnDismissListener)` (≈ строка 836) — центральный проход всех AlertDialog фрагментов.

```java
public Dialog showDialog(Dialog dialog, boolean allowInTransition, final Dialog.OnDismissListener onDismissListener) {
    if (dialog == null || parentLayout == null || parentLayout.isTransitionAnimationInProgress() || parentLayout.isSwipeInProgress() || !allowInTransition && parentLayout.checkTransitionAnimation()) {
        return null;
    }
    // SPECTRA: START — glass dialogs (ADVANCED, opt-in)
    if (org.telegram.spectra.SpectraConfig.isGlassDialogs()
            && org.telegram.spectra.SpectraGlassPanel.blurSupported()
            && dialog instanceof android.app.AlertDialog) {
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        // доп. тонирование/скругление делает SpectraThemeUtils через ResourcesProvider;
        // см. notes ниже — без проверки на реальном устройстве не включать.
    }
    // SPECTRA: END
    if (sheetsStack != null) {
```

Примечание: **продвинутый хук** — прозрачный фон AlertDialog требует согласованного фона у содержимого (`AlertsCreator` рисует собственный rounded-фонарь), иначе диалог станет «дыркой». Минимально безопасная реализация — тонирование через `ResourcesProvider` + `SpectraGlassPanel` в кастомном `AlertsCreator`; до этого держать `glassDialogs=false` по умолчанию.

### 28. B39 Speed options (расширенные скорости видео) — `org/telegram/ui/SpeedButtonsLayout.java` + `org/telegram/ui/PhotoViewer.java`

Точка вставки: конструктор `SpeedButtonsLayout` (org/telegram/ui/SpeedButtonsLayout.java ≈ строки 32–88) — добавляем пункт 2.5x/3.0x; и `PhotoViewer` ≈5991 (`speedItem.setStops(new float[]{0.5f, 1.0f, 1.5f, 2.0f, 2.5f})`).

```java
item = ActionBarMenuItem.addItem(this, R.drawable.msg_speed_superfast, LocaleController.getString(R.string.SpeedVeryFast), false, null);
item.setColors(0xfffafafa, 0xfffafafa);
item.setOnClickListener((view) -> {
    callback.onSpeedSelected(2f, true, true);
});
item.setSelectorColor(0x0fffffff);
speedItems[4] = item;
// SPECTRA: START — B39 extra speed options
if (org.telegram.spectra.SpectraConfig.isSpeedUp()) {
    item = ActionBarMenuItem.addItem(this, R.drawable.msg_speed_0_2 /* icon reuse */, "2.5x", false, null);
    item.setColors(0xfffafafa, 0xfffafafa);
    item.setOnClickListener((view) -> callback.onSpeedSelected(2.5f, true, true));
    item.setSelectorColor(0x0fffffff);
}
// SPECTRA: END
```

Примечание: слайдер ограничен `ActionBarMenuSlider.SpeedSlider.MAX_SPEED = 3.0f` (org/telegram/ui/ActionBar/ActionBarMenuSlider.java ≈529–530), поэтому 2.5x легален без правок слайдера; параллельно в PhotoViewer расширьте `speedItem.setStops(...)` до `{0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f}` и массив `update(...)`-подсветки того же файла. Реальный ExoPlayer позволяет до 8x, ограничение только UI.

### 29. B41 Double-tap seek step — `org/telegram/ui/PhotoViewer.java`

Точка вставки: `PhotoViewer.onDoubleTap(MotionEvent e)` (≈ строка 21400) — шаг ±10000 мс хардкодом (≈21410, 21412, 21427).

```java
if (canDoubleTapSeekVideo(e)) {
    long old = current;
    // SPECTRA: START — B41 configurable double-tap seek (5/10/15 sec)
    // index 0/1/2 => 5/10/15 sec
    long seekStep = (org.telegram.spectra.SpectraConfig.getDoubleTapSeek() + 1) * 5000L;
    // SPECTRA: END
    if (x >= width / 3 * 2) {
        current += seekStep;
    } else if (x < width / 3) {
        current -= seekStep;
    }
```

Примечание: `getDoubleTapSeek()` — индекс 0/1/2 («5 сек»/«10 сек»/«15 сек»), поэтому шаг = `(index + 1) * 5000` мс — ровно как в сниппете. Не забудьте заменить также `videoForwardDrawable.addTime(10000)` на `addTime(seekStep)` (≈21427), чтобы анимация «+10s» соответствовала реальному шагу.

### 30. B31 Voice radio (не отмечать голосовые «прослушанными») — `org/telegram/messenger/MediaController.java`

Точка вставки: `MediaController.playMessage(MessageObject messageObject, boolean silent)` (≈ строка 3678), блок отметки контента прочитанным (≈3693–3695).

```java
// SPECTRA: START — B31 voice radio: don't mark voice as listened
boolean spectraSkipListened = org.telegram.spectra.SpectraConfig.isVoiceRadio()
        && messageObject.isVoice(); // isVoice() exists (MessageObject ≈11307)
// SPECTRA: END
if (!messageObject.isOut() && (messageObject.isContentUnread()) && !spectraSkipListened) {
    MessagesController.getInstance(messageObject.currentAccount).markMessageContentAsRead(messageObject);
}
```

Примечание: альтернативный (более широкий) вариант — ранний return в `MessagesController.markMessageContentAsRead` (≈14676) при `isVoiceRadio() && messageObject.isVoice()`; тогда не отправятся и `TL_messages_readMessageContents`/`TL_channels_readMessageContents`. Побочно фича приглушает и «прослушано» в голосовых уведомлениях — это ожидаемое поведение «радио».

### 31. Spectra Settings entry — `tw/nekomimi/nekogram/settings/NekoSettingsActivity.java`

Точка вставки: `NekoSettingsActivity.updateRows()` (≈ строка 158, блок категорий ≈161–172), `onItemClick` (≈97) и `ListAdapter.onBindViewHolder` (≈196).

```java
// updateRows(): after debugRow = addRow("debug");
// SPECTRA: START — Spectra settings entry
spectraRow = addRow("spectra"); // add field: private int spectraRow;
// SPECTRA: END
categories2Row = addRow();

// onItemClick(): add branch
// SPECTRA: START — Spectra settings entry
} else if (position == spectraRow) {
    presentFragment(new org.telegram.spectra.SpectraSettingsActivity());
// SPECTRA: END

// ListAdapter.onBindViewHolder(), case TYPE_TEXT: add binding
// SPECTRA: START — Spectra settings entry
} else if (position == spectraRow) {
    textCell.setTextAndIcon("Spectra", R.drawable.msg_info, divider);
// SPECTRA: END
```

Примечание: строки между `categoriesRow` и `categories2Row` автоматически получают тип `TYPE_TEXT` (см. `getItemViewType` ≈254–267), поэтому достаточно трёх вставок выше. Альтернативная точка — главный `SettingsActivity`, `case 100: presentFragment(new NekoSettingsActivity())` (≈985–988): рядом можно вставить `case 101: presentFragment(new org.telegram.spectra.SpectraSettingsActivity());`.

### 32. Startup theme application — `org/telegram/messenger/ApplicationLoader.java`

Точка вставки: `ApplicationLoader.onCreate()` (≈ строка 332), сразу после `NativeLoader.initNativeLibs(...)` (≈360).

```java
NativeLoader.initNativeLibs(ApplicationLoader.applicationContext);
// SPECTRA: START — apply Spectra theme/palette at startup
try {
    org.telegram.spectra.SpectraConfig.prefs(); // force prefs init on main thread
    org.telegram.spectra.SpectraThemeUtils.apply();
    org.telegram.spectra.SpectraPalette.apply();
} catch (Throwable ignore) {}
// SPECTRA: END
```

Примечание: оба `apply()` — реальные статические методы (`SpectraThemeUtils.java:15`, `SpectraPalette.java:232`). Вызов до первого `LaunchActivity.onCreate` гарантирует, что accent/AMOLE-палитра применены до построения темы; повторное применение при смене настроек делает `SpectraConfig.notifyChanged → SpectraThemeUtils.onConfigChanged`.

---

## Чек-лист верификации

| # | Хук | Файл (TMessagesProj/src/main/java/…) | Метод/якорь | Статус |
|---|-----|--------------------------------------|-------------|--------|
| 1 | B7 anti-delete | org/telegram/messenger/MessagesController.java | processUpdateArray → deletedMessagesFinal (≈21510) | ✅ verified |
| 2 | B8 edit history | org/telegram/messenger/MessagesController.java | processUpdateArray → editingMessagesFinal (≈21342) | ✅ verified |
| 3 | B2 hideTyping | org/telegram/messenger/MessagesController.java | sendTyping (≈11654) | ✅ verified |
| 4 | A17 hideStories | org/telegram/ui/DialogsActivity.java | createView → dialogStoriesCell (≈5293/5718) | ✅ verified |
| 5 | A27+A28 bubbleRadius/disableTails | org/telegram/ui/ActionBar/MessageDrawable.java | draw(Canvas,Paint) rad/nearRad (≈572–586) | ✅ verified |
| 6 | A32 hideGroupAvatars | org/telegram/ui/Cells/ChatMessageCell.java | isAvatarVisible (≈7018) | ✅ verified |
| 7 | A35 hideQuickReactions | org/telegram/ui/Cells/ChatMessageCell.java | shouldDrawReactions → setMessage (≈7290) | ✅ verified |
| 8 | A41 capsuleInputBar | org/telegram/ui/Components/ChatActivityEnterView.java | messageEditTextContainer (≈2709/2750) | ✅ verified |
| 9 | A42 hideRecordButton | org/telegram/ui/Components/ChatActivityEnterView.java | createRecordCircle (≈4618) | ✅ verified |
| 10 | A43 hideAttachExtras | org/telegram/ui/Components/ChatActivityEnterView.java | attachButton (≈2877) | ✅ verified |
| 11 | B12 stripExif | org/telegram/messenger/SendMessagesHelper.java | prepareSendingMedia → tempPath (≈10992) | ✅ verified |
| 12 | B13 antiPhishing | org/telegram/ui/ChatActivity.java | processExternalUrl (≈38023) | ✅ verified |
| 13 | B15 fakePin | org/telegram/ui/Components/PasscodeView.java | processDone (≈1112/1128) | ✅ verified |
| 14 | B28 emptyMessages | org/telegram/messenger/SendMessagesHelper.java | sendMessage(SendMessageParams) (≈4451) | ✅ verified |
| 15 | B29 inlineCalc | org/telegram/messenger/SendMessagesHelper.java | sendMessage(SendMessageParams) (≈4451) | ✅ verified |
| 16 | B27 snippets | org/telegram/messenger/SendMessagesHelper.java | sendMessage(SendMessageParams) (≈4451) | ✅ verified |
| 17 | B22/B23 forward flags | org/telegram/messenger/SendMessagesHelper.java | sendMessage(ArrayList…) (≈2236, req.drop_author ≈2754) | ✅ verified |
| 18 | B45 reg date | org/telegram/ui/ProfileActivity.java | updateProfileData → newString2 (≈11847/11986) | ✅ verified |
| 19 | B46 forceHighRefreshRate | org/telegram/ui/LaunchActivity.java | onCreate → setContentView (≈436/511) | ✅ verified |
| 20 | B34 videoNoteFlash | org/telegram/ui/Components/InstantCameraView.java | updateFlash (≈452, torch ≈464/474) | ✅ verified |
| 21 | B25 jumpToStart | org/telegram/ui/ChatActivity.java | onSideControlButtonOnLongClick (≈49981) | ✅ verified |
| 22 | B26 searchUserMessages | org/telegram/ui/Components/ChatAvatarContainer.java | конструктор → avatarImageView (≈274) | ✅ verified |
| 23 | A20 drawerEnabled | — | — | ❌ не найдено: drawer в снапшоте удалён; искать `rg "openDrawer|closeDrawer|drawerLayoutAdapter"` |
| 24 | A24 hideAllChatsTab | org/telegram/ui/Components/FilterTabsView.java | onMeasure hideAllTab (≈1686; также ≈1783/1863/1899) | ✅ verified |
| 25 | A15 autoHideTabBar | org/telegram/ui/DialogsActivity.java | onScrolled (≈4633) | ✅ verified |
| 26 | A13 floatingTabBar | org/telegram/ui/DialogsActivity.java | contentView.addView(filterTabsView) (≈5286) | ✅ verified |
| 27 | A41 glass dialogs | org/telegram/ui/ActionBar/BaseFragment.java | showDialog(Dialog, boolean, listener) (≈836) | ✅ verified (advanced) |
| 28 | B39 speed options | org/telegram/ui/SpeedButtonsLayout.java + org/telegram/ui/PhotoViewer.java | конструктор (≈32–88) + speedItem.setStops (≈5991) | ✅ verified |
| 29 | B41 doubleTapSeek | org/telegram/ui/PhotoViewer.java | onDoubleTap (≈21400) | ✅ verified |
| 30 | B31 voiceRadio | org/telegram/messenger/MediaController.java | playMessage → markMessageContentAsRead (≈3693; alt MessagesController ≈14676) | ✅ verified |
| 31 | Spectra Settings entry | tw/nekomimi/nekogram/settings/NekoSettingsActivity.java | updateRows/onItemClick/onBindViewHolder (≈158/97/196; alt SettingsActivity case 100 ≈985) | ✅ verified |
| 32 | Startup theme | org/telegram/messenger/ApplicationLoader.java | onCreate (≈332) | ✅ verified |

**Итог: 32 хука; полностью верифицировано — 31, не найдено в снапшоте — 1 (A20 drawerEnabled, с готовым rg-запросом для повторного поиска).**
