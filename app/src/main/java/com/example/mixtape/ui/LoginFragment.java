package com.example.mixtape.ui;

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
import androidx.fragment.app.Fragment;
import com.example.mixtape.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;
    private Button loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    auth = FirebaseAuth.getInstance();
    loginEmail = findViewById(R.id.login_email);
    loginPassword = findViewById(R.id.login_password);

    loginButton = findViewById(R.id.loginButton);

    loginButton.setOnClickListener(new View.onClickListener(){
        @Override
        piblic void onClick(View view) {
            String user = loginEmail.getText().toString().trim();
            String pass = loginPassword.getText().toString().trim();

            if (user.isEmpty()) {
                loginEmail.setError("Email cannot be empty");
            }
            if (pass.isEmpty()) {
                loginPassword.setError("Password cannot be empty");
            } else {
                auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginFragment.this, "Log In Successful", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(LoginFragment.this));
                        } else {
                            Toast.makeText(LoginFragment.this, "Log in Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    });



}
