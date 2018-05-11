package com.anshi.kuaishou.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.anshi.kuaishou.R;
import com.anshi.kuaishou.domain.MailAnalysisResult;
import com.anshi.kuaishou.domain.ResponseBody;
import com.anshi.kuaishou.service.AppService;
import com.anshi.kuaishou.utils.BitmapUtil;
import com.anshi.kuaishou.utils.MailAnalyzer;
import com.tbruyelle.rxpermissions2.RxPermissions;
import java.io.File;
import java.net.ConnectException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    Button btn_scanner;
    Button btn_camera;
    Button btn_photo;
    TextView tv_result;
    TextView tv_orgin_result;
    ProgressDialog progressDialog;

    RxPermissions rxPermissions;

    private static final int CONNECT_TIMEOUT = 30;
    private static final int WRITE_TIMEOUT = 20;
    private static final int READ_TIMEOUT = 20;

    Retrofit retrofit = null;
    AppService service = null;
    Call<ResponseBody> response = null;

    String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rxPermissions = new RxPermissions(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        initView();
        initListener();
        initRetrofit();
    }

    private void initView() {
        btn_scanner = findViewById(R.id.btn_scanner);
        btn_camera = findViewById(R.id.btn_camera);
        btn_photo = findViewById(R.id.btn_photo);
        tv_result = findViewById(R.id.tv_result);
        tv_orgin_result = findViewById(R.id.tv_orgin_result);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在识别中，请稍等...");
        progressDialog.setCancelable(false);
    }

    private void initListener() {
        btn_scanner.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
    }

    private void loadData(RequestBody body) {
        progressDialog.show();
        try {
            response = service.scanner("qymgc", "XNs44IV5LZsI", body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.cancel();
                if (response == null || response.body() == null)
                    return;
                Log.d(TAG, response.body().toString());
                List<String> strs = response.body().getLinesText();

                if (strs.size() == 0) {
                    tv_orgin_result.setText("识别结果为空");
                    return;
                }

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < strs.size(); i++) {
                    stringBuilder.append(strs.get(i) + "\n");
                }
                tv_orgin_result.setText(stringBuilder.toString());

                MailAnalysisResult result = MailAnalyzer.doMailAnalysis(strs);
                Log.d(TAG, result.toString());
                if (result.getCode() == 200) {
                    tv_result.setText(result.toString());
                } else if (result.getCode() == 500) {
                    tv_result.setText("识别结果为空");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                final Throwable cause = t.getCause() != null ? t.getCause() : t;
                if (cause != null) {
                    progressDialog.cancel();
                    if (cause instanceof ConnectException) {
                        Toast.makeText(MainActivity.this, "未能连接到服务器", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "连接超时，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void initRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://imgs-sandbox.intsig.net")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(AppService.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scanner:
                break;
            case R.id.btn_camera:
                rxPermissions
                        .request(Manifest.permission.CAMERA)
                        .subscribe(granted -> {
                            if (granted) {
                                gotoCamera();
                            } else {

                            }
                        });
                break;
            case R.id.btn_photo:
                rxPermissions
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(granted -> {
                            if (granted) {
                                gotoSelectPhoto();
                            } else {

                            }
                        });
                break;
            default:
                break;
        }
    }

    private void gotoCamera() {
        // 获取路径
        String path = Environment.getExternalStorageDirectory() + File.separator + "images";
        // 定义文件名
        String fileName = new Date().getTime() + ".jpg";
        File file = new File(path, fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        filePath = file.getAbsolutePath();
        Log.d(TAG, filePath);
        Uri imageUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, 1000);
    }

    private void gotoSelectPhoto() {
        Intent photoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(photoIntent, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
            RequestBody body = null;
            try {
                body = RequestBody.create(MediaType.parse("multipart/form-data"), BitmapUtil.readStream(filePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadData(body);
        } else if (requestCode == 2000 && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            RequestBody body = null;
            try {
                body = RequestBody.create(MediaType.parse("multipart/form-data"), BitmapUtil.readStream(imagePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadData(body);
            c.close();
        }
    }
}
