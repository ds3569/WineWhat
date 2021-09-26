package com.kpu.winewhat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
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
    private static ProgressDialog customProgressDialog;

    private ImageView btn_photo;

    private ImageView btn_mypage;
    private ImageView btn_reviewList;
    final static int TAKE_PICTURE = 1;
    String mCurrentPhotoPath;
    final static int REQUEST_TAKE_PHOTO = 1;

    //서버 ip주소 설정
    private String SERVER_IP = "Server IP Address";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);
        btn_photo = findViewById(R.id.capture);
        btn_mypage = findViewById(R.id.btn_mypage);
        btn_reviewList = findViewById(R.id.btn_reviewList);

        //로딩창 객체 생성
        customProgressDialog = new ProgressDialog(this);
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        //카메라 동작
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


        //마이 페이지 이동
        btn_mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mypage = new Intent(getApplicationContext(), chatOperator.class);
                mypage.putExtra("msg", "Graph");
                chatOperator.orderCode = 7;
                startService(mypage); //서버에 그래프 데이터 요첟
            }
        });

        //마이 와이너리 이동
        btn_reviewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reviewlist = new Intent(getApplicationContext(), chatOperator.class);
                reviewlist.putExtra("msg", "List");
                chatOperator.orderCode = 8;
                startService(reviewlist); //서버에 리뷰 데이터 요청

            }
        });

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    } // 카메라로 촬영한 영상을 가져오기

    //촬영 후 서버에 이미지 전송 요청
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
                                bitmap = ImageDecoder.decodeBitmap(source); //촬영 이미지 불러오기
                                if (bitmap != null) {

                                    try {
                                        Intent msg = new Intent(getApplicationContext(), chatOperator.class);
                                        msg.putExtra("msg", "imageProcess");
                                        chatOperator.orderCode = 3; //서버에 이미지 전송 요청
                                        new Thread() {
                                            public void run() {
                                                imageSend(3100); //이미지 전송 메소드 시작
                                            }
                                        }.start();
                                        customProgressDialog.show(); //로딩창 출력
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

    // 촬영 이미지 저장
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    // 카메라 인텐트 실행
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


    //이미지 전송 메소드
    private void imageSend(int port) {
        try {

            //서버 접속
            Socket imsocket = new Socket(SERVER_IP, port);

            File im = new File(mCurrentPhotoPath);
            FileInputStream fis = new FileInputStream(im);
            byte[] ba = new byte[fis.available()];
            fis.read(ba);

            //이미지 전송
            ObjectOutputStream oos = new ObjectOutputStream(imsocket.getOutputStream());
            oos.writeObject(ba);
            oos.flush();


            oos.close();
            imsocket.close();


        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    //로딩창 삭제
    public static void loadingDel(){
        customProgressDialog.dismiss();
    }

}







