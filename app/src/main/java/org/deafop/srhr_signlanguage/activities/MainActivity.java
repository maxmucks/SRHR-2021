package org.deafop.srhr_signlanguage.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.deafop.srhr_signlanguage.R;
import org.deafop.srhr_signlanguage.config.AppConfig;
import org.deafop.srhr_signlanguage.fragments.FragmentCategory;
import org.deafop.srhr_signlanguage.fragments.FragmentFavorite;
import org.deafop.srhr_signlanguage.fragments.FragmentRecent;
import org.deafop.srhr_signlanguage.fragments.FragmentRefferal;
import org.deafop.srhr_signlanguage.fragments.FragmentSettings;
import org.deafop.srhr_signlanguage.utils.AppBarLayoutBehavior;
import org.deafop.srhr_signlanguage.utils.RtlViewPager;
import org.deafop.srhr_signlanguage.utils.SharedPref;
import org.deafop.srhr_signlanguage.utils.Tools;

import java.io.File;

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
    View view;
    //    String androidId;
    ImageButton btn_search;
    SharedPref sharedPref;
    public ImageButton btn_sort;
    CoordinatorLayout coordinatorLayout;
    FloatingActionButton pDF;
    ImageButton btn_countries;
    FloatingActionButton chat_us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        sharedPref = new SharedPref(this);
        if (AppConfig.ENABLE_RTL_MODE) {
            setContentView(R.layout.activity_main_rtl);
        } else {
            setContentView(R.layout.activity_main);
        }
        view = findViewById(android.R.id.content);
        if (AppConfig.ENABLE_RTL_MODE) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        btn_countries = findViewById(R.id.btn_countries);
        btn_countries.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ActivityCountries.class)));
        chat_us = findViewById(R.id.chatUs);
        chat_us.setOnClickListener(view -> {
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

        });
        pDF = findViewById(R.id.pDF);
        pDF.setOnClickListener(view -> {
            Snackbar.make(MainActivity.this.view, "PDF", BaseTransientBottomBar.LENGTH_LONG).setAction("Action", null).show();
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

        initToolbarIcon();

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
