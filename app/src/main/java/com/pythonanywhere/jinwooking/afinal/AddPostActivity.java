package com.pythonanywhere.jinwooking.afinal;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddPostActivity extends AppCompatActivity {

    EditText mTitleEt, mDescrEt;
    ImageView mPostIv;
    Button mUploadBtn;


    String mStoragePath = "All_Image_Uploads/";
    String mDatabasePath = "Data";


    Uri mFilePathUri;

    //Creating StorageReference and Database reference
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;

    ProgressDialog mProgressDialog;

    int IMAGE_REQUEST_CODE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        //Actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("글쓰기");

        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));
            }
        }else if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        mTitleEt = findViewById(R.id.pTitleEt);
        mDescrEt = findViewById(R.id.pDescrEt);
        mPostIv = findViewById(R.id.pImageIv);
        mUploadBtn = findViewById(R.id.pUploadBtn);

        mPostIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating intent
                Intent intent = new Intent();
                //setting intent type as image to select image from phone storage
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_REQUEST_CODE);
            }
        });
        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call method to upload data to firebase
                uploadDataToFirebase();
            }
        });

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(mDatabasePath);

        mProgressDialog = new ProgressDialog(AddPostActivity.this);
    }

    private void uploadDataToFirebase() {
        if (mFilePathUri != null)
        {
            mProgressDialog.setTitle("게시");
            mProgressDialog.show();
            StorageReference storageReference2nd = mStorageReference.child(mStoragePath + System.currentTimeMillis() + "." + getFileExtension(mFilePathUri));

            storageReference2nd.putFile(mFilePathUri)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadUri = uriTask.getResult();

                            String mPostTitle = mTitleEt.getText().toString().trim();
                            String mPostDescr = mDescrEt.getText().toString().trim();
                            mProgressDialog.dismiss();
                            Toast.makeText(AddPostActivity.this, "게시완료", Toast.LENGTH_SHORT).show();
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(mPostTitle, mPostDescr, downloadUri.toString(), mPostTitle.toLowerCase());
                            String imageUploadId = mDatabaseReference.push().getKey();
                            mDatabaseReference.child(imageUploadId).setValue(imageUploadInfo);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.dismiss();
                            Toast.makeText(AddPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressDialog.setTitle("게시중");
                        }
                    });
        } else {
            Toast.makeText(this, "Please select image or add image name", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            mFilePathUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mFilePathUri);
                mPostIv.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
