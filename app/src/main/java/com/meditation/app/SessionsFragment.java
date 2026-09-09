package com.meditation.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SessionsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sessions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Prefs prefs = new Prefs(requireContext());
        List<Prefs.Session> sessions = prefs.getSessions();

        RecyclerView rv = view.findViewById(R.id.rvSessions);
        TextView tvEmpty = view.findViewById(R.id.tvEmpty);

        if (sessions.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            rv.setLayoutManager(new LinearLayoutManager(requireContext()));
            rv.setAdapter(new SessionAdapter(sessions));
        }
    }

    /* ── Adapter ───────────────────────────────────────────── */

    static class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.VH> {
        private final List<Prefs.Session> items;

        SessionAdapter(List<Prefs.Session> items) { this.items = items; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_session, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            Prefs.Session s = items.get(pos);
            h.emoji.setText(s.emoji());
            h.category.setText(s.category);
            h.date.setText(s.formattedDate());
            h.duration.setText(s.minutes + " мин");
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final TextView emoji, category, date, duration;
            VH(View v) {
                super(v);
                emoji = v.findViewById(R.id.tvEmoji);
                category = v.findViewById(R.id.tvCategory);
                date = v.findViewById(R.id.tvDate);
                duration = v.findViewById(R.id.tvDuration);
            }
        }
    }
}
