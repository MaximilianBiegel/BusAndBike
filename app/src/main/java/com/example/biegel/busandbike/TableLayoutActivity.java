package com.example.biegel.busandbike;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.Vector;

public class TableLayoutActivity extends AppCompatActivity {
    private Vector<BusStop> busstops = new Vector<BusStop>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_layout);

        Intent intent = getIntent();
        String[] stops = intent.getStringArrayExtra("busStops");
        for (int i = 0; i < stops.length; i++) {
            Log.i("Table", stops[i]);
            String split = "name=";
            String[] x = stops[i].split(split);
            String[] y = x[1].split(", latitude=");
            String name = y[0];
            String[] v = y[1].split(", longitude=");
            String latitude = v[0];
            String[] w = v[1].split(", counter=");
            String longitude = w[0];
            Log.i("flick", name + " " + " " + latitude + " " + longitude + " " + w[1].length());
            String z = w[1].replace("}", "");
            String counter = z;
            double lat = Double.parseDouble(latitude);
            double lon = Double.parseDouble(latitude);
            int count = Integer.parseInt(counter);
            BusStop bs = new BusStop(name, lat, lon);
            bs.setCounter(count);
            busstops.addElement(bs);


        }


        TextView tw1 = (TextView) findViewById(R.id.tw1);
        TextView tw2 = (TextView) findViewById(R.id.tw2);
        TextView tw3 = (TextView) findViewById(R.id.tw3);
        TextView tw4 = (TextView) findViewById(R.id.tw4);
        TextView tw5 = (TextView) findViewById(R.id.tw5);
        TextView tw6 = (TextView) findViewById(R.id.tw6);
        TextView tw7 = (TextView) findViewById(R.id.tw7);
        TextView tw8 = (TextView) findViewById(R.id.tw8);
        TextView tw9 = (TextView) findViewById(R.id.tw9);
        TextView tw10 = (TextView) findViewById(R.id.tw10);

        TextView ctw1 = (TextView) findViewById(R.id.twc1);
        TextView ctw2 = (TextView) findViewById(R.id.twc2);
        TextView ctw3 = (TextView) findViewById(R.id.twc3);
        TextView ctw4 = (TextView) findViewById(R.id.twc4);
        TextView ctw5 = (TextView) findViewById(R.id.twc5);
        TextView ctw6 = (TextView) findViewById(R.id.twc6);
        TextView ctw7 = (TextView) findViewById(R.id.twc7);
        TextView ctw8 = (TextView) findViewById(R.id.twc8);
        TextView ctw9 = (TextView) findViewById(R.id.twc9);
        TextView ctw10 = (TextView) findViewById(R.id.twc10);

        BusStop[] arr = new BusStop[busstops.size()];
        Log.i("size", ""+arr.length);
        for (int x = 0; x < arr.length; x++) {
            arr[x] = busstops.get(x);
        }
        for(int z=0; z<arr.length;z++){
            for(int y=0; y< arr.length; y++){
                BusStop temp;
                if (arr[z].getCounter() > arr[y].getCounter()){
                    temp = arr[z];
                    arr[z]= arr[y];
                    arr[y]=temp;

                }
            }
        }
        tw1.setText(arr[0].getName());
        tw2.setText(arr[1].getName());
        tw3.setText(arr[2].getName());
        tw4.setText(arr[3].getName());
        tw5.setText(arr[4].getName());
        tw6.setText(arr[5].getName());
        tw7.setText(arr[6].getName());
        tw8.setText(arr[7].getName());
        tw9.setText(arr[8].getName());
        tw10.setText(arr[9].getName());

        ctw1.setText(""+arr[0].getCounter());
        ctw2.setText(""+arr[1].getCounter());
        ctw3.setText(""+arr[2].getCounter());
        ctw4.setText(""+arr[3].getCounter());
        ctw5.setText(""+arr[4].getCounter());
        ctw6.setText(""+arr[5].getCounter());
        ctw7.setText(""+arr[6].getCounter());
        ctw8.setText(""+arr[7].getCounter());
        ctw9.setText(""+arr[8].getCounter());
        ctw10.setText(""+arr[9].getCounter());

    }

}