package org.telegram.telegrios;

import java.util.List;

import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.NekoXConfig;
import xyz.nextalone.nagram.NaConfig;

/**
 * Telegrios Settings — page "home" (spec block A, rows A13..A26).
 * Tabs, header, chat list, folders and avatars of the main screen.
 */
public class TelegriosHomeActivity extends TelegriosBaseSettingsFragment {

    @Override
    protected String pageTitle() {
        return "Telegrios — Главный экран";
    }

    @Override
    protected void fillRows(List<Row> rows) {
        rows.add(Row.header("Вкладки и шапка"));
        rows.add(Row.specSwitch("floatingTabBar", "Плавающая панель вкладок", "Floating Glass Tab Bar в стиле iOS (экспериментально)")); // A13
        // A14: verified NekoConfig ConfigItem "TabTitleType" (configTypeInt); TITLE_TYPE_TEXT=0, TITLE_TYPE_ICON=1
        rows.add(Row.lambdaSwitch("Только иконки во вкладках", "Скрыть подписи под иконками нижней панели",
                () -> NekoConfig.tabsTitleType.Int() == NekoXConfig.TITLE_TYPE_ICON,
                v -> NekoConfig.tabsTitleType.setConfigInt(v ? NekoXConfig.TITLE_TYPE_ICON : NekoXConfig.TITLE_TYPE_TEXT)));
        rows.add(Row.specSwitch("autoHideTabBar", "Автоскрытие панели вкладок", "Прятать при прокрутке списка чатов вниз")); // A15
        // A16: verified NaConfig object val "centerActionBarTitle" (ConfigItem configTypeBool)
        rows.add(Row.lambdaSwitch("Заголовок по центру", "Центрирование заголовка шапки как в iOS",
                () -> NaConfig.INSTANCE.getCenterActionBarTitle().Bool(),
                v -> NaConfig.INSTANCE.getCenterActionBarTitle().setConfigBool(v)));
        rows.add(Row.shadow());

        rows.add(Row.header("Список чатов"));
        rows.add(Row.specSwitch("hideStories", "Скрыть панель историй", "Убрать Stories с главного экрана")); // A17
        rows.add(Row.specSwitch("compactChats", "Компактный список чатов", "Уменьшенная высота ячеек списка")); // A18
        rows.add(Row.specSwitch("hideDividers", "Скрыть разделители", "Без линий между чатами")); // A19
        rows.add(Row.specSwitch("drawerEnabled", "Боковое меню", "Включение и выключение Drawer")); // A20
        rows.add(Row.shadow());

        rows.add(Row.header("Приватность вида"));
        // A21: verified NekoConfig ConfigItem "HidePhone" (configTypeBool, default true)
        rows.add(Row.lambdaSwitch("Скрыть номер телефона", "Защита от подглядывания",
                () -> NekoConfig.hidePhone.Bool(),
                v -> NekoConfig.hidePhone.setConfigBool(v)));
        // A22: verified NaConfig object val "disableDialogsFloatingButton" (ConfigItem configTypeBool)
        rows.add(Row.lambdaSwitch("Скрыть кнопку нового чата", "Без FAB на главном экране",
                () -> NaConfig.INSTANCE.getDisableDialogsFloatingButton().Bool(),
                v -> NaConfig.INSTANCE.getDisableDialogsFloatingButton().setConfigBool(v)));
        rows.add(Row.shadow());

        rows.add(Row.header("Папки"));
        rows.add(Row.specSwitch("folderUnreadCount", "Счётчик на вкладке Все чаты", "Непрочитанные на иконке папки")); // A23
        rows.add(Row.specSwitch("hideAllChatsTab", "Скрыть вкладку Все чаты", "Если настроены пользовательские папки")); // A24
        rows.add(Row.shadow());

        rows.add(Row.header("Аватарки"));
        rows.add(Row.specSwitch("squircleAvatars", "Squircle-аватарки", "Скруглённые квадратные аватарки")); // A25
        rows.add(Row.specSlider("avatarCornerRadius", "Радиус скругления аватарок", "От 0% до 50%", 0, 50, "%")); // A25b
        rows.add(Row.specSwitch("onlineNeonRing", "Неоновое кольцо онлайн", "Анимированное кольцо вокруг аватарок в сети")); // A26
        rows.add(Row.shadow());

        rows.add(Row.info("Настройки вкладок и списка чатов применяются после возврата на главный экран."));
    }
}
