package org.telegram.telegrios;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Telegrios config storage. Generated from spec/settings_{a,b}.json by scripts/gen_telegrios_config.py.
 * Settings already provided by Nagram (NaConfig/NekoConfig) are NOT stored here;
 * they are surfaced in Telegrios Settings pages via direct NaConfig/NekoConfig accessors.
 */
public class TelegriosConfig {

    private static SharedPreferences prefs;
    private static final List<Listener> listeners = new ArrayList<>();

    public interface Listener {
        void onTelegriosConfigChanged(String key);
    }

    public static void addListener(Listener l) {
        if (!listeners.contains(l)) listeners.add(l);
    }

    public static void removeListener(Listener l) {
        listeners.remove(l);
    }

    public static SharedPreferences prefs() {
        if (prefs == null) {
            synchronized (TelegriosConfig.class) {
                if (prefs == null) {
                    prefs = org.telegram.messenger.ApplicationLoader.applicationContext.getSharedPreferences("telegriosconfig", Context.MODE_PRIVATE);
                }
            }
        }
        return prefs;
    }

    private static void notifyChanged(String key) {
        for (Listener l : new ArrayList<>(listeners)) {
            try { l.onTelegriosConfigChanged(key); } catch (Exception ignore) {}
        }
        try {
            TelegriosThemeUtils.onConfigChanged(key);
        } catch (Throwable ignore) {}
    }

    private static boolean getBool(String k, boolean d) { return prefs().getBoolean(k, d); }
    private static void putBool(String k, boolean v) { prefs().edit().putBoolean(k, v).apply(); notifyChanged(k); }
    private static int getInt(String k, int d) { return prefs().getInt(k, d); }
    private static void putInt(String k, int v) { prefs().edit().putInt(k, v).apply(); notifyChanged(k); }
    private static String getStr(String k, String d) { return prefs().getString(k, d); }
    private static void putStr(String k, String v) { prefs().edit().putString(k, v).apply(); notifyChanged(k); }

    public static final int CUSTOMFONT_0_СИСТЕМНЫЙ = 0;
    public static final int CUSTOMFONT_1_INTERSFPRO = 1;
    public static final int CUSTOMFONT_2_ROBOTOMEDIUM = 2;
    public static final int CUSTOMFONT_3_JETBRAINSMONO = 3;
    public static final String[] CUSTOMFONT_OPTIONS = {"Системный", "Inter / SF Pro", "Roboto Medium", "JetBrains Mono"};

    public static final int REPLACEMICBUTTON_0_НЕЗАМЕНЯТЬ = 0;
    public static final int REPLACEMICBUTTON_1_СТИКЕРЫ = 1;
    public static final int REPLACEMICBUTTON_2_ОТЛОЖЕННОЕСООБЩЕНИЕ = 2;
    public static final String[] REPLACEMICBUTTON_OPTIONS = {"Не заменять", "Стикеры", "Отложенное сообщение"};

    public static final int DOUBLETAPSEEK_0_5СЕК = 0;
    public static final int DOUBLETAPSEEK_1_10СЕК = 1;
    public static final int DOUBLETAPSEEK_2_15СЕК = 2;
    public static final String[] DOUBLETAPSEEK_OPTIONS = {"5 сек", "10 сек", "15 сек"};

    // ---- generated accessors ----
    public static boolean isLiquidGlass() { return getBool("liquidGlass", true); }
    public static void setLiquidGlass(boolean v) { putBool("liquidGlass", v); }

    public static int getGlassBlurRadius() { return getInt("glassBlurRadius", 55); }
    public static void setGlassBlurRadius(int v) { putInt("glassBlurRadius", v); }

    public static int getGlassAlpha() { return getInt("glassAlpha", 65); }
    public static void setGlassAlpha(int v) { putInt("glassAlpha", v); }

    public static boolean isNeonGlowBorder() { return getBool("neonGlowBorder", true); }
    public static void setNeonGlowBorder(boolean v) { putBool("neonGlowBorder", v); }

    public static int getGlowIntensity() { return getInt("glowIntensity", 45); }
    public static void setGlowIntensity(int v) { putInt("glowIntensity", v); }

    public static boolean isGlassDialogs() { return getBool("glassDialogs", true); }
    public static void setGlassDialogs(boolean v) { putBool("glassDialogs", v); }

    public static boolean isPureAmoled() { return getBool("pureAmoled", true); }
    public static void setPureAmoled(boolean v) { putBool("pureAmoled", v); }

    // ---- theme style (Telegrios Dark / iOS Light / iOS Dark) ----
    public static final int STYLE_TELEGRIOS = 0;
    public static final int STYLE_IOS_LIGHT = 1;
    public static final int STYLE_IOS_DARK = 2;
    public static final String[] STYLE_MODE_OPTIONS = {"Telegrios Dark", "iOS Light", "iOS Dark"};

    public static int getStyleMode() { return getInt("styleMode", STYLE_TELEGRIOS); }
    public static void setStyleMode(int v) { putInt("styleMode", v); }

    public static boolean isMonetAccent() { return getBool("monetAccent", false); }
    public static void setMonetAccent(boolean v) { putBool("monetAccent", v); }

    public static int getAccentColor() { return getInt("accentColor", (int) (0xFF2CE876L)); }
    public static void setAccentColor(int v) { putInt("accentColor", v); }

    public static boolean isGradientBubbles() { return getBool("gradientBubbles", true); }
    public static void setGradientBubbles(boolean v) { putBool("gradientBubbles", v); }

    public static boolean isCustomUnreadColor() { return getBool("customUnreadColor", false); }
    public static void setCustomUnreadColor(boolean v) { putBool("customUnreadColor", v); }

    public static int getUnreadColor() { return getInt("unreadColor", (int) (0xFF2CE876L)); }
    public static void setUnreadColor(int v) { putInt("unreadColor", v); }

    public static boolean isFloatingTabBar() { return getBool("floatingTabBar", true); }
    public static void setFloatingTabBar(boolean v) { putBool("floatingTabBar", v); }

    public static boolean isAutoHideTabBar() { return getBool("autoHideTabBar", false); }
    public static void setAutoHideTabBar(boolean v) { putBool("autoHideTabBar", v); }

    public static boolean isHideStories() { return getBool("hideStories", false); }
    public static void setHideStories(boolean v) { putBool("hideStories", v); }

    public static boolean isCompactChats() { return getBool("compactChats", false); }
    public static void setCompactChats(boolean v) { putBool("compactChats", v); }

    public static boolean isHideDividers() { return getBool("hideDividers", true); }
    public static void setHideDividers(boolean v) { putBool("hideDividers", v); }

    public static boolean isDrawerEnabled() { return getBool("drawerEnabled", true); }
    public static void setDrawerEnabled(boolean v) { putBool("drawerEnabled", v); }

    public static boolean isFolderUnreadCount() { return getBool("folderUnreadCount", false); }
    public static void setFolderUnreadCount(boolean v) { putBool("folderUnreadCount", v); }

    public static boolean isHideAllChatsTab() { return getBool("hideAllChatsTab", false); }
    public static void setHideAllChatsTab(boolean v) { putBool("hideAllChatsTab", v); }

    public static boolean isSquircleAvatars() { return getBool("squircleAvatars", true); }
    public static void setSquircleAvatars(boolean v) { putBool("squircleAvatars", v); }

    public static int getAvatarCornerRadius() { return getInt("avatarCornerRadius", 30); }
    public static void setAvatarCornerRadius(int v) { putInt("avatarCornerRadius", v); }

    public static boolean isOnlineNeonRing() { return getBool("onlineNeonRing", true); }
    public static void setOnlineNeonRing(boolean v) { putBool("onlineNeonRing", v); }

    public static int getBubbleRadius() { return getInt("bubbleRadius", 16); }
    public static void setBubbleRadius(int v) { putInt("bubbleRadius", v); }

    public static boolean isDisableTails() { return getBool("disableTails", false); }
    public static void setDisableTails(boolean v) { putBool("disableTails", v); }

    public static int getMessageFontSize() { return getInt("messageFontSize", 16); }
    public static void setMessageFontSize(int v) { putInt("messageFontSize", v); }

    public static boolean isHideGroupAvatars() { return getBool("hideGroupAvatars", false); }
    public static void setHideGroupAvatars(boolean v) { putBool("hideGroupAvatars", v); }

    public static boolean isHideForwardSender() { return getBool("hideForwardSender", false); }
    public static void setHideForwardSender(boolean v) { putBool("hideForwardSender", v); }

    public static boolean isTimeInsideBubble() { return getBool("timeInsideBubble", false); }
    public static void setTimeInsideBubble(boolean v) { putBool("timeInsideBubble", v); }

    public static boolean isHideQuickReactions() { return getBool("hideQuickReactions", false); }
    public static void setHideQuickReactions(boolean v) { putBool("hideQuickReactions", v); }

    public static boolean isHideChannelShare() { return getBool("hideChannelShare", false); }
    public static void setHideChannelShare(boolean v) { putBool("hideChannelShare", v); }

    public static int getPhotoCornerRadius() { return getInt("photoCornerRadius", 20); }
    public static void setPhotoCornerRadius(int v) { putInt("photoCornerRadius", v); }

    public static boolean isDisableAnimatedStickers() { return getBool("disableAnimatedStickers", false); }
    public static void setDisableAnimatedStickers(boolean v) { putBool("disableAnimatedStickers", v); }

    public static boolean isCapsuleInputBar() { return getBool("capsuleInputBar", true); }
    public static void setCapsuleInputBar(boolean v) { putBool("capsuleInputBar", v); }

    public static boolean isHideRecordButton() { return getBool("hideRecordButton", false); }
    public static void setHideRecordButton(boolean v) { putBool("hideRecordButton", v); }

    public static boolean isHideAttachExtras() { return getBool("hideAttachExtras", false); }
    public static void setHideAttachExtras(boolean v) { putBool("hideAttachExtras", v); }

    public static int getReplaceMicButton() { return getInt("replaceMicButton", 0); }
    public static void setReplaceMicButton(int v) { putInt("replaceMicButton", v); }

    public static boolean isTallerInputField() { return getBool("tallerInputField", false); }
    public static void setTallerInputField(boolean v) { putBool("tallerInputField", v); }

    public static boolean isSmoothKeyboard() { return getBool("smoothKeyboard", true); }
    public static void setSmoothKeyboard(boolean v) { putBool("smoothKeyboard", v); }

    public static boolean isHideStickerHints() { return getBool("hideStickerHints", false); }
    public static void setHideStickerHints(boolean v) { putBool("hideStickerHints", v); }

    public static boolean isDisableCameraButton() { return getBool("disableCameraButton", false); }
    public static void setDisableCameraButton(boolean v) { putBool("disableCameraButton", v); }

    public static boolean isNeonLinkHighlight() { return getBool("neonLinkHighlight", true); }
    public static void setNeonLinkHighlight(boolean v) { putBool("neonLinkHighlight", v); }

    public static boolean isZenMode() { return getBool("zenMode", false); }
    public static void setZenMode(boolean v) { putBool("zenMode", v); }

    public static boolean isHideTyping() { return getBool("hideTyping", false); }
    public static void setHideTyping(boolean v) { putBool("hideTyping", v); }

    public static boolean isAntiDelete() { return getBool("antiDelete", false); }
    public static void setAntiDelete(boolean v) { putBool("antiDelete", v); }

    public static boolean isEditHistory() { return getBool("editHistory", false); }
    public static void setEditHistory(boolean v) { putBool("editHistory", v); }

    public static boolean isHideSecretChats() { return getBool("hideSecretChats", false); }
    public static void setHideSecretChats(boolean v) { putBool("hideSecretChats", v); }

    public static boolean isStripExif() { return getBool("stripExif", true); }
    public static void setStripExif(boolean v) { putBool("stripExif", v); }

    public static boolean isAntiPhishing() { return getBool("antiPhishing", false); }
    public static void setAntiPhishing(boolean v) { putBool("antiPhishing", v); }

    public static boolean isConfirmInvites() { return getBool("confirmInvites", false); }
    public static void setConfirmInvites(boolean v) { putBool("confirmInvites", v); }

    public static boolean isFakePin() { return getBool("fakePin", false); }
    public static void setFakePin(boolean v) { putBool("fakePin", v); }

    public static String getFakePinValue() { return getStr("fakePinValue", "1234"); }
    public static void setFakePinValue(String v) { putStr("fakePinValue", v); }

    public static String getLockedChats() { return getStr("lockedChats", "[]"); }
    public static void setLockedChats(String v) { putStr("lockedChats", v); }

    public static boolean isSmartQuote() { return getBool("smartQuote", true); }
    public static void setSmartQuote(boolean v) { putBool("smartQuote", v); }

    public static boolean isForwardNoQuote() { return getBool("forwardNoQuote", false); }
    public static void setForwardNoQuote(boolean v) { putBool("forwardNoQuote", v); }

    public static boolean isForwardNoCaption() { return getBool("forwardNoCaption", false); }
    public static void setForwardNoCaption(boolean v) { putBool("forwardNoCaption", v); }

    public static boolean isJumpToStart() { return getBool("jumpToStart", true); }
    public static void setJumpToStart(boolean v) { putBool("jumpToStart", v); }

    public static boolean isSearchUserMessages() { return getBool("searchUserMessages", true); }
    public static void setSearchUserMessages(boolean v) { putBool("searchUserMessages", v); }

    public static boolean isSnippets() { return getBool("snippets", false); }
    public static void setSnippets(boolean v) { putBool("snippets", v); }

    public static String getSnippetsData() { return getStr("snippetsData", "{}"); }
    public static void setSnippetsData(String v) { putStr("snippetsData", v); }

    public static boolean isEmptyMessages() { return getBool("emptyMessages", false); }
    public static void setEmptyMessages(boolean v) { putBool("emptyMessages", v); }

    public static boolean isInlineCalc() { return getBool("inlineCalc", true); }
    public static void setInlineCalc(boolean v) { putBool("inlineCalc", v); }

    public static boolean isVoiceRadio() { return getBool("voiceRadio", false); }
    public static void setVoiceRadio(boolean v) { putBool("voiceRadio", v); }

    public static boolean isVoicePreview() { return getBool("voicePreview", false); }
    public static void setVoicePreview(boolean v) { putBool("voicePreview", v); }

    public static boolean isVideoNoteFlash() { return getBool("videoNoteFlash", false); }
    public static void setVideoNoteFlash(boolean v) { putBool("videoNoteFlash", v); }

    public static boolean isVideoToCircle() { return getBool("videoToCircle", false); }
    public static void setVideoToCircle(boolean v) { putBool("videoToCircle", v); }

    public static boolean isSendOriginalPhoto() { return getBool("sendOriginalPhoto", false); }
    public static void setSendOriginalPhoto(boolean v) { putBool("sendOriginalPhoto", v); }

    public static boolean isSaveTTLToGallery() { return getBool("saveTTLToGallery", false); }
    public static void setSaveTTLToGallery(boolean v) { putBool("saveTTLToGallery", v); }

    public static boolean isSpeedUp() { return getBool("speedUp", true); }
    public static void setSpeedUp(boolean v) { putBool("speedUp", v); }

    public static boolean isNoSwipeToNext() { return getBool("noSwipeToNext", false); }
    public static void setNoSwipeToNext(boolean v) { putBool("noSwipeToNext", v); }

    public static int getDoubleTapSeek() { return getInt("doubleTapSeek", 1); }
    public static void setDoubleTapSeek(int v) { putInt("doubleTapSeek", v); }

    public static boolean isShowRegDate() { return getBool("showRegDate", false); }
    public static void setShowRegDate(boolean v) { putBool("showRegDate", v); }

    public static boolean isForceHighRefreshRate() { return getBool("forceHighRefreshRate", false); }
    public static void setForceHighRefreshRate(boolean v) { putBool("forceHighRefreshRate", v); }

    // ---- misc helpers ----
    public static boolean useNeonAccent() { return !isMonetAccent(); }
    public static int accent() {
        int c = getAccentColor();
        try { if (isMonetAccent()) c = TelegriosThemeUtils.systemAccent(); } catch (Throwable ignore) {}
        return c;
    }
    public static void resetAll() { prefs().edit().clear().apply(); notifyChanged("*reset*"); }
}
