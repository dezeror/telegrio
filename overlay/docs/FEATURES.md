# Spectra — карта настроек и функций

Всего настроек: **103**. Из них: спектровских (новая логика + Liquid Glass) — **73**, подключённых к встроенным возможностям Nagram — **30**.

Страница настроек: главное меню Nagram → «Spectra Settings» (8 разделов).

Хранилище: `SpectraConfig.id` — собственные ключи мода (SharedPreferences `spectraconfig`); `Nagram: owner.item` — прямая привязка к ключам Nagram/NekoX (работают сразу, без патчей). Хуки подключения собственных функций описаны в [HOOKS.md](HOOKS.md).

## Liquid Glass и цвета (Блок A.1)

| № | Настройка | По умолчанию | Хранилище | Реализация |
|---|---|---|---|---|
| A1 | Liquid Glass | True | `SpectraConfig.liquidGlass` | SpectraGlassPanel master switch (RenderEffect API31+ / ScriptIntrinsicBlur fallback) |
| A2 | Сила размытия | 55 (%) | `SpectraConfig.glassBlurRadius` | SpectraGlassPanel.blurRadiusPx = 25 * value / 100 |
| A3 | Прозрачность панелей | 65 (%) | `SpectraConfig.glassAlpha` | SpectraGlassPanel tint alpha |
| A4 | Неоновая обводка | True | `SpectraConfig.neonGlowBorder` | SpectraGlassPanel.setStrokeGlow |
| A5 | Интенсивность свечения | 45 (%) | `SpectraConfig.glowIntensity` | SpectraGlassPanel.setGlowIntensity |
| A6 | Стекло в диалогах и шторках | True | `SpectraConfig.glassDialogs` | SpectraGlassPanel.wrapDialog |
| A7 | Pure AMOLED | True | `SpectraConfig.pureAmoled` | SpectraThemeUtils.apply() |
| A8 | Акцент из обоев (Material You) | False | `SpectraConfig.monetAccent` | SpectraThemeUtils.applyMonetAccent() |
| A8b | Акцентный цвет Spectra | 0xFF2CE876 | `SpectraConfig.accentColor` | SpectraThemeUtils palette |
| A9 | Градиентные пузыри | True | `SpectraConfig.gradientBubbles` | Theme keys chat_outBubbleGradient* + animated |
| A10 | Edge-to-Edge | True | Nagram: `NekoConfig.transparentStatusBar` | встроено в Nagram — работает сразу |
| A11 | Скрыть тени панелей | True | Nagram: `NekoConfig.disableAppBarShadow` | встроено в Nagram — работает сразу |
| A12 | Свой цвет счётчика непрочитанных | False | `SpectraConfig.customUnreadColor` | Theme.key_chats_unreadCounter override |
| A12b | Цвет счётчика | 0xFF2CE876 | `SpectraConfig.unreadColor` | Theme override |

## Навигация и главный экран (Блок A.2)

| № | Настройка | По умолчанию | Хранилище | Реализация |
|---|---|---|---|---|
| A13 | Плавающая панель вкладок | True | `SpectraConfig.floatingTabBar` | SpectraGlassPanel on FilterTabsView container (experimental) |
| A14 | Только иконки во вкладках | False | Nagram: `NekoConfig.tabsTitleType` | NekoXConfig.tabsTitleType = TITLE_TYPE_ICON |
| A15 | Автоскрытие панели вкладок | False | `SpectraConfig.autoHideTabBar` | scroll listener in DialogsActivity hook |
| A16 | Заголовок по центру | True | Nagram: `NekoConfig.centerActionBarTitle` | встроено в Nagram — работает сразу |
| A17 | Скрыть панель историй | False | `SpectraConfig.hideStories` | StoriesList visibility hook in DialogsActivity (no native toggle in Nagram) |
| A18 | Компактный список чатов | False | `SpectraConfig.compactChats` | DialogCell height factor hook |
| A19 | Скрыть разделители | True | `SpectraConfig.hideDividers` | Theme.key_chats_divider = transparent |
| A20 | Боковое меню | True | `SpectraConfig.drawerEnabled` | LaunchActivity drawer toggle hook |
| A21 | Скрыть номер телефона | True | Nagram: `NekoConfig.hidePhone` | встроено в Nagram — работает сразу |
| A22 | Скрыть кнопку нового чата | False | Nagram: `NekoConfig.disableDialogsFloatingButton` | встроено в Nagram — работает сразу |
| A23 | Счётчик на вкладке Все чаты | False | `SpectraConfig.folderUnreadCount` | FilterTabsView badge hook (no native toggle in Nagram) |
| A24 | Скрыть вкладку Все чаты | False | `SpectraConfig.hideAllChatsTab` | FilterTabsView hide tab 0 hook |
| A25 | Squircle-аватарки | True | `SpectraConfig.squircleAvatars` | SpectraAvatarUtils radius override |
| A25b | Радиус скругления аватарок | 30 (%) | `SpectraConfig.avatarCornerRadius` | SpectraAvatarUtils |
| A26 | Неоновое кольцо онлайн | True | `SpectraConfig.onlineNeonRing` | SpectraAvatarUtils online ring drawable |

## Пузыри чатов и типографика (Блок A.3)

| № | Настройка | По умолчанию | Хранилище | Реализация |
|---|---|---|---|---|
| A27 | Радиус углов сообщений | 16 (dp) | `SpectraConfig.bubbleRadius` | ChatMessageCell bubble radius override |
| A28 | Отключить хвостики пузырей | False | `SpectraConfig.disableTails` | ChatMessageCell tail drawing hook (no native toggle in Nagram) |
| A29 | Размер шрифта сообщений | 16 (sp) | `SpectraConfig.messageFontSize` | sets SharedConfig.fontSize + NotificationCenter.theme reload |
| A30 | Кастомный шрифт | 0 → «Системный» | Nagram: `NekoConfig.typeface` | встроено в Nagram — работает сразу |
| A31 | Системные эмодзи | False | Nagram: `NekoConfig.useSystemEmoji` | встроено в Nagram — работает сразу |
| A32 | Скрыть аватарки в группах | False | `SpectraConfig.hideGroupAvatars` | ChatMessageCell avatar visibility hook |
| A33 | Скрыть автора пересылки | False | `SpectraConfig.hideForwardSender` | forward pipeline default flag |
| A34 | Время внутри пузыря | False | `SpectraConfig.timeInsideBubble` | MessageLayoutController hook |
| A35 | Скрыть быстрые реакции | False | `SpectraConfig.hideQuickReactions` | ReactionsLayoutInBubble hook |
| A36 | Скрыть кнопку Поделиться | False | `SpectraConfig.hideChannelShare` | channel post cell hook |
| A37 | Скругление фото и видео | 20 (dp) | `SpectraConfig.photoCornerRadius` | media cell radius hook |
| A38 | Размер стикеров | 100 (%) | Nagram: `NekoConfig.stickerSize` | встроено в Nagram — работает сразу |
| A39 | Отключить анимацию стикеров | False | `SpectraConfig.disableAnimatedStickers` | VERIFY in Nagram (grep disableAnimatedEmoji), fallback Lottie hook |
| A40 | Размытие обоев чата | False | Nagram: `NekoConfig.forceBlurInChat` | встроено в Nagram — работает сразу |

## Поле ввода и клавиатура (Блок A.4)

| № | Настройка | По умолчанию | Хранилище | Реализация |
|---|---|---|---|---|
| A41 | Капсульное поле ввода | True | `SpectraConfig.capsuleInputBar` | SpectraGlassPanel on ChatActivity input container |
| A42 | Скрыть кнопку записи | False | `SpectraConfig.hideRecordButton` | ChatActivity enter/record button visibility hook |
| A43 | Скрыть скрепку и подарки | False | `SpectraConfig.hideAttachExtras` | attach button visibility hook |
| A44 | Замена кнопки микрофона | 0 → «Не заменять» | `SpectraConfig.replaceMicButton` | mic button action remap hook |
| A45 | Высокое поле ввода | False | `SpectraConfig.tallerInputField` | EditText maxLines hook |
| A46 | Плавная клавиатура | True | `SpectraConfig.smoothKeyboard` | setDecorFitsSystemWindows false + inset animation |
| A47 | Скрыть подсказки стикеров | False | `SpectraConfig.hideStickerHints` | sticker suggestions panel hook |
| A48 | Скрыть кнопку камеры | False | `SpectraConfig.disableCameraButton` | VERIFY in Nagram (grep disableCameraButton), fallback visibility hook |
| A49 | Неоновая подсветка ссылок | True | `SpectraConfig.neonLinkHighlight` | SpectraThemeUtils URLSpan color |
| A50 | Режим Дзен | False | `SpectraConfig.zenMode` | ChatAvatarContainer double-tap hook |

## Приватность и Призрак (Блок Б.1)

| № | Настройка | По умолчанию | Хранилище | Реализация |
|---|---|---|---|---|
| B1 | Режим Призрак | False | Nagram: `NekoConfig.ghostMode` | VERIFY: NekoConfig.setGhostMode/toggleGhostMode/isGhostModeActive |
| B2 | Не отправлять печатает... | False | `SpectraConfig.hideTyping` | MessagesController.sendTyping guard (no per-packet toggle in Nagram) |
| B3 | Читать без галочек | False | Nagram: `NekoConfig.sendReadMessagePackets` | встроено в Nagram — работает сразу |
| B4 | Прочитано при ответе | True | Nagram: `NekoConfig.markReadAfterSend` | встроено в Nagram — работает сразу |
| B5 | Скрывать статус в сети | False | Nagram: `NekoConfig.sendOnlinePackets` | встроено в Nagram — работает сразу |
| B6 | Истории инкогнито | False | Nagram: `NekoConfig.sendReadStoryPackets` | встроено в Nagram — работает сразу |
| B7 | Анти-удаление сообщений | False | `SpectraConfig.antiDelete` | SpectraDeletedStore + MessagesController.processUpdateDeleteMessages hook |
| B8 | История редактирования | False | `SpectraConfig.editHistory` | SpectraDeletedStore.onMessageEdited hook |
| B9 | Замок на чаты | False | Nagram: `PasscodeHelper` | tw.nekomimi.nekogram.helpers.PasscodeHelper + NekoPasscodeSettingsActivity |
| B10 | Скрыть секретные чаты | False | `SpectraConfig.hideSecretChats` | dialogs filter + long-press reveal hook |
| B11 | Запрет скриншотов | False | Nagram: `NekoXConfig.disableFlagSecure` | row inverts disableFlagSecure |
| B12 | Очистка EXIF | True | `SpectraConfig.stripExif` | SpectraExifCleaner on send path |
| B13 | Анти-фишинг | False | `SpectraConfig.antiPhishing` | ChatActivity open link hook + SpectraUtils.isSuspiciousUrl |
| B14 | Запрет авто-добавления | False | `SpectraConfig.confirmInvites` | TLRPC update hook (VERIFY native updateSettings threshold) |
| B15 | Фейковый код-пароль | False | `SpectraConfig.fakePin` | SpectraConfig.fakePinValue + PasscodeView hook |
| B15b | Код фейкового пароля | 1234 | `SpectraConfig.fakePinValue` | text input dialog |
| B15c | Скрытые чаты | [] | `SpectraConfig.lockedChats` | used by per-chat lock reveal |

## Умные чаты и работа с текстом (Блок Б.2)

| № | Настройка | По умолчанию | Хранилище | Реализация |
|---|---|---|---|---|
| B16 | Перевод сообщений | True | Nagram: `NekoConfig.translationProvider` | opens translation provider settings (NekoExperimentalSettingsActivity) |
| B17 | Автоперевод входящих | False | Nagram: `DialogConfig.autoTranslate` | per-dialog DialogConfig |
| B18 | Перевод ввода | False | Nagram: `NekoConfig.translateInputLang` | встроено в Nagram — работает сразу |
| B19 | Копирование части текста | True | Nagram: `NekoConfig.forceCopy` | встроено в Nagram — работает сразу |
| B20 | Умное цитирование | True | `SpectraConfig.smartQuote` | selection->quote action hook |
| B21 | Подтверждение отправки медиа | False | Nagram: `NekoConfig.confirmAVMessage` | VERIFY + link NaConfig.askBeforeSendingSticker/GIF rows |
| B21b | Подтверждение стикеров | False | Nagram: `NaConfig.askBeforeSendingSticker` | встроено в Nagram — работает сразу |
| B22 | Пересылка без цитаты | False | `SpectraConfig.forwardNoQuote` | forward pipeline default flag (VERIFY disableQuote) |
| B23 | Пересылка без подписи | False | `SpectraConfig.forwardNoCaption` | forward pipeline flag |
| B24 | Упомянуть всех | False | Nagram: `MemberAdminActions` | xyz.nextalone.nagram.MemberAdminActions |
| B25 | Кнопка В начало | True | `SpectraConfig.jumpToStart` | chat scroll-to-bottom button long-press hook |
| B26 | Поиск по автору | True | `SpectraConfig.searchUserMessages` | avatar long-press -> fromUser search hook |
| B27 | Сниппеты | False | `SpectraConfig.snippets` | SpectraUtils.applySnippets on send |
| B27b | Правила сниппетов | {} | `SpectraConfig.snippetsData` | text input dialog |
| B28 | Пустые сообщения | False | `SpectraConfig.emptyMessages` | SpectraUtils.INVISIBLE_CHAR on send |
| B29 | Калькулятор в поле ввода | True | `SpectraConfig.inlineCalc` | SpectraUtils.evaluateInline |
| B30 | Безлимитные закрепы | False | Nagram: `NekoConfig.unlimitedPinnedDialogs` | встроено в Nagram — работает сразу |

## Медиа, голос и кружочки (Блок Б.3)

| № | Настройка | По умолчанию | Хранилище | Реализация |
|---|---|---|---|---|
| B31 | Режим рации | False | `SpectraConfig.voiceRadio` | VERIFY in Nagram, fallback message read packet hook |
| B32 | Битрейт голосовых | 64 (kbps) | Nagram: `NekoConfig.customAudioBitrate` | NekoConfig.customAudioBitrate (ConfigItem int) |
| B33 | Пауза перед отправкой | False | `SpectraConfig.voicePreview` | record stop -> preview state hook |
| B34 | Вспышка при записи кружков | False | `SpectraConfig.videoNoteFlash` | CameraManager.setTorchMode hook |
| B35 | Камера для кружков | True | Nagram: `NekoConfig.rearVideoMessages` | встроено в Nagram — работает сразу |
| B36 | Видео в кружочек | False | `SpectraConfig.videoToCircle` | gallery send path crop-to-circle hook |
| B37 | Фото в максимальном качестве | False | `SpectraConfig.sendOriginalPhoto` | send photo quality flag hook |
| B38 | Сохранять самоудаляющиеся медиа | False | `SpectraConfig.saveTTLToGallery` | VERIFY native; fallback FileLoader cache copy hook |
| B39 | Ускорение до 3.0x | True | `SpectraConfig.speedUp` | playback speed options array hook |
| B40 | Не перескакивать на канал | False | `SpectraConfig.noSwipeToNext` | VERIFY in Nagram (grep swipeToNext), fallback swipe hook |
| B41 | Перемотка двойным тапом | 1 → «10 сек» | `SpectraConfig.doubleTapSeek` | VideoPlayer double-tap seek hook |
| B42 | Останавливать медиа при сворачивании | False | Nagram: `NekoConfig.autoPauseVideo` | встроено в Nagram — работает сразу |

## Система, производительность и аналитика (Блок Б.4)

| № | Настройка | По умолчанию | Хранилище | Реализация |
|---|---|---|---|---|
| B43 | Блокировка рекламы | True | Nagram: `NekoConfig.hideSponsoredMessage` | встроено в Nagram — работает сразу |
| B44 | Показывать ID и DC | True | Nagram: `NekoConfig.showMessageID` | showMessageID + DatacenterActivity (showIdAndDc) |
| B45 | Дата регистрации | False | `SpectraConfig.showRegDate` | SpectraUtils.estimateRegDate in ProfileActivity hook |
| B46 | 120/144 FPS | False | `SpectraConfig.forceHighRefreshRate` | Surface.frameRate + WindowManager preferredDisplayModeId hook |

---

## Честный статус функций

- **Работает сразу после сборки:** все настройки интерфейса Spectra (тема Spectra Dark, Liquid Glass-панели мода, иконки, страницы настроек), а также все пункты с хранилищем `Nagram: ...` (ghost-режим, антиреклама, безлимитные закрепы, подтверждения отправки и т.д.).
- **Требует применения хуков (HOOKS.md):** анти-удаление, история редактирования, скрытие Stories, режим Дзен, калькулятор в поле ввода, сниппеты, пустые сообщения, анти-фишинг, дата регистрации, 120 FPS и другие пункты с хранилищем `SpectraConfig.*` — их UI готов, поведение включается патчем соответствующего хука.
- Скрипт `apply_overlay.py` копирует код и брендинг автоматически; хуки накладываются по инструкции из HOOKS.md (каждый — 5–25 строк с маркерами `// SPECTRA: START/END`).

