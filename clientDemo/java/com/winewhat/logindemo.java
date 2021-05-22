package com.winewhat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class logindemo extends AppCompatActivity {



    private Button button;
    private EditText userid;



        @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        button = (Button) findViewById(R.id.login);
        userid = (EditText) findViewById(R.id.userID);

              button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = userid.getText().toString();
                ArrayList<String> idlist = new ArrayList<String>(Arrays.asList("userA", "userB", "userC", "userD", "userE"));
                if(id.equals("") || id == null){
                    Toast myToast = Toast.makeText(getApplicationContext(),"Check user ID", Toast.LENGTH_SHORT);
                    myToast.show();
                }
                else if (idlist.contains(id)){
                    Toast myToast = Toast.makeText(getApplicationContext(),"hellow " + id, Toast.LENGTH_SHORT);
                    myToast.show();
                    Intent intent = new Intent(getApplicationContext(), mainPage.class);
                    intent.putExtra("userID", id);
                    startActivity(intent);
                }
                else {
                    Toast myToast = Toast.makeText(getApplicationContext(),"Check user ID", Toast.LENGTH_SHORT);
                    myToast.show();
                }
            }


        });

    }

}
