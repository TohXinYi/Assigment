package com.example.assigment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText textInputEditTextEmail;
    private TextInputLayout textInputLayoutEmail;
    private Button button_send;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        button_send = (Button) findViewById(R.id.button_send);

        mAuth = FirebaseAuth.getInstance();

        setTitle("Recover password");
        button_send.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String email = textInputEditTextEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(getApplication(), "Enter you Email", Toast.LENGTH_SHORT).show();

                    return;
                }else{
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ForgotPasswordActivity.this,"Password send to your email",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }else {
                                String message = task.getException().getMessage();
                                Toast.makeText(ForgotPasswordActivity.this,"Email wrong"+message,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });
    }


}