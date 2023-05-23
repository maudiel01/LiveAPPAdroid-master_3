package com.agora.samtan.agorabroadcast;

//import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        Button acceder = (Button) findViewById(R.id.btnAccederA);
        Button registro = (Button) findViewById(R.id.btnRegistroA);

        acceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailid = (EditText) findViewById(R.id.TextEmailA);
                EditText passwordid = (EditText) findViewById(R.id.TextPassA);
                String email = emailid.getText().toString();
                String password = passwordid.getText().toString();
                if (email.isEmpty()) {
                    emailid.setError("Please enter the email id.");
                    passwordid.requestFocus();

                }else if (!(email.isEmpty() && password.isEmpty())) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("TAG", "signInWithEmail:success");
                                        Intent i = new Intent(login.this, MainActivity.class);
                                        startActivity(i);
                                        //login.this.finish();

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("TAG", "signInWithEmail:failure", task.getException());

                                    }
                                }
                            });

                }

            }
        });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(login.this, Register.class);
                startActivity(i);
            }
        });

    }


}