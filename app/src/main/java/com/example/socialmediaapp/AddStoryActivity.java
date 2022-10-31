package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class AddStoryActivity extends AppCompatActivity {
    private Uri img_Uri ;
    String img_Url ;
    FirebaseUser user ;

    private ImageView open_Camera , img_StoryPreview , close_AddStory;
    private TextView postStory ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);
        user = FirebaseAuth.getInstance().getCurrentUser();

        open_Camera = findViewById(R.id.open_Camera);
        img_StoryPreview = findViewById(R.id.img_StoryPreview);
        close_AddStory = findViewById(R.id.close_AddStory);
        postStory = findViewById(R.id.postStory);

        open_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(9 , 16)
                        .start(AddStoryActivity.this);
            }
        });

        postStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStory();
            }
        });

        close_AddStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void addStory(){
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Vui lòng đợi!");
        dialog.show();

        StorageReference storageReference  = FirebaseStorage.getInstance().getReference("img_Stories").child(""+System.currentTimeMillis());
        if(img_Uri!=null){
            storageReference.putFile(img_Uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Date date = new Date();

                                SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
                                final  String strDate = format.format(date);

                                img_Url = uri.toString();
                                String myId = user.getUid();
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Story").child(myId);
                                String storyId = reference1.push().getKey();

                                //Story view in 24h = 86400000ms
                                long timeEnd = System.currentTimeMillis() + 86400000;
                                HashMap<String , Object> map = new HashMap<>();
                                map.put("img_Story" , img_Url);
                                map.put("storyId" , storyId);
                                map.put("uId" , user.getUid());
                                map.put("postTime" , ""+strDate);
                                map.put("timeStart" , ServerValue.TIMESTAMP);
                                map.put("timeEnd" , timeEnd);

                                reference1.child(storyId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialog.dismiss();
                                        startActivity(new Intent(AddStoryActivity.this , MainActivity.class));
                                        finish();
                                    }
                                });

                            }
                        });
                    }
                }
            });
        }else {
            dialog.dismiss();
            Toast.makeText(AddStoryActivity.this , "Thêm ảnh!" , Toast.LENGTH_SHORT).show();
            finish();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            img_Uri = result.getUri();
            img_StoryPreview.setImageURI(img_Uri);
        }else {
            Toast.makeText(AddStoryActivity.this , "Lỗi thử lại!" , Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddStoryActivity.this , MainActivity.class));
            finish();
        }
    }
    
}