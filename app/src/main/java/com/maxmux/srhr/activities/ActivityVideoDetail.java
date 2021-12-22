package com.maxmux.srhr.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.multidex.BuildConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.droidhubworld.dialoglib.DefaultConstants;
import com.droidhubworld.dialoglib.messagedialog.CommonMessageDialog;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.maxmux.srhr.R;
import com.maxmux.srhr.adapters.AdapterSuggested;
import com.maxmux.srhr.callbacks.CallbackVideoDetail;
import com.maxmux.srhr.config.AppConfig;
import com.maxmux.srhr.databases.DatabaseHandlerFavorite;
import com.maxmux.srhr.models.Video;
import com.maxmux.srhr.rests.RestAdapter;
import com.maxmux.srhr.utils.AdNetwork;
import com.maxmux.srhr.utils.AdsPref;
import com.maxmux.srhr.utils.AppBarLayoutBehavior;
import com.maxmux.srhr.utils.Constant;
import com.maxmux.srhr.utils.SharedPref;
import com.maxmux.srhr.utils.Tools;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.util.Date;
import java.util.List;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.maxmux.srhr.utils.Constant.BANNER_POST_DETAIL;
import static com.maxmux.srhr.utils.Constant.NATIVE_AD_POST_DETAIL;

public class ActivityVideoDetail extends AppCompatActivity {

    private Call<CallbackVideoDetail> callbackCall = null;
    private LinearLayout lyt_main_content;
    private Video post;
    TextView txt_title, txt_category, txt_duration, txt_total_views, txt_date_time;
    LinearLayout lyt_view, lyt_date;
    ImageView video_thumbnail;
    private WebView video_description;
    DatabaseHandlerFavorite databaseHandler;
    CoordinatorLayout parent_view;
    private ShimmerFrameLayout lyt_shimmer;
    RelativeLayout lyt_suggested;
    private SwipeRefreshLayout swipe_refresh;
    SharedPref sharedPref;
    ImageButton image_favorite, btn_share;
    AdsPref adsPref;
    AdNetwork adNetwork;
    String youTubeURL = null;

    String WritePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    String ReadPermission = Manifest.permission.READ_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_video_detail);

        sharedPref = new SharedPref(this);
        adsPref = new AdsPref(this);
        adNetwork = new AdNetwork(this);

        if (AppConfig.ENABLE_RTL_MODE) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        databaseHandler = new DatabaseHandlerFavorite(getApplicationContext());

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        swipe_refresh = findViewById(R.id.swipe_refresh_layout);
        swipe_refresh.setColorSchemeResources(R.color.colorPrimary);
        swipe_refresh.setRefreshing(false);

        lyt_main_content = findViewById(R.id.lyt_main_content);
        lyt_shimmer = findViewById(R.id.shimmer_view_container);
        parent_view = findViewById(R.id.lyt_content);

        video_thumbnail = findViewById(R.id.video_thumbnail);
        txt_title = findViewById(R.id.video_title);
        txt_category = findViewById(R.id.category_name);
        txt_duration = findViewById(R.id.video_duration);
        video_description = findViewById(R.id.video_description);
        txt_total_views = findViewById(R.id.total_views);
        txt_date_time = findViewById(R.id.date_time);
        lyt_view = findViewById(R.id.lyt_view_count);
        lyt_date = findViewById(R.id.lyt_date);
        image_favorite = findViewById(R.id.img_favorite);
        btn_share = findViewById(R.id.btn_share);

        lyt_suggested = findViewById(R.id.lyt_suggested);

        post = (Video) getIntent().getSerializableExtra(Constant.EXTRA_OBJC);

        requestAction();

        swipe_refresh.setOnRefreshListener(() -> {
            lyt_shimmer.setVisibility(View.VISIBLE);
            lyt_shimmer.startShimmer();
            lyt_main_content.setVisibility(View.GONE);
            requestAction();
        });

        initToolbar();

        adNetwork.loadBannerAdNetwork(BANNER_POST_DETAIL);
        adNetwork.loadNativeAdNetwork(NATIVE_AD_POST_DETAIL);

    }

    private void requestAction() {
        showFailedView(false, "");
        swipeProgress(true);
        new Handler(Looper.getMainLooper()).postDelayed(this::requestPostData, 200);

    }

    private void requestPostData() {
        this.callbackCall = RestAdapter.createAPI(sharedPref.getApiUrl()).getVideoDetail(post.vid);
        this.callbackCall.enqueue(new Callback<CallbackVideoDetail>() {
            public void onResponse(Call<CallbackVideoDetail> call, Response<CallbackVideoDetail> response) {
                CallbackVideoDetail responseHome = response.body();
                if (responseHome == null || !responseHome.status.equals("ok")) {
                    onFailRequest();
                    return;
                }
                displayAllData(responseHome);
                swipeProgress(false);
                lyt_main_content.setVisibility(View.VISIBLE);
            }

            public void onFailure(Call<CallbackVideoDetail> call, Throwable th) {
                Log.e("onFailure", th.getMessage());
                if (!call.isCanceled()) {
                    onFailRequest();
                }
            }
        });
    }

    private void onFailRequest() {
        swipeProgress(false);
        lyt_main_content.setVisibility(View.GONE);
        showFailedView(true, getString(R.string.failed_text));
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = findViewById(R.id.lyt_failed_home);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            lyt_failed.setVisibility(View.GONE);
        }
        findViewById(R.id.failed_retry).setOnClickListener(view -> requestAction());
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipe_refresh.setRefreshing(show);
            lyt_shimmer.setVisibility(View.GONE);
            lyt_shimmer.stopShimmer();
            lyt_main_content.setVisibility(View.VISIBLE);
            return;
        }
        lyt_main_content.setVisibility(View.GONE);
    }

    private void displayAllData(CallbackVideoDetail responseHome) {
        displayData(responseHome.post);
        displaySuggested(responseHome.suggested);
    }

    @SuppressLint("SetTextI18n")
    public void displayData(final Video post) {

        txt_title.setText(post.video_title);
        txt_duration.setText(post.video_duration);

        if (AppConfig.ENABLE_VIEW_COUNT) {
            txt_total_views.setText(Tools.withSuffix(post.total_views) + " " + getResources().getString(R.string.views_count));
        } else {
            lyt_view.setVisibility(View.GONE);
        }

        if (AppConfig.ENABLE_DATE_DISPLAY && AppConfig.DISPLAY_DATE_AS_TIME_AGO) {
            PrettyTime prettyTime = new PrettyTime();
            long timeAgo = Tools.timeStringtoMilis(post.date_time);
            txt_date_time.setText(prettyTime.format(new Date(timeAgo)));
        } else if (AppConfig.ENABLE_DATE_DISPLAY && !AppConfig.DISPLAY_DATE_AS_TIME_AGO) {
            txt_date_time.setText(Tools.getFormatedDateSimple(post.date_time));
        } else {
            lyt_date.setVisibility(View.GONE);
        }

        if (post.video_type != null && post.video_type.equals("youtube")) {
            Picasso.get()
                    .load(Constant.YOUTUBE_IMAGE_FRONT + post.video_id + Constant.YOUTUBE_IMAGE_BACK_HQ)
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(video_thumbnail);
        } else {
            Picasso.get()
                    .load(sharedPref.getApiUrl() + "/upload/" + post.video_thumbnail)
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(video_thumbnail);
        }

        video_thumbnail.setOnClickListener(view -> {
            //calling this method to show our android custom alert dialog
            showCustomDialog();
        });

        video_description.setBackgroundColor(Color.TRANSPARENT);
        video_description.setFocusableInTouchMode(false);
        video_description.setFocusable(false);
        video_description.getSettings().setDefaultTextEncodingName("UTF-8");

        WebSettings webSettings = video_description.getSettings();
        Resources res = getResources();
        int fontSize = res.getInteger(R.integer.font_size);
        webSettings.setDefaultFontSize(fontSize);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = post.video_description;

        String bg_paragraph;
        if (sharedPref.getIsDarkTheme()) {
            bg_paragraph = "<style type=\"text/css\">body{color: #eeeeee;} a{color:#ffffff;}";
        } else {
            bg_paragraph = "<style type=\"text/css\">body{color: #000000;}";
        }

        String font_style_default = "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/font/custom_font.ttf\")}body {font-family: MyFont; font-size: medium; text-align: left;}</style>";

        String text = "<html><head>"
                + font_style_default
                + "<style>img{max-width:100%;height:auto;} figure{max-width:100%;height:auto;} iframe{width:100%;}</style> "
                + bg_paragraph
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        String text_rtl = "<html dir='rtl'><head>"
                + font_style_default
                + "<style>img{max-width:100%;height:auto;} figure{max-width:100%;height:auto;} iframe{width:100%;}</style> "
                + bg_paragraph
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        if (AppConfig.ENABLE_RTL_MODE) {
            video_description.loadDataWithBaseURL(null, text_rtl, mimeType, encoding, null);
        } else {
            video_description.loadDataWithBaseURL(null, text, mimeType, encoding, null);
        }

        btn_share.setOnClickListener(view -> {
            String share_title = android.text.Html.fromHtml(post.video_title).toString();
            String share_content = android.text.Html.fromHtml(getResources().getString(R.string.share_text)).toString();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, share_title + "\n\n" + share_content + "\n\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        });

        addToFavorite();

        new Handler(Looper.getMainLooper()).postDelayed(() -> lyt_suggested.setVisibility(View.VISIBLE), 1000);

    }
    private void showCustomDialog() {
        CommonMessageDialog dialog = new CommonMessageDialog.Builder(this)
                .title("SRHR Sign Language")
                .titleGravity(Gravity.CENTER)
                .titleSize(22)
                .cornerRadius(5.0f)
                .titleColor(R.color.black)
                .messageColor(R.color.black)
                .message("Do you want to download or view online?")
                .messageGravity(Gravity.START | Gravity.CENTER_VERTICAL)
                .messageSize(18)
                .buttonTextSize(16)
                .buttonTypeface(Typeface.NORMAL)
                .titleTypeface(Typeface.BOLD)
                .messageTypeface(Typeface.NORMAL)
                .showNegativeButton(true)
                .negativeButtonText("Online")
                .positiveButtonText("Download")
                .iconTitleMaxHeight(32)
                .iconTitleMaxWidth(32)
                .iconTitleMinHeight(18)
                .iconTitleMinWidth(18)
                .iconTitleThinColor(getResources().getColor(R.color.colorPrimary))
                .titleIcon(getResources().getDrawable(R.drawable.ic_success))
                .titleBackgroundDrawable(getResources().getDrawable(R.drawable.title_gradient_bg))
                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                .positiveButtonDrawable(getResources().getDrawable(R.drawable.default_button_selector))
                .negativeButtonDrawable(getResources().getDrawable(R.drawable.default_button_selector))
                .positiveButtonTextColor(getResources().getColor(R.color.colorWhite))
                .negativeButtonTextColor(getResources().getColor(R.color.colorWhite))
                .cancelable(true)
                .dialogWindowWidth(0.9f)
                //.dialogWindowHeight(0.3f)
                .style(R.style.dialogStyle)
                .buttonDividerColor(R.color.divider)
                .buttonDividerWeight(1)
                .showButtonDivider(true)
                .callBack((isPositive, viewTag) -> {
                    Log.e("CLICK ON : ", viewTag.toString());
                    switch (viewTag.toString()) {
                        case DefaultConstants.POSITIVE_BUTTON_TAG:
                            if (post.video_type != null && post.video_type.equals("youtube")) {
                                openPlay(18);
                            }
                            break;
                        case DefaultConstants.NEGATIVE_BUTTON_TAG:
                            if (Tools.isNetworkAvailable(ActivityVideoDetail.this)) {

                                if (post.video_type != null && post.video_type.equals("youtube")) {
                                    Intent intent = new Intent(getApplicationContext(), ActivityYoutubePlayer.class);
                                    intent.putExtra(Constant.KEY_VIDEO_ID, post.video_id);
                                    startActivity(intent);
                                } else if (post.video_type != null && post.video_type.equals("Upload")) {
                                    Intent intent = new Intent(getApplicationContext(), ActivityVideoPlayer.class);
                                    intent.putExtra("url", sharedPref.getApiUrl() + "/upload/video/" + post.video_url);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(getApplicationContext(), ActivityVideoPlayer.class);
                                    intent.putExtra("url", post.video_url);
                                    startActivity(intent);
                                }

                                loadViewed();

                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_required), Toast.LENGTH_SHORT).show();
                            }

                            break;
                    }
                }).build();
        dialog.show("Dialog");
    }
    private void displaySuggested(List<Video> list) {

        RecyclerView recyclerView = findViewById(R.id.recycler_view_suggested);
        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityVideoDetail.this));
        AdapterSuggested adapterSuggested = new AdapterSuggested(ActivityVideoDetail.this, recyclerView, list);
        recyclerView.setAdapter(adapterSuggested);
        recyclerView.setNestedScrollingEnabled(false);
        adapterSuggested.setOnItemClickListener((view, obj, position) -> {
            Intent intent = new Intent(getApplicationContext(), ActivityVideoDetail.class);
            intent.putExtra(Constant.EXTRA_OBJC, obj);
            startActivity(intent);
        });

        TextView txt_suggested = findViewById(R.id.txt_suggested);
        if (list.size() > 0) {
            txt_suggested.setText(getResources().getString(R.string.txt_suggested));
        } else {
            txt_suggested.setText("");
        }

    }

    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (sharedPref.getIsDarkTheme()) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorToolbarDark));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("");
        }

        txt_category.setText(post.category_name);

    }




    public void YouTubeVideoDownloadF(int iTag){

        if (ActivityCompat.checkSelfPermission(this, WritePermission) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, ReadPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{WritePermission, ReadPermission}, 1);
        } else {

            YTDownload(iTag);
        }
    }

    public void YTDownload(final int itag) {
        String VideoURLDownload = youTubeURL;
        @SuppressLint("StaticFieldLeak") YouTubeUriExtractor youTubeUriExtractor = new YouTubeUriExtractor(this) {
            @Override
            public void onUrisAvailable(String videoId, final String videoTitle, SparseArray<YtFile> ytFiles) {
                if ((ytFiles != null)) {
                    String downloadURL = ytFiles.get(itag).getUrl();
                    Log.e("Download URL: ", downloadURL);
                    if(itag==18 || itag == 22) {
                        String mp4=".mp4";
                        DownloadManagingF(downloadURL, videoTitle,mp4);

                    }else if (itag == 251){
                        String mp3=".mp3";
                        DownloadManagingF(downloadURL,videoTitle,mp3);
                    }

                } else Toast.makeText(ActivityVideoDetail.this, "Error With URL", Toast.LENGTH_LONG).show();
            }
        };
        youTubeUriExtractor.execute(VideoURLDownload);

    }



    public void ytvdownload(View view ) {
            if (youTubeURL.contains("http")) {
                YouTubeVideoDownloadF(18);
            }


    }
    public void openPlay(int itag){
        youTubeURL = post.video_url;
        String VideoURLDownload = youTubeURL;
        @SuppressLint("StaticFieldLeak") YouTubeUriExtractor youTubeUriExtractor = new YouTubeUriExtractor(this) {
            @Override
            public void onUrisAvailable(String videoId, final String videoTitle, SparseArray<YtFile> ytFiles) {
                if ((ytFiles != null)) {
                    String downloadURL = ytFiles.get(itag).getUrl();
                    Log.e("Download URL: ", downloadURL);
                    if(itag==18 || itag == 22) {
                        final String vidFilename = videoTitle + ".mp4";
                        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+File.separator+ vidFilename);
                        if (file.exists()){
                            Toast.makeText(getApplicationContext(), DIRECTORY_DOWNLOADS + vidFilename + "/n exists", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ActivityVideoPlayer.class);
                            intent.putExtra("url", post.video_url);
                            startActivity(intent);

                    }else if (itag == 251){

                        } else {
                            ytvdownload(null);

                        }
              //          DownloadManagingF(downloadURL,videoTitle,mp3);
                    }


                }
            }
        };

       youTubeUriExtractor.execute(VideoURLDownload);



    }

    public void DownloadManagingF(String downloadURL, String videoTitle,String extentiondwn){

        if (downloadURL != null) {
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadURL));
            request.setTitle(videoTitle);
            request.setDescription("Downloading");
            request.setDestinationInExternalFilesDir(getApplicationContext(),DIRECTORY_DOWNLOADS, videoTitle + extentiondwn);
            if (downloadManager != null) {
                Toast.makeText(getApplicationContext(),"Downloading...",Toast.LENGTH_SHORT).show();
                downloadManager.enqueue(request);

            }
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    Toast.makeText(getApplicationContext(),"Download Completed",Toast.LENGTH_SHORT).show();

                    Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + DIRECTORY_DOWNLOADS);
                    Intent intentop = new Intent(Intent.ACTION_VIEW);
                    intentop.setDataAndType(selectedUri, "resource/folder");

                    if (intentop.resolveActivityInfo(getPackageManager(), 0) != null)
                    {
                        startActivity(intentop);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Saved on: Downloads",Toast.LENGTH_LONG).show();
                        // restartApp();
                    }
                    unregisterReceiver(this);
                    finish();
                }
            };
            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        }

    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void addToFavorite() {

        List<Video> data = databaseHandler.getFavRow(post.vid);
        if (data.size() == 0) {
            image_favorite.setImageResource(R.drawable.ic_fav_outline);
        } else {
            if (data.get(0).getVid().equals(post.vid)) {
                image_favorite.setImageResource(R.drawable.ic_fav);
            }
        }

        image_favorite.setOnClickListener(view -> {
            List<Video> data1 = databaseHandler.getFavRow(post.vid);
            if (data1.size() == 0) {
                databaseHandler.AddtoFavorite(new Video(
                        post.category_name,
                        post.vid,
                        post.video_title,
                        post.video_url,
                        post.video_id,
                        post.video_thumbnail,
                        post.video_duration,
                        post.video_description,
                        post.video_type,
                        post.total_views,
                        post.date_time
                ));
                Snackbar.make(parent_view, R.string.favorite_added, Snackbar.LENGTH_SHORT).show();
                image_favorite.setImageResource(R.drawable.ic_fav);

            } else {
                if (data1.get(0).getVid().equals(post.vid)) {
                    databaseHandler.RemoveFav(new Video(post.vid));
                    Snackbar.make(parent_view, R.string.favorite_removed, Snackbar.LENGTH_SHORT).show();
                    image_favorite.setImageResource(R.drawable.ic_fav_outline);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void loadViewed() {
        new MyTask().execute(sharedPref.getApiUrl() + "/api/get_total_views/?id=" + post.vid);
    }

    @SuppressWarnings("deprecation")
    private static class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return Tools.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null == result || result.length() == 0) {
                Log.d("TAG", "no data found!");
            } else {

                try {

                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray("result");
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void onDestroy() {
        if (!(callbackCall == null || callbackCall.isCanceled())) {
            this.callbackCall.cancel();
        }
        lyt_shimmer.stopShimmer();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
