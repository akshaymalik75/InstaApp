package com.example.akshaymalk.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropImageActivity;
import com.soundcloud.android.crop.CropImageView;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText UserName,FullName,CountryName;
    private Button SaveInformationbutton;
    private CircleImageView ProfileImage;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;
    String currentUserID;
    final static int Gallery_pick=1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
       UserProfileImageRef= FirebaseStorage.getInstance().getReference().child("profile Images");


        UserName=(EditText)findViewById(R.id.setup_username);
        FullName=(EditText)findViewById(R.id.setup_full_name);
        CountryName=(EditText)findViewById(R.id.setup_country_name);
        SaveInformationbutton=(Button) findViewById(R.id.setup_information_button);
        ProfileImage=(CircleImageView)findViewById(R.id.setup_profile_image);
        loadingBar=new ProgressDialog(this);

        SaveInformationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
              SaveAccountSetupInformation();
            }
        });


        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
               Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_pick);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){}
    /*{
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_pick && requestCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidlines.ON)
                    .setAspestRatio(1, 1);
                    .start(this);

        }
        if (requestCode == CropImage.CRop_image_avtivity_request_code) {
            if (requestCode == RESULT_OK) {
                loadingBar.setTitle("Saving pro pic!!!");
                loadingBar.setMessage("Please wait,while we are uploading your profile image..");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);
                Uri resultUri = result.getUri();
                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SetupActivity.this, "profile image stored successfullty to firebase storage...voila", Toast.LENGTH_SHORT).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            UsersRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent selfIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                                startActivity(selfIntent);
                                                finish();
                                                Toast.makeText(SetupActivity.this, "profile pic stored in firebase db...", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            } else {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SetupActivity.this, "error" + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            } else {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }*/

    private void SaveAccountSetupInformation()
    {
        String username=UserName.getText().toString();
        String fullname=FullName.getText().toString();
        String countryname=CountryName.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this,"Please input UserName...",Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(fullname))
        {
            Toast.makeText(this,"Please enter your FullName...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(countryname))
        {
            Toast.makeText(this,"Please Confirm Your Country...",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Saving Information!!!");
            loadingBar.setMessage("Please wait,while we are creating your new account..");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            HashMap userMap=new HashMap();
            userMap.put("username",username);
            userMap.put("fullname",fullname);
            userMap.put("countryname",countryname);
            userMap.put("status","Hey i am a cricket lover");
            userMap.put("gender","none");
            userMap.put("DOB","");
            userMap.put("relationshipstatus","none");

            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {   SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Data Saved Successfully..@", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String message=task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error occured"+ message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent=new Intent(SetupActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
