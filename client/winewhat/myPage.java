package com.kpu.winewhat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class myPage extends Activity {
    private Chart chart;
    private ImageView recoim;
    private ImageView btn_reco;

    private String reco_wine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mypage);

        Intent frommain = getIntent();


        chart = findViewById(R.id.chart);
        recoim = findViewById(R.id.recoim);
        btn_reco = findViewById(R.id.anotherreco);


        String graph_data = frommain.getStringExtra("message"); //서버로 부터 전송받은 그래프 데이터

        //사용자 그래프, 유사한 사용자 평균 그래프 분리
        String[] favor_data = graph_data.split(":");
        String[] user_favor = favor_data[0].split(",");
        String[] aver_favor = favor_data[1].split(",");

        //String[] -> String
        String userFavor = TextUtils.join(",", user_favor);
        String averFavor = TextUtils.join(",", aver_favor);


        //그래프 작성
        drowgraph(userFavor, "RED", averFavor, "BLUE");


        //기존 추천 와인 리스트에서 렘덤 와인 선택
        Random random = new Random();
        String str_recommend_list = chatOperator.recommendList; //로그인 시에 생성된 추천와인 리스트
        String[] recommend_list = str_recommend_list.split(",");

        int ran_num = random.nextInt(recommend_list.length);
        String tmp = "id_" + recommend_list[ran_num];
        reco_wine = recommend_list[ran_num];

        //추천 와인 이미지
        int lid = this.getResources().getIdentifier(tmp, "drawable", this.getPackageName());
        recoim.setImageResource(lid);

        //추천 와인 정보
        recoim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent msg = new Intent(getApplicationContext(), chatOperator.class);
                msg.putExtra("info", reco_wine);
                chatOperator.orderCode = 6; //서버에 추천 와인 정보 요청
                startService(msg);
            }

        });

        //추천 와인 리스트 세로고침
        btn_reco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent msg = new Intent(getApplicationContext(), chatOperator.class);
                msg.putExtra("msg", "Recommend");
                chatOperator.orderCode = 4;
                startService(msg);
            }
        });

    }

    //새 추천 와인 리스트에서 렘덤 와인 선택
    @Override
    protected void onNewIntent(Intent intent){
        if (chatOperator.orderCode == 4){
            Random random = new Random();
            String str_recommend_list = intent.getStringExtra("message");
            String[] recommend_list = str_recommend_list.split(",");

            int ran_num = random.nextInt(recommend_list.length);
            String tmp = "id_" + recommend_list[ran_num];
            reco_wine = recommend_list[ran_num];

            //추천 와인 이미지
            int lid = this.getResources().getIdentifier(tmp, "drawable", this.getPackageName());
            recoim.setImageResource(lid);


        }

        super.onNewIntent(intent);
    }

    //그래프 데이터 셋 작성
    private RadarDataSet dataValue(String message, String color){

        //그래프 데이터 설정
        List<String> favorList = new ArrayList<String>(Arrays.asList(message.split(",")));
        Float sweet = Float.parseFloat(favorList.get(0).toString());
        Float acidic = Float.parseFloat(favorList.get(1).toString());
        Float body = Float.parseFloat(favorList.get(2).toString());
        Float tannin = Float.parseFloat((favorList.get(3).toString()));


        //엔트리 등록
        List<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry(sweet));
        entries.add(new RadarEntry(acidic));
        entries.add(new RadarEntry(body));
        entries.add(new RadarEntry(tannin));


        if (color.equals("RED")) { //사용자 데이터
            RadarDataSet dataSet = new RadarDataSet(entries, "User");
            dataSet.setColor(Color.RED);
            dataSet.setFormSize(15);
            return dataSet;
        }
        else { //유사한 사용자 평균 데이터
            RadarDataSet dataSet = new RadarDataSet(entries, "Average");
            dataSet.setColor(Color.BLUE);
            dataSet.setFormSize(15);
            return dataSet;
        }

    }

    //그래프 작성
    private void drowgraph (String userdata, String usercolor, String averdata, String avercolor){

        // 그래프 데이터 셋
        RadarDataSet userdataSet = dataValue(userdata, usercolor);
        RadarDataSet averdataSet = dataValue(averdata, avercolor);

        //그래프 항목 지정
        String[] status = {"Sweet", "Acidic", "Body", "Tannin"};



        //그래프 작성
        RadarData data = new RadarData();
        data.addDataSet(userdataSet);
        data.addDataSet(averdataSet);
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(20);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(status));
        chart.setData(data);
        Legend l = chart.getLegend();
        l.setTextSize(20);


    }



}
