package org.telegram.telegrios;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import org.telegram.messenger.AndroidUtilities;

/**
 * Squircle avatar radius helper + animated neon ring for online users (A25/A26).
 */
public final class TelegriosAvatarUtils {

    private TelegriosAvatarUtils() {}

    /** Radius in px for a square avatar of the given size, honoring user settings. */
    public static float cornerRadiusPx(int sizePx) {
        if (!TelegriosConfig.isSquircleAvatars()) return 0f;
        int pct = TelegriosConfig.getAvatarCornerRadius(); // 0..50 (% of half-size)
        pct = Math.max(0, Math.min(50, pct));
        return sizePx / 2f * pct / 50f;
    }

    /** Animated glowing ring drawn around an online avatar. */
    public static Drawable onlineRing(Context context, int sizePx) {
        if (!TelegriosConfig.isOnlineNeonRing()) return null;
        return new NeonRingDrawable(TelegriosConfig.accent(), sizePx);
    }

    public static class NeonRingDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int color;
        private final int size;
        private long startMs;
        private final boolean animated;

        NeonRingDrawable(int color, int size) {
            this.color = color;
            this.size = size;
            this.animated = true;
            this.startMs = System.currentTimeMillis();
        }

        @Override
        public void draw(Canvas canvas) {
            android.graphics.Rect b = getBounds();
            float cx = b.exactCenterX(), cy = b.exactCenterY();
            float half = Math.min(b.width(), b.height()) / 2f - 2f * AndroidUtilities.dp(1f);
            float radius = Math.min(half, size / 2f * TelegriosConfig.getAvatarCornerRadius() / 50f + AndroidUtilities.dp(3f));
            long t = animated ? (System.currentTimeMillis() - startMs) % 2000L : 1000L;
            float phase = t / 2000f;
            float alpha = (float) (0.45 + 0.35 * Math.sin(phase * Math.PI * 2));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(AndroidUtilities.dp(2f));
            paint.setColor(color);
            paint.setAlpha((int) (255 * alpha));
            canvas.drawCircle(cx, cy, radius, paint);
            paint.setStrokeWidth(AndroidUtilities.dp(4f));
            paint.setAlpha((int) (255 * alpha * 0.3f));
            canvas.drawCircle(cx, cy, radius + AndroidUtilities.dp(2f), paint);
            invalidateSelf();
        }

        @Override
        public void setAlpha(int alpha) {}

        @Override
        public void setColorFilter(ColorFilter cf) {}

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
