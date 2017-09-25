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

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CropImage extends AppCompatActivity implements View.OnClickListener {

    private CropImageView mCropImageView;
    private Button save_crop, discard_crop, crop_image;
    Bitmap cropped;
    String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);


        initview();
        clickListener();
        getImageFromIntent();

    }

    private void initview() {
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        save_crop = (Button) findViewById(R.id.save_crop);
        discard_crop = (Button) findViewById(R.id.discard_crop);
        crop_image = (Button) findViewById(R.id.crop_image);
    }

    private void clickListener() {
        save_crop.setOnClickListener(this);
        discard_crop.setOnClickListener(this);
        crop_image.setOnClickListener(this);
    }


    private void getImageFromIntent() {
        String imagePath = getIntent().getStringExtra("image");
        mCropImageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_crop:

                Intent intent = new Intent(CropImage.this, MainActivity.class);
                Log.e("cropped", cropped + "");
                intent.putExtra("croppedImage", cropped);
                getOutputMediaFile();
                //savebitmap(getString(R.string.app_name), cropped);
                startActivity(intent);

                break;
            case R.id.discard_crop:
                break;
            case R.id.crop_image:
                onCropImageClick();
                break;

        }
    }

    private void onCropImageClick() {
        cropped = mCropImageView.getCroppedImage();
        if (cropped != null)
            mCropImageView.setImageBitmap(cropped);


    }

/*    private void savebitmap(String filename, Bitmap bitmap) {

        try {
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/PhysicsSketchpad";
            File dir = new File(file_path);
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(dir, "sketchpad" + ".png");
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception exp) {
            exp.printStackTrace();
        }


    }*/

    private File getOutputMediaFile() {
         File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                 + "/Android/data/"            + getApplicationContext().getPackageName()            + "/Files");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;        }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
         mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;}
    }
