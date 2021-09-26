package com.kpu.winewhat;

import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.kpu.winewhat.chatOperator;


public class login extends AppCompatActivity {


    private ImageView btn_login;
    private  ImageView btn_regi;
    private EditText userid;
    private EditText userPW;

    private String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        userid = (EditText) findViewById(R.id.userID);
        userPW = (EditText) findViewById(R.id.userPassward);
        btn_login = (ImageView) findViewById(R.id.login);
        btn_regi = findViewById(R.id.regi);

        //권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(com.kpu.winewhat.login.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        //서버 접속
        if(chatOperator.orderCode == 0){
            Intent service = new Intent(getApplicationContext(), chatOperator.class);
            startService(service);
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = userid.getText().toString();
                String pw = userPW.getText().toString();

                user_id = id;

                //아이디 및 비밀번호 미입력 제어
                if (id.equals("") || pw.equals("")) {
                    Toast myToast = Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호를 확인하세요.", Toast.LENGTH_SHORT);
                    myToast.show();
                } else { //정상적으로 입력된 경우
                    String user_info = "Login" + id + ", " + pw;

                    Intent msg = new Intent(getApplicationContext(), chatOperator.class);
                    msg.putExtra("user_info", user_info);
                    chatOperator.orderCode = 1;
                    startService(msg); //서버에 입력된 아이디 및 비밀번호 전송
                }

            }
        });

        //회원가입 페이지 이동
        btn_regi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regi = new Intent(getApplicationContext(), register.class);
                startActivity(regi);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent){
        if (chatOperator.orderCode == 1){
            String result = intent.getStringExtra("message");
            if (result.equals("failed")){ //로그인 실패
                Toast myToast = Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호를 확인하세요.", Toast.LENGTH_SHORT);
                myToast.show();
            }
            else { //로그인 성공
                chatOperator.userID = user_id;
                chatOperator.recommendList = result; //해당 유저의 추천 와인 리스트
                Intent main = new Intent(getApplicationContext(), mainPage.class);
                startActivity(main);
            }
        }

        super.onNewIntent(intent);
    }





}




