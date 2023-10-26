package org.deafop.srhr_signlanguage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.BuildConfig;

import org.deafop.srhr_signlanguage.R;
import org.deafop.srhr_signlanguage.callbacks.CallbackAds;
import org.deafop.srhr_signlanguage.config.AppConfig;
import org.deafop.srhr_signlanguage.rests.RestAdapter;
import org.deafop.srhr_signlanguage.utils.SharedPref;
import org.deafop.srhr_signlanguage.utils.Tools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySplash extends AppCompatActivity {

    private static final String TAG = "ActivitySplash";
    private ProgressBar progressBar;
    String id = "0";
    String url = "";
    ImageView img_splash;
    SharedPref sharedPref;
    Call<CallbackAds> callbackCall = null;


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Tools.getTheme(this);
        setContentView((int) R.layout.activity_splash);
        sharedPref = new SharedPref(this);
        img_splash = (ImageView) findViewById(R.id.img_splash);
        if (sharedPref.getIsDarkTheme().booleanValue()) {
            img_splash.setImageResource(R.drawable.bg_splash_default);
        } else {
            img_splash.setImageResource(R.drawable.bg_splash_default);
        }
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        if (getIntent().hasExtra("nid")) {
            id = getIntent().getStringExtra("nid");
            url = getIntent().getStringExtra("external_link");
        }
        loadConfig();
    }

    private void requestAds(String str) {
        this.callbackCall = RestAdapter.createAPI(str).getAds(AppConfig.REST_API_KEY);
        this.callbackCall.enqueue(new Callback<CallbackAds>() {
            public void onResponse(Call<CallbackAds> call, Response<CallbackAds> response) {
                CallbackAds resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    ActivitySplash.this.onSplashFinished();
                    return;
                }
                onSplashFinished();
            }

            public void onFailure(Call<CallbackAds> call, Throwable th) {
                Log.e("onFailure", "" + th.getMessage());
                onSplashFinished();
            }
        });
    }



    private void loadConfig() {
        String decode = Tools.decodeBase64(AppConfig.SERVER_KEY);
        String data = Tools.decrypt(decode);
        String[] results = data.split("_applicationId_");
        String api_url = results[0].replace("http://localhost", "http://10.0.2.2");
        String application_id = results[1];
        sharedPref.saveConfig(api_url, application_id);

        if (application_id.equals(BuildConfig.APPLICATION_ID)) {
            if (Tools.isConnect(this)) {
                requestAds(api_url);
            } else {
                launchFirstTime();
            }
        }

        else {
            launchFirstTime();
        }
        Log.d(TAG, api_url);
        Log.d(TAG, application_id);
    }


    private void onSplashFinished() {

        launchFirstTime();

    }

    private void launchFirstTime() {
        Intent intent = new Intent(getApplicationContext(), FirstTime.class);
        startActivity(intent);
        new Handler(Looper.getMainLooper()).postDelayed(this::finish, 5000);
    }
}
