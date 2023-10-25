package org.deafop.srhr_signlanguage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FirstTime extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Boolean.valueOf(getSharedPreferences("PREFERENCE", 0).getBoolean("isFirstRun", true)).booleanValue()) {
            launchMainScreen();
            Toast.makeText(this, "First Run", Toast.LENGTH_LONG);
            finish();
        } else {
            launchMainScreen();
            finish();
        }
        getSharedPreferences("PREFERENCE", 0).edit().putBoolean("isFirstRun", false).apply();
    }

    private void launchMainScreen() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
