package com.example.mixtape;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mixtape.databinding.FragmentHomeBinding;
import com.example.mixtape.databinding.FragmentTracksBinding;
import com.example.mixtape.ui.home.HomeViewModel;

public class TracksFragment extends Fragment {
    private FragmentTracksBinding binding;
    private MainActivity mainActivity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTracksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mainActivity = (MainActivity) getActivity(); // Get the instance of MainActivity

        //Button getProfileBtn = (Button) root.findViewById(R.id.get_profile);
        Button getTracksBtn = (Button) root.findViewById(R.id.get_tracks);
        //Button getSpotifyBtn = (Button) root.findViewById(R.id.get_spotify);

        getTracksBtn.setOnClickListener(((v) -> {
            if (mainActivity != null) {
                mainActivity.onGetUserProfileClickedT(getActivity());
            }
        }));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
