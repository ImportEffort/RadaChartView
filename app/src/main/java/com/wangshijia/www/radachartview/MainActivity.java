package com.wangshijia.www.radachartview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

import com.wangshijia.www.radarchartview.RadarChartView;


public class MainActivity extends AppCompatActivity {

    private RadarChartView radarChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radarChartView = findViewById(R.id.radarCharView);
//        radarCharView.setDefaultShader(Color.parseColor("#ff8f4c"), Color.parseColor("#ff4c51"));
        radarChartView.addValues("市场", 60);
        radarChartView.addValues("题材", 60);
        radarChartView.addValues("财务", 60);
        radarChartView.addValues("主力", 60);
        radarChartView.addValues("技术", 60);
        radarChartView.addValues("技术1", 60);
        radarChartView.addValues("技术2", 60);
        radarChartView.addValues("技术3", 60);
        radarChartView.addValues("技术4", 60);
        radarChartView.invalidate();

        final SeekBar seekBar = findViewById(R.id.seekBar);
        final SeekBar seekBar1 = findViewById(R.id.seekBar1);
        seekBar1.setMax(30);
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSum(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar.setMax(30);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateData((int) (progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void updateSum(int progress) {
        radarChartView.setSideNum(progress);
    }

    private void updateData(int size) {
        radarChartView.clear();
        for (int i = 0; i < size; i++) {
            radarChartView.addValues("Text" + i, 60);
        }
        radarChartView.invalidate();
    }
}
