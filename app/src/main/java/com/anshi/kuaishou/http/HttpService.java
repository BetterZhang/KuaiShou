package com.anshi.kuaishou.http;

import com.anshi.kuaishou.domain.MailInfo;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Android Studio.
 * Author : zhangzhongqiang
 * Email  : betterzhang.dev@gmail.com
 * Time   : 2018/05/16 下午 1:42
 * Desc   : description
 */
public interface HttpService {

    /**
     * 入库
     */
    @POST("/inbound")
    Call<HttpResult> inbound(@Body HashMap<String, Object> params);


    /**
     * 出库
     * @return
     */
    @POST("/outbound")
    Call<HttpResult> outbound(@Body HashMap<String, Object> params);


    /**
     *
     * @return
     */
    @GET("/getPickInfo")
    Call<HttpResult<List<MailInfo>>> getPickInfo();

}
