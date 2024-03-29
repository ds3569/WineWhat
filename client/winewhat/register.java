package com.kpu.winewhat;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.kpu.winewhat.chatOperator;


public class register extends AppCompatActivity {


    private ImageView btn_next;
    private EditText userid;
    private EditText userPW;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        userid = (EditText) findViewById(R.id.userID_re);
        userPW = (EditText) findViewById(R.id.userPassward_re);
        btn_next = (ImageView) findViewById(R.id.next);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = userid.getText().toString();
                String pw = userPW.getText().toString();


                if (id.equals("") || pw.equals("")) { //아이디 및 비밀번호 미입력 제어
                    Toast myToast = Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호를 확인하세요.", Toast.LENGTH_SHORT);
                    myToast.show();
                } else { //정상적으로 입력된 경우
                    String user_info = "Register" + id + ", " + pw;

                    Intent msg = new Intent(getApplicationContext(), chatOperator.class);
                    msg.putExtra("user_info", user_info);
                    chatOperator.orderCode = 2;
                    startService(msg); //서버에 입력된 정보로 회원가입 요청
                }

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent){
        if (chatOperator.orderCode == 2){
            String result = intent.getStringExtra("message");
            if (result.equals("success")){ //회원가입 성공
                Intent login = new Intent(getApplicationContext(), login.class);
                Toast myToast = Toast.makeText(getApplicationContext(), "회원 가입이 완료되었습니다.", Toast.LENGTH_SHORT);
                myToast.show();
                startActivity(login);
            }
            else { //회원가입 실패
                Toast myToast = Toast.makeText(getApplicationContext(), "중복된 아이디입니다.", Toast.LENGTH_SHORT);
                myToast.show();
        }

        }
        super.onNewIntent(intent);
    }
}
