package com.example.mixtape;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mixtape.databinding.FragmentArtistsBinding;
import com.example.mixtape.databinding.FragmentHomeBinding;
import com.example.mixtape.ui.home.HomeViewModel;

public class ArtistsFragment extends Fragment {

    private MainActivity mainActivity;
    private FragmentArtistsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentArtistsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mainActivity = (MainActivity) getActivity(); // Get the instance of MainActivity

        Button getProfileBtn = (Button) root.findViewById(R.id.get_profile);
        //Button getSpotifyBtn = (Button) root.findViewById(R.id.get_spotify);

        getProfileBtn.setOnClickListener(((v) -> {
            if (mainActivity != null) {
                mainActivity.onGetUserProfileClickedA(getActivity());
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
