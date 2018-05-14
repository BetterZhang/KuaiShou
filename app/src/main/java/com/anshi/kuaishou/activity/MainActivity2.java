package com.anshi.kuaishou.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.anshi.kuaishou.R;
import okhttp3.RequestBody;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initView();
        initListener();
    }

    private void initView() {

    }

    private void initListener() {
    }


    private void loadData(RequestBody body) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

}
