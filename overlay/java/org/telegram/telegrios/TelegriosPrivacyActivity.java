package org.telegram.telegrios;

import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.settings.NekoPasscodeSettingsActivity;

import java.util.List;

/**
 * Telegrios Settings page "privacy" — spec B1-B15c («Приватность»).
 *
 * Row mapping (all Nagram keys re-verified by rg against the local clone):
 *
 *   B1   ghostMode        -> lambdaSwitch  NekoConfig.isGhostModeActive()/setGhostMode(v)
 *                            (methods verified in NekoConfig.java:290/309/312)
 *   B2   hideTyping       -> specSwitch    TelegriosConfig.isHideTyping
 *   B3   noReadOnOpen     -> naSwitch      NekoConfig.sendReadMessagePackets, invert=true
 *                            (plain public static boolean, default true = «send»)
 *   B4   readOnReply      -> naSwitch      NekoConfig.markReadAfterSend (plain static boolean, default true)
 *   B5   hideOnline       -> naSwitch      NekoConfig.sendOnlinePackets, invert=true
 *   B6   incognitoStories -> naSwitch      NekoConfig.sendReadStoryPackets, invert=true
 *   B7   antiDelete       -> specSwitch    TelegriosConfig.isAntiDelete
 *   B8   editHistory      -> specSwitch    TelegriosConfig.isEditHistory
 *   B9   perChatLock      -> action        presentFragment(new NekoPasscodeSettingsActivity())
 *                            (tw.nekomimi.nekogram.settings.NekoPasscodeSettingsActivity verified)
 *   B10  hideSecretChats  -> specSwitch    TelegriosConfig.isHideSecretChats
 *   B11  blockScreenshots -> naSwitch      NaConfig.disableFlagSecure, invert=true
 *                            (spec na guessed NekoXConfig; actual owner is NaConfig.kt:598;
 *                             disableFlagSecure=true means screenshots ALLOWED → inverted)
 *   B12  stripExif        -> specSwitch    TelegriosConfig.isStripExif
 *   B13  antiPhishing     -> specSwitch    TelegriosConfig.isAntiPhishing
 *   B14  confirmInvites   -> specSwitch    TelegriosConfig.isConfirmInvites
 *   B15  fakePin          -> specSwitch    TelegriosConfig.isFakePin
 *   B15b fakePinValue     -> info          managed by PasscodeView hook (TelegriosConfig.fakePinValue)
 *   B15c lockedChats      -> info          managed by per-chat lock hook (TelegriosConfig.lockedChats)
 */
public class TelegriosPrivacyActivity extends TelegriosBaseSettingsFragment {

    @Override
    protected String pageTitle() {
        return "Telegrios — Приватность";
    }

    @Override
    protected void fillRows(List<Row> rows) {
        rows.add(Row.header("Невидимость"));

        // B1 — master ghost switch over Nagram packet flags
        rows.add(Row.lambdaSwitch(
                "Режим Призрак",
                "Единый переключатель невидимости",
                () -> NekoConfig.isGhostModeActive(),
                v -> NekoConfig.setGhostMode(v)));

        // B2 — Telegrios-native typing guard
        rows.add(Row.specSwitch("hideTyping",
                "Не отправлять печатает...",
                "Также статус записи голосового"));

        // B3 — invert: row checked = read receipts suppressed
        rows.add(Row.naSwitch(
                "Читать без галочек",
                "Не отмечать прочитанным при открытии чата",
                "tw.nekomimi.nekogram.NekoConfig", "sendReadMessagePackets", true));

        // B4 — direct semantics (true = mark read on send)
        rows.add(Row.naSwitch(
                "Прочитано при ответе",
                "Отмечать прочитанным только при отправке ответа",
                "tw.nekomimi.nekogram.NekoConfig", "markReadAfterSend", false));

        // B5 — invert: row checked = online packet suppressed
        rows.add(Row.naSwitch(
                "Скрывать статус в сети",
                "Не отправлять Online при запуске",
                "tw.nekomimi.nekogram.NekoConfig", "sendOnlinePackets", true));

        // B6 — invert: row checked = story views hidden
        rows.add(Row.naSwitch(
                "Истории инкогнито",
                "Просмотр без отображения в зрителях",
                "tw.nekomimi.nekogram.NekoConfig", "sendReadStoryPackets", true));

        rows.add(Row.shadow());

        rows.add(Row.header("Сообщения"));

        // B7 — keep messages deleted by the peer, tagged [Удалено]
        rows.add(Row.specSwitch("antiDelete",
                "Анти-удаление сообщений",
                "Сохранять удалённые собеседником сообщения с пометкой [Удалено]"));

        // B8 — keep original text of edited messages
        rows.add(Row.specSwitch("editHistory",
                "История редактирования",
                "Просмотр оригинального текста изменённых сообщений"));

        // B10 — secret chats revealed by long-press on the header
        rows.add(Row.specSwitch("hideSecretChats",
                "Скрыть секретные чаты",
                "Вызов по долгому тапу на заголовок"));

        rows.add(Row.shadow());

        rows.add(Row.header("Защита"));

        // B9 — per-chat lock is managed by the native Nagram passcode screen
        rows.add(Row.action("Замок на чаты",
                () -> presentFragment(new NekoPasscodeSettingsActivity())));

        // B11 — inverted disableFlagSecure (true in Nagram = screenshots allowed)
        rows.add(Row.naSwitch(
                "Запрет скриншотов",
                "FLAG_SECURE внутри приложения (инвертирует Nekogram toggle)",
                "xyz.nextalone.nagram.NaConfig", "disableFlagSecure", true));

        // B12 — EXIF stripping on the send path
        rows.add(Row.specSwitch("stripExif",
                "Очистка EXIF",
                "Удаление геолокации и модели камеры при отправке фото"));

        // B13 — suspicious link warning
        rows.add(Row.specSwitch("antiPhishing",
                "Анти-фишинг",
                "Предупреждение перед подозрительными ссылками"));

        // B14 — confirm before being added to groups/channels
        rows.add(Row.specSwitch("confirmInvites",
                "Запрет авто-добавления",
                "Подтверждение при добавлении в группы и каналы"));

        rows.add(Row.shadow());

        rows.add(Row.header("Код-пароль"));

        // B15 — fake passcode opens an empty account view
        rows.add(Row.specSwitch("fakePin",
                "Фейковый код-пароль",
                "При вводе открывает пустой аккаунт или скрывает чаты"));

        // B15b — string entry, filled by the PasscodeView hook, not editable here in v1
        rows.add(Row.info(
                "Код фейкового пароля: что вводить для входа в пустой режим. Значение задаётся хуком PasscodeView (TelegriosConfig.fakePinValue)."));

        // B15c — string entry, maintained by the per-chat lock, not editable here in v1
        rows.add(Row.info(
                "Скрытые чаты: JSON-список chat_id, скрытых замком. Управляется замком чатов (TelegriosConfig.lockedChats)."));

        rows.add(Row.info(
                "Telegrios — Приватность. «Режим Призрак» и галочки переключают пакеты Nagram; остальные пункты — собственные хуки Telegrios."));
    }
}
