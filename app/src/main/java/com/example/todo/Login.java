package com.example.todo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText loginEmail,loginPassword;
    Button login;
    TextView signUpLink,forgotPwd;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Transparent Actionbar
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        getSupportActionBar().hide();



        fAuth = FirebaseAuth.getInstance();
        loginEmail =  findViewById(R.id.loginEmail);
        loginPassword =  findViewById(R.id.loginPwd);
        login =  findViewById(R.id.loginBtn);
        signUpLink = findViewById(R.id.signupLink);
        forgotPwd = findViewById(R.id.forgotPwd);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if(TextUtils.isEmpty(mail)){
                    loginEmail.setError("Enter the Email ");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    loginPassword.setError("Enter the password ");
                    return;
                }

                fAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = fAuth.getCurrentUser();
                            startActivity(new Intent(Login.this,MainActivity.class));
                        }
                        else{
                            Toast.makeText(Login.this,"Authentication Failed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        forgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, SignUp.class));
            }
        });

    }

    private void showRecoverPasswordDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        LinearLayout linearLayout=new LinearLayout(this);
        final EditText new_email= new EditText(this);

        // write the email using which you registered
        new_email.setHint("Enter email");
        new_email.setMinEms(16);
        new_email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(new_email);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);


        builder.setPositiveButton("Reset password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = new_email.getText().toString().trim();
                beginRecovery(email);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    ProgressDialog loadingBar;
    private void beginRecovery(String email) {
        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Sending Email....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        // calling sendPasswordResetEmail
        // open your email and write the new
        // password and then you can login
        fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if(task.isSuccessful())
                {
                    // if isSuccessful then done message will be shown
                    // and you can change the password
                    Toast.makeText(Login.this,"Done sent",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(Login.this,"Error Occurred",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                Toast.makeText(Login.this,"Error Failed",Toast.LENGTH_LONG).show();
            }
        });
    }


}