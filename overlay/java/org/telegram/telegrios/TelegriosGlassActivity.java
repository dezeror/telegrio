package org.telegram.telegrios;

import java.util.List;

import tw.nekomimi.nekogram.NekoConfig;

/**
 * Telegrios Settings — page "glass" (spec block A, rows A1..A12b).
 * Liquid Glass engine, theming, system integration and unread counter color.
 *
 * Hooks (from spec/settings_a.json):
 *  A1  TelegriosGlassPanel master switch (RenderEffect API31+ / ScriptIntrinsicBlur fallback)
 *  A2  TelegriosGlassPanel.blurRadiusPx = 25 * value / 100
 *  A8  TelegriosThemeUtils.applyMonetAccent()
 *  A12 Theme.key_chats_unreadCounter override
 */
public class TelegriosGlassActivity extends TelegriosBaseSettingsFragment {

    @Override
    protected String pageTitle() {
        return "Telegrios — Жидкое стекло";
    }

    @Override
    protected void fillRows(List<Row> rows) {
        rows.add(Row.header("Жидкое стекло"));
        rows.add(Row.specSwitch("liquidGlass", "Liquid Glass", "Полупрозрачные панели с размытием в реальном времени")); // A1
        rows.add(Row.specSlider("glassBlurRadius", "Сила размытия", "Blur radius стеклянных панелей", 0, 100, "%")); // A2
        rows.add(Row.specSlider("glassAlpha", "Прозрачность панелей", "Alpha стеклянных подложек", 10, 95, "%")); // A3
        rows.add(Row.specSwitch("neonGlowBorder", "Неоновая обводка", "Glow-бордер вокруг стеклянных панелей и пузырей")); // A4
        rows.add(Row.specSlider("glowIntensity", "Интенсивность свечения", "Сила неонового свечения обводки", 0, 100, "%")); // A5
        rows.add(Row.shadow());

        rows.add(Row.header("Стиль оформления"));
        rows.add(Row.specEnum("styleMode", "Стиль темы", "Telegrios Dark, iOS Light или iOS Dark",
                TelegriosConfig.STYLE_MODE_OPTIONS));
        rows.add(Row.info("iOS Light и iOS Dark повторяют палитру Telegram для iPhone: белые поверхности, "
                + "группированные фоны #F2F2F7, акцент iOS и фирменные пузыри сообщений. "
                + "Стиль применяется мгновенно, без перезапуска."));
        rows.add(Row.shadow());

        rows.add(Row.header("Поверхности и тема"));
        rows.add(Row.specSwitch("glassDialogs", "Стекло в диалогах и шторках", "Полупрозрачный фон диалогов и Bottom Sheets")); // A6
        rows.add(Row.specSwitch("pureAmoled", "Pure AMOLED", "Замена всех серых оттенков на чистый чёрный")); // A7
        rows.add(Row.specSwitch("monetAccent", "Акцент из обоев (Material You)", "Динамический акцентный цвет системы (Android 12+)")); // A8
        rows.add(Row.specColor("accentColor", "Акцентный цвет Telegrios", "Неоново-зелёный по умолчанию", TELEGRIOS_PALETTE)); // A8b
        rows.add(Row.specSwitch("gradientBubbles", "Градиентные пузыри", "Плавный перелив исходящих пузырей при скролле")); // A9
        rows.add(Row.shadow());

        rows.add(Row.header("Системная интеграция"));
        // A10: verified NekoConfig ConfigItem "TransparentStatusBar" (configTypeBool, default true)
        rows.add(Row.lambdaSwitch("Edge-to-Edge", "Полупрозрачные статус-бар и навигационная панель",
                () -> NekoConfig.transparentStatusBar.Bool(),
                v -> NekoConfig.transparentStatusBar.setConfigBool(v)));
        // A11: verified NekoConfig ConfigItem "DisableAppBarShadow" (configTypeBool)
        rows.add(Row.lambdaSwitch("Скрыть тени панелей", "Без теней под ActionBar и панелями",
                () -> NekoConfig.disableAppBarShadow.Bool(),
                v -> NekoConfig.disableAppBarShadow.setConfigBool(v)));
        rows.add(Row.shadow());

        rows.add(Row.header("Счётчик непрочитанных"));
        rows.add(Row.specSwitch("customUnreadColor", "Свой цвет счётчика непрочитанных", "Кастомный цвет бейджа непрочитанных")); // A12
        rows.add(Row.specColor("unreadColor", "Цвет счётчика", "Используется при включённом пункте выше", TELEGRIOS_PALETTE)); // A12b
        rows.add(Row.shadow());

        rows.add(Row.info("Изменения стекла и темы применяются мгновенно, перезапуск не требуется."));
    }
}
