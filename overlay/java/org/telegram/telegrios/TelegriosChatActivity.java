package org.telegram.telegrios;

import java.util.List;

import org.telegram.messenger.SharedConfig;

import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.settings.NekoChatSettingsActivity;
import tw.nekomimi.nekogram.settings.NekoGeneralSettingsActivity;

/**
 * Telegrios Settings — page "chat" (spec block A, rows A27..A40).
 * Bubbles, font, emoji, message layout, stickers and chat wallpaper.
 */
public class TelegriosChatActivity extends TelegriosBaseSettingsFragment {

    @Override
    protected String pageTitle() {
        return "Telegrios — Чаты";
    }

    @Override
    protected void fillRows(List<Row> rows) {
        rows.add(Row.header("Пузыри сообщений"));
        rows.add(Row.specSlider("bubbleRadius", "Радиус углов сообщений", "Скругление углов пузырей", 4, 24, "dp")); // A27
        rows.add(Row.specSwitch("disableTails", "Отключить хвостики пузырей", "Чистый минимализм")); // A28
        rows.add(Row.shadow());

        rows.add(Row.header("Шрифт"));
        // A29: Telegrios-native; also mirrors into SharedConfig.fontSize (verified public static int)
        Row fontSize = Row.specSlider("messageFontSize", "Размер шрифта сообщений", "От 11sp до 22sp", 11, 22, "sp");
        fontSize.intSet = v -> {
            TelegriosConfig.setMessageFontSize(v);
            SharedConfig.fontSize = v;
        };
        rows.add(fontSize);
        // A30: verified NekoConfig "TypefaceUseDefault" is a BOOL ConfigItem (configTypeBool),
        // not an int enum — so a 4-option Telegrios enum is impossible; expose the real toggle instead.
        rows.add(Row.lambdaSwitch("Системный шрифт интерфейса", "Использовать системный шрифт вместо встроенного",
                () -> NekoConfig.typeface.Bool(),
                v -> NekoConfig.typeface.setConfigBool(v)));
        rows.add(Row.info("Выбор конкретного кастомного шрифта доступен в настройках Nekogram."));
        rows.add(Row.action("Открыть настройки Nekogram", () -> presentFragment(new NekoGeneralSettingsActivity())));
        rows.add(Row.shadow());

        rows.add(Row.header("Эмодзи и содержимое"));
        // A31: verified NekoConfig ConfigItem "EmojiUseDefault" (configTypeBool)
        rows.add(Row.lambdaSwitch("Системные эмодзи", "Эмодзи Android вместо встроенных Telegram",
                () -> NekoConfig.useSystemEmoji.Bool(),
                v -> NekoConfig.useSystemEmoji.setConfigBool(v)));
        rows.add(Row.specSwitch("hideGroupAvatars", "Скрыть аватарки в группах", "Минималистичные групповые чаты")); // A32
        rows.add(Row.specSwitch("hideForwardSender", "Скрыть автора пересылки", "Пересылка без указания отправителя по умолчанию")); // A33
        rows.add(Row.shadow());

        rows.add(Row.header("Оформление сообщений"));
        rows.add(Row.specSwitch("timeInsideBubble", "Время внутри пузыря", "Время и галочки внутри или снаружи пузыря")); // A34
        rows.add(Row.specSwitch("hideQuickReactions", "Скрыть быстрые реакции", "Без кнопки реакции и панели под сообщениями")); // A35
        rows.add(Row.specSwitch("hideChannelShare", "Скрыть кнопку Поделиться", "Без кнопки рядом с медиа в каналах")); // A36
        rows.add(Row.specSlider("photoCornerRadius", "Скругление фото и видео", "Настраиваемый радиус медиа в чате", 0, 24, "dp")); // A37
        rows.add(Row.shadow());

        rows.add(Row.header("Стикеры и обои"));
        // A38: verified NekoConfig "stickerSize" is configTypeFloat (default 14.0f) with its own
        // slider cell in NekoChatSettingsActivity — an int slider row would corrupt the value,
        // so we link to the existing setting instead of duplicating it.
        rows.add(Row.info("Размер стикеров (50–150%) настраивается слайдером в настройках чата Nekogram."));
        rows.add(Row.action("Открыть настройки чата Nekogram", () -> presentFragment(new NekoChatSettingsActivity())));
        rows.add(Row.specSwitch("disableAnimatedStickers", "Отключить анимацию стикеров", "Без автовоспроизведения анимированных стикеров и эмодзи")); // A39
        // A40: verified NekoConfig ConfigItem "forceBlurInChat" (configTypeBool)
        rows.add(Row.lambdaSwitch("Размытие обоев чата", "Обои с эффектом размытия и затемнения",
                () -> NekoConfig.forceBlurInChat.Bool(),
                v -> NekoConfig.forceBlurInChat.setConfigBool(v)));
        rows.add(Row.shadow());

        rows.add(Row.info("Настройки чата применяются к новым сообщениям; для шрифта может понадобиться перезапуск."));
    }
}
