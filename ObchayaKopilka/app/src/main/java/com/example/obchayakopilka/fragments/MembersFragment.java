package com.example.obchayakopilka.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.obchayakopilka.R;
import com.example.obchayakopilka.database.AppDatabase;
import com.example.obchayakopilka.models.User;
import com.example.obchayakopilka.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class MembersFragment extends Fragment {

    private ListView lvMembers;
    private TextView tvMemberCount;
    private AppDatabase database;
    private SessionManager sessionManager;
    private User currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_members, container, false);

        lvMembers = view.findViewById(R.id.lv_members);
        tvMemberCount = view.findViewById(R.id.tv_member_count);

        database = AppDatabase.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());
        currentUser = sessionManager.getCurrentUser();

        loadMembers();

        return view;
    }

    private void loadMembers() {
        if (currentUser == null) return;

        new Thread(() -> {
            List<User> users = database.userDao().getAllUsers();
            List<String> memberNames = new ArrayList<>();
            for (User user : users) {
                memberNames.add(user.getUsername() + " (" + user.getRole() + ")");
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_list_item_1, memberNames);
                    lvMembers.setAdapter(adapter);
                    tvMemberCount.setText("Участников: " + users.size());
                });
            }
        }).start();
    }
}
