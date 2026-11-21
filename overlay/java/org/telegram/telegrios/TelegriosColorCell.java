package org.telegram.telegrios;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;

/** Color picker row: horizontal strip of palette dots, selected one ringed. */
public class TelegriosColorCell extends android.widget.FrameLayout {

    public interface OnColorPick {
        void onPick(int color, boolean fromUser);
    }

    private final TextView titleView;
    private final TextView descView;
    private final android.widget.LinearLayout dotsRow;
    private final Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int[] palette;
    private int selected;
    private OnColorPick callback;
    private boolean showDivider;

    public TelegriosColorCell(Context context) {
        super(context);
        setWillNotDraw(false);

        titleView = new TextView(context);
        titleView.setTextSize(16);
        titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        titleView.setPadding(AndroidUtilities.dp(21), AndroidUtilities.dp(9), 0, 0);
        addView(titleView, layoutParams());

        descView = new TextView(context);
        descView.setTextSize(12);
        descView.setAlpha(0.65f);
        descView.setPadding(AndroidUtilities.dp(21), 0, AndroidUtilities.dp(21), 0);
        addView(descView, layoutParams());

        dotsRow = new android.widget.LinearLayout(context);
        dotsRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        dotsRow.setPadding(AndroidUtilities.dp(21), AndroidUtilities.dp(6), AndroidUtilities.dp(21), AndroidUtilities.dp(10));
        addView(dotsRow, layoutParams());
    }

    private static FrameLayout.LayoutParams layoutParams() {
        return new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    }

    public void set(CharSequence title, CharSequence desc, int[] colors, int current, boolean divider, OnColorPick cb) {
        titleView.setText(title);
        descView.setText(desc == null || desc.length() == 0 ? "" : desc);
        descView.setVisibility(desc == null || desc.length() == 0 ? GONE : VISIBLE);
        palette = colors;
        selected = current;
        callback = cb;
        showDivider = divider;

        dotsRow.removeAllViews();
        if (colors == null) return;
        for (int color : colors) {
            View dot = new View(getContext());
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(color);
            bg.setStroke(AndroidUtilities.dp(2), Color.TRANSPARENT);
            dot.setBackground(bg);
            dot.setOnClickListener(v -> {
                selected = color;
                if (callback != null) callback.onPick(color, true);
                refreshSelection();
            });
            android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(AndroidUtilities.dp(30), AndroidUtilities.dp(30));
            lp.rightMargin = AndroidUtilities.dp(8);
            dot.setLayoutParams(lp);
            dotsRow.addView(dot);
        }
        refreshSelection();
    }

    private void refreshSelection() {
        for (int i = 0; i < dotsRow.getChildCount() && palette != null; i++) {
            View dot = dotsRow.getChildAt(i);
            if (dot.getBackground() instanceof GradientDrawable) {
                GradientDrawable gd = (GradientDrawable) dot.getBackground();
                int c = palette[i];
                boolean sel = c == selected;
                gd.setStroke(AndroidUtilities.dp(2), sel ? 0xFFFFFFFF : Color.TRANSPARENT);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int base = AndroidUtilities.dp(88);
        if (descView.getVisibility() == VISIBLE) base += AndroidUtilities.dp(18);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(base, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showDivider) {
            int h = AndroidUtilities.dp(1);
            dotPaint.setAlpha(60);
            dotPaint.setColor(0xFF888888);
            canvas.drawRect(AndroidUtilities.dp(21), getHeight() - h, getWidth() - AndroidUtilities.dp(21), getHeight(), dotPaint);
        }
    }
}
