package org.deafop.srhr_signlanguage.activities;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.multidex.MultiDex;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;
import org.deafop.srhr_signlanguage.activities.AppOpenAdManager;
import org.deafop.srhr_signlanguage.callbacks.CallbackAds;
import org.deafop.srhr_signlanguage.config.AppConfig;
import org.deafop.srhr_signlanguage.models.Ads;
import org.deafop.srhr_signlanguage.rests.RestAdapter;
import org.deafop.srhr_signlanguage.utils.AudienceNetworkInitializeHelper;
import org.deafop.srhr_signlanguage.utils.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyApplication extends Application {
    public static final String TAG = "MyApplication";
    private static MyApplication mInstance;
    Activity activity;
    String big_picture = "";
    String link = "";
    String message = "";
    long post_id = -1;
    String title = "";
    long unique_id = -1;

    FirebaseAnalytics mFirebaseAnalytics;
    private AppOpenAdManager appOpenAdManager;
    Ads ads;


    public MyApplication() {
        mInstance = this;
    }

    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, initializationStatus -> {
        });
        AudienceNetworkInitializeHelper.initialize(this);
        mInstance = this;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        appOpenAdManager = new AppOpenAdManager.Builder(this).build();
        OneSignal.disablePush(false);
        Log.d(TAG, "OneSignal Notification is enabled");

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        requestTopic();
//        FirebaseMessaging.getInstance().subscribeToTopic(FCM_NOTIFICATION_TOPIC);
//        OneSignal.setAppId(ONESIGNAL_APP_ID);

        OneSignal.setNotificationOpenedHandler(
                result -> {
                    title = result.getNotification().getTitle();
                    message = result.getNotification().getBody();
                    big_picture = result.getNotification().getBigPicture();
                    Log.d(TAG, title + ", " + message + ", " + big_picture);
                    try {
                        unique_id = result.getNotification().getAdditionalData().getLong("unique_id");
                        post_id = result.getNotification().getAdditionalData().getLong("post_id");
                        link = result.getNotification().getAdditionalData().getString("link");
                        Log.d(TAG, post_id + ", " + unique_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("unique_id", unique_id);
                    intent.putExtra("post_id", post_id);
                    intent.putExtra("title", title);
                    intent.putExtra("link", link);
                    startActivity(intent);
                });

        OneSignal.unsubscribeWhenNotificationsAreDisabled(true);
    }

    public AppOpenAdManager getAppOpenAdManager() {
        return this.appOpenAdManager;
    }


    private void requestTopic() {
        String decode = Tools.decodeBase64(AppConfig.SERVER_KEY);
        String data = Tools.decrypt(decode);
        String[] results = data.split("_applicationId_");
        String api_url = results[0].replace("http://localhost", "http://10.0.2.2");

        Call<CallbackAds> callbackCall = RestAdapter.createAPI(api_url).getAds(AppConfig.REST_API_KEY);
        callbackCall.enqueue(new Callback<CallbackAds>() {
            public void onResponse(Call<CallbackAds> call, Response<CallbackAds> response) {
                CallbackAds resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    ads = resp.ads;
                    FirebaseMessaging.getInstance().subscribeToTopic(ads.fcm_notification_topic);
                    OneSignal.setAppId(ads.onesignal_app_id);
                    Log.d(TAG, "FCM Subscribe topic : " + ads.fcm_notification_topic);
                    Log.d(TAG, "OneSignal App ID : " + ads.onesignal_app_id);
                }
            }

            public void onFailure(Call<CallbackAds> call, Throwable th) {
                Log.e("onFailure", "" + th.getMessage());
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }
}
