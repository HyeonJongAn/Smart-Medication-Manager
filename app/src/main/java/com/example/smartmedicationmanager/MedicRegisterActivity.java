package com.example.smartmedicationmanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.CameraController;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

public class MedicRegisterActivity extends AppCompatActivity {

    Bitmap image;
    Bitmap bitmap;
    Bitmap rotatedbitmap;

    private TessBaseAPI mTess;
    String datapath = "";

    PreviewView previewView;
    Button btnStartCamera;
    Button btnCaptureCamera;
    TextView textView;

    ProcessCameraProvider processCameraProvider;
    int lensFacing = CameraSelector.LENS_FACING_BACK;
    ImageCapture imageCapture;


    String OCRresult;

    Button btnRegister;
    private String imageFilepath;
    static final int REQUEST_IMAGE_CAPTURE = 672;

    String[] EdiCodearray;//EDI ?????? ????????? ???????????? ??????
    String[] medicList;//OpenAPI??? ????????? ????????? ?????? ????????? ???????????? ??????
    String data;

    UserData userData;
    com.example.smartmedicationmanager.MedicDBHelper myHelper;
    SQLiteDatabase sqlDB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_medicriegister);

        userData = (UserData) getApplicationContext();
        myHelper = new com.example.smartmedicationmanager.MedicDBHelper(this);
        previewView = (PreviewView) findViewById(R.id.previewView);
        btnStartCamera = (Button) findViewById(R.id.btnCameraStart);
        btnCaptureCamera = (Button) findViewById(R.id.btnPicture);
        textView = (TextView) findViewById(R.id.OCRTextResult);



        //?????? ?????? ?????? ??????
        datapath = getFilesDir() + "/tessaract/";

        //?????? ?????? ?????? ?????? ??????
        checkFile(new File(datapath + "tessdata/"), "eng");

        String lang = "eng";

        mTess = new TessBaseAPI();//TessBaseAPI ??????
        mTess.init(datapath, lang);//?????????

        //????????? ???????????? ??????????????? ???????????????, ?????????????????? ??????
        mTess.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, ".,!?@#$%&*()<>_-+=/:;'\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789");

        //????????? ????????? ?????? ?????? ??????
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        try {
            processCameraProvider = processCameraProvider.getInstance(this).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //????????? ????????? ??????
        btnStartCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MedicRegisterActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    previewView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    btnStartCamera.setVisibility(View.INVISIBLE);
                    btnStartCamera.setEnabled(false);
                    btnCaptureCamera.setVisibility(View.VISIBLE);
                    btnCaptureCamera.setEnabled(true);

                    bindPreview();
                    bindImageCapture();

                }
            }
        });

        //???????????? ????????? ?????? ?????? ??????
        btnCaptureCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageCapture.takePicture(ContextCompat.getMainExecutor(MedicRegisterActivity.this),
                        new ImageCapture.OnImageCapturedCallback() {
                            @Override
                            public void onCaptureSuccess(@NonNull ImageProxy image) {
                                @SuppressLint("UnsafeExperimentalUsageError")
                                Image mediaImage = image.getImage();
                                bitmap = ImageUtil.mediaImageToBitmap(mediaImage);



                                Log.d("MainActivity", Integer.toString(bitmap.getWidth())); //4128
                                Log.d("MainActivity", Integer.toString(bitmap.getHeight())); //3096

                                //imageView.setImageBitmap(bitmap);
                                rotatedbitmap = ImageUtil.rotateBitmap(bitmap, image.getImageInfo().getRotationDegrees());

                                Log.d("MainActivity", Integer.toString(rotatedbitmap.getWidth())); //3096
                                Log.d("MainActivity", Integer.toString(rotatedbitmap.getHeight())); //4128
                                Log.d("MainAtivity", Integer.toString(image.getImageInfo().getRotationDegrees()));
                                //90 //0, 90, 180, 90 //???????????? ????????? ???????????? ?????? ???????????? ??????????????? ??????

                                processCameraProvider.unbindAll();//????????? ????????? ??????
                                //pictureImage.setImageBitmap(rotatedBitmap);
                                previewView.setVisibility(View.INVISIBLE);
                                //pictureImage.setVisibility(View.VISIBLE);

                                super.onCaptureSuccess(image);

                                int height=rotatedbitmap.getHeight();
                                int width=rotatedbitmap.getWidth();

                                //AlertDialog??? ????????? ????????? ???????????? ???????????? ???????????? ?????? ?????? ???
                                Bitmap popupBitmap=Bitmap.createScaledBitmap(rotatedbitmap,1000,height/(width/1000),true);

                                //????????? ????????? ????????????
                                processCameraProvider.unbindAll();

                                ImageView capturedimage = new ImageView(MedicRegisterActivity.this);
                                capturedimage.setImageBitmap(popupBitmap);

                                //?????? ?????? ????????? AlertDialog??? ?????? ?????? ????????? ????????????
                                AlertDialog.Builder captureComplete=new AlertDialog.Builder(MedicRegisterActivity.this)
                                        .setTitle("?????? ??????")
                                        .setMessage("??? ????????? ????????????????")
                                        .setView(capturedimage)
                                        //????????? ????????? ?????? OCR ??????
                                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String OCRresult=null;
                                                mTess.setImage(rotatedbitmap);
                                                OCRresult=mTess.getUTF8Text();

                                                textView.setText(OCRresult);
                                            }
                                        })
                                        //???????????? ????????? ?????? bitmap??? ????????? ????????? ????????? ????????? ?????? ????????? ???????????? ????????????
                                        .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                bitmap=null;
                                                bindPreview();
                                                bindImageCapture();
                                                textView.setVisibility(View.INVISIBLE);
                                                //pictureImage.setVisibility(View.INVISIBLE);
                                                previewView.setVisibility(View.VISIBLE);
                                            }
                                        });

                                captureComplete.setCancelable(false);

                                captureComplete.create().show();
                            }
                        });
            }
        });

    }

    void bindPreview(){
        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();

        Preview preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        processCameraProvider.bindToLifecycle(this,cameraSelector,preview);
    }

    void bindImageCapture(){
        CameraSelector cameraSelector=new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();
        imageCapture=new ImageCapture.Builder()
                .build();

        processCameraProvider.bindToLifecycle(this,cameraSelector,imageCapture);
    }

    //??????????????? ?????? ?????? ??????
    private void copyFiles(String lang){
        try{
            String filepath=datapath+"/tessdata/"+lang+".traineddata";

            AssetManager assetManager=getAssets();

            InputStream inputStream=assetManager.open("tessdata/"+lang+".traineddata");
            OutputStream outputStream=new FileOutputStream(filepath);

            byte[] buffer=new byte[1024];
            int read;

            while ((read=inputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //??????????????? ????????? ?????? ??? ??????
    private void checkFile(File dir, String lang){
        if(!dir.exists()&&dir.mkdirs()){
            copyFiles(lang);
        }
        if(dir.exists()){
            String datafilepath=datapath+"/tessdata/"+lang+".traineddata";
            File datafile=new File(datafilepath);
            if(!datafile.exists()){
                copyFiles(lang);
            }
        }
    }


}
