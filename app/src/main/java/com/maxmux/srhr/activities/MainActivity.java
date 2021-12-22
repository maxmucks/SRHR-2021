package com.maxmux.srhr.activities;

import static com.maxmux.srhr.utils.Constant.ADMOB;
import static com.maxmux.srhr.utils.Constant.AD_STATUS_ON;
import static com.maxmux.srhr.utils.Constant.APPLOVIN;
import static com.maxmux.srhr.utils.Constant.BANNER_HOME;
import static com.maxmux.srhr.utils.Constant.INTERSTITIAL_POST_LIST;
import static com.maxmux.srhr.utils.Constant.STARTAPP;
import static com.maxmux.srhr.utils.Constant.UNITY;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.maxmux.srhr.BuildConfig;
import com.maxmux.srhr.R;
import com.maxmux.srhr.config.AppConfig;
import com.maxmux.srhr.fragments.FragmentCategory;
import com.maxmux.srhr.fragments.FragmentFavorite;
import com.maxmux.srhr.fragments.FragmentRecent;
import com.maxmux.srhr.fragments.FragmentSettings;
import com.maxmux.srhr.utils.AdNetwork;
import com.maxmux.srhr.utils.AdsPref;
import com.maxmux.srhr.utils.AppBarLayoutBehavior;
import com.maxmux.srhr.utils.GDPR;
import com.maxmux.srhr.utils.RtlViewPager;
import com.maxmux.srhr.utils.SharedPref;
import com.maxmux.srhr.utils.Tools;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private long exitTime = 0;
    MyApplication myApplication;
    private BottomNavigationView navigation;
    private ViewPager viewPager;
    private RtlViewPager viewPagerRTL;
    private TextView title_toolbar;
    MenuItem prevMenuItem;
    int pager_number = 4;
//    SharedPreferences preferences;
    AdNetwork adNetwork;
    View view;
//    String androidId;
    ImageButton btn_search;
    SharedPref sharedPref;
    public ImageButton btn_sort;
    CoordinatorLayout coordinatorLayout;
    AdsPref adsPref;
    FloatingActionButton pDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        sharedPref = new SharedPref(this);
        adsPref = new AdsPref(this);
        if (adsPref.getAdStatus().equals(AD_STATUS_ON) && adsPref.getAdType().equals(STARTAPP)) {
            StartAppSDK.init(MainActivity.this, adsPref.getStartappAppID(), false);
            StartAppSDK.setTestAdsEnabled(BuildConfig.DEBUG);
        }
        if (AppConfig.ENABLE_RTL_MODE) {
            setContentView(R.layout.activity_main_rtl);
        } else {
            setContentView(R.layout.activity_main);
        }
        view = findViewById(android.R.id.content);
        if (AppConfig.ENABLE_RTL_MODE) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        if (adsPref.getAdStatus().equals(AD_STATUS_ON)) {
            switch (adsPref.getAdType()) {
                case STARTAPP:
                    StartAppSDK.setUserConsent(this, "pas", System.currentTimeMillis(), true);
                    StartAppAd.disableSplash();
                    break;
                case ADMOB:
                    MobileAds.initialize(this, initializationStatus -> {
                        Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                        for (String adapterClass : statusMap.keySet()) {
                            AdapterStatus status = statusMap.get(adapterClass);
                            assert status != null;
                            Log.d("MyApp", String.format("Adapter name: %s, Description: %s, Latency: %d", adapterClass, status.getDescription(), status.getLatency()));
                            Log.d("Open Bidding", "FAN open bidding with AdMob as mediation partner selected");
                        }
                    });
                    GDPR.updateConsentStatus(this);
                    break;
                case UNITY:
                    UnityAds.addListener(new IUnityAdsListener() {
                        @Override
                        public void onUnityAdsReady(String placementId) {
                            Log.d(TAG, placementId);
                        }

                        @Override
                        public void onUnityAdsStart(String placementId) {

                        }

                        @Override
                        public void onUnityAdsFinish(String placementId, UnityAds.FinishState finishState) {

                        }

                        @Override
                        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String message) {

                        }
                    });
                    UnityAds.initialize(getApplicationContext(), adsPref.getUnityGameId(), BuildConfig.DEBUG, new IUnityAdsInitializationListener() {
                        @Override
                        public void onInitializationComplete() {
                            Log.d(TAG, "Unity Ads Initialization Complete");
                            Log.d(TAG, "Unity Ads Game ID : " + adsPref.getUnityGameId());
                        }

                        @Override
                        public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                            Log.d(TAG, "Unity Ads Initialization Failed: [" + error + "] " + message);
                        }
                    });
                    break;
                case APPLOVIN:
                    AppLovinSdk.getInstance(this).setMediationProvider(AppLovinMediationProvider.MAX);
                    AppLovinSdk.getInstance(this).initializeSdk(config -> {
                    });
                    final String sdkKey = AppLovinSdk.getInstance(getApplicationContext()).getSdkKey();
                    if (!sdkKey.equals(getString(R.string.applovin_sdk_key))) {
                        Log.e(TAG, "AppLovin ERROR : Please update your sdk key in the manifest file.");
                    }
                    Log.d(TAG, "AppLovin SDK Key : " + sdkKey);
                    break;
            }
        }

        pDF = findViewById(R.id.pDF);
        pDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDDFDownload();
            }
        });

        AppBarLayout appBarLayout = findViewById(R.id.appbarLayout);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        myApplication = MyApplication.getInstance();

        title_toolbar = findViewById(R.id.title_toolbar);
        btn_sort = findViewById(R.id.btn_sort);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        navigation = findViewById(R.id.navigation);
        navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        if (AppConfig.ENABLE_RTL_MODE) {
            initRTLViewPager();
        } else {
            initViewPager();
        }

        Tools.notificationOpenHandler(this, getIntent());
        Tools.getCategoryPosition(this, getIntent());

        adNetwork = new AdNetwork(this);
        adNetwork.loadBannerAdNetwork(BANNER_HOME);
        adNetwork.loadInterstitialAdNetwork(INTERSTITIAL_POST_LIST);

        initToolbarIcon();

    }

    private void pDDFDownload() {

        String URL = "http://deafopkenyavideos.deafopkenya.org/videos/APP_SCRIPT.pdf";

    }

    public void showInterstitialAd() {
        adNetwork.showInterstitialAdNetwork(INTERSTITIAL_POST_LIST, adsPref.getInterstitialAdInterval());
    }

    public void initViewPager() {
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(pager_number);
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_category:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_favorite:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_settings:
                    viewPager.setCurrentItem(3);
                    return true;
            }
            return false;
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);

                if (viewPager.getCurrentItem() == 1) {
                    title_toolbar.setText(getResources().getString(R.string.app_name));
                    showSortMenu(true);
                } else if (viewPager.getCurrentItem() == 0) {
                    title_toolbar.setText(getResources().getString(R.string.app_name));
                    //title_toolbar.setText(getResources().getString(R.string.title_nav_category));
                    showSortMenu(false);
                } else if (viewPager.getCurrentItem() == 2) {
                    title_toolbar.setText(getResources().getString(R.string.title_nav_favorite));
                    showSortMenu(false);
                } else if (viewPager.getCurrentItem() == 3) {
                    title_toolbar.setText(getResources().getString(R.string.title_nav_settings));
                    showSortMenu(false);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void initRTLViewPager() {
        viewPagerRTL = findViewById(R.id.viewpager_rtl);
        viewPagerRTL.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewPagerRTL.setOffscreenPageLimit(pager_number);
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPagerRTL.setCurrentItem(0);
                    return true;
                case R.id.navigation_category:
                    viewPagerRTL.setCurrentItem(1);
                    return true;
                case R.id.navigation_favorite:
                    viewPagerRTL.setCurrentItem(2);
                    return true;
                case R.id.navigation_settings:
                    viewPagerRTL.setCurrentItem(3);
                    return true;
            }
            return false;
        });

        viewPagerRTL.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);

                if (viewPagerRTL.getCurrentItem() == 1) {
                    title_toolbar.setText(getResources().getString(R.string.app_name));
                    showSortMenu(true);
                } else if (viewPagerRTL.getCurrentItem() == 0) {
                    title_toolbar.setText(getResources().getString(R.string.app_name));
                    //title_toolbar.setText(getResources().getString(R.string.title_nav_category));
                    showSortMenu(false);
                } else if (viewPagerRTL.getCurrentItem() == 2) {
                    title_toolbar.setText(getResources().getString(R.string.title_nav_favorite));
                    showSortMenu(false);
                } else if (viewPagerRTL.getCurrentItem() == 3) {
                    title_toolbar.setText(getResources().getString(R.string.title_nav_settings));
                    showSortMenu(false);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void selectCategory() {
        viewPager.setCurrentItem(1);
    }

    public void showSortMenu(Boolean show) {
        if (show) {
            btn_sort.setVisibility(View.VISIBLE);
        } else {
            btn_sort.setVisibility(View.GONE);
        }
    }

    public class MyAdapter extends FragmentPagerAdapter {

        MyAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new FragmentCategory();
                case 1:
                    return new FragmentRecent();
                case 2:
                    return new FragmentFavorite();
                case 3:
                    return new FragmentSettings();
            }
            return null;
        }

        @Override
        public int getCount() {
            return pager_number;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void initToolbarIcon() {

        if (sharedPref.getIsDarkTheme()) {
            findViewById(R.id.toolbar).setBackgroundColor(getResources().getColor(R.color.colorToolbarDark));
            navigation.setBackgroundColor(getResources().getColor(R.color.colorToolbarDark));
        } else {
            findViewById(R.id.toolbar).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(view -> new Handler().postDelayed(() -> startActivity(new Intent(getApplicationContext(), ActivitySearch.class)), 50));

    }

    @Override
    public void onBackPressed() {
        if (AppConfig.ENABLE_RTL_MODE) {
            if (viewPagerRTL.getCurrentItem() != 0) {
                viewPagerRTL.setCurrentItem((0), true);
            } else {
                exitApp();
            }
        } else {
            if (viewPager.getCurrentItem() != 0) {
                viewPager.setCurrentItem((0), true);
            } else {
                exitApp();
            }
        }
    }

    public void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public AssetManager getAssets() {
        return getResources().getAssets();
    }

}
