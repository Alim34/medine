package com.meditation.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StatsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Prefs prefs = new Prefs(requireContext());

        ((TextView) view.findViewById(R.id.tvStreakBig)).setText(String.valueOf(prefs.getStreak()));
        ((TextView) view.findViewById(R.id.tvTotalSessions)).setText(String.valueOf(prefs.getTotalSessions()));
        ((TextView) view.findViewById(R.id.tvTotalMinutes)).setText(String.valueOf(prefs.getTotalMinutes()));
        ((TextView) view.findViewById(R.id.tvAvgDuration)).setText(String.valueOf(prefs.getAvgDuration()));
        ((TextView) view.findViewById(R.id.tvBestStreak)).setText(String.valueOf(prefs.getBestStreak()));
    }
}
