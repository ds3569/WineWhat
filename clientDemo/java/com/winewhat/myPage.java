package com.winewhat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class myPage extends Activity {
    private TextView user_IDmypage;
    private TextView mypage_nevigate;
    private Chart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);

        Intent frommain = getIntent();
        user_IDmypage = findViewById(R.id.userID_mypage);
        chart = findViewById(R.id.chart);
        mypage_nevigate = findViewById(R.id.mypage_nevigate);
        ///mypage_nevigate.setText("your tast graph by your reviews");
        user_IDmypage.setText(frommain.getExtras().getString("userID") + "'s MyPage");

        String favor = frommain.getExtras().getString("favorList");
        mypage_nevigate.setText("Favor Taste Graph, on user's Review");

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
