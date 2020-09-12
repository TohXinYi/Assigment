package com.example.assigment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends AppCompatActivity {

    TextInputEditText email, password;
    CheckBox remember;
    Button login;
    TextView register;
    TextView forgotPassword;
    Boolean isEmailValid, isPasswordValid;
    TextInputLayout emailError, passwordError;
    private FirebaseAuth mAuth;
    //private static final String TAG = "GoogleSignInActivity";
    //private static final int RC_SIGN_IN = 9001;
    //private GoogleSignInClient mGoogleSignInClient;
    //private SignInButton mSignIn;
    CallbackManager mCallbackManager;
    private LoginButton loginButton;
    private static final String TAG = "FacebookAuthentication";
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (TextInputEditText) findViewById(R.id.email_login);
        password = (TextInputEditText) findViewById(R.id.password_login);
        remember = (CheckBox) findViewById(R.id.remember_chkbox);
        login = (Button) findViewById(R.id.login_button);
        register = (TextView) findViewById(R.id.text_register);
        emailError = (TextInputLayout) findViewById(R.id.emailError);
        passwordError = (TextInputLayout) findViewById(R.id.passwordError);
        forgotPassword = (TextView) findViewById(R.id.text_reset);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //mSignIn = findViewById(R.id.button_google);

        //Initialise Facebook SDK
        FacebookSdk.sdkInitialize(MainActivity.this);


        forgotPassword.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Forgot Password", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),ForgotPasswordActivity.class));

            }
        });


        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember","");
        
        if (checkbox.equals("true")){
            Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intent);
        }else if (checkbox.equals("false")){
            Toast.makeText(this,"Please Sign In First.", Toast.LENGTH_SHORT).show();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uemail = email.getText().toString().trim();
                String upassword = password.getText().toString().trim();

                SetValidation();

                mAuth.signInWithEmailAndPassword(uemail,upassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Log In Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), ResetPasswordActivity.class));
                        }else {
                            Toast.makeText(MainActivity.this,"LogIn Failed.",Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to RegisterActivity

                /*startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                Toast.makeText(MainActivity.this,"Register First", Toast.LENGTH_SHORT).show();*/
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });


        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                    Toast.makeText(MainActivity.this, "Checked", Toast.LENGTH_SHORT).show();
                }else if (!compoundButton.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    Toast.makeText(MainActivity.this, "Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Configure Google Sign In
        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        mSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
               signIn();
            }
        });*/


        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.button_facebook);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG,"onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,"onError" + error);

            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!= null){
                    updateUI(user);
                }
                else {
                    updateUI(null);
                }

            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null){
                    mAuth.signOut();
                }
            }
        };

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }

                // ...
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUI(FirebaseUser fUser){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        /*if (account != null){
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();

            Toast.makeText(MainActivity.this, personName + personEmail, Toast.LENGTH_SHORT).show();
        }*/
        /*if (fUser != null){
            Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this,"Please sign in again.", Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (authStateListener != null){
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void SetValidation() {
        // Check for a valid USERNAME.
        if (email.getText().toString().isEmpty()) {
            emailError.setError(getResources().getString(R.string.email_error));
            isEmailValid = false;
        } else  {
            isEmailValid = true;
            emailError.setErrorEnabled(false);
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

        /*if (isEmailValid && isPasswordValid) {
            Toast.makeText(getApplicationContext(), "Successfully LogIn", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(), "Log In Fail", Toast.LENGTH_SHORT).show();
        }*/
    }
}