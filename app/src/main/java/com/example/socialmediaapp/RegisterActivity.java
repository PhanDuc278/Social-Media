package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    private CircleImageView  img_Register;
    private EditText name_Register , email_Register , rePass_Register , pass_Register ;
    private Button btn_SignUp ;
    private TextView txt_SingIn;
    private ProgressBar progress_SignUp ;
    FirebaseAuth auth;
    FirebaseUser user ;
    String img_Url ;

    Uri img_Uri ;

    final int PICK_IMAGE = 100 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activiry);

        auth = FirebaseAuth.getInstance();

        img_Register = (CircleImageView) findViewById(R.id.img_Register);
        name_Register = (EditText) findViewById(R.id.name_Register);
        email_Register = (EditText) findViewById(R.id.email_Register);
        rePass_Register = (EditText) findViewById(R.id.rePass_Register);
        pass_Register = (EditText) findViewById(R.id.pass_Register);
        btn_SignUp = (Button) findViewById(R.id.btn_SignUp);
        txt_SingIn = (TextView) findViewById(R.id.txt_SignIn);
        progress_SignUp = (ProgressBar) findViewById(R.id.progress_SignUp);

        progress_SignUp.setVisibility(View.GONE);

        img_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent ,"Chọn"), PICK_IMAGE);
            }
        });

        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = email_Register.getText().toString().trim();
                String name  = name_Register.getText().toString().trim();
                String pass = pass_Register.getText().toString().trim();
                String re_Pass = rePass_Register.getText().toString().trim();

                //Check conditions
                if(TextUtils.isEmpty(email)){
                    email_Register.setError("Nhập email!");
                }else if(!email.matches(LoginActivity.emailPattern)){
                    email_Register.setError("Email không đúng!");
                }else if(TextUtils.isEmpty(name)){
                    name_Register.setError("Nhập họ tên!");
                }else if(TextUtils.isEmpty(pass)){
                    pass_Register.setError("Nhập mật khẩu");
                }else if(pass.length()<6){
                    pass_Register.setError("Mật khẩu > 6!");
                }else if(!re_Pass.matches(pass)){
                    rePass_Register.setError("Mật khẩu không khớp!");
                }else {
                    progress_SignUp.setVisibility(View.VISIBLE);
                    registerUser(name , email , pass);
                }
            }
        });

        txt_SingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this , LoginActivity.class));
                finish();
            }
        });


    }

    private void registerUser(String name, String email, String pass) {
        auth.createUserWithEmailAndPassword(email , pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String timeStamp = ""+System.currentTimeMillis();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("img_Profile").child(timeStamp);

                    if(img_Uri!=null){
                        storageReference.putFile(img_Uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            img_Url = uri.toString();

                                            HashMap<String , Object> map = new HashMap<>();
                                            map.put("uId" ,auth.getUid());
                                            map.put("name" , name);
                                            map.put("email" , email);
                                            map.put("img_Profile" , img_Url);

                                            reference.child(auth.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        progress_SignUp.setVisibility(View.GONE);
                                                        Toast.makeText(RegisterActivity.this , "Đăng ký thành công !", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(RegisterActivity.this , LoginActivity.class));
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    }else {

                        img_Url = "https://firebasestorage.googleapis.com/v0/b/socialmediaapp-1291b.appspot.com/o/profile.jfif?alt=media&token=6407fa9d-bbdf-4060-80fb-0f101980d385";
                        HashMap<String , Object> map = new HashMap<>();
                        map.put("uId" ,auth.getUid());
                        map.put("name" , name);
                        map.put("email" , email);
                        map.put("img_Profile" , img_Url);

                        reference.child(auth.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    progress_SignUp.setVisibility(View.GONE);
                                    Toast.makeText(RegisterActivity.this , "Đăng ký thành công !", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this , LoginActivity.class));
                                    finish();
                                }
                            }
                        });

                    }
                }else {
                    progress_SignUp.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this , "Đăng ký thất bại !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE){
            if(resultCode == RESULT_OK && data!=null){
                img_Uri = data.getData();
                img_Register.setImageURI(img_Uri);
            }
        }
    }
}