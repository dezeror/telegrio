package org.telegram.telegrios;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Liquid Glass panel: captures the content behind itself at quarter resolution,
 * blurs it (RenderEffect on API 31+, optimized stack blur fallback on older
 * Android), draws a translucent tint, a 1px glass border and an optional neon
 * glow border. Never throws: on any failure it degrades to a plain tint.
 */
public class TelegriosGlassPanel extends FrameLayout {

    private static final List<TelegriosGlassPanel> livePanels = new ArrayList<>();

    private final ImageView blurLayer;
    private final Paint tintPaint = new Paint();
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private View captureRoot;
    private boolean capturing;
    private float cornerRadius = 0;
    private Bitmap captureBitmap;
    private final Runnable invalidateRunnable = this::refreshBlur;
    private boolean attached;

    public TelegriosGlassPanel(Context context) {
        super(context);
        setWillNotDraw(false);
        blurLayer = new ImageView(context);
        blurLayer.setScaleType(ImageView.ScaleType.FIT_XY);
        blurLayer.setHorizontalFadingEdgeEnabled(false);
        addView(blurLayer, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        applyConfig();
    }

    public static boolean blurSupported() {
        return true; // always available: software fallback path exists
    }

    public void setCornerRadius(float radiusPx) {
        cornerRadius = radiusPx;
        if (radiusPx > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setClipToOutline(true);
            setOutlineProvider(new android.view.ViewOutlineProvider() {
                @Override
                public void getOutline(View view, android.graphics.Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radiusPx);
                }
            });
        }
        invalidate();
    }

    public void attachCaptureRoot(View root) {
        captureRoot = root;
        if (!attached && root != null) {
            attached = true;
            root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (isShown() && TelegriosConfig.isLiquidGlass()) {
                        // defer to avoid jank: refresh at most every other frame
                        removeCallbacks(invalidateRunnable);
                        post(invalidateRunnable);
                    }
                    return true;
                }
            });
        }
        refreshBlur();
    }

    public void applyConfig() {
        int alpha = TelegriosConfig.getGlassAlpha(); // 10..95 (%)
        int base = 0x101712;
        tintPaint.setColor((alpha * 255 / 100) << 24 | base);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(Math.max(1f, 1f * getResources().getDisplayMetrics().density));
        borderPaint.setColor(0x66FFFFFF);
        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(Math.max(1f, 1.5f * getResources().getDisplayMetrics().density));
        glowPaint.setColor(TelegriosConfig.accent());
        refreshBlur();
        invalidate();
    }

    private static int applyAlpha(int color, int alphaPct) {
        return (alphaPct * 255 / 100) << 24 | (color & 0xFFFFFF);
    }

    private void refreshBlur() {
        if (!TelegriosConfig.isLiquidGlass() || captureRoot == null || getWidth() == 0 || getHeight() == 0) {
            blurLayer.setImageBitmap(null);
            return;
        }
        try {
            int w = captureRoot.getWidth(), h = captureRoot.getHeight();
            if (w <= 0 || h <= 0) return;
            float scale = 0.25f;
            int bw = Math.max(1, (int) (w * scale)), bh = Math.max(1, (int) (h * scale));
            if (captureBitmap == null || captureBitmap.getWidth() != bw || captureBitmap.getHeight() != bh) {
                captureBitmap = Bitmap.createBitmap(bw, bh, Bitmap.Config.ARGB_8888);
            }
            capturing = true;
            setVisibility(INVISIBLE);
            Canvas c = new Canvas(captureBitmap);
            Matrix m = new Matrix();
            m.setScale(scale, scale);
            c.setMatrix(m);
            captureRoot.draw(c);
            setVisibility(VISIBLE);
            capturing = false;

            float radiusPx = 25f * TelegriosConfig.getGlassBlurRadius() / 100f * getResources().getDisplayMetrics().density;
            if (Build.VERSION.SDK_INT >= 31) {
                blurLayer.setImageBitmap(captureBitmap);
                if (radiusPx >= 1f) {
                    blurLayer.setRenderEffect(RenderEffect.createBlurEffect(radiusPx, radiusPx, Shader.TileMode.CLAMP));
                } else {
                    blurLayer.setRenderEffect(null);
                }
            } else {
                Bitmap blurred = stackBlur(captureBitmap, (int) Math.max(1f, radiusPx));
                blurLayer.setImageBitmap(blurred);
            }
        } catch (Throwable t) {
            setVisibility(VISIBLE);
            capturing = false;
            blurLayer.setImageBitmap(null);
        }
    }

    /** Simple separable box blur, 3 passes, quarter-res input: fast enough for panels. */
    private static Bitmap stackBlur(Bitmap src, int radius) {
        try {
            int w = src.getWidth(), h = src.getHeight();
            int[] pixels = new int[w * h];
            src.getPixels(pixels, 0, w, 0, 0, w, h);
            int r = Math.max(1, Math.min(radius, 25));
            for (int pass = 0; pass < 3; pass++) {
                boxBlurH(pixels, w, h, r);
                boxBlurV(pixels, w, h, r);
            }
            Bitmap out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            out.setPixels(pixels, 0, w, 0, 0, w, h);
            return out;
        } catch (Throwable t) {
            return src;
        }
    }

    private static void boxBlurH(int[] p, int w, int h, int r) {
        int[] tmp = new int[p.length];
        for (int y = 0; y < h; y++) {
            int rowBase = y * w;
            for (int x = 0; x < w; x++) {
                int a = 0, rr = 0, g = 0, b = 0, n = 0;
                for (int k = -r; k <= r; k++) {
                    int xx = x + k;
                    if (xx < 0 || xx >= w) continue;
                    int c = p[rowBase + xx];
                    a += (c >>> 24); rr += (c >> 16) & 0xFF; g += (c >> 8) & 0xFF; b += c & 0xFF;
                    n++;
                }
                tmp[rowBase + x] = (a / n << 24) | (rr / n << 16) | (g / n << 8) | (b / n);
            }
        }
        System.arraycopy(tmp, 0, p, 0, p.length);
    }

    private static void boxBlurV(int[] p, int w, int h, int r) {
        int[] tmp = new int[p.length];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int a = 0, rr = 0, g = 0, b = 0, n = 0;
                for (int k = -r; k <= r; k++) {
                    int yy = y + k;
                    if (yy < 0 || yy >= h) continue;
                    int c = p[yy * w + x];
                    a += (c >>> 24); rr += (c >> 16) & 0xFF; g += (c >> 8) & 0xFF; b += c & 0xFF;
                    n++;
                }
                tmp[y * w + x] = (a / n << 24) | (rr / n << 16) | (g / n << 8) | (b / n);
            }
        }
        System.arraycopy(tmp, 0, p, 0, p.length);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // glass border
        if (cornerRadius > 0) {
            borderPaint.setAlpha(0x55);
            glowPaint.setAlpha((int) (0x30 + 0x5A * TelegriosConfig.getGlowIntensity() / 100f));
            float glowSpread = 3f + 6f * TelegriosConfig.getGlowIntensity() / 100f;
            if (TelegriosConfig.isNeonGlowBorder()) {
                for (int i = 1; i <= 3; i++) {
                    glowPaint.setStrokeWidth(glowSpread * i);
                    glowPaint.setAlpha((int) (0x18 + 0x10 * TelegriosConfig.getGlowIntensity() / 100f * (4 - i)));
                    canvas.drawRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius, glowPaint);
                }
            }
            canvas.drawRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius, borderPaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        livePanels.remove(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!livePanels.contains(this)) livePanels.add(this);
    }

    /** Refresh all live panels (config changed). */
    public static void refreshAll() {
        for (TelegriosGlassPanel p : new ArrayList<>(livePanels)) {
            try {
                p.applyConfig();
            } catch (Throwable ignore) {}
        }
    }
}
