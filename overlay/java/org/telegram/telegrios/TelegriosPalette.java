package org.telegram.telegrios;

import java.lang.reflect.Field;

/**
 * Telegrios palettes. Three complete styles, switched at runtime from
 * TelegriosConfig.styleMode and applied via reflection against Theme.key_*
 * fields so the overlay never references a key missing in the host codebase.
 *
 *   STYLE_TELEGRIOS  — AMOLED black + neon green (the classic Telegrios look)
 *   STYLE_IOS_LIGHT  — authentic Telegram-iOS day palette (white surfaces,
 *                      #F2F2F7 grouped background, #007AFF accent, green #E1FEC6
 *                      outgoing bubbles — the colors of Telegram for iPhone)
 *   STYLE_IOS_DARK   — authentic Telegram-iOS night palette (black surfaces,
 *                      #1C1C1E cards, #0A84FF accent, blue outgoing bubbles)
 */
public final class TelegriosPalette {

    public static final int STYLE_TELEGRIOS = 0;
    public static final int STYLE_IOS_LIGHT = 1;
    public static final int STYLE_IOS_DARK = 2;

    public static final String[] STYLE_NAMES = {
        "Telegrios Dark",
        "iOS Light",
        "iOS Dark"
    };

    private static final String[] KEYS = {
        "key_windowBackgroundWhite",
        "key_windowBackgroundWhiteBlackText",
        "key_windowBackgroundWhiteGrayText",
        "key_windowBackgroundWhiteGrayIcon",
        "key_windowBackgroundWhiteValueText",
        "key_windowBackgroundWhiteLinkText",
        "key_windowBackgroundWhiteLinkSelection",
        "key_windowBackgroundWhiteInputField",
        "key_windowBackgroundWhiteInputFieldActivated",
        "key_windowBackgroundWhiteBlueText",
        "key_windowBackgroundWhiteBlueHeader",
        "key_windowBackgroundWhiteBlueButton",
        "key_windowBackgroundWhiteGreenText",
        "key_windowBackgroundGray",
        "key_windowBackgroundGrayShadow",
        "key_dialogBackground",
        "key_dialogBackgroundGray",
        "key_listSelector",
        "key_divider",
        "key_windowBackgroundChecked",
        "key_windowBackgroundUnchecked",
        "key_actionBarDefault",
        "key_actionBarDefaultIcon",
        "key_actionBarDefaultSubtitle",
        "key_actionBarDefaultSelector",
        "key_actionBarActionModeDefault",
        "key_actionBarActionModeDefaultIcon",
        "key_actionBarActionModeDefaultSelector",
        "key_actionBarBrowser",
        "key_chats_unreadCounter",
        "key_chats_unreadCounterMuted",
        "key_chats_unreadCounterText",
        "key_chats_menuName",
        "key_chats_menuBackground",
        "key_chats_menuItemIcon",
        "key_chats_menuItemText",
        "key_chats_menuItemCheck",
        "key_chats_name",
        "key_chats_nameMessage",
        "key_chats_message",
        "key_chats_date",
        "key_chats_muteIcon",
        "key_chats_sentCheck",
        "key_chats_attachMessage",
        "key_chats_actionBackground",
        "key_chats_actionIcon",
        "key_chats_verifiedBackground",
        "key_avatar_backgroundRed",
        "key_avatar_backgroundOrange",
        "key_avatar_backgroundViolet",
        "key_avatar_backgroundGreen",
        "key_avatar_backgroundCyan",
        "key_avatar_backgroundBlue",
        "key_avatar_backgroundPink",
        "key_avatar_text",
        "key_chat_wallpaper",
        "key_chat_inBubble",
        "key_chat_inBubbleSelected",
        "key_chat_outBubble",
        "key_chat_outBubbleSelected",
        "key_chat_outBubbleGradient1",
        "key_chat_outBubbleGradient2",
        "key_chat_outBubbleGradient3",
        "key_chat_messageTextIn",
        "key_chat_messageTextOut",
        "key_chat_inSentClock",
        "key_chat_outSentClock",
        "key_chat_messageLinkIn",
        "key_chat_messageLinkOut",
        "key_chat_serviceText",
        "key_chat_serviceBackground",
        "key_chat_messagePanelBackground",
        "key_chat_messagePanelText",
        "key_chat_messagePanelHint",
        "key_chat_messagePanelIcons",
        "key_chat_messagePanelSend",
        "key_chat_messagePanelVoicePressed",
        "key_chat_messagePanelCursor",
        "key_chat_messagePanelVoiceBackground",
        "key_chat_stickersHintPanel",
        "key_chat_outSentCheck",
        "key_chat_outSentCheckRead",
        "key_chat_status",
        "key_chat_searchPanelText",
        "key_profile_title",
        "key_profile_status",
        "key_profile_actionBackground",
        "key_profile_actionIcon",
        "key_profile_tabText",
        "key_profile_tabSelectedText",
        "key_profile_tabSelectedLine",
        "key_passport_authorizeBackground",
        "key_passport_authorizeBackgroundSelected",
        "key_switchTrack",
        "key_switchTrackChecked",
        "key_switch2Track",
        "key_switch2TrackChecked",
        "key_progressCircle",
        "key_radioBackground",
        "key_radioBackgroundChecked",
        "key_checkboxSquareBackground",
        "key_checkboxSquareCheck",
        "key_checkboxCheck",
        "key_fastScrollActive",
        "key_undo_background",
        "key_undo_infoColor",
        "key_undo_cancelColor",
    };

    private static final int[] VALUES_TELEGRIOS = {
        0xFF050706, 0xFFEAF6EF, 0xFF8FA89B, 0xFF8FA89B, 0xFF2CE876, 0xFF2CE876,
        0x3330E876, 0xFF0D1411, 0xFF0D1411, 0xFF2CE876, 0xFF2CE876, 0xFF2CE876,
        0xFF2CE876, 0xFF050706, 0x00000000, 0xFF0D1411, 0xFF0D1411, 0x1F2CE876,
        0x142CE876, 0xFF101712, 0xFF050706, 0xFF0D1411, 0xFFEAF6EF, 0xFF8FA89B,
        0x1F2CE876, 0xFF0D1411, 0xFFEAF6EF, 0x1F2CE876, 0xFF0D1411, 0xFF2CE876,
        0xFF0E8F45, 0xFF050706, 0xFFEAF6EF, 0xFF0D1411, 0xFF8FA89B, 0xFFEAF6EF,
        0xFF2CE876, 0xFFEAF6EF, 0xFF8FA89B, 0xFF8FA89B, 0xFF8FA89B, 0xFF8FA89B,
        0xFF2CE876, 0xFF8FA89B, 0xFF2CE876, 0xFF050706, 0xFF2CE876, 0xFFE84A6C,
        0xFFFFB74D, 0xFF7C4DFF, 0xFF2CE876, 0xFF37E0FF, 0xFF4FA3FF, 0xFFFF5C7A,
        0xFF050706, 0xFF050706, 0xFF111C15, 0xFF17241B, 0xFF14251B, 0xFF1B3324,
        0xFF2CE876, 0xFF12B857, 0xFF0E8F45, 0xFFEAF6EF, 0xFFEFFAF2, 0xFF8FA89B,
        0xFFBFF0D2, 0xFF2CE876, 0xFF12B857, 0xFFEAF6EF, 0x59000000, 0xFF0D1411,
        0xFFEAF6EF, 0xFF8FA89B, 0xFF8FA89B, 0xFF2CE876, 0xFF12B857, 0xFF2CE876,
        0xFF2CE876, 0xFF0D1411, 0xFF2CE876, 0xFF2CE876, 0xFF2CE876, 0xFF8FA89B,
        0xFFEAF6EF, 0xFF8FA89B, 0xFF0D1411, 0xFF2CE876, 0xFF8FA89B, 0xFF2CE876,
        0xFF2CE876, 0xFF2CE876, 0xFF12B857, 0xFF22302A, 0xFF2CE876, 0xFF22302A,
        0xFF2CE876, 0xFF2CE876, 0xFF22302A, 0xFF2CE876, 0xFF2CE876, 0xFF050706,
        0xFF050706, 0xFF2CE876, 0xCC0D1411, 0xFFEAF6EF, 0xFF2CE876,
    };

    private static final int[] VALUES_IOS_LIGHT = {
        0xFFFFFFFF, 0xFF000000, 0xFF8A8A8E, 0xFF8A8A8E, 0xFF007AFF, 0xFF007AFF,
        0x33007AFF, 0xFFEFEFF4, 0xFFE5E5EA, 0xFF007AFF, 0xFF007AFF, 0xFF007AFF,
        0xFF34C759, 0xFFF2F2F7, 0x00000000, 0xFFFFFFFF, 0xFFF2F2F7, 0x14000000,
        0xFFC6C6C8, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFF000000, 0xFF8A8A8E,
        0x14000000, 0xFFFFFFFF, 0xFF000000, 0x14000000, 0xFFFFFFFF, 0xFFFF3B30,
        0xFF8A8A8E, 0xFFFFFFFF, 0xFF000000, 0xFFFFFFFF, 0xFF000000, 0xFF000000,
        0xFF007AFF, 0xFF000000, 0xFF8A8A8E, 0xFF8A8A8E, 0xFF8A8A8E, 0xFF8A8A8E,
        0xFF007AFF, 0xFF8A8A8E, 0xFF007AFF, 0xFFFFFFFF, 0xFF007AFF, 0xFFFF3B30,
        0xFFFF9500, 0xFFAF52DE, 0xFF34C759, 0xFF5AC8FA, 0xFF007AFF, 0xFFFF2D55,
        0xFFFFFFFF, 0xFFD8E4EE, 0xFFFFFFFF, 0xFFE3E9EE, 0xFFE1FEC6, 0xFFD2F4B0,
        0xFFE9FFD4, 0xFFE1FEC6, 0xFFD8F9B9, 0xFF000000, 0xFF000000, 0xFF8A8A8E,
        0xFF64A838, 0xFF007AFF, 0xFF007AFF, 0xFFFFFFFF, 0x668E8E93, 0xFFF7F7F9,
        0xFF000000, 0xFF8A8A8E, 0xFF007AFF, 0xFF007AFF, 0xFFFF3B30, 0xFF007AFF,
        0xFF007AFF, 0xFFF7F7F9, 0xFF007AFF, 0xFF007AFF, 0xFF007AFF, 0xFF8A8A8E,
        0xFF000000, 0xFF007AFF, 0xFFEFF1F4, 0xFF007AFF, 0xFF8A8A8E, 0xFF000000,
        0xFF007AFF, 0xFF007AFF, 0xFF0062CC, 0xFFE5E5EA, 0xFF34C759, 0xFFD1D1D6,
        0xFF34C759, 0xFF007AFF, 0xFFC7C7CC, 0xFF007AFF, 0xFF007AFF, 0xFFFFFFFF,
        0xFF007AFF, 0x66000000, 0xE61C1C1E, 0xFFFFFFFF, 0xFF0A84FF,
    };

    private static final int[] VALUES_IOS_DARK = {
        0xFF000000, 0xFFFFFFFF, 0xFF8D8D93, 0xFF8D8D93, 0xFF0A84FF, 0xFF0A84FF,
        0x330A84FF, 0xFF1C1C1E, 0xFF2C2C2E, 0xFF0A84FF, 0xFF0A84FF, 0xFF0A84FF,
        0xFF30D158, 0xFF0A0A0C, 0x00000000, 0xFF1C1C1E, 0xFF000000, 0x1F787880,
        0xFF38383A, 0xFF1C1C1E, 0xFF000000, 0xFF000000, 0xFFFFFFFF, 0xFF8D8D93,
        0x1F787880, 0xFF1C1C1E, 0xFFFFFFFF, 0x1F787880, 0xFF000000, 0xFFFF453A,
        0xFF8D8D93, 0xFFFFFFFF, 0xFFFFFFFF, 0xFF1C1C1E, 0xFFFFFFFF, 0xFFFFFFFF,
        0xFF0A84FF, 0xFFFFFFFF, 0xFF8D8D93, 0xFF8D8D93, 0xFF8D8D93, 0xFF8D8D93,
        0xFF0A84FF, 0xFF8D8D93, 0xFF0A84FF, 0xFFFFFFFF, 0xFF0A84FF, 0xFFFF453A,
        0xFFFF9F0A, 0xFFBF5AF2, 0xFF30D158, 0xFF64D2FF, 0xFF0A84FF, 0xFFFF375F,
        0xFFFFFFFF, 0xFF0B0E12, 0xFF1C1C1E, 0xFF2C2C2E, 0xFF2C5378, 0xFF3C6E96,
        0xFF35618C, 0xFF2C5378, 0xFF24486A, 0xFFFFFFFF, 0xFFFFFFFF, 0xFF8D8D93,
        0xFF8FB8DF, 0xFF0A84FF, 0xFFA5C8F0, 0xFFFFFFFF, 0x59000000, 0xFF1C1C1E,
        0xFFFFFFFF, 0xFF8D8D93, 0xFF0A84FF, 0xFF0A84FF, 0xFFFF453A, 0xFF0A84FF,
        0xFF0A84FF, 0xFF1C1C1E, 0xFFFFFFFF, 0xFFFFFFFF, 0xFF0A84FF, 0xFF8D8D93,
        0xFFFFFFFF, 0xFF0A84FF, 0xFF1C1C1E, 0xFF0A84FF, 0xFF8D8D93, 0xFFFFFFFF,
        0xFF0A84FF, 0xFF0A84FF, 0xFF0066CC, 0xFF39393D, 0xFF30D158, 0xFF39393D,
        0xFF30D158, 0xFF0A84FF, 0xFF5A5A5E, 0xFF0A84FF, 0xFF0A84FF, 0xFFFFFFFF,
        0xFF0A84FF, 0x598D8D93, 0xE62C2C2E, 0xFFFFFFFF, 0xFF409CFF,
    };

    public static int[] paletteFor(int style) {
        if (style == STYLE_IOS_LIGHT) return VALUES_IOS_LIGHT;
        if (style == STYLE_IOS_DARK) return VALUES_IOS_DARK;
        return VALUES_TELEGRIOS;
    }

    public static void apply() {
        int style = TelegriosConfig.getStyleMode();
        int[] values = paletteFor(style);
        for (int i = 0; i < KEYS.length && i < values.length; i++) {
            replace(KEYS[i], values[i]);
        }
        if (style == STYLE_TELEGRIOS && TelegriosConfig.isPureAmoled()) {
            applyAmoled();
        }
        try {
            int unread = TelegriosConfig.isCustomUnreadColor()
                    ? TelegriosConfig.getUnreadColor()
                    : (style == STYLE_TELEGRIOS ? 0xFF2CE876
                       : style == STYLE_IOS_LIGHT ? 0xFFFF3B30 : 0xFFFF453A);
            replace("key_chats_unreadCounter", unread);
        } catch (Throwable ignore) {}
    }

    private static void applyAmoled() {
        replace("key_windowBackgroundWhite", 0xFF000000);
        replace("key_windowBackgroundGray", 0xFF000000);
        replace("key_chat_wallpaper", 0xFF000000);
        replace("key_dialogBackground", 0xFF0A0F0C);
        replace("key_sheet_other", 0xFF0A0F0C);
        replace("key_actionBarDefault", 0xFF050706);
        replace("key_chat_messagePanelBackground", 0xFF0A0F0C);
    }

    private static void replace(String key, int value) {
        try {
            Field f = org.telegram.ui.ActionBar.Theme.class.getField(key);
            f.setInt(null, value);
        } catch (Throwable ignore) {}
    }

    private TelegriosPalette() {}
}
