package com.example.galbenabu1.classscanner.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.galbenabu1.classscanner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Logic.Managers.LoggedInUserDetailsManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //    Listener called when there is a change in the authentication state.
//    Use addAuthStateListener(AuthStateListener) and removeAuthStateListener(AuthStateListener) to register or unregister listeners.
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // UI references.
    private EditText mEmail, mPassword;
    private Button btnCreateAccount;
    private Button btnResetPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate >> ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //declare buttons and edit texts in oncreate
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        Button btnSignIn = (Button) findViewById(R.id.email_sign_in_button);
        btnCreateAccount = (Button) findViewById(R.id.create_account_button);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        mAuth = FirebaseAuth.getInstance();


//    This method gets invoked in the UI thread on changes in the authentication state:
//
//    Right after the listener has been registered
//    When a user is signed in
//    When the current user is signed out
//    When the current user changes
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                    LoggedInUserDetailsManager.initUserDetailsOnLogin(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
//                    LoggedInUserDetailsManager.initUserDetailsOnLogin(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    //startActivity(intent);
//                    Intent intent = new Intent(MainActivity.this, CropImageActivity.class);
 //                   startActivity(intent);
                } else {
                    // User is signed out
                    Log.e(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
            }
        };

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: Switching Activities.");

                Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(intent);

//                String email = mEmail.getText().toString();
//                String pass = mPassword.getText().toString();
//                createAccount(email, pass);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                final String pass = mPassword.getText().toString();
                if (!email.equals("") && !pass.equals("")) {
                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        // there was an error
                                        if (pass.length() < 6) {
                                            mPassword.setError(getString(R.string.minimum_password));
                                        } else {
                                            Toast.makeText(MainActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        LoggedInUserDetailsManager.initUserDetailsOnLogin(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                } else {
                    toastMessage("You didn't fill in all the fields.");
                }
            }
        });
        Log.e(TAG, "onCreate >>");
    }

    @Override
    public void onBackPressed() {
        // Override and do nothing to cancel clicking back from this activity.
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required.");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Required.");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }

    private void resetPassword()
    {
        Intent intent = new Intent(MainActivity.this, ForgetPasswordActivity.class);
        startActivity(intent);
    }

}

