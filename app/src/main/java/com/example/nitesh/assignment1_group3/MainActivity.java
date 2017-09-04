package com.example.nitesh.assignment1_group3;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static GraphView graphView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creating objects of all the UI elements which are required
        final Button btnRunObj = (Button)findViewById(R.id.btnRun);
        final Button btnStopObj = (Button)findViewById(R.id.btnStop);

        final TextView patName =(TextView)findViewById(R.id.txtPatName);
        final TextView patId = (TextView) findViewById(R.id.txtPatID);
        final TextView patAge = (TextView) findViewById(R.id.txtAge);

        final ViewGroup graphLayout = (ViewGroup)findViewById(R.id.GraphLyout);

        final float arr[] = new float[10];

        final String VAxis[] = {"1.00","0.75","0.5","0.25","0"};
        final String XAxis[] = {"0","0.25","0.5","0.75","1.00"};
        MainActivity.graphView = new GraphView(MainActivity.this,arr,"Patient Health Monitor",XAxis,VAxis,true);
        graphView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        graphView.setBackgroundColor(Color.BLACK);

        btnStopObj.setEnabled(false);
        graphLayout.addView(graphView);

        btnRunObj.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                System.out.println(patName.getText());
                if(patName.getText().length() == 0  || patId.getText().length() == 0 || patAge.getText().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "Enter All Details", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    Random rd = new Random();
                    for (int i = 0; i < 10; i++) {
                        arr[i] = rd.nextFloat();
                    }
                    MainActivity.graphView = new GraphView(MainActivity.this, arr, "Patient Health Monitor", XAxis, VAxis, true);
                    graphView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    graphView.setBackgroundColor(Color.BLACK);
                    graphLayout.addView(graphView);
                    btnRunObj.setEnabled(false);
                    btnStopObj.setEnabled(true);
                }
            }
        });

        btnStopObj.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                graphLayout.removeView(graphView);
                btnRunObj.setEnabled(true);
                btnStopObj.setEnabled(false);
            }
        });
    }
}
