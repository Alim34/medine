package com.meditation.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private static final String[] QUOTES = {
        "«Иногда, чтобы увидеть свет, нужно закрыть глаза».",
        "«Дыши. Это достаточно. Просто дыши».",
        "«Покой — это не место. Это то, что ты несёшь внутри».",
        "«Настоящий момент всегда будет».",
        "«Тишина — это язык Бога, всё остальное — лишь перевод».",
        "«Начни с одного вдоха. Всего одного»."
    };

    private static final String[] AUTHORS = {
        "— Крым", "— Тит Нат Хан", "— Тит Нат Хан",
        "— Экхарт Толле", "— Руми", "— Нет Нет"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Prefs prefs = new Prefs(requireContext());

        // Greeting
        String name = prefs.getName();
        TextView tvGreeting = view.findViewById(R.id.tvGreeting);
        tvGreeting.setText("Привет, " + name + " 👋");

        // Settings button
        ImageView btnSettings = view.findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SettingsActivity.class)));

        // Quote
        int idx = (int) (Math.random() * QUOTES.length);
        ((TextView) view.findViewById(R.id.tvQuote)).setText(QUOTES[idx]);
        ((TextView) view.findViewById(R.id.tvQuoteAuthor)).setText(AUTHORS[idx]);

        // Stats
        ((TextView) view.findViewById(R.id.tvStreak)).setText(String.valueOf(prefs.getStreak()));
        int mins = prefs.getTotalMinutes();
        String timeStr = mins >= 60 ? (mins / 60) + " ч " + (mins % 60) + " м" : mins + " м";
        ((TextView) view.findViewById(R.id.tvTotalTime)).setText(timeStr);
        ((TextView) view.findViewById(R.id.tvToday)).setText(String.valueOf(prefs.getTodaySessions()));

        // Start session button
        view.findViewById(R.id.btnStartSession).setOnClickListener(v -> openSession("Расслабление", 10));

        // Category tiles
        view.findViewById(R.id.catBreath).setOnClickListener(v -> openSession("Дыхание", 5));
        view.findViewById(R.id.catRelax).setOnClickListener(v -> openSession("Расслабление", 10));
        view.findViewById(R.id.catSleep).setOnClickListener(v -> openSession("Сон", 20));
        view.findViewById(R.id.catFocus).setOnClickListener(v -> openSession("Фокус", 10));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh stats when returning from session
        if (getView() != null) {
            Prefs prefs = new Prefs(requireContext());
            ((TextView) getView().findViewById(R.id.tvStreak)).setText(String.valueOf(prefs.getStreak()));
            int mins = prefs.getTotalMinutes();
            String timeStr = mins >= 60 ? (mins / 60) + " ч " + (mins % 60) + " м" : mins + " м";
            ((TextView) getView().findViewById(R.id.tvTotalTime)).setText(timeStr);
            ((TextView) getView().findViewById(R.id.tvToday)).setText(String.valueOf(prefs.getTodaySessions()));
        }
    }

    private void openSession(String category, int minutes) {
        Intent intent = new Intent(requireContext(), SessionActivity.class);
        intent.putExtra("category", category);
        intent.putExtra("minutes", minutes);
        startActivity(intent);
    }
}
