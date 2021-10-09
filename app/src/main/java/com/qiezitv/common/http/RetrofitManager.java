package com.qiezitv.common.http;

import android.util.Log;

import com.qiezitv.common.Constants;
import com.qiezitv.common.http.factory.EmptyJsonLenientConverterFactory;
import com.qiezitv.common.http.factory.NobodyConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    private static final String TAG = RetrofitManager.class.getSimpleName();

    private Retrofit retrofit;

    private String baseUrl;

    private RetrofitManager() {
        initServerUrl();
        initRetrofit();
    }

    public static final RetrofitManager getInstance() {
        return InnerHolder.mInstance;
    }

    private void initRetrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
//                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(8888))) // 设置代理
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    if (request.url().toString().contains("/service-admin/auth")) {
                        request = request.newBuilder()
                                .addHeader("Content-Type", "application/json;charset=UTF-8")
                                .addHeader("Accept", "application/json")
                                .build();
                    } else {
                        request = request.newBuilder()
                                .addHeader("Content-Type", "application/json;charset=UTF-8")
                                .addHeader("Accept", "application/json")
                                .addHeader("Authorization", Constants.ACCESS_TOKEN != null ? Constants.ACCESS_TOKEN : "")
                                .build();
                    }
                    return chain.proceed(request);
                })
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(java.sql.Timestamp.class, new JsonDeserializer<Timestamp>() {
                    public java.sql.Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new java.sql.Timestamp(json.getAsJsonPrimitive().getAsLong());
                    }
                })
                .registerTypeAdapter(java.sql.Date.class, new JsonDeserializer<java.sql.Date>() {
                    public java.sql.Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new java.sql.Date(json.getAsJsonPrimitive().getAsLong());
                    }
                })
                .registerTypeAdapter(java.util.Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
                    String dateStr = json.getAsJsonPrimitive().getAsString();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                    try {
                        return simpleDateFormat.parse(dateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(NobodyConverterFactory.create())
                .addConverterFactory(new EmptyJsonLenientConverterFactory(GsonConverterFactory.create(gson)))
                .client(okHttpClient)
                .build();
    }

    private void initServerUrl() {
        baseUrl = "https://" + Constants.SERVER_DEFAULT_IP;
        Log.d(TAG, "baseUrl:" + baseUrl);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public static String formatAuthorizationHeader(String accessToken) {
        return String.format("Bearer %s", accessToken);
    }

    private static final class InnerHolder {
        private static final RetrofitManager mInstance = new RetrofitManager();
    }

}
