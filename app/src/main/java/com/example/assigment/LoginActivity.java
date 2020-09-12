package com.example.assigment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText username, password;
    Button login;
    TextView register;
    Boolean isUsernameValid, isPasswordValid;
    TextInputLayout usernameError, passwordError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (TextInputEditText) findViewById(R.id.email_login);
        password = (TextInputEditText) findViewById(R.id.password_login);
        login = (Button) findViewById(R.id.login_button);
        register = (TextView) findViewById(R.id.text_register);
        usernameError = (TextInputLayout) findViewById(R.id.emailError);
        passwordError = (TextInputLayout) findViewById(R.id.passwordError);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = username.getText().toString().trim();
                String upassword = password.getText().toString().trim();

                SetValidation();
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to RegisterActivity
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void SetValidation() {
        // Check for a valid USERNAME.
        if (username.getText().toString().isEmpty()) {
            usernameError.setError(getResources().getString(R.string.name_error));
            isUsernameValid = false;
        } else  {
            isUsernameValid = true;
            usernameError.setErrorEnabled(false);
        }

        // Check for a valid password.
        if (password.getText().toString().isEmpty()) {
            passwordError.setError(getResources().getString(R.string.pass_error));
            isPasswordValid = false;
        } else if (password.getText().length() < 6) {
            passwordError.setError(getResources().getString(R.string.error_invalid_password));
            isPasswordValid = false;
        } else  {
            isPasswordValid = true;
            passwordError.setErrorEnabled(false);
        }

        if (isUsernameValid && isPasswordValid) {
            Toast.makeText(getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
