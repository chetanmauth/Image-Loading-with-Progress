package com.satyukt.imageprogress;

import okhttp3.OkHttpClient;
import okhttp3.Response;

public class OkHttpClientWithProgress {

    public static OkHttpClient getClient(ProgressResponseBody.ProgressListener progressListener) {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                            .build();
                })
                .build();
    }
}
