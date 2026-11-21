package org.telegram.telegrios;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;

/**
 * Self-contained crash report screen (safe mode): shows the recorded stack trace
 * and boot milestones, offers sharing, copying to Downloads and a clean restart.
 * No Telegram resources or classes are used, so it renders even when the app's
 * normal startup is broken. The screen stays open until the user closes it.
 */
public class TelegriosCrashActivity extends Activity {

    public static void launchSafeMode(Context ctx) {
        try {
            Intent i = new Intent(ctx, TelegriosCrashActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ctx.startActivity(i);
        } catch (Throwable ignore) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int pad = dp(14f);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#0B0F0D"));
        root.setPadding(pad, pad, pad, pad);

        TextView title = new TextView(this);
        title.setText("Telegrios \u2014 \u043E\u0442\u0447\u0451\u0442 \u043E \u0441\u0431\u043E\u0435");
        title.setTextColor(Color.parseColor("#5AB9FF"));
        title.setTextSize(18f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setPadding(0, 0, 0, dp(8f));
        root.addView(title);

        TextView hint = new TextView(this);
        hint.setText("\u041D\u0430\u0436\u043C\u0438\u0442\u0435 \u00AB\u041F\u043E\u0434\u0435\u043B\u0438\u0442\u044C\u0441\u044F\u00BB \u0438 \u043E\u0442\u043F\u0440\u0430\u0432\u044C\u0442\u0435 \u043E\u0442\u0447\u0451\u0442 \u0440\u0430\u0437\u0440\u0430\u0431\u043E\u0442\u0447\u0438\u043A\u0443 \u043C\u043E\u0434\u0430 \u2014 \u0442\u0430\u043C \u0442\u043E\u0447\u043D\u0430\u044F \u043F\u0440\u0438\u0447\u0438\u043D\u0430 \u043F\u0430\u0434\u0435\u043D\u0438\u044F.");
        hint.setTextColor(Color.parseColor("#8FA9B8"));
        hint.setTextSize(13f);
        hint.setPadding(0, 0, 0, dp(10f));
        root.addView(hint);

        TextView body = new TextView(this);
        body.setText(readTrace());
        body.setTextColor(Color.parseColor("#D8E6DC"));
        body.setTextSize(11f);
        body.setTypeface(Typeface.MONOSPACE);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(body);
        root.addView(scroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(10f), 0, 0);
        root.addView(row);

        row.addView(makeButton("\u041F\u043E\u0434\u0435\u043B\u0438\u0442\u044C\u0441\u044F", v -> shareTrace()));
        row.addView(makeButton("\u0412 \u00AB\u0417\u0430\u0433\u0440\u0443\u0437\u043A\u0438\u00BB", v -> saveToDownloads()));
        row.addView(makeButton("\u041F\u0435\u0440\u0435\u0437\u0430\u043F\u0443\u0441\u0442\u0438\u0442\u044C", v -> restartApp()));
        row.addView(makeButton("\u0417\u0430\u043A\u0440\u044B\u0442\u044C", v -> closeApp()));

        setContentView(root);
    }

    private Button makeButton(String label, View.OnClickListener listener) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setOnClickListener(listener);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(dp(2f), 0, dp(2f), 0);
        b.setLayoutParams(lp);
        return b;
    }

    private int dp(float v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    private String readTrace() {
        try {
            File f = TelegriosCrashHook.crashFile(this);
            if (!f.exists()) {
                return "(\u0424\u0430\u0439\u043B \u043E\u0442\u0447\u0451\u0442\u0430 \u043D\u0435 \u043D\u0430\u0439\u0434\u0435\u043D \u2014 \u0432\u0435\u0440\u043E\u044F\u0442\u043D\u043E, \u043F\u0440\u0435\u0434\u044B\u0434\u0443\u0449\u0438\u0439 \u0437\u0430\u043F\u0443\u0441\u043A \u0437\u0430\u0432\u0435\u0440\u0448\u0438\u043B\u0441\u044F \u043D\u0435\u0448\u0442\u0430\u0442\u043D\u043E.\n"
                        + "\u041D\u0438\u0436\u0435 \u043C\u0435\u0442\u043A\u0438 \u043F\u043E\u0441\u043B\u0435\u0434\u043D\u0435\u0433\u043E \u0437\u0430\u043F\u0443\u0441\u043A\u0430.)\n\n"
                        + TelegriosCrashHook.readText(TelegriosCrashHook.bootTraceFile(this));
            }
            String s = TelegriosCrashHook.readText(f);
            if (s.trim().length() > 0) {
                return s;
            }
            return "(\u0444\u0430\u0439\u043B \u043E\u0442\u0447\u0451\u0442\u0430 \u043F\u0443\u0441\u0442)";
        } catch (Throwable t) {
            return "(\u043D\u0435 \u0443\u0434\u0430\u043B\u043E\u0441\u044C \u043F\u0440\u043E\u0447\u0438\u0442\u0430\u0442\u044C \u043E\u0442\u0447\u0451\u0442: " + t.toString() + ")";
        }
    }

    private void shareTrace() {
        try {
            Intent s = new Intent(Intent.ACTION_SEND);
            s.setType("text/plain");
            s.putExtra(Intent.EXTRA_TEXT, readTrace());
            startActivity(Intent.createChooser(s, "\u041E\u0442\u043F\u0440\u0430\u0432\u0438\u0442\u044C \u043E\u0442\u0447\u0451\u0442"));
        } catch (Throwable ignore) {
        }
    }

    private void saveToDownloads() {
        try {
            String text = readTrace();
            boolean saved = false;
            if (Build.VERSION.SDK_INT >= 29) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, "telegrios_crash.txt");
                values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    java.io.OutputStream os = getContentResolver().openOutputStream(uri);
                    os.write(text.getBytes("UTF-8"));
                    os.close();
                    saved = true;
                }
            } else {
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File f = new File(dir, "telegrios_crash.txt");
                java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
                fos.write(text.getBytes("UTF-8"));
                fos.close();
                saved = true;
            }
            Toast.makeText(this, saved ? "\u0421\u043E\u0445\u0440\u0430\u043D\u0435\u043D\u043E \u0432 \u00AB\u0417\u0430\u0433\u0440\u0443\u0437\u043A\u0438\u00BB" : "\u041D\u0435 \u0443\u0434\u0430\u043B\u043E\u0441\u044C \u0441\u043E\u0445\u0440\u0430\u043D\u0438\u0442\u044C", Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            Toast.makeText(this, "\u041E\u0448\u0438\u0431\u043A\u0430: " + t.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
        }
    }

    private void restartApp() {
        try {
            TelegriosCrashHook.crashFile(this).delete();
            TelegriosCrashHook.bootTraceFile(this).delete();
        } catch (Throwable ignore) {
        }
        try {
            Intent i = getPackageManager().getLaunchIntentForPackage(getPackageName());
            if (i != null) {
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        } catch (Throwable ignore) {
        }
        finishAndKill();
    }

    private void closeApp() {
        finishAndKill();
    }

    private void finishAndKill() {
        try {
            finishAffinity();
        } catch (Throwable ignore) {
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                Process.killProcess(Process.myPid());
                System.exit(10);
            }
        }, "telegrios-kill").start();
    }
}
