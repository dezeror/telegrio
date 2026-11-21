package org.telegram.telegrios;

import java.util.List;

/**
 * Telegrios Settings page "input" — spec A41-A50 («Поле ввода»).
 *
 * Every entry on this page is Telegrios-native (telegrios:true in the spec), so each row
 * resolves through TelegriosReflection against TelegriosConfig using the exact config id:
 *
 *   A41 capsuleInputBar    -> specSwitch  (TelegriosConfig.isCapsuleInputBar)
 *   A42 hideRecordButton   -> specSwitch  (TelegriosConfig.isHideRecordButton)
 *   A43 hideAttachExtras   -> specSwitch  (TelegriosConfig.isHideAttachExtras)
 *   A44 replaceMicButton   -> specEnum    (TelegriosConfig.getReplaceMicButton, 0/1/2)
 *   A45 tallerInputField   -> specSwitch  (TelegriosConfig.isTallerInputField)
 *   A46 smoothKeyboard     -> specSwitch  (TelegriosConfig.isSmoothKeyboard)
 *   A47 hideStickerHints   -> specSwitch  (TelegriosConfig.isHideStickerHints)
 *   A48 disableCameraButton-> specSwitch  (TelegriosConfig.isDisableCameraButton)
 *   A49 neonLinkHighlight  -> specSwitch  (TelegriosConfig.isNeonLinkHighlight)
 *   A50 zenMode            -> specSwitch  (TelegriosConfig.isZenMode)
 *
 * No Nagram owner keys are referenced here; hooks apply the values to ChatActivity.
 */
public class TelegriosInputActivity extends TelegriosBaseSettingsFragment {

    @Override
    protected String pageTitle() {
        return "Telegrios — Поле ввода";
    }

    @Override
    protected void fillRows(List<Row> rows) {
        rows.add(Row.header("Поле ввода"));

        // A41 — glass capsule around the ChatActivity input container
        rows.add(Row.specSwitch("capsuleInputBar",
                "Капсульное поле ввода",
                "Стеклянное скруглённое поле ввода (Capsule Input Bar)"));

        // A42 — hide the enter/record button
        rows.add(Row.specSwitch("hideRecordButton",
                "Скрыть кнопку записи",
                "Защита от случайных нажатий голосовых"));

        // A43 — hide attach button and gift extras
        rows.add(Row.specSwitch("hideAttachExtras",
                "Скрыть скрепку и подарки",
                "Минималистичное поле ввода"));

        // A44 — mic button remap: 0 = keep mic, 1 = stickers, 2 = scheduled message
        rows.add(Row.specEnum("replaceMicButton",
                "Замена кнопки микрофона",
                "Стикеры или отложенная отправка",
                new String[]{"Не заменять", "Стикеры", "Отложенное сообщение"}));

        // A45 — extra lines in the multi-line EditText
        rows.add(Row.specSwitch("tallerInputField",
                "Высокое поле ввода",
                "Больше строк в многострочном поле"));

        // A46 — WindowInsets-driven keyboard animation
        rows.add(Row.specSwitch("smoothKeyboard",
                "Плавная клавиатура",
                "Анимация появления клавиатуры (WindowInsets)"));

        rows.add(Row.shadow());

        rows.add(Row.header("Подсказки и элементы"));

        // A47 — no sticker suggestions panel while typing emoji
        rows.add(Row.specSwitch("hideStickerHints",
                "Скрыть подсказки стикеров",
                "Без панели стикеров при вводе эмодзи"));

        // A48 — hide the camera switch button in the input field
        rows.add(Row.specSwitch("disableCameraButton",
                "Скрыть кнопку камеры",
                "Без переключения на камеру из поля ввода"));

        // A49 — accent-colored URLs and mentions
        rows.add(Row.specSwitch("neonLinkHighlight",
                "Неоновая подсветка ссылок",
                "Ссылки и упоминания акцентным цветом"));

        // A50 — double-tap on the chat header hides secondary UI
        rows.add(Row.specSwitch("zenMode",
                "Режим Дзен",
                "Скрытие лишних элементов по тапу на шапку чата"));

        rows.add(Row.shadow());
        rows.add(Row.info(
                "Все параметры страницы хранятся в TelegriosConfig и применяются к полю ввода чата хуками Telegrios. Значение «Замена кнопки микрофона» переключается по тапу: Не заменять → Стикеры → Отложенное сообщение."));
    }
}
