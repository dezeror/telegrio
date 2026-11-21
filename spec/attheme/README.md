# .attheme Format & Theme Keys — Research

> Snapshot: `DrKLO/Telegram` @ `master`, downloaded 2026-11-20 via `raw.githubusercontent.com`.
> Source of truth: `ui/ActionBar/Theme.java` + `ui/ActionBar/ThemeColors.java` (+ shipped assets `assets/*.attheme` used for validation).
> Machine-readable inventory: [`attheme_keys.json`](./attheme_keys.json), [`attheme_keys.csv`](./attheme_keys.csv).

---

## 1. What an .attheme file is

`.attheme` is the theming format of Telegram for Android ("Android Telegram theme"). It is consumed by:

- the in-app Theme Editor (created/edited themes),
- theme sharing (files shared as documents) and `themes.telegram.org` gallery,
- `attheme.org` wallpaper links embedded as `WLS=` lines.

## 2. File structure (authoritative parser: `Theme.getThemeFileValues()`)

The file is read as raw bytes, split on `\n`. Parsing is line-based and **case-sensitive**:

| Line shape | Meaning |
|---|---|
| `themeKeyName=intValue` | A color. Key must exist in `ThemeColors.stringKeyToInt()` map, otherwise **silently ignored**. |
| `themeKeyName=#RRGGBB` / `#AARRGGBB` | Also accepted — parsed with `Color.parseColor`, fallback to decimal. |
| `WLS=<url>` | Wallpaper Link String. Typically `https://attheme.org?slug=<pattern>&intensity=<0..100>&bg_color=<rrggbb[-gradient1~gradient2~gradient3]>&rotation=<deg>&mode=blur+motion`. |
| `WPS\n` | **Wallpaper Pattern Start** — everything after this line is binary wallpaper data (JPEG compressed at quality 87); parsing stops and the byte offset is stored under internal key `wallpaperFileOffset`. |
| `\nWPE\n` | Wallpaper Pattern End (written after the JPEG blob). |

Written by `Theme.saveCurrentTheme()` (main themes) and `ThemeAccent.saveToFile()` (accent shares).

### Value encoding
- Canonical writer emits **signed 32-bit decimal** (`-14509328`), not hex.
- Reader accepts decimal ints and `#RRGGBB(AA)` hex.

### Tolerance
- Unknown keys are dropped (`stringKeyToInt` → `-1`).
- Shipped assets still contain keys removed from the current map (`chat_attachLocationText`, `avatar_actionBarSelectorGreen`, `chat_emojiPanelBadgeBackground`) — proving forward-compat by ignoring.
- Shipped-asset bug worth knowing: `bluebubbles.attheme` line 2 contains the *Java constant name* `key_telegram_color_text=-14054705` instead of `telegram_color_text`; it is ignored by the parser (falls back to `key_windowBackgroundWhiteBlueText4`).

## 3. Key naming

- **String name in files** = value of `ThemeColors.createColorKeysMap()` — a mix of camelCase and snake_case, e.g. `dialogTextBlue`, `windowBackgroundWhite`, `chat_inBubble`, `chats_nameMessage`, `avatar_backgroundRed`, `voipgroup_inviteMembersBackground`.
- **Java constant** = `key_` + same name (`key_chat_inBubble`), declared as `public static final int key_X = colorsCount++;` — **declaration order defines the numeric index** (index 0 = `key_wallpaperFileOffset`, a sentinel, not a color).
- Master totals (this snapshot): **866 keys** = 865 real color keys + `wallpaperFileOffset`.
  - 862 have string names (themed via text files).
  - 848 have defaults in `ThemeColors.createDefaultColors()`.
  - 4 have **no string name** (unreachable via text files): `settings_listSelector`, `avatar_backgroundGray`, `starsGradient1`, `starsGradient2`.
- Rough categories (by prefix): chat 376, voip(+group) 63, chats (chat list) 54, dialog 49, windowBackground 34, avatar 32, actionBar 30, statistic charts 20, premium 19, profile 12, location 12, player 11, and ~40 more small groups (see `category_counts` in JSON).

## 4. Color resolution chain at runtime

`Theme.getColor(key)` → `getColorFromTable(currentColors, key)`:

1. `currentColors` (theme file values + accent overrides).
2. If missing → **`fallbackKeys`** map (203 pairs, e.g. `key_actionBarDefaultTitle → key_windowBackgroundWhiteBlackText`, `key_glass_tabSelected → key_chat_messagePanelSend`).
3. If still missing → `defaultColors` from `ThemeColors.createDefaultColors()`.
4. Special cases override everything: `key_chat_serviceBackground/Selected` are **computed from the wallpaper average color** (`serviceMessageColor`), not themed.
5. `key_windowBackgroundWhite`, `key_windowBackgroundGray`, `key_actionBarDefault`, `key_actionBarDefaultArchived` are forced opaque (`|= 0xff000000`).
6. Compact-mode chat rendering swaps a fixed set of keys for `key_chat_compact_*` (16 override groups, see `compact_mode_overrides` in JSON).

Keys **without defaults** and how they resolve:

| Keys | Resolution |
|---|---|
| `chat_wallpaper`, `chat_wallpaper_gradient_to1/2/3`, `chat_wallpaper_gradient_rotation` | Wallpaper subsystem; deleted from `currentColors` when default wallpaper is used; drive gradient wallpaper + `WLS` export. |
| `chat_serviceBackground`, `chat_serviceBackgroundSelected` | Computed from wallpaper average color at runtime. |
| `chat_outBubbleGradient1/2/3`, `chat_outBubbleGradientAnimated` | Injected from the accent's `myMessagesGradientAccentColor1..3` on save/apply. |
| `chat_inBubbleSelectedOverlay`, `chat_outBubbleSelectedOverlay` | Derived at apply time (see `Theme.applyCurrentColors` block around Theme.java:883). |
| `chats_menuTopShadow`, `chats_menuTopShadowCats`, `chats_menuTopBackground` | Derived from wallpaper/gradient (avatar bg) at runtime. |
| `chat_outArticleCodeBackground` | Has fallback → `key_chat_outCodeBackground`. |
| `fill_RedDark`, `premiumStartSmallStarsColor(2)`, `premiumGradientBackgroundOverlay` | Set by code paths outside `createDefaultColors()` (premium/story UI); not accent-excluded. |

## 5. Accent themes (in-app "color" variations)

- Accents derive colors from a base accent color; keys in **`themeAccentExclusionKeys`** (136 entries incl. expanded `keys_avatar_*` / `keys_colors` groups: statistic chart palette, voipgroup screens, premium gradients, code/table blocks, stories rings) are never auto-derived.
- "My messages" (outgoing bubble) keys are governed by index ranges (declaration order): `myMessagesBubbles [316..322)`, `myMessages [322..385)`, `myMessages2 [385..392)`.
- **`ThemeAccent.saveToFile()` writes numeric keys** (`"<index>=<color>"`) instead of names, and appends a `WLS=` line with the accent wallpaper URL — a compat quirk any importer should tolerate. (Note: current `stringKeyToInt` does *not* resolve numeric strings, so text-remapping of accent files relies on the same-Client defaults pipeline; treat numeric-key lines as ignorable.)

## 6. Themes beyond colors (adjacent "keys", not in .attheme)

- `key_drawable_*` String constants (`drawableBotInline`, …) — themed **resource** remapping (drawables/anims), applied via resource overrides, not color files.
- Day/night theme pairing, auto-night switching, and per-theme wallpaper overrides (`OverrideWallpaperInfo`: slug, color, gradients, intensity, blur/motion).

## 7. Validation performed

- Parsed all 5 shipped assets (`day`, `night`, `darkblue`, `bluebubbles`, `arctic`) against the extracted map: **865/873 non-comment lines resolve**; the 8 unresolved lines are the legacy/buggy keys documented above.
- `colorsCount` cross-check: 866 declarations ↔ 866 map entries incl. `wallpaperFileOffset`.

## 8. Recommendations for spectra-mod

1. Ship `attheme_keys.json` (or a generated Java/Python table) as the single source of truth for key names/indices.
2. When adding **custom mod keys**, always `colorsCount++` **appended at the end** so numeric indices of existing keys (used by accent export/import and serialized state) stay stable.
3. Give every new key both a default color and a string name; add fallbacks where sensible.
4. Keep the parser contract: ignore unknown keys; support both decimal and `#RRGGBB(AA)` values; handle `WLS=`, `WPS\n…WPE\n`.
5. Maintain an accent-exclusion policy for keys whose colors must not be hue-shifted (mirror upstream `themeAccentExclusionKeys`).

## 9. Artifacts & tooling

- `scripts/research/extract_attheme_keys.py` — regenerates `spec/attheme/attheme_keys.{json,csv}` from `Theme.java` + `ThemeColors.java` (kept in `scripts/research/src/`).
- `scripts/research/validate_against_assets.py` — re-validates the map against the 5 shipped `.attheme` assets.
- Raw upstream sources: `scripts/research/src/{Theme,ThemeColors,ThemePreviewActivity}.java`, `scripts/research/src/*.attheme`.
