package org.telegram.telegrios;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.os.Process;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

/**
 * Telegrios diagnostics v3.
 *
 * Records a milestone ("breadcrumb") boot trace at every startup stage, installs
 * a process-wide default UncaughtExceptionHandler, and — critically — is also
 * delegated from Nagram's MAIN-THREAD specific handler (see the patched
 * ApplicationLoader.attachBaseContext), which otherwise swallows main-thread
 * crashes before any default handler can run.
 *
 * On a crash the full stack trace + boot trace are saved and the app re-opens
 * into TelegriosCrashActivity (safe mode). If the previous session ended before
 * the UI became ready, the next launch enters safe mode automatically, so the
 * report is always visible — including after native crashes or process kills,
 * because the boot trace survives process death.
 */
public final class TelegriosCrashHook {

    private static final long CRASH_REPORT_WINDOW_MS = 10 * 60_000L;

    private TelegriosCrashHook() {
    }

    public static File dir(Context ctx) {
        return ctx.getDir("telegrios", Context.MODE_PRIVATE);
    }

    public static File crashFile(Context ctx) {
        return new File(dir(ctx), "last_crash.txt");
    }

    public static File bootTraceFile(Context ctx) {
        return new File(dir(ctx), "boot_trace.txt");
    }

    private static volatile long startMs = 0L;

    /** Append a milestone to the boot trace. Never throws. */
    public static void mark(Context ctx, String stage) {
        try {
            File f = bootTraceFile(ctx);
            long now = android.os.SystemClock.elapsedRealtime();
            if (startMs == 0L) {
                startMs = now;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("t+").append(now - startMs).append("ms  ").append(stage).append("\n");
            writeText(f, sb.toString(), true);
        } catch (Throwable ignore) {
        }
    }

    /**
     * Called when the UI became ready (LaunchActivity.onResume): the session is
     * healthy. Clears the boot trace; keeps a fresh crash report (10 minutes) so
     * the next launch can still surface it in safe mode.
     */
    public static void uiReady(Context ctx) {
        try {
            bootTraceFile(ctx).delete();
        } catch (Throwable ignore) {
        }
        try {
            File f = crashFile(ctx);
            long age = System.currentTimeMillis() - f.lastModified();
            if (age < 0 || age > CRASH_REPORT_WINDOW_MS) {
                f.delete();
            }
        } catch (Throwable ignore) {
        }
    }

    /** True when the previous session died before the UI became ready (any age). */
    public static boolean enterSafeModeIfJustCrashed(Context ctx) {
        try {
            File boot = bootTraceFile(ctx);
            if (boot.exists()) {
                return true;
            }
            File f = crashFile(ctx);
            if (f.exists()) {
                long age = System.currentTimeMillis() - f.lastModified();
                return age >= -5_000L && age < CRASH_REPORT_WINDOW_MS;
            }
            return false;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Installs the process-wide default handler. Idempotent. The previous
     * default handler (framework KillApplicationHandler) is deliberately NOT
     * delegated to: it would kill the process before the report screen opens.
     * The system respawns the process into TelegriosCrashActivity instead.
     */
    public static void install(final Context ctx) {
        try {
            final Context app = ctx.getApplicationContext();
            if (Thread.getDefaultUncaughtExceptionHandler() instanceof TelegriosHandler) {
                return;
            }
            Thread.setDefaultUncaughtExceptionHandler(new TelegriosHandler(app));
        } catch (Throwable ignore) {
        }
    }

    /**
     * Delegation point for Nagram's MAIN-THREAD specific UncaughtExceptionHandler
     * (installed in ApplicationLoader.attachBaseContext). Nagram's per-thread
     * handler otherwise swallows main-thread crashes without ever reaching the
     * default handler — the reason diagnostics never fired in v1.2.
     */
    public static void onMainCrash(Context ctx, Thread thread, Throwable throwable) {
        try {
            Context app = ctx.getApplicationContext();
            record(app, thread, throwable);
            openReport(app);
        } catch (Throwable ignore) {
        }
    }

    static final class TelegriosHandler implements Thread.UncaughtExceptionHandler {

        private final Context app;

        TelegriosHandler(Context app) {
            this.app = app;
        }

        @Override
        public void uncaughtException(final Thread thread, final Throwable throwable) {
            try {
                record(app, thread, throwable);
            } catch (Throwable ignore) {
            }
            try {
                openReport(app);
            } catch (Throwable ignore) {
                killSoon();
            }
        }
    }

    /**
     * Opens the crash report synchronously (so AMS registers it even if the
     * crashing process dies immediately afterwards) and schedules a process
     * kill shortly after, so the system respawns the process straight into the
     * report screen.
     */
    private static void openReport(final Context app) {
        final Intent intent = new Intent(app, TelegriosCrashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        boolean started = false;
        try {
            app.startActivity(intent);
            started = true;
        } catch (Throwable ignore) {
        }
        if (!started) {
            // retry from a background thread, then kill so the system respawns us
            Thread reporter = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Looper.prepare();
                    } catch (Throwable ignore) {
                    }
                    try {
                        app.startActivity(intent);
                    } catch (Throwable ignore) {
                    }
                    try {
                        Thread.sleep(900L);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Throwable ignore) {
                    }
                    try {
                        Process.killProcess(Process.myPid());
                    } catch (Throwable ignore) {
                    }
                    System.exit(10);
                }
            }, "telegrios-crash-report");
            reporter.start();
        } else {
            killSoon();
        }
    }

    private static void killSoon() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(700L);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    try {
                        Process.killProcess(Process.myPid());
                    } catch (Throwable ignore) {
                    }
                    System.exit(10);
                }
            }, "telegrios-kill").start();
        } catch (Throwable ignore) {
        }
    }

    private static void record(Context app, Thread thread, Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("=== Telegrios crash report ===");
        pw.println("time: " + new Date().toString());
        pw.println("thread: " + (thread == null ? "?" : thread.getName()));
        try {
            pw.println("package: " + app.getPackageName());
            pw.println("version: " + app.getPackageManager().getPackageInfo(app.getPackageName(), 0).versionName);
        } catch (Throwable ignore) {
        }
        try {
            pw.println("android: " + Build.VERSION.SDK_INT + " (" + Build.VERSION.RELEASE + ")");
            pw.println("device: " + Build.MANUFACTURER + " " + Build.MODEL);
            pw.println("abis: " + Arrays.toString(Build.SUPPORTED_ABIS));
        } catch (Throwable ignore) {
        }
        pw.println();
        if (throwable != null) {
            pw.println("--- Java stack trace ---");
            throwable.printStackTrace(pw);
            pw.println();
        }
        try {
            File boot = bootTraceFile(app);
            if (boot.exists()) {
                pw.println("--- Boot trace (last startup milestones) ---");
                pw.println(readText(boot));
                pw.println("(no UI_READY marker = the previous session died before the interface appeared)");
            } else {
                pw.println("--- Boot trace: UI reached a ready state in the previous session ---");
            }
        } catch (Throwable ignore) {
        }
        pw.println();
        pw.println("(diagnostic build - this screen will be removed once the cause is fixed)");
        writeText(crashFile(app), sw.toString(), false);
        try {
            File ext = app.getExternalFilesDir(null);
            if (ext != null) {
                writeText(new File(ext, "telegrios_crash.txt"), sw.toString(), false);
            }
        } catch (Throwable ignore) {
        }
    }

    static String readText(File f) {
        try {
            java.io.FileInputStream in = new java.io.FileInputStream(f);
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) > 0) {
                    out.write(buf, 0, n);
                }
                return new String(out.toByteArray(), StandardCharsets.UTF_8);
            } finally {
                in.close();
            }
        } catch (Throwable t) {
            return "(not available: " + t + ")";
        }
    }

    private static void writeText(File f, String text, boolean append) {
        try {
            File dir = f.getParentFile();
            if (dir != null && !dir.isDirectory()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(f, append);
            try {
                OutputStreamWriter w = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                w.write(text);
                w.flush();
                w.close();
            } finally {
                fos.close();
            }
        } catch (Throwable ignore) {
        }
    }
}
