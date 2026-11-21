package org.telegram.telegrios;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Base framework for all Telegrios Settings pages.
 * Pages only override fillRows(List<Row>) using the static row factories.
 * Telegrios-native settings are resolved by reflection against TelegriosConfig
 * (no typos possible); Nagram settings are resolved by owner class + item name.
 */
public abstract class TelegriosBaseSettingsFragment extends BaseFragment {

    // tiny functional interfaces (avoid java.util.function desugaring concerns)
    public interface BoolGet { boolean get(); }
    public interface BoolSet { void set(boolean v); }
    public interface IntGet { int get(); }
    public interface IntSet { void set(int v); }
    public interface StrGet { String get(); }
    public interface StrSet { void set(String v); }

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_SWITCH = 1;
    public static final int TYPE_SLIDER = 2;
    public static final int TYPE_COLOR = 3;
    public static final int TYPE_ACTION = 4;
    public static final int TYPE_INFO = 5;
    public static final int TYPE_SHADOW = 6;
    public static final int TYPE_ENUM = 7;

    public static class Row {
        public int type;
        public CharSequence title;
        public CharSequence desc;
        public boolean divider = true;
        public BoolGet boolGet;
        public BoolSet boolSet;
        public IntGet intGet;
        public IntSet intSet;
        public StrGet strGet;
        public StrSet strSet;
        public Runnable action;
        public int min, max;
        public String unit = "";
        public String[] options;
        public int[] palette;
        public String configKey; // telegrios config id, for change notifications

        public static Row header(String t) {
            Row r = new Row(); r.type = TYPE_HEADER; r.title = t; r.divider = false; return r;
        }

        public static Row info(String t) {
            Row r = new Row(); r.type = TYPE_INFO; r.title = t; r.divider = false; return r;
        }

        public static Row shadow() {
            Row r = new Row(); r.type = TYPE_SHADOW; r.divider = false; return r;
        }

        public static Row action(String t, Runnable runnable) {
            Row r = new Row(); r.type = TYPE_ACTION; r.title = t; r.action = runnable; return r;
        }

        /** Telegrios-native boolean toggle (reflection against TelegriosConfig). */
        public static Row specSwitch(String configId, String t, String d) {
            Row r = new Row(); r.type = TYPE_SWITCH; r.title = t; r.desc = d; r.configKey = configId;
            r.boolGet = () -> TelegriosReflection.specBool(configId);
            r.boolSet = v -> TelegriosReflection.setSpecBool(configId, v);
            return r;
        }

        /** Telegrios-native slider (int) setting. */
        public static Row specSlider(String configId, String t, String d, int mn, int mx, String u) {
            Row r = new Row(); r.type = TYPE_SLIDER; r.title = t; r.desc = d; r.configKey = configId;
            r.min = mn; r.max = mx; r.unit = u;
            r.intGet = () -> TelegriosReflection.specInt(configId);
            r.intSet = v -> TelegriosReflection.setSpecInt(configId, v);
            return r;
        }

        /** Telegrios-native enum (cycle through options on tap). */
        public static Row specEnum(String configId, String t, String d, String[] options) {
            Row r = new Row(); r.type = TYPE_ENUM; r.title = t; r.desc = d; r.configKey = configId;
            r.options = options;
            r.intGet = () -> TelegriosReflection.specInt(configId);
            r.intSet = v -> TelegriosReflection.setSpecInt(configId, v);
            return r;
        }

        /** Telegrios-native color picker row. */
        public static Row specColor(String configId, String t, String d, int[] palette) {
            Row r = new Row(); r.type = TYPE_COLOR; r.title = t; r.desc = d; r.configKey = configId;
            r.palette = palette;
            r.intGet = () -> TelegriosReflection.specInt(configId);
            r.intSet = v -> TelegriosReflection.setSpecInt(configId, v);
            return r;
        }

        /** Nagram/NekoX ConfigItem boolean toggle, e.g. naSwitch("...", "tw.nekomimi.nekogram.NekoConfig", "hidePhone", false). */
        public static Row naSwitch(String t, String d, String ownerClass, String itemName, boolean invert) {
            Row r = new Row(); r.type = TYPE_SWITCH; r.title = t; r.desc = d;
            r.boolGet = () -> TelegriosReflection.naBool(ownerClass, itemName, invert);
            r.boolSet = v -> TelegriosReflection.setNaBool(ownerClass, itemName, invert, v);
            return r;
        }

        /** Nagram/NekoX ConfigItem int slider, e.g. NekoConfig.customAudioBitrate. */
        public static Row naSlider(String t, String d, String ownerClass, String itemName, int mn, int mx, String u) {
            Row r = new Row(); r.type = TYPE_SLIDER; r.title = t; r.desc = d;
            r.min = mn; r.max = mx; r.unit = u;
            r.intGet = () -> TelegriosReflection.naInt(ownerClass, itemName);
            r.intSet = v -> TelegriosReflection.setNaInt(ownerClass, itemName, v);
            return r;
        }

        /** Plain lambda switch, for anything special (e.g. NekoConfig.sendReadMessagePackets logic). */
        public static Row lambdaSwitch(String t, String d, BoolGet get, BoolSet set) {
            Row r = new Row(); r.type = TYPE_SWITCH; r.title = t; r.desc = d;
            r.boolGet = get; r.boolSet = set;
            return r;
        }
    }

    private final List<Row> rows = new ArrayList<>();
    private RecyclerListView listView;
    private TelegriosConfig.Listener configListener;
    private TelegriosConfig.Listener themeListener;
    private Runnable pendingUpdate;

    protected abstract String pageTitle();

    protected void fillRows(List<Row> rows) {
    }

    @Override
    public boolean onFragmentCreate() {
        if (!super.onFragmentCreate()) return false;
        configListener = key -> scheduleRebind();
        TelegriosConfig.addListener(configListener);
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        if (configListener != null) TelegriosConfig.removeListener(configListener);
        super.onFragmentDestroy();
    }

    private void scheduleRebind() {
        if (pendingUpdate != null) return;
        pendingUpdate = () -> {
            pendingUpdate = null;
            if (listView != null && listView.getAdapter() != null) {
                rebuildValues();
                listView.getAdapter().notifyDataSetChanged();
            }
        };
        if (listView != null) {
            listView.postDelayed(pendingUpdate, 16);
        }
    }

    private void rebuildValues() {
        // values are read lazily in bindViewHolder, nothing to precompute
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(org.telegram.messenger.R.drawable.ic_ab_back);
        actionBar.setTitle(pageTitle());
        actionBar.setAllowOverlayTitle(true);

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(true);
        listView.setItemAnimator(null);
        listView.setEmptyView(null);
        listView.setClipToPadding(true);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setAdapter(new RecyclerListView.SelectionAdapter() {
            @Override
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                int pos = holder.getAdapterPosition();
                if (pos < 0 || pos >= rows.size()) return false;
                Row r = rows.get(pos);
                return r.type == TYPE_SWITCH || r.type == TYPE_ACTION || r.type == TYPE_ENUM || r.type == TYPE_SLIDER;
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v;
                switch (viewType) {
                    case TYPE_HEADER:
                        v = new HeaderCell(parent.getContext());
                        break;
                    case TYPE_SWITCH:
                        v = new TextCheckCell(parent.getContext());
                        break;
                    case TYPE_SLIDER:
                        v = new TelegriosSliderCell(parent.getContext());
                        break;
                    case TYPE_COLOR:
                        v = new TelegriosColorCell(parent.getContext());
                        break;
                    case TYPE_ACTION:
                        v = new TextCell(parent.getContext());
                        break;
                    case TYPE_ENUM: {
                        TextCell c = new TextCell(parent.getContext());
                        v = c;
                        break;
                    }
                    case TYPE_INFO:
                        v = new TextInfoPrivacyCell(parent.getContext());
                        break;
                    case TYPE_SHADOW:
                    default:
                        v = new TextInfoPrivacyCell(parent.getContext());
                        break;
                }
                v.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new RecyclerView.ViewHolder(v) {};
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                Row r = rows.get(position);
                switch (r.type) {
                    case TYPE_HEADER:
                        ((HeaderCell) holder.itemView).setText(r.title);
                        break;
                    case TYPE_SWITCH: {
                        TextCheckCell c = (TextCheckCell) holder.itemView;
                        boolean val;
                        try { val = r.boolGet.get(); } catch (Throwable t) { val = false; }
                        c.setTextAndCheck(r.title, val, r.divider);
                        break;
                    }
                    case TYPE_SLIDER: {
                        TelegriosSliderCell c = (TelegriosSliderCell) holder.itemView;
                        int val;
                        try { val = r.intGet.get(); } catch (Throwable t) { val = r.min; }
                        c.set(r.title, r.desc, val, r.min, r.max, r.unit, r.divider, (value, fromUser) -> {
                            try { r.intSet.set(value); } catch (Throwable ignore) {}
                        });
                        break;
                    }
                    case TYPE_COLOR: {
                        TelegriosColorCell c = (TelegriosColorCell) holder.itemView;
                        int val;
                        try { val = r.intGet.get(); } catch (Throwable t) { val = r.palette != null && r.palette.length > 0 ? r.palette[0] : 0xFF2CE876; }
                        c.set(r.title, r.desc, r.palette, val, r.divider, (color, fromUser) -> {
                            try { r.intSet.set(color); } catch (Throwable ignore) {}
                        });
                        break;
                    }
                    case TYPE_ACTION: {
                        TextCell c = (TextCell) holder.itemView;
                        c.setText(r.title, r.divider);
                        break;
                    }
                    case TYPE_ENUM: {
                        TextCell c = (TextCell) holder.itemView;
                        int val;
                        try { val = r.intGet.get(); } catch (Throwable t) { val = 0; }
                        String opt = (r.options != null && val >= 0 && val < r.options.length) ? r.options[val] : "?";
                        c.setTextAndValue(r.title, opt, r.divider);
                        break;
                    }
                    case TYPE_INFO:
                        ((TextInfoPrivacyCell) holder.itemView).setText(r.title);
                        break;
                    case TYPE_SHADOW:
                    default: {
                        TextInfoPrivacyCell c = (TextInfoPrivacyCell) holder.itemView;
                        c.setText("");
                        break;
                    }
                }
            }

            @Override
            public int getItemViewType(int position) {
                return rows.get(position).type;
            }

            @Override
            public int getItemCount() {
                return rows.size();
            }
        });

        listView.setOnItemClickListener((view, position) -> {
            if (position < 0 || position >= rows.size()) return;
            Row r = rows.get(position);
            try {
                if (r.type == TYPE_SWITCH) {
                    boolean nv = !r.boolGet.get();
                    r.boolSet.set(nv);
                    ((TextCheckCell) view).setChecked(nv);
                } else if (r.type == TYPE_ACTION) {
                    if (r.action != null) r.action.run();
                } else if (r.type == TYPE_ENUM) {
                    int nv = (r.intGet.get() + 1) % Math.max(1, r.options.length);
                    r.intSet.set(nv);
                    listView.getAdapter().notifyItemChanged(position);
                }
            } catch (Throwable t) {
                TelegriosUtils.showToast(getContext(), "Telegrios: " + t.getClass().getSimpleName());
            }
        });

        rows.clear();
        fillRows(rows);
        listView.getAdapter().notifyDataSetChanged();

        return frameLayout;
    }

    /** Convenience: palette used by color rows. */
    public static final int[] TELEGRIOS_PALETTE = {
            0xFF2CE876, 0xFF12B857, 0xFF0E8F45, 0xFF37E0FF,
            0xFF4FA3FF, 0xFF7C4DFF, 0xFFB388FF, 0xFFFF5C7A,
            0xFFFF8A65, 0xFFFFC94D, 0xFFE0E0E0, 0xFF9DEDB3
    };
}
