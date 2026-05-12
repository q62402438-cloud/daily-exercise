package com.example.myapplication.network;

import android.util.Log;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit 单例
 */
public class RetrofitClient {

    private static final String BASE_URL =
            "http://192.168.222.101:8082/";

    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            // 创建 OkHttpClient 并添加拦截器
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request originalRequest = chain.request();
                            
                            // 打印请求信息（仅调试用）
                            Log.d("Retrofit", "请求URL: " + originalRequest.url());
                            Log.d("Retrofit", "请求方法: " + originalRequest.method());
                            
                            // 打印请求体
                            if (originalRequest.body() != null) {
                                RequestBody body = originalRequest.body();
                                Buffer buffer = new Buffer();
                                body.writeTo(buffer);
                                Log.d("Retrofit", "请求体: " + buffer.readUtf8());
                            }

                            // 添加必要的请求头
                            Request requestWithHeaders = originalRequest.newBuilder()
                                    .header("Content-Type", "application/json")
                                    .header("Accept", "application/json")
                                    .build();
                            
                            // 打印请求头
                            Log.d("Retrofit", "请求头: " + requestWithHeaders.headers());

                            Response response = chain.proceed(requestWithHeaders);

                            // 打印响应信息
                            Log.d("Retrofit", "响应码: " + response.code());
                            Log.d("Retrofit", "响应消息: " + response.message());

                            return response;
                        }
                    })
                    .build();

            // 配置 Gson，确保继承的字段也被正确序列化
            Gson gson = new GsonBuilder()
                    .enableComplexMapKeySerialization()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}