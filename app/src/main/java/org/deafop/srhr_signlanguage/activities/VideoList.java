package org.deafop.srhr_signlanguage.activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import org.deafop.srhr_signlanguage.R;
import org.deafop.srhr_signlanguage.adapters.TitleAdapter;
import org.deafop.srhr_signlanguage.models.DataModel;
import org.deafop.srhr_signlanguage.models.VideoData;

public class VideoList extends AppCompatActivity {

    public static final int RequestPermissionCode = 1;
    String URL = "https://deafopkenyavideos.deafopkenya.org/videos/Covid.pdf";

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel> data;
    static View.OnClickListener myOnClickListener;
  //  private static ArrayList<Integer> removedItems;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        myOnClickListener = new MyOnClickListener(this);

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DataModel>();
        for (int i = 0; i < VideoData.nameArray.length; i++) {
            data.add(new DataModel(
                    VideoData.nameArray[i],
                    VideoData.id_[i],
                    VideoData.drawableArray[i]
            ));
        }


        adapter = new TitleAdapter(data);
        recyclerView.setAdapter(adapter);




        if (checkPermission()) {

            Toast.makeText(VideoList.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();

        } else {
            permissionAccess();

        }


    }

    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);
            if (position == 0){

                s1e1(v);
            } else if (position == 1) {

                s1e2(v);
            } else if (position == 2) {
                s1e3(v);
            } else if (position == 3) {
                s1e4(v);
            } else if (position == 4) {
                s1e5(v);
            } else if (position == 5) {
                s1e6(v);
            } else if (position == 6) {
                s1e7(v);
            } else if (position == 7) {
                s1e8(v);
            } else if (position == 8) {
                s2e1(v);
            } else if (position == 9) {
                s2e2(v);
            } else if (position == 10) {
                s2e3(v);
            } else if (position == 11) {
                s2e4(v);
            } else if (position == 12) {
                s2e5(v);
            } else if (position == 13) {
                s2e6(v);
            } else if (position == 14) {
                silent(v);
            }
        }

        private void s1e1(View v) {

        }

        private void s1e2(View v) {

        }

        private void s1e3(View v) {

        }


        private void s1e4(View v) {

        }

        private void s1e5(View v) {

        }

        private void s1e6(View v) {

        }

        private void s1e7(View v) {
          //  Intent contraceptiveIntent = new Intent(v.getContext(), ContraceptiveActivity.class);
           // v.getContext().startActivity(contraceptiveIntent);
        }

        private void s1e8(View v) {
           // Intent abortionIntent = new Intent(v.getContext(), AbortionActivity.class);
         //   v.getContext().startActivity(abortionIntent);
        }

        private void s2e1(View v) {

        }

        private void s2e2(View v) {

        }

        private void s2e3(View v) {


        }

        private void s2e4(View v) {


        }

        private void s2e5(View v) {


        }

        private void s2e6(View v) {


        }

        private void silent(View v) {


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
     //   if (id == R.id.action_settings) {
       //     return true;
       // }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder((this));
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable((false))
                .setPositiveButton("Yes", (dialog, id) -> VideoList.this.finish())
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void permissionAccess() {

        ActivityCompat.requestPermissions(VideoList.this, new String[]
                {
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE
                }, RequestPermissionCode);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean ReadStoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (ReadStoragePermission && WriteStoragePermission) {

                        Toast.makeText(VideoList.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(VideoList.this, "Permission Denied", Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;
    }
}
