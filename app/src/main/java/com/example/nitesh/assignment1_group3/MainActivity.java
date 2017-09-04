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

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static GraphView graphView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("On cretae started....\n");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button btnRunObj = (Button)findViewById(R.id.btnRun);
        final TextView tex=(TextView)findViewById(R.id.editText);
        final Button btnStopObj = (Button)findViewById(R.id.btnStop);
        //btnRunObj.setOnClickListener(this);
        //btnStopObj.setOnClickListener(this);
        //btnStopObj.setEnabled(false);

        final ViewGroup graphLayout = (ViewGroup)findViewById(R.id.GraphLyout);
        final float arr[] = new float[10];

        final String VAxis[] = {"30","20","10","0"};
        final String XAxis[] = {"0","10","20","30"};
        MainActivity.graphView = new GraphView(MainActivity.this,arr,"Patient Health Monitor",XAxis,VAxis,true);
        graphView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        graphView.setBackgroundColor(Color.BLACK);

        graphLayout.addView(graphView);

        btnRunObj.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                System.out.println("\n\n\nInside on click...\n\n\n");
                Intent intent = getIntent();
                //finish();
                startActivity(intent);
                setContentView(R.layout.activity_main);
                final ViewGroup graphLayout = (ViewGroup)findViewById(R.id.GraphLyout);
                Random rd = new Random();
                for(int i=0;i<10;i++) {
                    arr[i] = rd.nextFloat();
                }
                MainActivity.graphView = new GraphView(MainActivity.this,arr,"Patient Health Monitor",XAxis,VAxis,true);
                graphView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                graphView.setBackgroundColor(Color.BLACK);
                graphLayout.addView(graphView);
                btnStopObj.setOnClickListener(this);
                //btnStopObj.getKeyListener();
                //btnStopObj.setEnabled(true);
                //btnRunObj.setEnabled(false);
            }
        });

        btnStopObj.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                System.out.println("Inside Stop Click");
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                setContentView(R.layout.activity_main);
                MainActivity.graphView = new GraphView(MainActivity.this,arr,"Patient Health Monitor",XAxis,VAxis,true);
                MainActivity.graphView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                MainActivity.graphView.setBackgroundColor(Color.BLACK);
                graphLayout.addView(graphView);
                btnRunObj.setOnClickListener(this);
            }
        });
    }
}
