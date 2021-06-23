package com.kpu.winewhat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

import com.kpu.winewhat.chatOperator;



public class mainPage extends Activity {
    final private static String TAG = "winewhat";

    private ImageView logo;
    private ImageView btn_photo;
    private ImageView recoim;
    private ImageView btn_reco;
    final static int TAKE_PICTURE = 1;
    String mCurrentPhotoPath;
    final static int REQUEST_TAKE_PHOTO = 1;

    private String SERVER_IP = "182.209.238.112";
    //private String SERVER_IP = "192.168.219.106";

    private String reco_wine;
    private String reco_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);
        logo = findViewById(R.id.logo);
        btn_photo = findViewById(R.id.capture);
        recoim = findViewById(R.id.recoim);
        btn_reco = findViewById(R.id.anotherreco);

        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.capture:
                        dispatchTakePictureIntent();
                        break;
                }

            }
        });

        btn_reco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent msg = new Intent(getApplicationContext(), chatOperator.class);
                msg.putExtra("msg", "Recommend");
                chatOperator.orderCode = 4;
                startService(msg);
            }
        });
        recoim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), wineInfo.class);
                intent2.putExtra("message", reco_info);
                startActivity(intent2);
            }

        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ester = new Intent(getApplicationContext(), chatOperator.class);
                ester.putExtra("msg", "Graph");
                chatOperator.orderCode = 7;
                startService(ester);
            }
        });

        Intent msg = new Intent(getApplicationContext(), chatOperator.class);
        msg.putExtra("msg", "Recommend");
        chatOperator.orderCode = 4;
        startService(msg);

    }

    @Override
    protected void onNewIntent(Intent intent){

        if (chatOperator.orderCode == 4){
            Random random = new Random();
            String str_recommend_list = intent.getStringExtra("message");
            String[] recommend_list = str_recommend_list.split(",");

            int ran_num = random.nextInt(recommend_list.length);
            String tmp = "id_" + recommend_list[ran_num];
            reco_wine = recommend_list[ran_num];

            int lid = this.getResources().getIdentifier(tmp, "drawable", this.getPackageName());
            recoim.setImageResource(lid);

            Intent msg = new Intent(getApplicationContext(), chatOperator.class);
            msg.putExtra("info", reco_wine);
            chatOperator.orderCode = 6;
            startService(msg);

        }
        if (chatOperator.orderCode == 6){
            reco_info = intent.getStringExtra("message");
        }
        super.onNewIntent(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    } // 카메라로 촬영한 영상을 가져오는 부분

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {
                    if (resultCode == RESULT_OK) {
                        File file = new File(mCurrentPhotoPath);
                        Bitmap bitmap;
                        if (Build.VERSION.SDK_INT >= 29) {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file));
                            try {
                                bitmap = ImageDecoder.decodeBitmap(source);
                                if (bitmap != null) {

                                    try {
                                        Intent msg = new Intent(getApplicationContext(), chatOperator.class);
                                        msg.putExtra("msg", "imageProcess");
                                        chatOperator.orderCode = 3;
                                        new Thread() {
                                            public void run() {
                                                imageSend(3100);
                                            }
                                        }.start();
                                        Toast myToast = Toast.makeText(getApplicationContext(), "이미지를 처리하는 중입니다. 잠시만 기다려 주세요.", Toast.LENGTH_LONG);
                                        myToast.show();
                                        startService(msg);
                                    }
                                    catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), "load error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    break;
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    // 사진 촬영 후 썸네일만 띄워줌. 이미지를 파일로 저장해야 함
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    // 카메라 인텐트 실행하는 부분
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.winewhat.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }




    private void imageSend(int port) {
        try {

            //서버 접속
            Socket imsocket = new Socket(SERVER_IP, port);

            File im = new File(mCurrentPhotoPath);
            FileInputStream fis = new FileInputStream(im);
            byte[] ba = new byte[fis.available()];
            fis.read(ba);

            ObjectOutputStream oos = new ObjectOutputStream(imsocket.getOutputStream());
            oos.writeObject(ba);
            oos.flush();


            oos.close();
            imsocket.close();


        } catch (Exception e) {
            e.printStackTrace();

        }

    }
}







