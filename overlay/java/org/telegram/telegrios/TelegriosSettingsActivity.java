package org.telegram.telegrios;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Root "Telegrios Settings" hub: branded header + 8 module entries.
 * Presented from the drawer / settings hook (see docs/HOOKS.md).
 */
public class TelegriosSettingsActivity extends BaseFragment {

    private static final String[][] SECTIONS = {
            {"Liquid Glass и цвета", "Стекло, AMOLED, акценты", "glass"},
            {"Навигация и главный экран", "Вкладки, аватарки, скрытие элементов", "home"},
            {"Пузыри чатов и типографика", "Скругления, шрифты, стикеры", "chat"},
            {"Поле ввода и клавиатура", "Капсульный ввод, кнопки", "input"},
            {"Приватность и Призрак", "Невидимость, анти-удаление, замки", "privacy"},
            {"Умные чаты и текст", "Перевод, сниппеты, калькулятор", "smart"},
            {"Медиа, голос и кружочки", "Битрейт, вспышка, перемотка", "media"},
            {"Система и производительность", "ID, реклама, 120 FPS", "system"},
    };

    @Override
    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(org.telegram.messenger.R.drawable.ic_ab_back);
        actionBar.setTitle("Telegrios Settings");
        actionBar.setAllowOverlayTitle(true);

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));

        // branded header
        FrameLayout header = new FrameLayout(context);
        header.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));

        ImageView logo = new ImageView(context);
        logo.setImageResource(org.telegram.messenger.R.drawable.ic_telegrios_logo);
        header.addView(logo, LayoutHelper.createFrame(72, 72, android.view.Gravity.CENTER_HORIZONTAL, 0, 16, 0, 0));

        TextView title = new TextView(context);
        title.setText("Telegrios");
        title.setTextSize(20);
        title.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        title.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        title.setGravity(android.view.Gravity.CENTER);
        header.addView(title, LayoutHelper.createFrame(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 92, 0, 0));

        TextView subtitle = new TextView(context);
        subtitle.setText("Liquid Glass мод для Telegram\nv1.3 • Telegrios Dark + iOS Light/Dark • " + SECTIONS.length + " модуля настроек");
        subtitle.setTextSize(13);
        subtitle.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        subtitle.setGravity(android.view.Gravity.CENTER);
        header.addView(subtitle, LayoutHelper.createFrame(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 126, 0, 0));

        RecyclerListView listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        listView.setItemAnimator(null);

        List<Object[]> items = new ArrayList<>();
        for (String[] s : SECTIONS) items.add(new Object[]{s[0], s[1], s[2]});

        listView.setAdapter(new RecyclerListView.SelectionAdapter() {
            @Override
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return true;
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                TextCell cell = new TextCell(parent.getContext());
                cell.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new RecyclerView.ViewHolder(cell) {};
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                Object[] it = items.get(position);
                ((TextCell) holder.itemView).setTextAndValue((String) it[0], (String) it[1], true);
            }

            @Override
            public int getItemCount() {
                return items.size();
            }
        });

        listView.setOnItemClickListener((view, position) -> {
            String id = (String) items.get(position)[2];
            BaseFragment f = pageFor(id);
            if (f != null) {
                presentFragment(f);
            }
        });

        FrameLayout content = new FrameLayout(context);
        content.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        content.addView(header, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        content.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 0, 0, 150, 0, 0));
        frameLayout.addView(content, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        return frameLayout;
    }

    private BaseFragment pageFor(String id) {
        switch (id) {
            case "glass": return new TelegriosGlassActivity();
            case "home": return new TelegriosHomeActivity();
            case "chat": return new TelegriosChatActivity();
            case "input": return new TelegriosInputActivity();
            case "privacy": return new TelegriosPrivacyActivity();
            case "smart": return new TelegriosSmartActivity();
            case "media": return new TelegriosMediaActivity();
            case "system": return new TelegriosSystemActivity();
            default: return null;
        }
    }
}
