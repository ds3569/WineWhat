package com.winewhat;

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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class mainPage extends Activity {
    final private static String TAG = "winewhat";
    private Button btn_photo;
    private ImageView iv_photo;
    private TextView userID_main;
    private TextView wineResult;
    private Button btn_Recommend;
    private TextView recommendResult;
    private Button btn_Mypage;
    final static int TAKE_PICTURE = 1;
    String mCurrentPhotoPath;
    final static int REQUEST_TAKE_PHOTO = 1;

    private int orderCode = 0;
    private String favorList;

    private Socket client;
    private PrintWriter printwriter;
    private BufferedReader bufferedReader;

    private String SERVER_IP = "182.209.238.112";
    //private String SERVER_IP = "192.168.219.104";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);
        iv_photo = findViewById(R.id.imageView);
        btn_photo = findViewById(R.id.catuer);
        userID_main = findViewById(R.id.userID_main);
        wineResult = findViewById(R.id.wineResult);
        btn_Recommend = findViewById(R.id.recommend);
        recommendResult = findViewById(R.id.reommendResult);
        btn_Mypage = findViewById(R.id.btn_mypage);


        Intent userID = getIntent();
        userID_main.setText(userID.getExtras().getString("userID") + "'s MainPage");

        mainPage.ChatOperator chatOperator = new mainPage.ChatOperator();
        chatOperator.execute();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(mainPage.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }


    } // 권한 요청

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
                                    iv_photo.setImageBitmap(bitmap);
                                    try{
                                        Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
                                        wineResult.setText("Now on Processing");
                                        Toast.makeText(getApplicationContext(), "load OK", Toast.LENGTH_SHORT).show();
                                        final Sender messageSender = new Sender(); // Initialize chat sender AsyncTask.
                                        String text = "imageProcess" + BitmapToBase64(bm);

                                        orderCode = 1;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                            messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, text);
                                        } else {
                                            messageSender.execute(text);
                                        }

                                    }
                                    catch(Exception e){
                                        Toast.makeText(getApplicationContext(), "load error", Toast.LENGTH_SHORT).show();}
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                                if (bitmap != null) {
                                    iv_photo.setImageBitmap(bitmap);
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

    public static String BitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;
    }


    private class ChatOperator extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                client = new Socket(SERVER_IP, 9999); // Creating the server socket.
                if (client != null) {
                    //자동 flushing 기능이 있는 PrintWriter 객체를 생성한다.
                    //client.getOutputStream() 서버에 출력하기 위한 스트림을 얻는다.
                    printwriter = new PrintWriter(client.getOutputStream(), true);
                    InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
                    //입력 스트림 inputStreamReader에 대해 기본 크기의 버퍼를 갖는 객체를 생성한다.
                    bufferedReader = new BufferedReader(inputStreamReader);
                    wineResult.setText("Connected");
                } else {
                    wineResult.setText("Server has not bean started on port 9999.");
                }

            } catch (UnknownHostException e) {
                System.out.println("Faild to connect server " + SERVER_IP);
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Faild to connect server " + SERVER_IP);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            btn_Recommend.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent userID = getIntent();
                    final mainPage.Sender messageSender = new mainPage.Sender(); // Initialize chat sender AsyncTask.
                    String text = userID.getExtras().getString("userID") + "Recommend";
                    orderCode = 2;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, text);
                    } else {
                        messageSender.execute(text);
                    }
                }
            });
            btn_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.catuer:
                            dispatchTakePictureIntent();
                            break;
                    }

                }
            });
            btn_Mypage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent userID = getIntent();
                    final mainPage.Sender messageSender = new mainPage.Sender(); // Initialize chat sender AsyncTask.
                    String text = userID.getExtras().getString("userID") + "Graph";
                    orderCode = 3;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, text);
                    } else {
                        messageSender.execute(text);
                    }

                }
            });
            if ( client != null) {
                mainPage.Receiver receiver = new mainPage.Receiver(); // Initialize chat receiver AsyncTask.
                receiver.execute();
            }
        }
    }
    private class Receiver extends AsyncTask<Void, Void, Void> {
        private String message;

        @Override
        protected Void doInBackground(Void... params) {
            while (true) {
                try {
                    //스트림으로부터 읽어올수 있으면 true를 반환한다.
                    if (bufferedReader.ready()) {
                        //'\n', '\r'을 만날 때까지 읽어온다.(한줄을 읽어온다.)
                        message = bufferedReader.readLine();
                        publishProgress(null);



                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) { }
            }
        }

        //publishProgress(null)에 의해서 호출된다. '
        //서버에서 전달받은 문자열을 읽어서 화면에 출력해준다.

        @Override
        protected void onProgressUpdate(Void... values) {
            switch (orderCode){
                case 1:
                    wineResult.setText(message);
                    break;
                case 2:
                    recommendResult.setText("Recommend Wine ID : " + message);
                    break;
                case 3:
                    Intent userID = getIntent();
                    Intent mypageintent = new Intent(getApplicationContext(), myPage.class);
                    mypageintent.putExtra("userID", userID.getExtras().getString("userID"));
                    mypageintent.putExtra("favorList", message);
                    startActivity(mypageintent);

                    break;
            }
        }
    }

    /**
     * This AsyncTask sends the chat message through the output stream.
     */

    private class Sender extends AsyncTask<String, String, Void> {
        private String message;

        @Override
        protected Void doInBackground(String... params) {
            message = params[0];
            //문자열을 스트림에 기록한다.
            printwriter.write(message + "\n");
            //스트림을 플러쉬한다.
            printwriter.flush();
            return null;
        }
    }
}





