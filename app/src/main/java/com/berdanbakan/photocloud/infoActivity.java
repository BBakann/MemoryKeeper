package com.berdanbakan.photocloud;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.berdanbakan.photocloud.databinding.ActivityInfoBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class infoActivity extends AppCompatActivity {
    private ActivityInfoBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> requestPermissionLauncher;
    Bitmap selectedImage;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });
        registerLauncher();
    }

    public void selectImage(View view) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
        //android 33 üstü = read media images ister
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {


                    Snackbar.make(view, "Are you sure you want to go to the gallery", Snackbar.LENGTH_INDEFINITE).setAction("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);

                        }
                    }).show();

                } else {
                    //request permission
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);

                }

            }else{
                //gallery
                Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);// galeriden gidip görsel alacağımızı söylüyoruz burada.
                activityResultLauncher.launch(intentToGallery);

            }
        }else {
            //android 32altındaysa read external storage
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {


                    Snackbar.make(view, "Are you sure you want to go to the gallery", Snackbar.LENGTH_INDEFINITE).setAction("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);

                        }
                    }).show();

                } else {
                    //request permission
                    requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);

                }

            }else{
                //gallery
                Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);// galeriden gidip görsel alacağımızı söylüyoruz burada.
                activityResultLauncher.launch(intentToGallery);

            }
        }



    }
    private void registerLauncher(){


        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK){
                    Intent intentFromResult=result.getData();
                    result.getData().getData();
                    if(intentFromResult!=null){
                      Uri imageData= intentFromResult.getData();
                        //binding.imageView3.setImageURI(imageData);

                        try {
                            if(Build.VERSION.SDK_INT>=28){
                                ImageDecoder.Source source= ImageDecoder.createSource(infoActivity.this.getContentResolver(),imageData);
                                selectedImage= ImageDecoder.decodeBitmap(source);
                                binding.imageView3.setImageBitmap(selectedImage);
                                binding.imageView3.setContentDescription("Seçilen resim");
                            }else {
                                selectedImage= MediaStore.Images.Media.getBitmap(infoActivity.this.getContentResolver(),imageData);
                                binding.imageView3.setImageBitmap(selectedImage);
                                binding.imageView3.setContentDescription("Seçilen resim");
                            }
                        }catch (Exception e){
                            e.printStackTrace();

                        }
                    }

                }

            }
        });

        requestPermissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    //permission granted
                    Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);

                }else {
                    //permission denied
                    Toast.makeText(infoActivity.this, "Permission Needed", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }


    public void save(View view) {
        String nameText=binding.nameText.getText().toString();
        String dateText=binding.dateText.getText().toString();
        Bitmap smallImage=makeSmallerImage(selectedImage,300);


        ByteArrayOutputStream outputStream= new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray=outputStream.toByteArray();

        try {
            database=this.openOrCreateDatabase("Memories",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS memories(id INTEGER PRIMARY KEY,memoryname VARCHAR,date VARCHAR,image BLOB)");// ımage'in kaydı BLOB'tur.


            String sQLstring= "INSERT INTO memories(memoryname,date,image)VALUES(?,?,?,?)";
            SQLiteStatement sqLiteStatement= database.compileStatement(sQLstring);
            sqLiteStatement.bindString(1,nameText);
            sqLiteStatement.bindString(2,dateText);
            sqLiteStatement.bindBlob(3,byteArray);

            sqLiteStatement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }
        Intent intent=new Intent(infoActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }

    public  Bitmap makeSmallerImage(Bitmap image,int maxSize){
        int width=image.getWidth();
        int height=image.getHeight();

        float bitMapRatio= (float) width/ (float) height;
        if (bitMapRatio>1){
            //landscape
            width=maxSize;
            height=(int) (width/ bitMapRatio);

        }else {
            //portrait
            height=maxSize;
            width=(int) (height*bitMapRatio);

        }

        return image.createScaledBitmap(image,width,height,true);
    }

    public void back(View view) {
    }
}