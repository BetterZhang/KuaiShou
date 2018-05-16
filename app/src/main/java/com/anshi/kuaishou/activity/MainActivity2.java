package com.anshi.kuaishou.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.anshi.kuaishou.R;
import com.anshi.kuaishou.domain.InBoundParamVo;
import com.anshi.kuaishou.domain.MailAnalysisResult;
import com.anshi.kuaishou.http.HttpResult;
import com.anshi.kuaishou.http.HttpService;
import com.anshi.kuaishou.http.HttpUtil;
import com.anshi.kuaishou.utils.DialogHelp;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity2";

    Button btn_scan;
    Button btn_store;
    EditText et_num;
    EditText et_phone;
    EditText et_name;
    FloatingActionButton fab_pickup;

    ProgressDialog progressDialog;

    private HttpService httpService;
    Call<HttpResult> response = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initView();
        initListener();
        httpService = HttpUtil.getInstance().getHttpService();
    }

    private void initView() {
        btn_scan = findViewById(R.id.btn_scan);
        btn_store = findViewById(R.id.btn_store);
        et_num = findViewById(R.id.et_num);
        et_phone = findViewById(R.id.et_phone);
        et_name = findViewById(R.id.et_name);
        fab_pickup = findViewById(R.id.fab_pickup);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在入库，请稍等...");
        progressDialog.setCancelable(false);
    }

    private void initListener() {
        btn_scan.setOnClickListener(this);
        btn_store.setOnClickListener(this);
        fab_pickup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                gotoScanActivity();
                break;
            case R.id.btn_store:
                if (TextUtils.isEmpty(et_num.getText().toString().trim())) {
                    Toast.makeText(this, "请输入运单号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(et_phone.getText().toString().trim())) {
                    Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(et_name.getText().toString().trim())) {
                    Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
                    return;
                }
                store();
                break;
            case R.id.fab_pickup:
                gotoMailListActivity();
                break;
            default:
                break;
        }
    }

    private void gotoScanActivity() {
        Intent intent = new Intent(MainActivity2.this, ScannerActivity.class);
        startActivityForResult(intent, 1000);
    }

    // 调用入库接口
    private void store() {
        progressDialog.show();
        HashMap<String, Object> params = new HashMap<>();

        List<InBoundParamVo.PacketInfoBean> beanList = new ArrayList<>();
        InBoundParamVo.PacketInfoBean packetInfoBean = new InBoundParamVo.PacketInfoBean();
        packetInfoBean.setMailNo(et_num.getText().toString().trim());
        packetInfoBean.setFrameCode("A");
        packetInfoBean.setNum("720011");
        packetInfoBean.setRecipientMobile(et_phone.getText().toString().trim());
        packetInfoBean.setRecipientName(et_name.getText().toString().trim());
        beanList.add(packetInfoBean);

        params.put("stationId", 721);
        params.put("stationName", "熊猫快收服务站");
        params.put("companyId", 125);
        params.put("version", "1.1.1");
        params.put("inputway", 1);
        params.put("companyName", "中通速递");
        params.put("source", 2);
        params.put("packetInfo", beanList);

        response = httpService.inbound(params);

        response.enqueue(new Callback<HttpResult>() {
            @Override
            public void onResponse(Call<HttpResult> call, Response<HttpResult> response) {
                progressDialog.cancel();
                HttpResult result = response.body();
                if (result == null)
                    return;
                if (result.isSuccess()) {
                    DialogHelp.getMessageDialog(MainActivity2.this, "提示", result.getM() + result.getE()).show();
                    resetInput();
                } else {
                    DialogHelp.getMessageDialog(MainActivity2.this, "提示", result.getM()).show();
                }
            }

            @Override
            public void onFailure(Call<HttpResult> call, Throwable t) {
                final Throwable cause = t.getCause() != null ? t.getCause() : t;
                if (cause != null) {
                    progressDialog.cancel();
                    if (cause instanceof ConnectException) {
                        Toast.makeText(MainActivity2.this, "未能连接到服务器", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity2.this, "连接超时，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void gotoMailListActivity() {
        Intent intent = new Intent(MainActivity2.this, MailListActivity.class);
        startActivity(intent);
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

    private void resetInput() {
        et_num.setText("");
        et_phone.setText("");
        et_name.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.cancel();
    }
}
