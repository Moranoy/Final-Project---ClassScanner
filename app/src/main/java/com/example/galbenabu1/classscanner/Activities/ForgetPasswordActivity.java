package com.example.galbenabu1.classscanner.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.galbenabu1.classscanner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button btnResetPassword;
    private EditText mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        mAuth = FirebaseAuth.getInstance();
        mEmail = (EditText) findViewById(R.id.email);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
    }


    public void onResetPasswordClicked(View v) {
        final String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required.");
        }
        else
        {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        toastMessage("Password sent to " + email);
                        Intent intent = new Intent(ForgetPasswordActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        toastMessage("Reset password failed");
                    }

                }
            });
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
