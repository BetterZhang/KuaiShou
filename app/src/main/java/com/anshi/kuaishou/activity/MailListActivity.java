package com.anshi.kuaishou.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import com.anshi.kuaishou.R;
import com.anshi.kuaishou.adapter.MailAdapter;
import com.anshi.kuaishou.domain.MailInfo;
import com.anshi.kuaishou.http.HttpResult;
import com.anshi.kuaishou.http.HttpService;
import com.anshi.kuaishou.http.HttpUtil;
import com.anshi.kuaishou.utils.DensityUtil;
import com.anshi.kuaishou.utils.DialogHelp;
import com.anshi.kuaishou.view.TopSpaceItemDecoration;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android Studio.
 * Author : zhangzhongqiang
 * Email  : betterzhang.dev@gmail.com
 * Time   : 2018/05/14 下午 3:42
 * Desc   : description
 */
public class MailListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swplParent;
    RecyclerView rcy_mail;

    private MailAdapter mAdapter;

    ProgressDialog progressDialog;

    private HttpService httpService;
    Call<HttpResult<List<MailInfo>>> response = null;
    Call<HttpResult> outResponse = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_list);

        initView();
        httpService = HttpUtil.getInstance().getHttpService();

        initListener();
        loadData();
    }

    private void initListener() {
        swplParent.setOnRefreshListener(this);
        rcy_mail.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                MailInfo mailInfo = (MailInfo) adapter.getItem(position);
                outBound(mailInfo);
            }
        });
    }

    private void initView() {
        swplParent = findViewById(R.id.swplParent);
        rcy_mail = findViewById(R.id.rcy_mail);

        swplParent.setColorSchemeResources(
                R.color.common_swiperefresh_1,
                R.color.common_swiperefresh_2,
                R.color.common_swiperefresh_3,
                R.color.common_swiperefresh_4
        );

        rcy_mail.addItemDecoration(new TopSpaceItemDecoration(DensityUtil.dpTopx(this, 10)));
        rcy_mail.setHasFixedSize(true);
        rcy_mail.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new MailAdapter(null);
        mAdapter.openLoadAnimation();

        rcy_mail.setAdapter(mAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("");
        progressDialog.setCancelable(false);
    }

    private void loadData() {
        progressDialog.show();
        response = httpService.getPickInfo();
        response.enqueue(new Callback<HttpResult<List<MailInfo>>>() {
            @Override
            public void onResponse(Call<HttpResult<List<MailInfo>>> call, Response<HttpResult<List<MailInfo>>> response) {
                progressDialog.cancel();
                HttpResult result = response.body();
                if (result == null)
                    return;
                if (result.isSuccess()) {
                    List<MailInfo> mailInfoList = response.body().getD();
                    if (mailInfoList == null || mailInfoList.size() == 0) {
                        Toast.makeText(MailListActivity.this, "查询列表为空", Toast.LENGTH_SHORT).show();
                    } else {
                        mAdapter.setNewData(mailInfoList);
                    }

                } else {
                    DialogHelp.getMessageDialog(MailListActivity.this, "提示", result.getM()).show();
                }
            }

            @Override
            public void onFailure(Call<HttpResult<List<MailInfo>>> call, Throwable t) {
                final Throwable cause = t.getCause() != null ? t.getCause() : t;
                if (cause != null) {
                    progressDialog.cancel();
                    if (cause instanceof ConnectException) {
                        Toast.makeText(MailListActivity.this, "未能连接到服务器", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MailListActivity.this, "连接超时，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void outBound(MailInfo mailInfo) {
        progressDialog.show();
        HashMap<String, Object> params = new HashMap<>();
        params.put("stationId", 721);
        params.put("stationName", "熊猫快收服务站");
        params.put("packetIds", mailInfo.getParcelId());
        params.put("takeCode", mailInfo.getTakeCode());
        params.put("mobile", mailInfo.getRecipientMobile());
        params.put("outWay", 2);
        params.put("superPwd", "123");

        outResponse = httpService.outbound(params);
        outResponse.enqueue(new Callback<HttpResult>() {
            @Override
            public void onResponse(Call<HttpResult> call, Response<HttpResult> response) {
                progressDialog.cancel();
                HttpResult result = response.body();
                if (result == null)
                    return;
                DialogHelp.getMessageDialog(MailListActivity.this, "提示", result.getM()).show();
                if (result.isSuccess())
                    loadData();
            }

            @Override
            public void onFailure(Call<HttpResult> call, Throwable t) {
                final Throwable cause = t.getCause() != null ? t.getCause() : t;
                if (cause != null) {
                    progressDialog.cancel();
                    if (cause instanceof ConnectException) {
                        Toast.makeText(MailListActivity.this, "未能连接到服务器", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MailListActivity.this, "连接超时，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.cancel();
    }

    @Override
    public void onRefresh() {
        loadData();
    }

}
