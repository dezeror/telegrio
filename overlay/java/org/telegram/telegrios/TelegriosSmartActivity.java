package org.telegram.telegrios;

import tw.nekomimi.nekogram.settings.NekoGeneralSettingsActivity;

import java.util.List;

/**
 * Telegrios Settings page "smart" — spec B16-B27b («Умные чаты»).
 *
 * Row mapping (all Nagram keys re-verified by rg against the local clone):
 *
 *   B16   translateMessage  -> action    opens NekoGeneralSettingsActivity: the whole translation
 *                                        stack (NekoConfig.translationProvider = int provider,
 *                                        useTelegramTranslateInChat, translateToLang) lives there —
 *                                        verified rows translationProviderRow/useTelegramTranslateInChatRow
 *   B17   autoTranslate     -> info      DialogConfig.autoTranslate is per-dialog
 *                                        ("autoTranslate_" + dialogId), no global toggle exists
 *   B18   translateInput    -> info      NekoConfig.translateInputLang is a language string,
 *                                        edited in the Nagram translation settings
 *   B19   forceCopy         -> naSwitch  NaConfig.forceCopy (spec na guessed NekoConfig;
 *                                        actual owner is NaConfig.kt:29 "ForceCopy")
 *   B20   smartQuote        -> specSwitch TelegriosConfig.isSmartQuote
 *   B21   confirmSendMedia  -> naSwitch  NekoConfig.confirmAVMessage (ConfigItem bool, verified)
 *   B21b  confirmSticker    -> naSwitch  NaConfig.askBeforeSendingSticker (ConfigItem bool, verified)
 *   B22   forwardNoQuote    -> specSwitch TelegriosConfig.isForwardNoQuote
 *   B23   forwardNoCaption  -> specSwitch TelegriosConfig.isForwardNoCaption
 *   B24   mentionAll        -> info      provided by xyz.nextalone.nagram.helper.MemberAdminActions
 *                                        (verified in the local clone)
 *   B25   jumpToStart       -> specSwitch TelegriosConfig.isJumpToStart
 *   B26   searchUserMessages-> specSwitch TelegriosConfig.isSearchUserMessages
 *   B27   snippets          -> specSwitch TelegriosConfig.isSnippets
 *   B27b  snippetsData      -> info      managed by the send-path hook (TelegriosConfig.snippetsData)
 */
public class TelegriosSmartActivity extends TelegriosBaseSettingsFragment {

    @Override
    protected String pageTitle() {
        return "Telegrios — Умные чаты";
    }

    @Override
    protected void fillRows(List<Row> rows) {
        rows.add(Row.header("Перевод"));

        // B16 — entry point into the verified Nagram translation stack
        rows.add(Row.action("Настроить перевод в Nagram",
                () -> presentFragment(new NekoGeneralSettingsActivity())));

        // B17 — per-dialog setting in Nagram, cannot be a global switch
        rows.add(Row.info(
                "Автоперевод входящих: перевод на выбранный язык в реальном времени. Включается для каждого чата отдельно в меню чата Nagram (DialogConfig.autoTranslate)."));

        // B18 — target language of input translation is chosen in Nagram settings
        rows.add(Row.info(
                "Перевод ввода: перевод набираемого текста перед отправкой. Язык выбирается в настройках перевода Nagram (translateInputLang)."));

        rows.add(Row.shadow());

        rows.add(Row.header("Копирование и отправка"));

        // B19 — force copy on restricted content (owner: NaConfig)
        rows.add(Row.naSwitch(
                "Копирование части текста",
                "Выделение фрагмента из любого сообщения",
                "xyz.nextalone.nagram.NaConfig", "forceCopy", false));

        // B20 — one-tap quote of the selected fragment
        rows.add(Row.specSwitch("smartQuote",
                "Умное цитирование",
                "Цитата выделенного фрагмента в один тап"));

        // B21 — confirm before sending stickers/GIF/voice/video notes
        rows.add(Row.naSwitch(
                "Подтверждение отправки медиа",
                "Подтверждение стикеров, GIF, голосовых и кружков",
                "tw.nekomimi.nekogram.NekoConfig", "confirmAVMessage", false));

        // B21b — sticker-specific confirmation (owner: NaConfig)
        rows.add(Row.naSwitch(
                "Подтверждение стикеров",
                "Спросить перед отправкой стикера",
                "xyz.nextalone.nagram.NaConfig", "askBeforeSendingSticker", false));

        // B22 — forward without quoting by default
        rows.add(Row.specSwitch("forwardNoQuote",
                "Пересылка без цитаты",
                "Forward without quoting по умолчанию"));

        // B23 — forward media without the author caption
        rows.add(Row.specSwitch("forwardNoCaption",
                "Пересылка без подписи",
                "Медиафайлы без подписи автора"));

        rows.add(Row.shadow());

        rows.add(Row.header("Группа и навигация"));

        // B24 — mention-all is provided by Nagram's MemberAdminActions (admin group menu)
        rows.add(Row.info(
                "Упомянуть всех: @all / кнопка в меню группы. Предоставляется Nagram (xyz.nextalone.nagram.helper.MemberAdminActions) и доступна администраторам групп."));

        // B25 — long-press on the scroll-to-bottom button jumps to the first message
        rows.add(Row.specSwitch("jumpToStart",
                "Кнопка В начало",
                "Переход к самому первому сообщению чата"));

        // B26 — avatar long-press searches messages by author
        rows.add(Row.specSwitch("searchUserMessages",
                "Поиск по автору",
                "Все сообщения участника группы в один клик"));

        rows.add(Row.shadow());

        rows.add(Row.header("Сниппеты"));

        // B27 — text auto-replace on send
        rows.add(Row.specSwitch("snippets",
                "Сниппеты",
                "Автозамена текста по шаблонам"));

        // B27b — string entry, maintained by the send-path hook, not editable here in v1
        rows.add(Row.info(
                "Правила сниппетов: JSON вида {\":sm\": \"...\"}. Задаётся хуком отправки (TelegriosConfig.snippetsData)."));

        rows.add(Row.info(
                "Telegrios — Умные чаты. Пункты без пометки Telegrios изменяют настройки Nagram напрямую; перевод настроен в разделе «Настройки Nagram → Общие»."));
    }
}
