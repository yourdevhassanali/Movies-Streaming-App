package com.hassan.moviestream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class UploadThumbnailActivity extends AppCompatActivity {

    Uri videothumnailUri;
    String thumnail_url;
    ImageView album_art;
    StorageReference mStrogerefthumnail;
    DatabaseReference referenceVideos;
    TextView txtViewthumSelected;
    private RadioButton radioButtonLatest,radioButtonpopular
            ,radionnoselect,radioSlidermovies;
    StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_thumbnail);
        txtViewthumSelected = findViewById(R.id.txtViewthumSelected);
        album_art = findViewById(R.id.imageView);
        seletedBestAndLatestMovies();
        //  referenceVideosthumnail = FirebaseDatabase.getInstance().getReference().child("VideoThumnails");
        mStrogerefthumnail = FirebaseStorage.getInstance().getReference().child("VideoThumnails");
        referenceVideos = FirebaseDatabase.getInstance().getReference().child("videos");
    }
    private void seletedBestAndLatestMovies(){
        radioButtonLatest = findViewById(R.id.radiolatestmovies);
        radioButtonpopular = findViewById(R.id.radiopopularmovies);
        radionnoselect = findViewById(R.id.radionnoselect);
        radioSlidermovies = findViewById(R.id.radioSlidermovies);

        String currentuid = getIntent().getExtras().getString("currentuid");
        final DatabaseReference updateData = FirebaseDatabase.getInstance()
                .getReference("videos")
                .child(currentuid);

        radioSlidermovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String movieslide =  radioSlidermovies.getText().toString();
                Toast.makeText(UploadThumbnailActivity.this, "text : "+movieslide, Toast.LENGTH_SHORT).show();

                updateData.child("video_slide").setValue(movieslide);
            }
        });

        radioButtonLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String latestmovie =  radioButtonLatest.getText().toString();
                Toast.makeText(UploadThumbnailActivity.this, "text : "+latestmovie, Toast.LENGTH_SHORT).show();

                updateData.child("video_type").setValue(latestmovie);
                updateData.child("video_slide").setValue("");
            }
        });
        radioButtonpopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String popularmovie =  radioButtonpopular.getText().toString();
                Toast.makeText(UploadThumbnailActivity.this, "text : "+popularmovie, Toast.LENGTH_SHORT).show();
                updateData.child("video_type").setValue(popularmovie);
                updateData.child("video_slide").setValue("");
            }
        });

        radionnoselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData.child("video_type").setValue("");
                updateData.child("video_slide").setValue("");
            }
        });
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    public void showFileChooser (View view) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i,102);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 102 && resultCode == RESULT_OK && data.getData() != null)
        {
            videothumnailUri = data.getData();
            try {
                String thumname = getFileName(videothumnailUri);
                txtViewthumSelected.setText(thumname);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), videothumnailUri);
                album_art.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public   void  uploadfileToFirebase (View v ){
        if(txtViewthumSelected.getText().equals("No Thumbnail Selected")){
            Toast.makeText(this, "Please Select a Picture To Upload", Toast.LENGTH_SHORT).show();

        }else {


            if(mUploadTask != null && mUploadTask.isInProgress()){
                Toast.makeText(this, "Uploading in Progress", Toast.LENGTH_SHORT).show();

            }else {
                uploadFiles();
            }
        }
    }



    private void uploadFiles() {
        //checking if file is available
        if (videothumnailUri != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Thumbnail Please Wait!");
            progressDialog.show();
            String video_title = getIntent().getExtras().getString("thumnail_name");
            String thumb_name = txtViewthumSelected.getText().toString();
            //getting the storage reference
            final StorageReference sRef = mStrogerefthumnail.child(thumb_name
                    + "." + getFileExtension(videothumnailUri));

            //adding the file to reference
            sRef.putFile(videothumnailUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    thumnail_url = uri.toString();
                                    String currentuid = getIntent().getExtras().getString("currentuid");
                                    DatabaseReference updateData = FirebaseDatabase.getInstance()
                                            .getReference("videos")
                                            .child(currentuid);
                                    updateData.child("video_thumb").setValue(thumnail_url);


                                    progressDialog.dismiss();
                                    //displaying success toast
                                    Toast.makeText(getApplicationContext(), "Thumbnail Uploaded", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            //display an error if no file is selected
        }

    }
    @SuppressLint("Range")
    private String getFileName(Uri uri) {

        String result = null;
        if(uri.getScheme().equals("content")){

            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally {
                cursor.close();
            }
        }

        if(result == null){

            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut != -1){
                result = result.substring(cut + 1);

            }

        }
        return result;
    }
}