package com.kpu.winewhat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import javax.crypto.spec.DHParameterSpec;

public class reviews extends Activity {

    private LinearLayout con;
    private LayoutInflater layoutInflater;
    private View view;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reviews);

        con = findViewById(R.id.con);
        layoutInflater = LayoutInflater.from(this);

        //서버에 리뷰 데이터 요청
        if (chatOperator.orderCode == 8){
            Intent intent = getIntent();
            String result = intent.getStringExtra("message");
            String[] reviews = result.split(",");
            layoutEdit(reviews);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (chatOperator.orderCode == 8){
            String result = intent.getStringExtra("message");

            Toast myToast = Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT);
            myToast.show();
            }


        super.onNewIntent(intent);
    }

    //리뷰 데이터 출력
    private void layoutEdit(String[] reviews){



        for (int i = 0; i < reviews.length; i++ ){
            if (reviews[i].equals("0") == false){
                view = layoutInflater.inflate(R.layout.reviewinfo, null, false);

                int lid = this.getResources().getIdentifier("id_" + Integer.toString(i+1), "drawable", this.getPackageName());
                ImageView imageView = view.findViewById(R.id.reviewim);
                imageView.setImageResource(lid);

                RatingBar ratingBar = view.findViewById(R.id.reviewrating);
                ratingBar.setRating(Integer.parseInt(reviews[i]));

                con.addView(view);

                int finalI = i + 1;
                view.setOnClickListener(new View.OnClickListener() {
                    //리뷰한 와인을 선택했을 경우
                    @Override
                    public void onClick(View v) {
                        Intent msg = new Intent(getApplicationContext(), chatOperator.class);
                        msg.putExtra("msg", Integer.toString(finalI));
                        chatOperator.orderCode = 9;
                        startService(msg); //와인 데이터 요청

                    }
                });
            }
        }

    }
}
