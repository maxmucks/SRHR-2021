package org.deafop.srhr_signlanguage.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.multidex.BuildConfig;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.deafop.srhr_signlanguage.R;
import org.deafop.srhr_signlanguage.config.AppConfig;
import org.deafop.srhr_signlanguage.fragments.FragmentCategory;
import org.deafop.srhr_signlanguage.fragments.FragmentFavorite;
import org.deafop.srhr_signlanguage.fragments.FragmentRecent;
import org.deafop.srhr_signlanguage.fragments.FragmentRefferal;
import org.deafop.srhr_signlanguage.fragments.FragmentSettings;
import org.deafop.srhr_signlanguage.utils.AdNetwork;
import org.deafop.srhr_signlanguage.utils.AdsPref;
import org.deafop.srhr_signlanguage.utils.AppBarLayoutBehavior;
import org.deafop.srhr_signlanguage.utils.GDPR;
import org.deafop.srhr_signlanguage.utils.RtlViewPager;
import org.deafop.srhr_signlanguage.utils.SharedPref;
import org.deafop.srhr_signlanguage.utils.Tools;

import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

import org.deafop.srhr_signlanguage.utils.Constant;

import java.io.File;
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
    ImageButton btn_countries;
    FloatingActionButton chat_us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        sharedPref = new SharedPref(this);
        adsPref = new AdsPref(this);
        if (adsPref.getAdStatus().equals(Constant.AD_STATUS_ON) && adsPref.getAdType().equals(Constant.STARTAPP)) {
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

        if (adsPref.getAdStatus().equals(Constant.AD_STATUS_ON)) {
            switch (adsPref.getAdType()) {
                case Constant.STARTAPP:
                    StartAppSDK.setUserConsent(this, "pas", System.currentTimeMillis(), true);
                    StartAppAd.disableSplash();
                    break;
                case Constant.ADMOB:
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
                case Constant.APPLOVIN:
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
        btn_countries = (ImageButton) findViewById(R.id.btn_countries);
        btn_countries.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), ActivityCountries.class));
            }
        });
        chat_us = findViewById(R.id.chatUs);
        chat_us.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String str = "https://api.whatsapp.com/send?phone=" + "+254 739890070";
                try {
                    MainActivity.this.getApplicationContext().getPackageManager().getPackageInfo("com.whatsapp", 1);
                } catch (PackageManager.NameNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse(str));
                startActivity(intent);

            }
        });
        pDF = findViewById(R.id.pDF);
        pDF.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Snackbar.make(MainActivity.this.view, (CharSequence) "PDF", BaseTransientBottomBar.LENGTH_LONG).setAction((CharSequence) "Action", (View.OnClickListener) null).show();
                if (new File(MainActivity.this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/App_Script.pdf").exists()) {
                    Toast.makeText(MainActivity.this, "Exists", Toast.LENGTH_SHORT).show();
                    File file = new File(MainActivity.this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/App_Script.pdf");
                    MainActivity mainActivity = MainActivity.this;
                    Uri uriForFile = FileProvider.getUriForFile(mainActivity, MainActivity.this.getApplicationContext().getPackageName() + ".provider", file);
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setDataAndType(uriForFile, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException unused) {
                        Toast.makeText(MainActivity.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //  downloadAndOpenPDF();
                }
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
        adNetwork.loadBannerAdNetwork(Constant.BANNER_HOME);
        adNetwork.loadInterstitialAdNetwork(Constant.INTERSTITIAL_POST_LIST);

        initToolbarIcon();

    }

    private void pDDFDownload() {

        String URL = "http://deafopkenyavideos.deafopkenya.org/videos/APP_SCRIPT.pdf";

    }

    public void showInterstitialAd() {
        adNetwork.showInterstitialAdNetwork(Constant.INTERSTITIAL_POST_LIST, adsPref.getInterstitialAdInterval());
    }

    public void initViewPager() {
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(pager_number);
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_category:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_favorite:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_home:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_referal:
                    viewPager.setCurrentItem(3);
                    return true;
                case R.id.navigation_settings:
                    viewPager.setCurrentItem(4);
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
                    title_toolbar.setText(getResources().getString(R.string.title_nav_home));
                    showSortMenu(true);
                } else if (viewPager.getCurrentItem() == 0) {
                    title_toolbar.setText(getResources().getString(R.string.title_nav_category));
                    //title_toolbar.setText(getResources().getString(R.string.title_nav_category));
                    showSortMenu(false);
                } else if (viewPager.getCurrentItem() == 4) {
                    title_toolbar.setText(getResources().getString(R.string.title_nav_referal));
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
                case R.id.navigation_category:
                    viewPagerRTL.setCurrentItem(0);
                    return true;
                case R.id.navigation_favorite:
                    viewPagerRTL.setCurrentItem(2);
                    return true;
                case R.id.navigation_home:
                    viewPagerRTL.setCurrentItem(1);
                    return true;
                case R.id.navigation_referal:
                    viewPagerRTL.setCurrentItem(4);
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
                    title_toolbar.setText(getResources().getString(R.string.title_nav_home));
                    showSortMenu(true);
                } else if (viewPagerRTL.getCurrentItem() == 0) {
                    title_toolbar.setText(getResources().getString(R.string.title_nav_category));
                    //title_toolbar.setText(getResources().getString(R.string.title_nav_category));
                    showSortMenu(false);
                } else if (viewPagerRTL.getCurrentItem() == 4) {
                    title_toolbar.setText(getResources().getString(R.string.title_nav_referal));
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
                case 4:
                    return new FragmentRefferal();
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
