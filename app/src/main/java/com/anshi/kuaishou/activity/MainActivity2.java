package com.anshi.kuaishou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.anshi.kuaishou.R;
import com.anshi.kuaishou.domain.MailAnalysisResult;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity2";

    Button btn_scan;
    Button btn_store;
    EditText et_num;
    EditText et_phone;
    EditText et_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initView();
        initListener();
    }

    private void initView() {
        btn_scan = findViewById(R.id.btn_scan);
        btn_store = findViewById(R.id.btn_store);
        et_num = findViewById(R.id.et_num);
        et_phone = findViewById(R.id.et_phone);
        et_name = findViewById(R.id.et_name);
    }

    private void initListener() {
        btn_scan.setOnClickListener(this);
        btn_store.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                gotoScanActivity();
                break;
            case R.id.btn_store:
                break;
            default:
                break;
        }
    }

    private void gotoScanActivity() {
        Intent intent = new Intent(MainActivity2.this, ScannerActivity.class);
        startActivityForResult(intent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                MailAnalysisResult result = (MailAnalysisResult) data.getExtras().get("result");
                if (result == null)
                    return;
                et_num.setText(result.getMailNo());
                et_phone.setText(result.getRecipientPhone());
                et_name.setText(result.getRecipientName());
            }
        }
    }
}
