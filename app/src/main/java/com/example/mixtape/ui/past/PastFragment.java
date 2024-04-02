package com.example.mixtape.ui.past;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mixtape.databinding.FragmentPastBinding;

public class PastFragment extends Fragment {

    private FragmentPastBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PastViewModel pastViewModel =
                new ViewModelProvider(this).get(PastViewModel.class);

        binding = FragmentPastBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textPast;
//        pastViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}