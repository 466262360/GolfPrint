package com.mashangyou.golfprint.api;


import com.mashangyou.golfprint.bean.res.AppVersion;
import com.mashangyou.golfprint.bean.res.CountWriteRes;
import com.mashangyou.golfprint.bean.res.LoginRes;
import com.mashangyou.golfprint.bean.res.OrderListRes;
import com.mashangyou.golfprint.bean.res.PassWordRes;
import com.mashangyou.golfprint.bean.res.PublishListRes;
import com.mashangyou.golfprint.bean.res.ResponseBody;
import com.mashangyou.golfprint.bean.res.SelectWriteRes;
import com.mashangyou.golfprint.bean.res.VerifyRes;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {
    @FormUrlEncoded
    @POST("mobile/login")
    Observable<ResponseBody<LoginRes>> login(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("mobile/resetPwd")
    Observable<ResponseBody<PassWordRes>> passWord(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("mobile/multiple/verification")
    Observable<ResponseBody<VerifyRes>> verify(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("mobile/bigcode/verification")
    Observable<ResponseBody<VerifyRes>> verifyTrus(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("mobile/use")
    Observable<ResponseBody> use(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("mobile/quit")
    Observable<ResponseBody> quit(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("mobile/countWrite")
    Observable<ResponseBody<CountWriteRes>> countWrite(@Field("token") String token);

    @FormUrlEncoded
    @POST("mobile/selectWrite")
    Observable<ResponseBody<SelectWriteRes>> selectWrite(@Field("token") String token);

    @GET("mobile/orderlist")
    Observable<ResponseBody<OrderListRes>> getOrder(@Query("token") String token);

    @FormUrlEncoded
    @POST("mobile/querySeasion")
    Observable<ResponseBody<PublishListRes>> query(@FieldMap Map<String, String> fields);

    @GET("mobile/version/1")
    Observable<ResponseBody<AppVersion>> update();
}
