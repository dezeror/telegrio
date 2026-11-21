# Telegrios — Liquid Glass мод для Telegram (Android)

**Telegrios** (ранее Spectra) — кастомный клиент Telegram на базе открытого форка **Nagram**
(NekoX-линия), v1.3: фирменная тема **Telegrios Dark (AMOLED + неон)**, **два iOS-стиля**
(iOS Light и iOS Dark — палитра Telegram для iPhone), иконка в стиле оригинального Telegram
и страница **Telegrios Settings** со **103+ настройками** (69 новых ключей мода + 34 пункта,
привязанных к встроенным возможностям Nagram).

Пакет самодостаточен: `overlay/tools/apply_overlay.py` содержит **все** Java-классы, иконки
и темы внутри себя — применяется поверх свежего клона Nagram одной командой или автоматически
в GitHub Actions.

---

## Что нового в v1.3

- **Переименование**: приложение «Telegrios», applicationId `org.telegrios.messenger`,
  код мода `org.telegram.telegrios` (устанавливается как отдельное приложение).
- **Новая иконка** в стиле оригинального Telegram: синий градиент + белый самолётик,
  все плотности + adaptive + round, обе схемы имён (`ic_launcher*` и `ic_launcher_nagram*`).
- **Три темы**, переключаются мгновенно в «Telegrios Settings → Стиль оформления»:
  | Стиль | Палитра |
  |---|---|
  | Telegrios Dark | AMOLED-чёрный + неоново-зелёный, Pure AMOLED |
  | iOS Light | белые поверхности, #F2F2F7, акцент #007AFF, пузыри #E1FEC6 |
  | iOS Dark | #1C1C1E карточки, акцент #0A84FF, синие пузыри |
- **Диагностика сбоев v3**: в v1.2 отчёт о крэше не показывался, потому что Nagram ставит
  собственный main-thread UncaughtExceptionHandler, который «глотает» падения до default
  handler. Теперь хук ставится в `ApplicationLoader.attachBaseContext`, main-thread
  handler обёрнут, на каждом этапе старта пишутся milestone-метки (переживают смерть
  процесса), а при сбое приложение открывается в `TelegriosCrashActivity` (отчёт,
  «Поделиться», «В Загрузки», «Перезапустить»). Если прошлый запуск не дошёл до UI —
  следующий старт идёт в safe mode и показывает отчёт.

## Сборка

GitHub Actions → «Telegrios Build» → Run workflow. Артефакты: **Telegrios-APK**
(`Telegrios-v1.3-release-arm64.apk`) и **Telegrios-keystore** (сохраните для стабильной
подписи будущих обновлений). Опциональные секреты: `ANDROID_KEYSTORE_B64`,
`ANDROID_KEYSTORE_PASS`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASS`.

## Локальный запуск оверлея

```
python3 overlay/tools/apply_overlay.py --repo /path/to/Nagram
cd /path/to/Nagram && NATIVE_TARGET=arm64-v8a ./gradlew TMessagesProj:assembleRelease
```

## Код мода (`org.telegram.telegrios`, 23 файла)

| Файл | Назначение |
|---|---|
| `TelegriosSettingsActivity` | Хаб настроек: логотип + 8 разделов |
| `Telegrios{Glass,Home,Chat,Input,Privacy,Smart,Media,System}Activity` | Страницы настроек |
| `TelegriosBaseSettingsFragment` | Фреймворк страниц (switch/slider/color/enum) |
| `TelegriosConfig` | Ключи настроек (SharedPreferences) + styleMode |
| `TelegriosPalette` | 3 полных палитры × 107 ключей Theme.* |
| `TelegriosReflection` | Отказоустойчивая привязка к ключам Nagram/NaConfig |
| `TelegriosGlassPanel` | Liquid Glass: RenderEffect (API 31+) + fallback-blur |
| `TelegriosCrashHook` / `TelegriosCrashActivity` | Диагностика v3 |
| остальные | Утилиты, анти-удаление, EXIF, аватары и т.д. |

## Структура репозитория

```
spectra-mod/ (репозиторий)
├── overlay/tools/apply_overlay.py   # САМОДОСТАТОЧНЫЙ упаковщик (классы+иконки+темы внутри)
├── overlay/java/, overlay/res/      # legacy-копии оверлея v1.0 (больше не используются скриптом)
├── spec/                            # исходники-спецификации настроек
└── .github/workflows/spectra-build.yml
```
