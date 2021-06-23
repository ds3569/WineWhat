package com.kpu.winewhat;

import android.app.Activity;
import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.TextView;

public class PopupActivity extends Activity {

    private TextView txtText;
    private RatingBar rating;

    private String wine_ID;
    private String wine_Name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_activity);
        //UI 객체생성
        txtText = findViewById(R.id.txtText);
        rating = findViewById(R.id.rating);

        //데이터 가져오기
        Intent intent = getIntent();
        wine_ID = intent.getStringExtra("wine_ID");
        wine_Name = intent.getStringExtra("wine_Name");

        txtText.setText(wine_Name);

    }

    //확인 버튼 클릭
    public void mOnCloseOK(View v){
        //데이터 전달하기
        int point = (int) rating.getRating();
        String result = Integer.toString(point);
        Intent intent = new Intent(getApplicationContext(), chatOperator.class);
        intent.putExtra("wine_ID", wine_ID);
        intent.putExtra("point", result);
        chatOperator.orderCode = 5;
        startService(intent);

        //액티비티(팝업) 닫기
        finish();
    }
    public void mOnCloseNO(View v){
        //데이터 전달하기
        int point = 0;
        String result = Integer.toString(point);
        Intent intent = new Intent(getApplicationContext(), chatOperator.class);
        intent.putExtra("wine_ID", wine_ID);
        intent.putExtra("point", result);
        chatOperator.orderCode = 5;
        startService(intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}


