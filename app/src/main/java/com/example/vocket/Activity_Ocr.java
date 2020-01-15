package com.example.vocket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Activity_Ocr extends AppCompatActivity {
    FrameLayout previewFrame;
    ImageView scanButton, back;
    public static String postUrl;
    String str;  // 전체? 밑줄?
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS  = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED){
                //허가 안된 퍼미션 발견
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        postUrl= "http://165.194.17.140:8000/";
        str = getIntent().getStringExtra("str");
        Log.i("str",str);
        postUrl=postUrl+str;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        previewFrame = (FrameLayout) findViewById(R.id.cameraFrame);
        scanButton = (ImageView) findViewById(R.id.scanbutton);
        back = (ImageView) findViewById(R.id.back);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (!hasPermissions(PERMISSIONS))
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);//퍼미션 허가 안되어있다면 사용자에게 요청

        final CameraSurfaceView cameraView = new CameraSurfaceView(this);
        previewFrame.addView(cameraView);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.capture(new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                        String ex_storage =Environment.getExternalStorageDirectory().getAbsolutePath(); // Get Absolute Path in External Sdcard
                        String foler_name = "/vocket/";
                        String file_name = "ocr.jpg";
                        String string_path = ex_storage+foler_name;
                        File file_path;
                        try{
                            file_path = new File(string_path);
                            if(!file_path.isDirectory())
                                file_path.mkdirs();

                            FileOutputStream out = new FileOutputStream(string_path+file_name);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.close();

                            connectServer();

                        }catch(FileNotFoundException exception){
                            Log.e("FileNotFoundException", exception.getMessage());
                        }catch(IOException exception){
                            Log.e("IOException", exception.getMessage());
                        }

                        camera.startPreview();
                    }
                });
            }
        });
    }

    // 내부클래스 정의
    private class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera camera = null;

        public CameraSurfaceView(Context context) {
            super(context);
            mHolder = getHolder();
            mHolder.addCallback(this);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            camera = Camera.open();
            //자동 근접 초점
            Camera.Parameters params = camera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            camera.setParameters(params);

            setCameraOrientation();
            try {
                camera.setPreviewDisplay(mHolder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            camera.startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        public boolean capture(Camera.PictureCallback handler) {
            if (camera != null) {
                camera.takePicture(null, null, handler);
                return true;
            } else {
                return false;
            }
        }

        public void setCameraOrientation() {
            if (camera == null) {
                return;
            }

            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(0, info);

            WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            int rotation = manager.getDefaultDisplay().getRotation();

            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0: degrees = 0; break;
                case Surface.ROTATION_90: degrees = 90; break;
                case Surface.ROTATION_180: degrees = 180; break;
                case Surface.ROTATION_270: degrees = 270; break;
            }

            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }

            camera.setDisplayOrientation(result);
        }

    }
    public void connectServer(){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // Read BitMap by file path

        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/vocket/ocr.jpg", options);

        Matrix matrix = new Matrix();
        matrix.setRotate(90);

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2, true);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Log.i("ghkrdls",getOrientationOfImage("/sdcard/vocket/ocr.jpg")+"");

        RequestBody postBodyImage = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                .build();

        postRequest(postUrl, postBodyImage);

    }

    void postRequest(String postUrl, RequestBody postBody) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("잠시만 기다려 주세요...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
        progressDialog.show();

        OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Activity_Ocr.this, "ok", Toast.LENGTH_SHORT).show();
                        //"fail"
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String pass=response.body().string();
                            //Toast.makeText(Activity_Ocr.this, response.body().string(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                            Intent gotoAdd = new Intent(getApplicationContext(), Activity_OcrAdd.class);
                            gotoAdd.putExtra("uid",getIntent().getStringExtra("uid"));
                            startActivity(gotoAdd);
                            finish();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    public void Back(View view) {
        finish();
    }

    public int getOrientationOfImage(String filepath) {
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(filepath);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

        if (orientation != -1) {
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
            }
        }

        return 0;
    }
}
