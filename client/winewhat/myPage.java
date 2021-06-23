package com.kpu.winewhat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class myPage extends Activity {
    private Chart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mypage);

        Intent frommain = getIntent();
        chart = findViewById(R.id.chart);
        ///mypage_nevigate.setText("your tast graph by your reviews");

        String favor = frommain.getStringExtra("message");

        drowgraph(dataValue(favor));

    }
    private RadarDataSet dataValue(String message){

        List<String> favorList = new ArrayList<String>(Arrays.asList(message.split(",")));
        Float sweet = Float.parseFloat(favorList.get(0).toString());
        Float acidic = Float.parseFloat(favorList.get(1).toString());
        Float body = Float.parseFloat(favorList.get(2).toString());
        Float tannin = Float.parseFloat((favorList.get(3).toString()));


    List<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry(sweet));
        entries.add(new RadarEntry(acidic));
        entries.add(new RadarEntry(body));
        entries.add(new RadarEntry(tannin));

        RadarDataSet dataSet = new RadarDataSet(entries, "Favors");
        dataSet.setColor(Color.RED);
        return dataSet;
    }

    private void drowgraph (RadarDataSet dataSet){

        String[] status = {"Sweet", "Acidic", "Body", "Tannin"};



        RadarData data = new RadarData();
        data.addDataSet(dataSet);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(status));
        chart.setData(data);
    }



}
