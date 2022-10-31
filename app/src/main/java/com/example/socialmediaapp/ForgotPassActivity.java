package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassActivity extends AppCompatActivity {
    private EditText email_ForgotPass ;
    private Button btn_RecievePass ;
    private ProgressBar progress_Forgot ;
    private ImageView img_Back ;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        auth = FirebaseAuth.getInstance();
        email_ForgotPass = (EditText) findViewById(R.id.email_ForgotPass);
        btn_RecievePass = (Button) findViewById(R.id.btn_RecievePass);
        progress_Forgot = (ProgressBar) findViewById(R.id.progress_Forgotpass);
        img_Back = (ImageView) findViewById(R.id.img_backLogin);

        progress_Forgot.setVisibility(View.GONE);

        btn_RecievePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_ForgotPass.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    email_ForgotPass.setError("Nhập email!");
                }else if(!email.matches(LoginActivity.emailPattern)){
                    email_ForgotPass.setError("Nhập lại email!");
                }else {
                    progress_Forgot.setVisibility(View.VISIBLE);
                    recivePass(email);
                }
            }
        });

        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPassActivity.this , LoginActivity.class));
                finish();
            }
        });
    }

    private void recivePass(String email) {
        auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progress_Forgot.setVisibility(View.GONE);
                Toast.makeText(ForgotPassActivity.this , "Kiểm tra email của bạn!" ,Toast.LENGTH_SHORT).show();;

                startActivity(new Intent(ForgotPassActivity.this , LoginActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress_Forgot.setVisibility(View.GONE);
                String erro = ""+e.getMessage();
                Toast.makeText(ForgotPassActivity.this , "Lỗi!" ,Toast.LENGTH_SHORT).show();;
            }
        });
    }
}