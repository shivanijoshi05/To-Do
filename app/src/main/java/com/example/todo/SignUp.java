package com.example.todo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    FirebaseAuth fAuth;
    EditText email,password,confirmPassword;
    TextView loginLink;
    Button signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Transparent Actionbar
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        getSupportActionBar().hide();

        fAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.signupEmail);
        password = findViewById(R.id.signupPwd);
        confirmPassword = findViewById(R.id.signupCpwd);
        signUp = findViewById(R.id.signupBtn);
        loginLink = findViewById(R.id.loginLink);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString().trim();
                String pwd = password.getText().toString().trim();
                String cPwd = confirmPassword.getText().toString().trim();

                if(TextUtils.isEmpty(mail)){
                    email.setError("Enter the Email ");
                    return;
                }
                if(TextUtils.isEmpty(pwd)){
                    password.setError("Enter the password ");
                    return;
                }
                if(TextUtils.isEmpty(cPwd)){
                    confirmPassword.setError("Enter the Confirm Password");
                    return;
                }

                if(pwd.length() < 6){
                    password.setError("Password must be more than 6 letters");
                    return;
                }
                if(!cPwd.equals(pwd)){
                    confirmPassword.setError("Your password is not matching");
                    return;
                }
                fAuth.createUserWithEmailAndPassword(mail,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUp.this,"User Registered.",Toast.LENGTH_SHORT).show();
                            Intent SignUpIntent = new Intent(SignUp.this,MainActivity.class);
                            startActivity(SignUpIntent);
                        }
                        else{
                            Toast.makeText(SignUp.this,"Error "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this,Login.class));
            }
        });
    }
}