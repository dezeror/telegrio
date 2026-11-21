package org.telegram.telegrios;

import java.util.List;

/**
 * Telegrios Settings — page "system" (spec B43–B46): ads, IDs/DC, reg date, refresh rate.
 *
 * Row owners verified against the Nagram clone:
 * - B43  NekoConfig.hideSponsoredMessage (ConfigItem bool) — NekoConfig.java:180
 * - B44  NaConfig.showMessageID          (ConfigItem bool) — NaConfig.kt:205 (Kotlin object;
 *        spec "na" said NekoConfig.showMessageID, but the real owner is NaConfig —
 *        NekoChatSettingsActivity.java:89 uses NaConfig.INSTANCE.getShowMessageID()).
 *        DC overview lives in tw.nekomimi.nekogram.DatacenterActivity (verified path).
 * - B45/B46 Telegrios-native ids (showRegDate, forceHighRefreshRate) resolve by
 *        reflection against TelegriosConfig accessors.
 *
 * All Nagram rows go through TelegriosReflection (owner class + item name), which
 * resolves Kotlin-object ConfigItems via INSTANCE.getXxx() and degrades to a
 * disabled row instead of crashing when a key is missing.
 */
public class TelegriosSystemActivity extends TelegriosBaseSettingsFragment {

    @Override
    protected String pageTitle() {
        return "Telegrios — Система";
    }

    @Override
    protected void fillRows(List<Row> rows) {
        rows.add(Row.header("Реклама в каналах"));

        // B43 hideSponsored — Nagram: NekoConfig.hideSponsoredMessage
        rows.add(Row.naSwitch("Блокировка рекламы", "Без Sponsored Messages в каналах",
                "tw.nekomimi.nekogram.NekoConfig", "hideSponsoredMessage", false));

        rows.add(Row.shadow());
        rows.add(Row.header("ID и дата-центр"));

        // B44 showIDs — Nagram: NaConfig.showMessageID (Kotlin object via INSTANCE getter)
        rows.add(Row.naSwitch("Показывать ID и DC", "ID пользователя/чата и дата-центр (DC 1-5)",
                "xyz.nextalone.nagram.NaConfig", "showMessageID", false));
        rows.add(Row.info("Дата-центр: Neko Settings → Datacenter (tw.nekomimi.nekogram.DatacenterActivity)"));

        rows.add(Row.shadow());
        rows.add(Row.header("Производительность"));

        // B45 showRegDate — Telegrios-native
        rows.add(Row.specSwitch("showRegDate", "Дата регистрации", "Приблизительно по ID в профиле"));
        // B46 forceHighRefreshRate — Telegrios-native
        rows.add(Row.specSwitch("forceHighRefreshRate", "120/144 FPS", "Принудительная высокая частота обновления"));

        rows.add(Row.shadow());
        rows.add(Row.info("Дата регистрации оценивается по ID и может отличаться от точной даты."));
    }
}
