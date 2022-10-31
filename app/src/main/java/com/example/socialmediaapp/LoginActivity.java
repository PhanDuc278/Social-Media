package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private EditText edt_Email , edt_Pass ;
    private Button btn_SignIn ;
    private TextView txt_SignUp , txt_ForgotPass ;
    private ImageView img_Google ;
    private ProgressBar progress_Login;
    FirebaseAuth auth ;
    FirebaseUser user ;
    private String email , pass ;
    public static final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    GoogleSignInClient googleSignInClient ;
    private static final int SIGN_IN = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edt_Email       = (EditText) findViewById(R.id.emailLogin);
        edt_Pass        = (EditText) findViewById(R.id.pass_Login);
        btn_SignIn      = (Button) findViewById(R.id.loginbtn);
        txt_SignUp      = (TextView) findViewById(R.id.signUp);
        txt_ForgotPass = (TextView) findViewById(R.id.forgotpass);
        img_Google      = (ImageView) findViewById(R.id.login_Google);
        progress_Login  = (ProgressBar) findViewById(R.id.progress_Login);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        progress_Login.setVisibility(View.GONE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_wed_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this , gso);

        btn_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check conditions
                email = edt_Email.getText().toString().trim();
                pass = edt_Pass.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    edt_Email.setError("Nhập email!");
                }
                else if(TextUtils.isEmpty(pass)){
                    edt_Pass.setError("Nhập mật khẩu!");
                }else if(!email.matches(emailPattern)){
                    edt_Email.setError("Nhập lại email!");
                }else if(pass.length()<6){
                    edt_Pass.setError("Mật khẩu > 6!");
                }else {
                    progress_Login.setVisibility(View.VISIBLE);
                    signInWithEmail(email , pass);
                }

            }
        });

        img_Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithGoogle();
            }
        });

        txt_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this , RegisterActivity.class));
                finish();
            }
        });

        txt_ForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this , ForgotPassActivity.class));
                finish();
            }
        });

    }

    //Login with email and
    private void signInWithEmail(String email, String pass) {
        auth.signInWithEmailAndPassword(email , pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progress_Login.setVisibility(View.GONE);
                    startActivity(new Intent(LoginActivity.this , MainActivity.class));
                    finish();
                }else {
                    progress_Login.setVisibility(View.GONE);
                    String erro = ""+task.getException().getMessage();
                    Toast.makeText(LoginActivity.this , ""+ erro ,Toast.LENGTH_SHORT ).show();
                }
            }
        });
    }

    //Login with Google
    private void loginWithGoogle() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent , SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SIGN_IN:{
                if(resultCode == RESULT_OK && data!=null){
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                    try {
                        GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);

                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    HashMap<String , Object> map = new HashMap<>();
                                    map.put("name" , googleSignInAccount.getDisplayName());
                                    map.put("email" , googleSignInAccount.getEmail());
                                    map.put("img_Profile" , String.valueOf(googleSignInAccount.getPhotoUrl()));
                                    map.put("uId", user.getUid());

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                                    reference.child(user.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(LoginActivity.this , "Thành công" , Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(LoginActivity.this , MainActivity.class));
                                                finish();
                                            }
                                        }
                                    });
                                }else {
                                    Toast.makeText(LoginActivity.this , task.getException().getMessage() , Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }catch (Exception e){
                        Toast.makeText(LoginActivity.this , e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}