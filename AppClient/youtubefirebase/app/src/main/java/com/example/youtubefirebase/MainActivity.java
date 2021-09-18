package com.example.youtubefirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText email, password;
    private Button signIn;
    private TextView register;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signIn = (Button) findViewById(R.id.loginButton);
        signIn.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);
        email = (EditText) findViewById(R.id.emailSignIn);
        password = (EditText) findViewById(R.id.passwordSignIn);

        register = (TextView) findViewById(R.id.registerAccount);
        register.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerAccount:
                startActivity(new Intent(this, Register.class));
                break;
            case R.id.loginButton:
                signIn();
                break;
        }
    }

    private void signIn() {

        String emailString = email.getText().toString().trim();
        String passwordString = password.getText().toString().trim();

        if (emailString.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailString).matches()){
            email.setError("Ensure to enter a email");
            email.requestFocus();
            return;
        }


        if (passwordString.isEmpty()){
            password.setError("Ensure to enter a email");
            password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // Redirect the user to there user profile
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                    if (user.isEmailVerified()) {
                    if (true) {
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    }
                    else {
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check your email to verify your account", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Failed to login check your creds", Toast.LENGTH_LONG);
                }
            }
        });
    }
}