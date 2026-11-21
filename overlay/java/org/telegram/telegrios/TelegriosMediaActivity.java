package org.telegram.telegrios;

import java.util.List;

/**
 * Telegrios Settings — page "media" (spec B31–B42): voice, video notes, photos, player.
 *
 * Row owners verified against the Nagram clone:
 * - B32  NekoConfig.customAudioBitrate  (ConfigItem int, default 32) — NekoConfig.java:196
 * - B35  NekoConfig.rearVideoMessages   (ConfigItem bool)            — NekoConfig.java:98
 * - B42  NekoConfig.autoPauseVideo      (ConfigItem bool)            — NekoConfig.java:162
 * Telegrios-native ids (voiceRadio, voicePreview, videoNoteFlash, videoToCircle,
 * sendOriginalPhoto, saveTTLToGallery, speedUp, noSwipeToNext, doubleTapSeek)
 * resolve by reflection against TelegriosConfig accessors.
 */
public class TelegriosMediaActivity extends TelegriosBaseSettingsFragment {

    @Override
    protected String pageTitle() {
        return "Telegrios — Медиа и голос";
    }

    @Override
    protected void fillRows(List<Row> rows) {
        rows.add(Row.header("Голосовые сообщения"));

        // B31 voiceRadio — Telegrios-native
        rows.add(Row.specSwitch("voiceRadio", "Режим рации", "Прослушивание голосовых без статуса прослушивания"));
        // B32 voiceBitrate — Nagram: NekoConfig.customAudioBitrate (16–128 kbps)
        rows.add(Row.naSlider("Битрейт голосовых", "До 128 kbps + шумоподавление (NoiseSuppress)",
                "tw.nekomimi.nekogram.NekoConfig", "customAudioBitrate", 16, 128, "kbps"));
        // B33 voicePreview — Telegrios-native
        rows.add(Row.specSwitch("voicePreview", "Пауза перед отправкой", "Предпрослушивание голосового перед отправкой"));

        rows.add(Row.shadow());
        rows.add(Row.header("Кружочки и камера"));

        // B34 videoNoteFlash — Telegrios-native
        rows.add(Row.specSwitch("videoNoteFlash", "Вспышка при записи кружков", "Фонарик включается на время записи"));
        // B35 chooseCamera — Nagram: NekoConfig.rearVideoMessages
        rows.add(Row.naSwitch("Камера для кружков", "Основная / фронтальная до записи",
                "tw.nekomimi.nekogram.NekoConfig", "rearVideoMessages", false));
        // B36 videoToCircle — Telegrios-native
        rows.add(Row.specSwitch("videoToCircle", "Видео в кружочек", "Конвертация видео из галереи в видеосообщение"));

        rows.add(Row.shadow());
        rows.add(Row.header("Фото и медиа"));

        // B37 sendOriginalPhoto — Telegrios-native
        rows.add(Row.specSwitch("sendOriginalPhoto", "Фото в максимальном качестве", "Без сжатия в один тап"));
        // B38 saveTTLToGallery — Telegrios-native
        rows.add(Row.specSwitch("saveTTLToGallery", "Сохранять самоудаляющиеся медиа", "Фото/видео с таймером в галерею"));

        rows.add(Row.shadow());
        rows.add(Row.header("Плеер"));

        // B39 speedUp — Telegrios-native
        rows.add(Row.specSwitch("speedUp", "Ускорение до 3.0x", "Добавить скорости 2.5x/3.0x для видео и аудио"));
        // B40 noSwipeToNext — Telegrios-native
        rows.add(Row.specSwitch("noSwipeToNext", "Не перескакивать на канал", "Отключить автопереход при свайпе вверх"));
        // B41 doubleTapSeek — Telegrios-native enum (0/1/2)
        rows.add(Row.specEnum("doubleTapSeek", "Перемотка двойным тапом", "Настраиваемый шаг перемотки",
                TelegriosConfig.DOUBLETAPSEEK_OPTIONS));
        // B42 stopMediaOnBackground — Nagram: NekoConfig.autoPauseVideo
        rows.add(Row.naSwitch("Останавливать медиа при сворачивании", "Или фоновое воспроизведение",
                "tw.nekomimi.nekogram.NekoConfig", "autoPauseVideo", false));

        rows.add(Row.shadow());
        rows.add(Row.info("Битрейт голосовых применяется к новым записям; шаг перемотки переключается тапом."));
    }
}
