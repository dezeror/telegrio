# Spectra → Nagram Feature Map (100 features)

> Reference snapshot: **Nagram `github.com/NextAlone/Nagram` @ `8f2a8a03aa7aec599150b09d302bfd5023283344`** (Sun Nov 8 10:25:47 2026 +0800).
> Source tree audited at `TMessagesProj/src/main/java` + `TMessagesProj/src/main/kotlin`.
> Generated: 2026-11-21. Total features: **100**.

## Legend

| Status | Meaning |
|---|---|
| `native` | 🟢 Native (upstream Telegram) |
| `mod` | 🔵 Nagram mod (toggle already exists) |
| `partial` | 🟡 Partial |
| `new` | ⚪ New (Spectra must add) |

Every `mod` entry below was verified against the snapshot: the config key exists in `NaConfig.kt` / `NekoConfig.java` / `NekoXConfig.java` / `DialogConfig.java` and the listed code anchors exist in the tree.

## A. Privacy & Ghost Mode

| ID | Feature | Status | Config key(s) | Settings page | Code anchor(s) in Nagram | Notes |
|---|---|---|---|---|---|---|
| P-01 | Ghost mode master toggle (drawer switch) | `mod` | `showGhostToggleInDrawer` | NekoGhostModeActivity / drawer | `tw/nekomimi/nekogram/NekoConfig.java (setGhostMode/toggleGhostMode/isGhostModeActive)`; `tw/nekomimi/nekogram/settings/NekoGhostModeActivity.java`; `org/telegram/ui/DialogsActivity.java (drawer toggle)` | One switch applies all ghost packet toggles. |
| P-02 | Disable read receipts (don't send read packets) | `mod` | `sendReadMessagePackets` | NekoGhostModeActivity | `tw/nekomimi/nekogram/NekoConfig.java`; `org/telegram/tgnet/ConnectionsManager.java (packet filter)` | Blocks markRead packets globally. |
| P-03 | Hide online status / offline after online | `mod` | `sendOnlinePackets`, `sendOfflineAfterOnline` | NekoGhostModeActivity | `tw/nekomimi/nekogram/NekoConfig.java` | Suppresses updateStatus packets. |
| P-04 | Don't send story read receipts | `mod` | `sendReadStoryPackets` | NekoGhostModeActivity | `tw/nekomimi/nekogram/NekoConfig.java` | Stories ghost-read. |
| P-05 | Mark chats read only after sending | `mod` | `markReadAfterSend` | NekoGhostModeActivity | `tw/nekomimi/nekogram/NekoConfig.java` | Defers read state until reply. |
| P-06 | Don't send upload progress | `mod` | `sendUploadProgress` | NekoGhostModeActivity | `tw/nekomimi/nekogram/NekoConfig.java` | Hides typing/upload activity during media send. |
| P-07 | Hide phone number | `mod` | `hidePhone` | NekoAccountSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` | Strips own phone from UI. |
| P-08 | Do not share my phone number | `mod` | `doNotShareMyPhoneNumber` | NekoExperimentalSettingsActivity | `kotlin/xyz/nextalone/nagram/NaConfig.kt` | Privacy prompt suppression. |
| P-09 | Hide 'seen'/online tooltip in chat | `mod` | `hideMessageSeenTooltip` | NekoChatSettingsActivity | `org/telegram/ui/ChatActivity.java`; `org/telegram/ui/Components/ChatAvatarContainer.java` | Verified referenced in both UI classes. |
| P-10 | Hide sponsored messages / proxy sponsor channel | `mod` | `hideSponsoredMessage`, `hideProxySponsorChannel` | NekoChatSettingsActivity / NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` | Ad-hiding for channels & proxy sponsors. |

## B. Security & Locking

| ID | Feature | Status | Config key(s) | Settings page | Code anchor(s) in Nagram | Notes |
|---|---|---|---|---|---|---|
| S-01 | Chat passcode lock (app-level) | `mod` | — | NekoPasscodeSettingsActivity | `tw/nekomimi/nekogram/helpers/PasscodeHelper.java`; `tw/nekomimi/nekogram/settings/NekoPasscodeSettingsActivity.java`; `org/telegram/ui/PasscodeActivity.java`; `org/telegram/ui/Components/PasscodeView.java` | Passcode used across LaunchActivity / PasscodeView. |
| S-02 | Biometric / system unlock instead of PIN | `mod` | `useSystemUnlock` | NekoPasscodeSettingsActivity | `tw/nekomimi/nekogram/settings/NekoPasscodeSettingsActivity.java` | NaConfig toggle. |
| S-03 | Disable FLAG_SECURE (allow screenshots) | `mod` | `disableFlagSecure` | NekoExperimentalSettingsActivity | `tw/nekomimi/nekogram/NekoXConfig.java`; `kotlin/xyz/nextalone/nagram/NaConfig.kt` | NekoXConfig.disableFlagSecure reads NaConfig. |
| S-04 | Two-step verification (cloud password) | `native` | — | Settings > Privacy & Security | `org/telegram/ui/TwoStepVerificationActivity.java` | Upstream Telegram feature. |
| S-05 | Auto-delete messages (chat timers) | `native` | — | Chat > Auto-delete | `org/telegram/ui/AutoDeleteMessagesActivity.java` | Upstream. |
| S-06 | Privacy center (last seen, calls, invites, blocked) | `native` | — | Settings > Privacy & Security | `org/telegram/ui/PrivacySettingsActivity.java` | Upstream. |

## C. Chat Behavior

| ID | Feature | Status | Config key(s) | Settings page | Code anchor(s) in Nagram | Notes |
|---|---|---|---|---|---|---|
| C-01 | Double-tap action (incoming/outgoing, per chat) | `mod` | `doubleTapActionIncoming`, `doubleTapActionOutgoing` | NekoChatSettingsActivity | `kotlin/xyz/nextalone/nagram/helper/DoubleTap.kt`; `tw/nekomimi/nekogram/settings/cell/DoubleTapCell.java` | Action map includes send-reaction etc. |
| C-02 | Undo / redo in message input | `mod` | `showTextUndoRedo` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/settings/NekoChatSettingsActivity.java` | Nagram headline feature. |
| C-03 | Force copy (bypass copy restrictions) | `mod` | `forceCopy` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/settings/NekoChatSettingsActivity.java` | Nagram headline feature. |
| C-04 | Keep formatting when copying | `mod` | `keepFormatting` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/settings/NekoChatSettingsActivity.java` | NaConfig toggle. |
| C-05 | Confirm before sending A/V message | `mod` | `confirmAVMessage` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` | NekoX legacy toggle. |
| C-06 | Ask before calling | `mod` | `askBeforeCall` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` |  |
| C-07 | Hide keyboard on chat scroll | `mod` | `hideKeyboardOnChatScroll` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java`; `org/telegram/ui/ChatActivity.java` |  |
| C-08 | Remember all back-stack messages (chat history) | `mod` | `rememberAllBackMessages` | NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/ChatHistoryActivity.java`; `tw/nekomimi/nekogram/NekoConfig.java` | Dedicated history activity. |

## D. Message Menu & Actions

| ID | Feature | Status | Config key(s) | Settings page | Code anchor(s) in Nagram | Notes |
|---|---|---|---|---|---|---|
| M-01 | Add to Saved Messages in context menu | `mod` | `showAddToSavedMessages` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java`; `org/telegram/ui/ChatActivity.java (menu builder)` |  |
| M-02 | Repeat action / repeat as copy | `mod` | `showRepeat`, `showRepeatAsCopy` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java`; `kotlin/xyz/nextalone/nagram/NaConfig.kt` | Repeat + confirm dialog (repeatConfirm). |
| M-03 | Message details (JSON/RPC inspector) | `mod` | `showMessageDetails`, `showRPCError` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/ui/MessageDetailsActivity.java` | Full TLRPC dump viewer. |
| M-04 | Admin actions & change-permissions menu items | `mod` | `showAdminActions`, `showChangePermissions` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` | Also adminActionsOnAvatarLongPress/MentionLongPress (NaConfig). |
| M-05 | Back-button recent chats/history menu | `mod` | `showViewHistory` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/BackButtonMenuRecent.java`; `tw/nekomimi/nekogram/NekoConfig.java` |  |
| M-06 | Compact & hidden message-menu options | `mod` | `compactMessageMenuOptions`, `hiddenMessageMenuOptions` | NekoChatSettingsActivity | `xyz/nextalone/nagram/helper/MessageMenuCompact.kt` | Per-option persistence via JSON list. |

## E. Message Display & Info

| ID | Feature | Status | Config key(s) | Settings page | Code anchor(s) in Nagram | Notes |
|---|---|---|---|---|---|---|
| D-01 | Show message ID on messages | `mod` | `showMessageID` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/settings/NekoChatSettingsActivity.java` |  |
| D-02 | Forward count & forwarded date display | `mod` | `showForwardCount`, `dateOfForwardedMsg` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/settings/NekoChatSettingsActivity.java` | Original-date forwarding. |
| D-03 | Edited message icon/label customization | `mod` | `showEditedIcon`, `customEditedMessage` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/settings/NekoChatSettingsActivity.java` |  |
| D-04 | Invert reply layout | `mod` | `showInvertReply` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/settings/NekoChatSettingsActivity.java` | Nagram headline feature. |
| D-05 | Timestamps on service messages | `mod` | `showServicesTime` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/settings/NekoChatSettingsActivity.java` |  |
| D-06 | Show spoilers directly (no tap-to-reveal) | `mod` | `showSpoilersDirectly` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` | Pairs with hideTimeForSticker. |

## F. Text & Input Formatting

| ID | Feature | Status | Config key(s) | Settings page | Code anchor(s) in Nagram | Notes |
|---|---|---|---|---|---|---|
| T-01 | Bold style option in input menu | `mod` | `showTextBold` | NekoChatSettingsActivity | `xyz/nextalone/nagram/helper/EntitiesHelper.java` | Editable text style suite (Nagram feature). |
| T-02 | Italic style option | `mod` | `showTextItalic` | NekoChatSettingsActivity | `xyz/nextalone/nagram/helper/EntitiesHelper.java` |  |
| T-03 | Monospace style option | `mod` | `showTextMono`, `defaultMonoLanguage` | NekoChatSettingsActivity | `xyz/nextalone/nagram/helper/EntitiesHelper.java` | Mono language hint for syntax highlight. |
| T-04 | Strikethrough style option | `mod` | `showTextStrikethrough` | NekoChatSettingsActivity | `xyz/nextalone/nagram/helper/EntitiesHelper.java` |  |
| T-05 | Underline style option | `mod` | `showTextUnderline` | NekoChatSettingsActivity | `xyz/nextalone/nagram/helper/EntitiesHelper.java` |  |
| T-06 | Quote entity option | `mod` | `showTextQuote` | NekoChatSettingsActivity | `xyz/nextalone/nagram/helper/EntitiesHelper.java` |  |
| T-07 | Spoiler entity option | `mod` | `showTextSpoiler` | NekoChatSettingsActivity | `xyz/nextalone/nagram/helper/EntitiesHelper.java` |  |
| T-08 | Create link option | `mod` | `showTextCreateLink` | NekoChatSettingsActivity | `xyz/nextalone/nagram/helper/EntitiesHelper.java` |  |
| T-09 | Create mention option | `mod` | `showTextCreateMention`, `addCommaAfterMention` | NekoChatSettingsActivity | `xyz/nextalone/nagram/helper/EntitiesHelper.java` |  |
| T-10 | Regular (plain) style option | `mod` | `showTextRegular` | NekoChatSettingsActivity | `xyz/nextalone/nagram/helper/EntitiesHelper.java` |  |

## G. Media & Downloads

| ID | Feature | Status | Config key(s) | Settings page | Code anchor(s) in Nagram | Notes |
|---|---|---|---|---|---|---|
| F-01 | In-app camera (vs system) | `mod` | `inappCamera` | NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` |  |
| F-02 | Disable / hide instant camera | `mod` | `disableInstantCamera`, `hideInstantCamera` | NekoChatSettingsActivity / NekoExperimental | `tw/nekomimi/nekogram/NekoConfig.java` | Two complementary toggles. |
| F-03 | Rear camera for video messages | `mod` | `rearVideoMessages` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` |  |
| F-04 | Auto-pause video when leaving chat | `mod` | `autoPauseVideo` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` |  |
| F-05 | System photo picker | `mod` | `useSystemPhotoPicker` | NekoExperimentalSettingsActivity | `kotlin/xyz/nextalone/nagram/NaConfig.kt` | Android 13+ picker. |
| F-06 | Media preview on receive (gallery hook) | `mod` | `mediaPreview` | NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` |  |
| F-07 | Always send videos in HD + enhanced bitrate | `mod` | `alwaysSendVideosInHD`, `enhancedVideoBitrate` | NekoExperimentalSettingsActivity | `kotlin/xyz/nextalone/nagram/NaConfig.kt` | Also customAudioBitrate (voice). |
| F-08 | Custom save path & cache directory | `mod` | `customSavePath`, `cachePath` | NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/utils/FileUtil.kt`; `tw/nekomimi/nekogram/NekoConfig.java` | External storage supported. |
| F-09 | Always show download icon / enhanced file loader | `mod` | `alwaysShowDownloadIcon`, `enhancedFileLoader` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` |  |
| F-10 | Ask before sending sticker / GIF | `mod` | `askBeforeSendingSticker`, `askBeforeSendingGIF` | NekoChatSettingsActivity | `kotlin/xyz/nextalone/nagram/NaConfig.kt` |  |

## H. Stickers & Emoji

| ID | Feature | Status | Config key(s) | Settings page | Code anchor(s) in Nagram | Notes |
|---|---|---|---|---|---|---|
| E-01 | Custom emoji packs | `mod` | `useCustomEmoji` | NekoEmojiSettingsActivity | `tw/nekomimi/nekogram/helpers/remote/EmojiHelper.java`; `tw/nekomimi/nekogram/settings/NekoEmojiSettingsActivity.java` | NekoX custom emoji subsystem. |
| E-02 | System emoji instead of Telegram emoji | `mod` | `useSystemEmoji` | NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/NekoXConfig.java (getSystemEmojiTypeface)`; `tw/nekomimi/nekogram/NekoConfig.java` |  |
| E-03 | Sticker size scaling | `mod` | `stickerSize` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/settings/StickerSizePreviewMessagesCell.java` | Live preview cell. |
| E-04 | Unlimited favorite stickers | `mod` | `unlimitedFavedStickers`, `removeFavouriteStickersInRecentStickers` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` |  |
| E-05 | Recent stickers count limit | `mod` | `maxRecentStickerCount` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` |  |
| E-06 | Pin sticker on long-press | `mod` | `enableStickerPin` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/ui/PinnedStickerHelper.java` |  |
| E-07 | External sticker cache directory | `mod` | `externalStickerCache`, `externalStickerCacheAutoRefresh`, `externalStickerCacheDirNameType` | NekoExperimentalSettingsActivity | `tw/nekomimi/nekogram/utils/StickersUtil.kt` | Export stickers as files. |
| E-08 | Disable premium sticker animation / remote emoji interactions | `mod` | `disablePremiumStickerAnimation`, `disableRemoteEmojiInteractions` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` |  |

## I. Dialogs & Navigation

| ID | Feature | Status | Config key(s) | Settings page | Code anchor(s) in Nagram | Notes |
|---|---|---|---|---|---|---|
| N-01 | Dialog sorting (unread / unmuted / user / contacts) | `mod` | `sortByUnread`, `sortByUnmuted`, `sortByUser`, `sortByContacts` | NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java`; `org/telegram/ui/DialogsActivity.java` | NekoX sorter. |
| N-02 | Filter tabs (users/groups/channels/bots/admins/mute/unread) | `mod` | `filterUsers`, `filterGroups`, `filterChannels`, `filterBots`, `filterAdmins`, `filterUnmuted`, `filterUnread`, `filterUnmutedAndUnread` | NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/settings/FilterTabsPreviewCell.java`; `tw/nekomimi/nekogram/NekoConfig.java` | NekoX-style extra tabs. |
| N-03 | Folder tab title style (text/icon/mixed, tab strokes) | `mod` | `tabsTitleType`, `mainTabsStyle`, `tabStyleStroke` | NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/NekoXConfig.java (TITLE_TYPE_*)`; `tw/nekomimi/nekogram/NekoConfig.java` |  |
| N-04 | Unlimited pinned dialogs | `mod` | `unlimitedPinnedDialogs` | NekoChatSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` |  |
| N-05 | Archive behavior (open-on-pull, keep archived on swipe) | `mod` | `openArchiveOnPull`, `doNotUnarchiveBySwipe` | NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` |  |
| N-06 | Disable pull-down search & floating compose button | `mod` | `disablePullDownSearch`, `disableDialogsFloatingButton` | NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` |  |
| N-07 | Custom dialogs menu / sidebar (per-entry overrides) | `mod` | `customDialogsMenu`, `customDialogsMenuTheme`, `customDialogsMenuNewGroup`, `customDialogsMenuSavedMessages`, `customDialogsMenuSettings`, `customDialogsMenuProxy`, `sidebarSettingsActivity` | NekoGeneralSettingsActivity | `kotlin/xyz/nextalone/nagram/NaConfig.kt`; `org/telegram/ui/DialogsActivity.java` | Replace drawer with configurable menu. |
| N-08 | Recent chats on tab long-press / recent-forward tab | `mod` | `showRecentChatsOnTabLongPress`, `showRecentForwardTab` | NekoGeneralSettingsActivity | `xyz/nextalone/nagram/helper/DialogsRecentForwardHelper.java` |  |

## J. Translation & AI

| ID | Feature | Status | Config key(s) | Settings page | Code anchor(s) in Nagram | Notes |
|---|---|---|---|---|---|---|
| L-01 | Translation provider selection | `mod` | `translationProvider` | NekoExperimentalSettingsActivity | `tw/nekomimi/nekogram/transtale/source/GoogleAppTranslator.kt`; `tw/nekomimi/nekogram/transtale/source/GoogleCloud2Translator.kt`; `tw/nekomimi/nekogram/transtale/source/DeepLFreeTranslator.kt`; `tw/nekomimi/nekogram/transtale/source/TranSmartTranslator.kt`; `tw/nekomimi/nekogram/transtale/source/VolcengineTranslator.kt` | Pluggable translator backend. |
| L-02 | Translate target language & input translation | `mod` | `translateToLang`, `translateInputLang`, `useTelegramTranslateInChat` | NekoExperimentalSettingsActivity | `tw/nekomimi/nekogram/parts/MessageTrans.kt`; `tw/nekomimi/nekogram/NekoConfig.java` | In-chat translate + input box translate. |
| L-03 | Per-dialog auto-translate | `mod` | `autoTranslate` | Chat > ⋮ (auto-translate) | `tw/nekomimi/nekogram/DialogConfig.java`; `tw/nekomimi/nekogram/transtale/popupwrapper/AutoTranslatePopupWrapper.java` | Per-dialog/topic persistence. |
| L-04 | DeepL family (official API, free API, DeepLX, formality) | `mod` | `deepLApiKey`, `deepLFreeApiKey`, `deepLxCustomApi`, `deepLFormality` | NekoExperimentalSettingsActivity | `tw/nekomimi/nekogram/transtale/source/DeepLXTranslator.kt`; `tw/nekomimi/nekogram/transtale/source/AbstractDeepLTranslator.kt` |  |
| L-05 | Google Cloud Translate key | `mod` | `googleCloudTranslateKey` | NekoExperimentalSettingsActivity | `tw/nekomimi/nekogram/transtale/source/GoogleCloud2Translator.kt`; `tw/nekomimi/nekogram/NekoConfig.java` |  |
| L-06 | LLM integration (provider, API format, keys, models, prompts, temperature) | `mod` | `llmProvider`, `llmApiFormat`, `llmApiKeys`, `llmApiUrl`, `llmOpenAIModel`, `llmGeminiModel`, `llmGroqModel`, `llmDeepSeekModel`, `llmXAIModel`, `llmZhipuAIModel`, `llmMistralModel`, `llmOpenRouterModel`, `llmQwenModel`, `llmMoonshotModel`, `llmSiliconFlowModel`, `llmCustomModel`, `llmSystemPrompt`, `llmTranslationPrompt`, `llmUseContext`, `llmTemperature` | NekoLLMSettingsActivity | `tw/nekomimi/nekogram/settings/NekoLLMSettingsActivity.java` | Full multi-provider AI stack (translation + summarization). |
| L-07 | Summarize-text AI button in chat | `mod` | `summarizeTextButton`, `useSystemAiService` | NekoGeneralSettingsActivity / NekoChatSettingsActivity | `org/telegram/ui/ChatActivity.java`; `tw/nekomimi/nekogram/settings/NekoGeneralSettingsActivity.java` | Verified referenced in ChatActivity. |
| L-08 | OpenCC Chinese conversion (send/receive/input) | `mod` | `ccToLang`, `ccInputLang` | NekoExperimentalSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` | NekoX i18n feature. |

## K. Proxy & Network

| ID | Feature | Status | Config key(s) | Settings page | Code anchor(s) in Nagram | Notes |
|---|---|---|---|---|---|---|
| X-01 | Built-in proxy protocols (MTProto, VMess, SS, SSR, Trojan) | `mod` | — | Proxy settings | `org/telegram/ui/ProxyListActivity.java`; `org/telegram/ui/ProxySettingsActivity.java`; `org/telegram/utils/proxy/ProxySettings.java`; `org/telegram/utils/proxy/WebProxyTransport.java` | NekoX proxy stack. |
| X-02 | Public proxy & proxy subscription | `mod` | `enablePublicProxy`, `autoUpdateSubInfo` | NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/utils/ProxyUtil.kt`; `tw/nekomimi/nekogram/NekoConfig.java` | CF-WS relay + subscription parsing. |
| X-03 | Proxy auto-switcher | `mod` | `proxyAutoSwitch` | NekoGeneralSettingsActivity | `org/telegram/messenger/ProxyRotationController.java`; `tw/nekomimi/nekogram/NekoConfig.java` | Verified rotation controller exists. |
| X-04 | Auto-disable proxy when VPN enabled | `mod` | `disableProxyWhenVpnEnabled` | NekoGeneralSettingsActivity | `kotlin/xyz/nextalone/nagram/NaConfig.kt`; `tw/nekomimi/nekogram/NekoXConfig.java` |  |
| X-05 | Custom DNS / DoH endpoint | `mod` | `useSystemDNS`, `customDoH` | NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/utils/DnsFactory.kt`; `tw/nekomimi/nekogram/NekoConfig.java` |  |
| X-06 | Custom API ID/hash (app credentials) | `mod` | — | NekoExperimentalSettingsActivity | `tw/nekomimi/nekogram/NekoXConfig.java (customApi/customAppId/customAppHash, currentAppId())` | Debug/experimental area. |
| X-07 | Custom server / DC switch & DC info viewer | `mod` | `showIdAndDc` | NekoSettings > Datacenter | `tw/nekomimi/nekogram/DatacenterActivity.java` | Datacenter list + own DC display. |
| X-08 | OSMDroid maps, map preview provider, Google-Maps drift fix | `mod` | `useOSMDroidMap`, `mapPreviewProvider`, `mapDriftingFixForGoogleMaps` | NekoGeneralSettingsActivity | `nekox/messenger/NekoLocationSource.java`; `tw/nekomimi/nekogram/NekoConfig.java` |  |

## L. Appearance & Theming

| ID | Feature | Status | Config key(s) | Settings page | Code anchor(s) in Nagram | Notes |
|---|---|---|---|---|---|---|
| A-01 | Icon decoration variants & event skins (New Year) | `mod` | `iconDecoration`, `notificationIcon`, `eventType`, `newYear` | NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java`; `tw/nekomimi/nekogram/settings/NekoGeneralSettingsActivity.java` | Many launcher icon variants shipped in assets. |
| A-02 | Transparent status bar & app-bar shadow off | `mod` | `transparentStatusBar`, `disableAppBarShadow` | NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` |  |
| A-03 | Centered action-bar title, custom title & fonts | `mod` | `centerActionBarTitle`, `customTitle`, `customTitleUserName`, `useSystemFontInTitle`, `typeface` | NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` |  |
| A-04 | Chat decoration, avatar-background blur/darken, chat blur | `mod` | `chatDecoration`, `avatarBackgroundBlur`, `avatarBackgroundDarken`, `forceBlurInChat`, `chatBlueAlphaValue`, `largeAvatarInDrawer` | NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` | NekoX avatar-as-background feature. |
| A-05 | Liquid glass / glare / gooey effects & input animations | `mod` | `disableGlareEffects`, `liquidGlassAngle`, `liquidGlassIntensity`, `disableGooeyAvatarAnimation`, `inputTextAnimations`, `inputAnimationStrength` | NekoExperimentalSettingsActivity | `kotlin/xyz/nextalone/nagram/NaConfig.kt` | Modern Android visual effects suite. |
| A-06 | Themes, day/night, Material Design themes | `native` | — | Settings > Theming | `org/telegram/ui/ThemeActivity.java` | Upstream theming engine; Nagram ships extra themes. |

## M. Premium & Monetization

| ID | Feature | Status | Config key(s) | Settings page | Code anchor(s) in Nagram | Notes |
|---|---|---|---|---|---|---|
| R-01 | Local premium unlock (UI-level) | `mod` | `localPremium`, `showPremiumStarInChat`, `showPremiumAvatarAnimation` | NekoExperimentalSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java` | Visual-only premium. |
| R-02 | Disable premium upsells (stars, gifts, featured, birthdays, upgrade banners) | `mod` | `disableStarsSubscription`, `disablePremiumExpiring`, `disablePremiumUpgrade`, `disablePremiumChristmas`, `disableBirthdayContact`, `disablePremiumRestore`, `disableFeatuerdEmojis`, `disableFeaturedStickers`, `disableFeaturedGifs`, `disableNonPremiumChannelChatShow`, `disableGifts`, `disableEmptyStarButton`, `disablePremiumSendTodo`, `disableShortcutTagActions` | NekoExperimentalSettingsActivity | `kotlin/xyz/nextalone/nagram/NaConfig.kt` | Extensive de-clutter toggle family. |

## N. Notifications & Push

| ID | Feature | Status | Config key(s) | Settings page | Code anchor(s) in Nagram | Notes |
|---|---|---|---|---|---|---|
| V-01 | Notification behavior (bubbles, vibration, badge, system color) | `mod` | `disableNotificationBubbles`, `disableVibration`, `unreadBadgeOnBackButton`, `ignoreMutedCount`, `useSystemNotificationColor` | NekoGeneralSettingsActivity | `tw/nekomimi/nekogram/NekoConfig.java`; `tw/nekomimi/nekogram/utils/VibrateUtil.kt`; `org/telegram/messenger/NotificationsController.java` |  |
| V-02 | Push service types incl. Unified Push / web push gateway | `mod` | `pushServiceType`, `pushServiceTypeInAppDialog`, `pushServiceTypeUnifiedGateway`, `pushServiceTypeUnifiedWebPushPrivateKey`, `pushServiceTypeUnifiedWebPushPublicKey`, `pushServiceTypeUnifiedWebPushAuthSecret` | NekoExperimentalSettingsActivity | `tw/nekomimi/nekogram/NekoXPushService.kt`; `org/telegram/messenger/UnifiedPushService.java`; `org/telegram/messenger/PushListenerController.java` | Verified unified-push plumbing exists. |

## O. Utilities & Dev Tools

| ID | Feature | Status | Config key(s) | Settings page | Code anchor(s) in Nagram | Notes |
|---|---|---|---|---|---|---|
| U-01 | Debug toolbox: message history, DC info, network log, RPC errors | `mod` | `showRPCError`, `showHiddenFeature` | NekoDebugSettingsActivity / NekoSettings | `tw/nekomimi/nekogram/ChatHistoryActivity.java`; `tw/nekomimi/nekogram/DatacenterActivity.java`; `tw/nekomimi/nekogram/ErrorDatabase.java`; `tw/nekomimi/nekogram/database/NetworkLogItem.java` | Developer diagnostics. |
| U-02 | Settings import/export/reset & cloud settings | `mod` | — | NekoSettings | `tw/nekomimi/nekogram/helpers/SettingsHelper.java`; `tw/nekomimi/nekogram/helpers/CloudSettingsHelper.java`; `xyz/nextalone/nagram/helper/BackupFileHelper.java`; `tw/nekomimi/nekogram/settings/NekoSettingsActivity.java (importSettingsRow/exportSettingsRow/resetSettingsRow)` | Full config portability. |

## Coverage summary

| Category | Features |
|---|---|
| A. Privacy & Ghost Mode | 10 |
| B. Security & Locking | 6 |
| C. Chat Behavior | 8 |
| D. Message Menu & Actions | 6 |
| E. Message Display & Info | 6 |
| F. Text & Input Formatting | 10 |
| G. Media & Downloads | 10 |
| H. Stickers & Emoji | 8 |
| I. Dialogs & Navigation | 8 |
| J. Translation & AI | 8 |
| K. Proxy & Network | 8 |
| L. Appearance & Theming | 6 |
| M. Premium & Monetization | 2 |
| N. Notifications & Push | 2 |
| O. Utilities & Dev Tools | 2 |
| **Total** | **100** |

| Status | Count |
|---|---|
| `mod` | 96 |
| `native` | 4 |
| `partial` | 0 |
| `new` | 0 |

## Implementation playbook (Spectra overlay)

1. **Config**: add new toggles to `NkmrConfig.kt`/`NaConfig.kt` (Kotlin) or `NekoConfig.java` (Java) via `addConfig(key, type, default)`; wire into `loadConfig`.
2. **Settings UI**: register rows in `tw/nekomimi/nekogram/settings/Neko*SettingsActivity.java`; use `config/cell/ConfigCell*` components. Strings go to `strings.xml` + Crowdin.
3. **Logic**: prefer minimal diff patching inside `org/telegram/**` classes (`ChatActivity.java`, `MessagesController.java`, `MessageObject.java`); put Spectra logic in `tw/nekomimi/nekogram/**` or `xyz/nextalone/nagram/**` and call it from the patched site.
4. **Overlay layout**: keep sources under `spectra-mod/overlay/java/...` mirroring the Nagram package layout, with build scripts in `overlay/tools/` and CI in `overlay/workflows/`.
