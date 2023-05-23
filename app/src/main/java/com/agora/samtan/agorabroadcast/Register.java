package com.agora.samtan.agorabroadcast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        Context context = getApplicationContext();

        Button register = findViewById(R.id.btnRegistroR);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText emailTxt = findViewById(R.id.TextEmailR);
                EditText passTxt = findViewById(R.id.TextPassR);
                String email = emailTxt.getText().toString();
                String pass = passTxt.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "createUserWithEmail:success");

                                    CharSequence text = "Usuario Creado Exitosamente!";
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                    intent = new Intent(context, login.class);
                                    startActivity(intent);


                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                    Register.this.finish();

                                    //updateUI(null);
                                }
                            }
                        });

            }
        });

    }
}