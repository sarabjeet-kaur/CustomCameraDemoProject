package com.example.customcamerademoproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {
    private Camera mCamera;
    private static String TAG="CameraActivity";
    public static  Bitmap bitmap;
    private boolean isFilterOpen=false;
    private ImageView filterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        // Add a listener to the Capture button
        ImageView captureButton = (ImageView) findViewById(R.id.button_capture);
        if (captureButton != null) {
            captureButton.setOnClickListener(this);
        }
        // Add listener to filter button
        filterButton = (ImageView) findViewById(R.id.filterButton);
        if (filterButton != null) {
            filterButton.setOnClickListener(this);
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public  Camera getCameraInstance(){
        Camera c = null;
        try {
            mCamera = Camera.open();
        }
        catch (Exception e){
        }
        return mCamera;
    }

//camera call back
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Log.e("bitmap from camera",bitmap+"");
            Intent intent=new Intent(CameraActivity.this,CropImage.class);
            intent.putExtra("tag","Camera");
            startActivity(intent);
            finish();

        }
    };


    @Override
    protected void onResume() {
        Log.e("on","resume");
        super.onResume();
        mCamera = getCameraInstance();
        if(mCamera!=null) {
            final CameraPreview cameraPreview = new CameraPreview(CameraActivity.this, mCamera);
            final LinearLayout preview=(LinearLayout)findViewById(R.id.camera_preview);
            if (preview != null) {

                CameraActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        preview.addView(cameraPreview);

                    }
                });

            }
        }else {
            Toast.makeText(CameraActivity.this, R.string.camera_error, Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.filterButton:
                if (!isFilterOpen){
                    findViewById(R.id.record_filter_layout).setVisibility(View.VISIBLE);
                    filterButton.setImageResource(R.drawable.filter_on);
                    isFilterOpen=true;
                }
                else {
                    findViewById(R.id.record_filter_layout).setVisibility(View.GONE);
                    filterButton.setImageResource(R.drawable.filter_off);
                    isFilterOpen=false;
                }
                break;
            case R.id.button_capture:
                mCamera.takePicture(null,null,mPicture);

                break;

        }
    }
    //method to set filters
    public void colorEffectFilter(View v){
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            switch (v.getId()) {
                case R.id.effectNone:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectAqua:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_AQUA);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectMono:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectNegative:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_NEGATIVE);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectPosterize:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_POSTERIZE);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectSepia:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_SEPIA);
                    mCamera.setParameters(parameters);
                    break;

            }
        }catch (Exception ex){
            Log.d(TAG,ex.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("camera activity","on pause");
        mCamera.release();
    }



    @Override
    public void onBackPressed() {
        if (isFilterOpen){
            findViewById(R.id.record_filter_layout).setVisibility(View.GONE);
            filterButton.setImageResource(R.drawable.filter_off);
            isFilterOpen=false;
        }
        else {
          super.onBackPressed();
        }
    }

}
