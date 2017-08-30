package com.citcall.network;


import android.content.Context;

import com.citcall.R;
import com.citcall.constant.Const;
import com.citcall.util.NetworkUtil;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Agustya on 12/28/15.
 */

public class RestClient {
    private static ApiService mApiService;

    public static ApiService getClient(){
        if(mApiService == null){
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(Const.Value.CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(Const.Value.CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                    .build();

            Retrofit client = new Retrofit.Builder()
                    .baseUrl(Const.Url.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mApiService = client.create(ApiService.class);
        }

        return mApiService;
    }

    public static String getErrorFail(Context context, Throwable t) {
        String error;
        if(!NetworkUtil.isConnected(context)){
            error = context.getString(R.string.error_no_internet_connection);
        }else if(t instanceof SocketTimeoutException){
            error = context.getString(R.string.error_slow_internet_connection);
        }else{
            error = context.getString(R.string.error_internal_server_error);
        }

        return error;
    }

    public interface ApiService {
        @FormUrlEncoded
        @POST(Const.Url.VERIFY)
        Call<ResponseBody> verify(@Field("trxid") String trxId, @Field("token") String token);

        @FormUrlEncoded
        @POST(Const.Url.REQUEST)
        Call<ResponseBody> request(@Field("cid") String cId, @Field("tryno") int tryNo, @Field("username") String username,
                                   @Field("passwd") String password);
    }
}
