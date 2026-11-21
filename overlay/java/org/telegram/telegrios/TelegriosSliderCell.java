package org.telegram.telegrios;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.Gravity;
import android.widget.SeekBar;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

/** Slider row for Telegrios settings pages. Fixed height 64dp, accent-colored thumb. */
public class TelegriosSliderCell extends android.widget.FrameLayout {

    public interface OnSliderChange {
        void onChange(int value, boolean fromUser);
    }

    private final TextView titleView;
    private final TextView descView;
    private final TextView valueView;
    private final SeekBar seekBar;
    private final Paint dividerPaint;
    private boolean showDivider;
    private int min, max;
    private OnSliderChange callback;
    private boolean silent;
    private String unit = "";

    public TelegriosSliderCell(Context context) {
        super(context);
        setWillNotDraw(false);

        titleView = new TextView(context);
        titleView.setTextSize(16);
        titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        titleView.setPadding(AndroidUtilities.dp(21), AndroidUtilities.dp(9), 0, 0);
        addView(titleView, LayoutParams());

        valueView = new TextView(context);
        valueView.setTextSize(13);
        valueView.setPadding(0, AndroidUtilities.dp(11), AndroidUtilities.dp(21), 0);
        valueView.setGravity(Gravity.END);
        addView(valueView, LayoutParams());

        descView = new TextView(context);
        descView.setTextSize(12);
        descView.setAlpha(0.65f);
        descView.setPadding(AndroidUtilities.dp(21), 0, AndroidUtilities.dp(21), 0);
        addView(descView, LayoutParams());

        seekBar = new SeekBar(context);
        seekBar.setMax(100);
        int accent = TelegriosConfig.accent();
        seekBar.setProgressTintList(ColorStateList.valueOf(accent));
        seekBar.setThumbTintList(ColorStateList.valueOf(accent));
        seekBar.setPadding(AndroidUtilities.dp(16), 0, AndroidUtilities.dp(16), 0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                if (callback != null && fromUser && !silent) {
                    int v = min + (max - min) * progress / 100;
                    valueView.setText(format(v));
                    callback.onChange(v, true);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar bar) {}
            @Override public void onStopTrackingTouch(SeekBar bar) {}
        });
        LayoutParams sbp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        sbp.topMargin = AndroidUtilities.dp(30);
        addView(seekBar, sbp);

        dividerPaint = new Paint();
        dividerPaint.setColor(Theme.getColor(Theme.key_divider) | 0xFF000000);
    }

    private static FrameLayout.LayoutParams LayoutParams() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        return lp;
    }

    private String format(int v) {
        return v + (unit == null || unit.isEmpty() ? "" : " " + unit);
    }

    public void set(CharSequence title, CharSequence desc, int value, int mn, int mx, String u, boolean divider, OnSliderChange cb) {
        titleView.setText(title);
        descView.setText(desc == null || desc.length() == 0 ? "" : desc);
        descView.setVisibility(desc == null || desc.length() == 0 ? GONE : VISIBLE);
        min = mn; max = mx; unit = u; callback = cb; showDivider = divider;
        int clamped = Math.max(mn, Math.min(mx, value));
        silent = true;
        seekBar.setMax(100);
        seekBar.setProgress(max == min ? 0 : 100 * (clamped - mn) / (max - mn));
        silent = false;
        valueView.setText(format(clamped));
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int base = AndroidUtilities.dp(64);
        if (descView.getVisibility() == VISIBLE) base += AndroidUtilities.dp(18);
        if (titleView.getPaint() != null && titleView.getText().length() > 0) {
            // long titles wrap to second line
            int available = MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(42);
            float tw = titleView.getPaint().measureText(titleView.getText().toString());
            if (tw > available) base += AndroidUtilities.dp(20);
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(base, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showDivider) {
            int h = AndroidUtilities.dp(1);
            dividerPaint.setAlpha(60);
            canvas.drawRect(AndroidUtilities.dp(21), getHeight() - h, getWidth() - AndroidUtilities.dp(21), getHeight(), dividerPaint);
        }
    }
}
