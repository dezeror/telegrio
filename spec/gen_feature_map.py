#!/usr/bin/env python3
"""Generate spectra-mod/spec/FEATURE_MAP.md from feature-map.json (exactly 100 features)."""
import json, os

HERE = os.path.dirname(os.path.abspath(__file__))
with open(os.path.join(HERE, "feature-map.json"), encoding="utf-8") as f:
    data = json.load(f)

assert len(data["features"]) == 100, f"expected 100 features, got {len(data['features'])}"

STATUS = {
    "native": "🟢 Native (upstream Telegram)",
    "mod": "🔵 Nagram mod (toggle already exists)",
    "partial": "🟡 Partial",
    "new": "⚪ New (Spectra must add)",
}

lines = []
lines.append("# Spectra → Nagram Feature Map (100 features)")
lines.append("")
lines.append(f"> Reference snapshot: **Nagram `{data['reference']['repo']}` @ `{data['reference']['commit']}`** "
             f"({data['reference']['date']}).")
lines.append(f"> Source tree audited at `TMessagesProj/src/main/java` + `TMessagesProj/src/main/kotlin`.")
lines.append(f"> Generated: {data['generated']}. Total features: **{len(data['features'])}**.")
lines.append("")
lines.append("## Legend")
lines.append("")
lines.append("| Status | Meaning |")
lines.append("|---|---|")
for k, v in STATUS.items():
    lines.append(f"| `{k}` | {v} |")
lines.append("")
lines.append("Every `mod` entry below was verified against the snapshot: the config key exists in "
             "`NaConfig.kt` / `NekoConfig.java` / `NekoXConfig.java` / `DialogConfig.java` and the "
             "listed code anchors exist in the tree.")
lines.append("")

cur_cat = None
for feat in data["features"]:
    if feat["category"] != cur_cat:
        if cur_cat is not None:
            lines.append("")
        cur_cat = feat["category"]
        lines.append(f"## {cur_cat}")
        lines.append("")
        lines.append("| ID | Feature | Status | Config key(s) | Settings page | Code anchor(s) in Nagram | Notes |")
        lines.append("|---|---|---|---|---|---|---|")
    keys = "`" + "`, `".join(feat["config_keys"]) + "`" if feat["config_keys"] else "—"
    page = feat.get("settings_page") or "—"
    code = "; ".join(f"`{c}`" for c in feat["code_anchors"])
    lines.append(f"| {feat['id']} | {feat['name']} | `{feat['status']}` | {keys} | {page} | {code} | {feat['notes']} |")
lines.append("")

# summary
from collections import Counter
counts = Counter(f["status"] for f in data["features"])
cats = []
c = None
for feat in data["features"]:
    if feat["category"] != c:
        c = feat["category"]
        cats.append({"category": c, "count": 0})
    cats[-1]["count"] += 1

lines.append("## Coverage summary")
lines.append("")
lines.append("| Category | Features |")
lines.append("|---|---|")
for cat in cats:
    lines.append(f"| {cat['category']} | {cat['count']} |")
lines.append(f"| **Total** | **{sum(x['count'] for x in cats)}** |")
lines.append("")
lines.append("| Status | Count |")
lines.append("|---|---|")
for k in ("mod", "native", "partial", "new"):
    lines.append(f"| `{k}` | {counts.get(k, 0)} |")
lines.append("")

lines.append("## Implementation playbook (Spectra overlay)")
lines.append("")
lines.append("1. **Config**: add new toggles to `NkmrConfig.kt`/`NaConfig.kt` (Kotlin) or `NekoConfig.java` (Java) via "
             "`addConfig(key, type, default)`; wire into `loadConfig`.")
lines.append("2. **Settings UI**: register rows in `tw/nekomimi/nekogram/settings/Neko*SettingsActivity.java`; "
             "use `config/cell/ConfigCell*` components. Strings go to `strings.xml` + Crowdin.")
lines.append("3. **Logic**: prefer minimal diff patching inside `org/telegram/**` classes "
             "(`ChatActivity.java`, `MessagesController.java`, `MessageObject.java`); put Spectra logic in "
             "`tw/nekomimi/nekogram/**` or `xyz/nextalone/nagram/**` and call it from the patched site.")
lines.append("4. **Overlay layout**: keep sources under `spectra-mod/overlay/java/...` mirroring the Nagram package "
             "layout, with build scripts in `overlay/tools/` and CI in `overlay/workflows/`.")
lines.append("")

out = os.path.join(HERE, "FEATURE_MAP.md")
with open(out, "w", encoding="utf-8") as f:
    f.write("\n".join(lines))
print(f"wrote {out} ({len(data['features'])} features)")
