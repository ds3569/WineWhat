package com.kpu.winewhat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class wineInfo extends Activity {
    private ImageView wineImage;
    private TextView wineName;
    private TextView producer;
    private TextView contry;
    private TextView variety;
    private TextView food;
    private  TextView kind;
    private TextView sweet;
    private TextView body;
    private TextView tannin;
    private TextView acidic;


    private String wine_ID;
    private String wine_Name;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wineinfo);

        wineImage = findViewById(R.id.wineImage);
        wineName = findViewById(R.id.wineName);
        producer = findViewById(R.id.producer);
        contry = findViewById(R.id.contry);
        variety = findViewById(R.id.variety);
        food = findViewById(R.id.food);
        kind = findViewById(R.id.kind);
        sweet = findViewById(R.id.sweet);
        body = findViewById(R.id.body);
        tannin = findViewById(R.id.tannin);
        acidic = findViewById(R.id.acidic);



        Intent data = getIntent();
        String str_wine_data = data.getStringExtra("message"); //서버로 부터 전송받은 와인 데이터
        String[] wine_data = str_wine_data.split(":"); //String -> Stirng[]

        //drawable 이미지 호출 및 imageview 변경
        wine_ID = wine_data[0];
        wine_ID.replace("(", "");
        String tmp = "id_" + wine_ID;

        wine_Name = wine_data[1];


        int lid = this.getResources().getIdentifier(tmp, "drawable", this.getPackageName());
        wineImage.setImageResource(lid);

        //화면에 와인 정보 출력
        wineName.setText("Name : " + wine_data[1]);
        producer.setText("Producer : " + wine_data[5]);
        contry.setText("Conrty : " + wine_data[3]);
        variety.setText("Variety : " + wine_data[7]);
        food.setText("Food : " + wine_data[16]);
        kind.setText("Kind : " + wine_data[8]);
        sweet.setText("Sweet : " + stars(Integer.parseInt(wine_data[11])));
        body.setText("Body : " + stars(Integer.parseInt(wine_data[13])));
        tannin.setText("Tannin :" + stars(Integer.parseInt(wine_data[14])));
        acidic.setText("Acidic : " + stars(Integer.parseInt(wine_data[12])));

        mainPage.loadingDel();


    }

    //와인 특성 점수 출력
    public String stars (int num){
        String stars = "";
        for (int i = 0; i < num; i++){
            stars = stars + "★";
        }
        for (int i = 0; i < (5-num); i++){
            stars = stars + "☆";
        }
        return stars;
    }

    //창이 없어질 때 리뷰 팝업 요청
    @Override
    protected void onDestroy() {
        if (chatOperator.orderCode == 3 || chatOperator.orderCode == 9){
            Intent data = new Intent(this, PopupActivity.class);
            data.putExtra("wine_ID", wine_ID);
            data.putExtra("wine_Name", wine_Name);
            startActivity(data);
        }

        super.onDestroy();
    }
}