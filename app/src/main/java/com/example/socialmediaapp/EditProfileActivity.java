package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediaapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    String uId ;
    String img_Url;

    private ImageView back_EditProfile , open_ImageEdit ;
    private TextView save_Profile ;
    private CircleImageView img_Edit;
    private EditText edt_NameEdit , edt_EmailEdit , edt_BioEdit ;
    private Button btn_UpdateProfile ;
    private ProgressBar progress_EditProfile ;
    private Toolbar toolBarEdit;

    Uri img_Uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        uId = getIntent().getStringExtra("uId");
        //Init UI views

        open_ImageEdit = findViewById(R.id.open_ImageEdit);
        img_Edit = findViewById(R.id.img_Edit);
        edt_NameEdit = findViewById(R.id.edt_NameEdit);
        edt_EmailEdit = findViewById(R.id.edt_EmailEdit);
        edt_BioEdit = findViewById(R.id.edt_BioEdit);
        btn_UpdateProfile = findViewById(R.id.btn_UpdateProfile);
        progress_EditProfile = findViewById(R.id.progress_EditProfile);
        toolBarEdit = findViewById(R.id.toolBarEdit);
        setSupportActionBar(toolBarEdit);
        getSupportActionBar().setTitle("Chỉnh sửa hồ sơ!");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolBarEdit.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        progress_EditProfile.setVisibility(View.GONE);

        loadProfile();

        open_ImageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1 ,1 )
                        .start(EditProfileActivity.this);
            }
        });

        btn_UpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = edt_NameEdit.getText().toString().trim();
                String email = edt_EmailEdit.getText().toString().trim();
                String bio = edt_BioEdit.getText().toString().trim();
                if(TextUtils.isEmpty(name)){
                    edt_NameEdit.setError("Nhập họ Tên!");
                }else if(TextUtils.isEmpty(email)){
                    edt_EmailEdit.setError("Nhập email!");
                }else {
                    progress_EditProfile.setVisibility(View.VISIBLE);
                    update(name , email , bio);
                }
            }
        });
    }

    private void update(String name, String email, String bio) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uId);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("upload_ImgEdit").child(""+System.currentTimeMillis());

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
                                map.put("uId" , uId);
                                map.put("name" , name);
                                map.put("email" , email);
                                map.put("img_Profile" , img_Url);
                                map.put("description" , bio);

                                reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(EditProfileActivity.this , "Thành Công" , Toast.LENGTH_SHORT).show();
                                            finish();
                                            progress_EditProfile.setVisibility(View.GONE);
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
            map.put("uId" , uId);
            map.put("name" , name);
            map.put("email" , email);
            map.put("img_Profile" , img_Url);
            map.put("description" , bio);

            reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(EditProfileActivity.this , "Thành Công" , Toast.LENGTH_SHORT).show();
                        progress_EditProfile.setVisibility(View.GONE);
                        finish();
                    }else {
                        Toast.makeText(EditProfileActivity.this , "Lỗi Thử lại!" , Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void loadProfile() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                edt_NameEdit.setText(user.getName());
                edt_EmailEdit.setText(user.getEmail());
                Picasso.get().load(user.getImg_Profile()).into(img_Edit);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            img_Uri = result.getUri();
            img_Edit.setImageURI(img_Uri);
        }
    }
}