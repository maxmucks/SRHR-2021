package org.deafop.srhr_signlanguage.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;


import org.deafop.srhr_signlanguage.R;
import org.deafop.srhr_signlanguage.config.AppConfig;
import org.deafop.srhr_signlanguage.utils.AdsPref;
import org.deafop.srhr_signlanguage.utils.Constant;
import org.deafop.srhr_signlanguage.utils.CustomWebChromeClient;

public class ActivityYoutubePlayer extends AppCompatActivity {

    private static final int RECOVERY_REQUEST = 1;
    private WebView youTube;
    private String str_video_id;
    AdsPref adsPref;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_youtube);

        adsPref = new AdsPref(this);

        if (AppConfig.FORCE_PLAYER_TO_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        Intent intent = getIntent();
        if (null != intent) {
            str_video_id = intent.getStringExtra(Constant.KEY_VIDEO_ID);
            //str_vid = intent.getStringExtra(Constant.KEY_VID);
        }


        //loadViewed();

        youTube = findViewById(R.id.youtube_view);
        youTube.getSettings().setJavaScriptEnabled(true);
        youTube.setWebViewClient(new WebViewClient());
        youTube.setWebChromeClient(new CustomWebChromeClient(ActivityYoutubePlayer.this));


        String videoId = str_video_id;

        String htmlCode = "<html><body style='margin:0;padding:0;'><iframe allow='fullscreen;' id='player' type='text/html' width='100%' height='100%' src='https://www.youtube.com/embed/" + videoId + "?enablejsapi=1' frameborder='0'></iframe></body></html>";

        // webView.loadUrl("https://www.youtube.com");
        youTube.loadDataWithBaseURL(null, htmlCode, "text/html", "UTF-8", null);




    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onBackPressed() {
        if (youTube.canGoBack()) {
            youTube.goBack();
        } else {
            super.onBackPressed();
        }
    }

}