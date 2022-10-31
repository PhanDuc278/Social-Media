
package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
    private RoundedImageView ImagePost ;
    private ImageView add_ImagePost  , close_Post;
    private TextView txt_Post ;
    private EditText description_Post ;

    Uri img_Uri;
    String img_Url ;

    FirebaseAuth auth ;
    ProgressDialog dialog ;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        auth = FirebaseAuth.getInstance();
        Toolbar toolBar_Comment = findViewById(R.id.toolBar_Post);
        setSupportActionBar(toolBar_Comment);
        getSupportActionBar().setTitle("Thêm Bài Viết");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolBar_Comment.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this , MainActivity.class));
            }
        });
        ImagePost = findViewById(R.id.ImagePost);
        add_ImagePost = findViewById(R.id.add_ImagePost);
        txt_Post = findViewById(R.id.txt_Post);
        description_Post = findViewById(R.id.description_Post);


        add_ImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1 ,1 )
                        .start(PostActivity.this);
            }
        });

        txt_Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(PostActivity.this);
                dialog.setMessage("Vui lòng đợi");
                dialog.create();
                dialog.show();
                uploadImage();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            img_Uri = result.getUri();
            ImagePost.setImageURI(img_Uri);
        }else {
            startActivity(new Intent(PostActivity.this , MainActivity.class));
            finish();
        }
    }

    private void uploadImage() {

        String description = description_Post.getText().toString().trim();

        Date date = new Date();

        SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
        final  String strDate = format.format(date);
        final  String timeStamp = ""+System.currentTimeMillis();
        if(img_Uri !=null){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("img_Post").child(timeStamp);
            storageReference.putFile(img_Uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                img_Url = uri.toString();

                                HashMap<String , Object> map = new HashMap<>();
                                map.put("id" , ""+timeStamp);
                                map.put("img_Post" ,""+ img_Url);
                                map.put("post_Time" ,""+ strDate);
                                map.put("description", description);
                                map.put("post_By" ,auth.getCurrentUser().getUid());

                                reference.child(timeStamp).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            dialog.dismiss();
                                            Toast.makeText(PostActivity.this , "Thành Công" , Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(PostActivity.this , MainActivity.class));
                                            finish();
                                        }else {
                                            Toast.makeText(PostActivity.this , ""+task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });
                    }
                }
            });
        }
    }
}