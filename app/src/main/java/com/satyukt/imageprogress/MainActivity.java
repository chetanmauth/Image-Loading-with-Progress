package com.satyukt.imageprogress;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.InputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    ImageView imageView;

    //example image of 2MB
    String url = "https://www.sefram.com/images/products/photos/hi_res/9816B.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        Button button = findViewById(R.id.button);

        Handler handler = new Handler(Looper.getMainLooper());
        ProgressResponseBody.ProgressListener progressListener = progress -> handler.post(() -> {
            Log.d("Loading Progress", String.valueOf(progress));
            progressBar.setProgress(progress);
        });

        OkHttpClient client = OkHttpClientWithProgress.getClient(progressListener);

        //Register listener
        Glide.get(this)
                .getRegistry()
                .replace(GlideUrl.class,
                        InputStream.class,
                        new OkHttpUrlLoader.Factory((Call.Factory) client));

        //on button click
        button.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            setImage();
        });

    }

    //set Image
    private void setImage() {

        Glide.with(this)
                .load(url)
                .override(Target.SIZE_ORIGINAL)
                .timeout(20000)
                .diskCacheStrategy(DiskCacheStrategy.NONE)      //can be change
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        // Handle when the image is ready
                        progressBar.setVisibility(View.GONE);
                        Glide.with(MainActivity.this)
                                .load(resource)
                                .transition(withCrossFade(200))
                                .into(imageView);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle cleanup if necessary
                    }
                });
    }
}