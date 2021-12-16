package com.maxmux.srhr.activities;

import static com.maxmux.srhr.utils.Constant.BANNER_POST_DETAIL;
import static com.maxmux.srhr.utils.Constant.NATIVE_AD_POST_DETAIL;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.maxmux.srhr.BuildConfig;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_video_detail);

        sharedPref = new SharedPref(this);
        adsPref = new AdsPref(this);
        adNetwork = new AdNetwork(this);

        if (AppConfig.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
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
        new Handler().postDelayed(this::requestPostData, 200);
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
        if (Tools.isConnect(ActivityVideoDetail.this)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.failed_text));
        }
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

        new Handler().postDelayed(() -> lyt_suggested.setVisibility(View.VISIBLE), 1000);

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
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
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
