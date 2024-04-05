package com.example.mixtape.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mixtape.MainActivity;
import com.example.mixtape.R;
import com.example.mixtape.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mainActivity = (MainActivity) getActivity(); // Get the instance of MainActivity

        Button getProfileBtn = root.findViewById(R.id.get_profile);
        Button getSpotifyBtn = root.findViewById(R.id.get_spotify);

        getSpotifyBtn.setOnClickListener((v) -> {
                if (mainActivity != null) {
                    mainActivity.getToken(getActivity());
                }
        });
        getProfileBtn.setOnClickListener((v) -> {
                if (mainActivity != null) {
                    mainActivity.onGetUserProfileClicked(getActivity());
                }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Clear any previous data or state associated with the buttons
        // For example, if you have text associated with the buttons:
        Button getProfileBtn = requireView().findViewById(R.id.get_profile);
        Button getSpotifyBtn = requireView().findViewById(R.id.get_spotify);

        getSpotifyBtn.setOnClickListener((v) -> {
            if (mainActivity != null) {
                mainActivity.getToken(getActivity());
            }
        });

        getProfileBtn.setOnClickListener((v) -> {
            if (mainActivity != null) {
                mainActivity.onGetUserProfileClicked(getActivity());
            }
        });

    }
}
