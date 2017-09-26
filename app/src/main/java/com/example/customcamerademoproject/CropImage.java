package com.example.customcamerademoproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.customcamerademoproject.utility.AppConstants;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CropImage extends AppCompatActivity implements View.OnClickListener {

    private CropImageView mCropImageView;
    private Button save_crop, discard_crop, crop_image;
    public String tag;
    private FileOutputStream fos = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);


        initview();
        clickListener();


        if (tag.equals("Gallery")) {
            getImageFromGallery();
        } else if (tag.equals("Camera")) {
            getImageFromCamera();
        }

    }
    //initialise view
    private void initview() {
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        save_crop = (Button) findViewById(R.id.save_crop);
        discard_crop = (Button) findViewById(R.id.discard_crop);
        crop_image = (Button) findViewById(R.id.crop_image);

        tag = getIntent().getStringExtra("tag");

    }

    //method for clickListener
    private void clickListener() {
        save_crop.setOnClickListener(this);
        discard_crop.setOnClickListener(this);
        crop_image.setOnClickListener(this);
    }

//method for getting image from gallery to crop
    private void getImageFromGallery() {
        String imagePath = getIntent().getStringExtra("image");
        mCropImageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
    }
    //method for getting image from camera to crop
    private void getImageFromCamera() {
        Log.e("camera image", CameraActivity.bitmap + "");
        mCropImageView.setImageBitmap(CameraActivity.bitmap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_crop:
                Intent intent = new Intent(CropImage.this, MainActivity.class);
                Log.e("AppConstants.cropped", AppConstants.cropped + "");
                startActivity(intent);
                finish();
                break;
            case R.id.discard_crop:
                startActivity(new Intent(CropImage.this, MainActivity.class));
                break;
            case R.id.crop_image:
                onCropImageClick();
                saveToInternalStorage(AppConstants.cropped);

                break;

        }
    }

   //method to crop image
    private void onCropImageClick() {
        AppConstants.cropped = mCropImageView.getCroppedImage();
        if (AppConstants.cropped != null)
            mCropImageView.setImageBitmap(AppConstants.cropped);


    }
    //method to save cropped image to storage
    private void saveToInternalStorage(final Bitmap bitmapImage) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                File directory = null;
                try {
                    File myFilename = new File(Environment.getExternalStorageDirectory().toString() + "/cropped images");
                    if (!myFilename.exists()) {
                        myFilename.mkdirs();
                    }            // Create imageDir
                    File mypath = new File(myFilename, "profile.jpg");
                    fos = new FileOutputStream(mypath);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
