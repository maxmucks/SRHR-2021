package org.deafop.srhr_signlanguage.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import org.deafop.srhr_signlanguage.R;
import org.deafop.srhr_signlanguage.utils.Constant;
import org.deafop.srhr_signlanguage.utils.Tools;

import java.io.File;

public class pdf extends AppCompatActivity {
    PDFView pdfView;
    private String PDFfile;
    File file;
    ImageButton btn_back;
    private static final int STORAGE_REQUEST_CODE = 100;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Tools.getTheme(this);
        setContentView(R.layout.activity_pdf);

        PDFView pdfView = findViewById(R.id.pdfView);
        pdfView.fromAsset("script.pdf")
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }


}