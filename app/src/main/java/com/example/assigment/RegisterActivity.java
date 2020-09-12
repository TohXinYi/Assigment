package com.example.assigment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText username, email, password, retypepassword;
    Button register;
    boolean isNameValid, isEmailValid, isPasswordValid, isRecoveryPasswordValid;
    TextInputLayout nameError, emailError, passError, retypepassError;
    private FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    String userID;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.username_register);
        email = (EditText) findViewById(R.id.edit_email_address);
        password = (TextInputEditText) findViewById(R.id.pass_register);
        retypepassword = (TextInputEditText) findViewById(R.id.retype_password);
        register = (Button) findViewById(R.id.register_button);
        nameError = (TextInputLayout) findViewById(R.id.nameError);
        emailError = (TextInputLayout) findViewById(R.id.emailError);
        passError = (TextInputLayout)  findViewById(R.id.passError);
        retypepassError = (TextInputLayout) findViewById(R.id.retypepassError);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        /*if (mAuth.getCurrentUser() !=null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }*/

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = username.getText().toString().trim();
                final String useremail = email.getText().toString().trim();
                final String upassword = password.getText().toString().trim();
                final String recoverypassword = retypepassword.getText().toString().trim();
                SetValidation();

                mAuth.createUserWithEmailAndPassword(useremail,upassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Register Successful.",Toast.LENGTH_SHORT).show();
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("Username", name);
                            user.put("Email", useremail);
                            //user.put("Password",upassword);
                            //user.put("Recovery Password", recoverypassword);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for" + userID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else {
                            Toast.makeText(RegisterActivity.this,"Register Failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });
    }

    private void SetValidation() {
        // Check for a valid name.
        if (username.getText().toString().isEmpty()) {
            nameError.setError(getResources().getString(R.string.name_error));
            isNameValid = false;
        } else  {
            isNameValid = true;
            nameError.setErrorEnabled(false);
        }

        // Check for a valid email address.
        if (email.getText().toString().isEmpty()) {
            emailError.setError(getResources().getString(R.string.email_error));
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            emailError.setError(getResources().getString(R.string.error_invalid_email));
            isEmailValid = false;
        } else  {
            isEmailValid = true;
            emailError.setErrorEnabled(false);
        }

        // Check for a valid password.
        if (password.getText().toString().isEmpty()) {
            passError.setError(getResources().getString(R.string.pass_error));
            isPasswordValid = false;
        } else if (password.getText().length() <= 6) {
            passError.setError(getResources().getString(R.string.error_invalid_password));
            isPasswordValid = false;
        } else  {
            isPasswordValid = true;
            passError.setErrorEnabled(false);
        }

        if (isNameValid && isEmailValid && isPasswordValid && isRecoveryPasswordValid) {
            Toast.makeText(getApplicationContext(), "Successfully Register", Toast.LENGTH_SHORT).show();
        }

        if (retypepassword.getText().toString().isEmpty()) {
            retypepassError.setError(getResources().getString(R.string.retypepass_error));
            isRecoveryPasswordValid = false;
        } else if (retypepassword.getText().length() <= 6) {
            retypepassError.setError(getResources().getString(R.string.error_invalid_retypepassword));
            isRecoveryPasswordValid = false;
        } else  {
            isRecoveryPasswordValid = true;
            retypepassError.setErrorEnabled(false);
        }

        /*if (isNameValid && isEmailValid && isPasswordValid && isRecoveryPasswordValid) {
            Toast.makeText(getApplicationContext(), "Successfully Register", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(),"Please fill the correct detail.", Toast.LENGTH_SHORT).show();
        }*/

    }
}