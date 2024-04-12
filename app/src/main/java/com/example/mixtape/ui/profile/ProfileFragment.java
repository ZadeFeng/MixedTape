package com.example.mixtape.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;

import com.example.mixtape.DetailActivity;
import com.example.mixtape.MainActivity;
import com.example.mixtape.R;
import com.example.mixtape.databinding.FragmentProfileBinding;
import com.example.mixtape.ui.past.PastViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel pastViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button editButton = root.findViewById(R.id.profile_editBtn);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("mixtape");

                View view = LayoutInflater.from(getContext()).inflate(R.layout.confirm_edit, null);

                Button no = view.findViewById(R.id.dltNo);
                Button yes = view.findViewById(R.id.dltYes);

                AlertDialog editConfirm = new AlertDialog.Builder(requireContext()).setView(view).create();
                editConfirm.setCancelable(false);
                editConfirm.show();

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editConfirm.dismiss();
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.edit_user, null);

                        EditText username = view.findViewById(R.id.edit_username);
                        EditText password = view.findViewById(R.id.edit_password);
                        Button submit = view.findViewById(R.id.submit);

                        AlertDialog editUser = new AlertDialog.Builder(requireContext()).setView(view).create();
                        editUser.setCancelable(false);
                        editUser.show();

                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String newUsername = username.getText().toString();
                                String newPassword = password.getText().toString();

                                Bundle bundle = new Bundle();
                                bundle.putString("newUsername", newUsername);
                                bundle.putString("newPassword", newPassword);

                                getParentFragmentManager().setFragmentResult("editProfile", bundle);
                                editUser.dismiss();
                            }
                        });
                    }
                });

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editConfirm.dismiss();
                    }
                });

            }
        });

        Button deleteButton = root.findViewById(R.id.profile_deleteBtn);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("mixtape");

                View view = LayoutInflater.from(getContext()).inflate(R.layout.confirm_delete, null);

                Button no = view.findViewById(R.id.dltNo);
                Button yes = view.findViewById(R.id.dltYes);

                AlertDialog deleteConfirm = new AlertDialog.Builder(requireContext()).setView(view).create();
                deleteConfirm.setCancelable(false);
                //deleteConfirm.setView(R.layout.confirm_delete);
                //AlertDialog dialog = deleteConfirm.create();
                deleteConfirm.show();

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteConfirm.dismiss();
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.delete_user, null);

                        EditText username = view.findViewById(R.id.delete_username);
                        EditText password = view.findViewById(R.id.delete_password);
                        Button submit = view.findViewById(R.id.submit);

                        AlertDialog deleteUser = new AlertDialog.Builder(requireContext()).setView(view).create();
                        deleteUser.setCancelable(false);
                        //deleteConfirm.setView(R.layout.confirm_delete);
                        //AlertDialog dialog = deleteConfirm.create();
                        deleteUser.show();

                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String username2 = username.getText().toString();
                                reference.child(username2).removeValue();
                                Toast.makeText(getContext(), "Mixtape Deleted", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getContext(), MainActivity.class));
                            }
                        });
                        //finish();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteConfirm.dismiss();
                    }
                });
            }
        });

//        final TextView textView = binding.textProfile;
//        pastViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}