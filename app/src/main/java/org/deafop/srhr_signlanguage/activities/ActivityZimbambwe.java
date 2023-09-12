package org.deafop.srhr_signlanguage.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.deafop.srhr_signlanguage.R;
import org.deafop.srhr_signlanguage.adapters.ZimbambweAdapter;
import org.deafop.srhr_signlanguage.models.ZimData;
import org.deafop.srhr_signlanguage.models.Zimbambwe;
import org.deafop.srhr_signlanguage.utils.Constant;
import org.deafop.srhr_signlanguage.utils.Tools;

import java.util.ArrayList;

public class ActivityZimbambwe extends AppCompatActivity {
    private static RecyclerView.Adapter adapter;
    private static ArrayList<Zimbambwe> data;
    public static View.OnClickListener myOnClickListener;
    /* access modifiers changed from: private */
    public static RecyclerView zimbambwe;
    private RecyclerView.LayoutManager layoutManager;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Tools.getTheme(this);
        setContentView((int) R.layout.activity_zimbambwe);
        myOnClickListener = new MyOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_Zimbambwe);
        zimbambwe = recyclerView;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.layoutManager = linearLayoutManager;
        zimbambwe.setLayoutManager(linearLayoutManager);
        zimbambwe.setItemAnimator(new DefaultItemAnimator());
        data = new ArrayList<>();
        for (int i = 0; i < ZimData.nameArray.length; i++) {
            data.add(new Zimbambwe(ZimData.nameArray[i], ZimData.id_[i].intValue(), ZimData.drawableArray[i].intValue()));
        }
        ZimbambweAdapter zimbambweAdapter = new ZimbambweAdapter(data);
        adapter = zimbambweAdapter;
        zimbambwe.setAdapter(zimbambweAdapter);
        zimbambwe.setAdapter(adapter);
    }

    private class MyOnClickListener implements View.OnClickListener {
        private final Context context;

        private MyOnClickListener(Context context2) {
            this.context = context2;
        }

        public void onClick(View view) {
            int childAdapterPosition = ActivityZimbambwe.zimbambwe.getChildAdapterPosition(view);
            if (childAdapterPosition == 0) {
                deafaccesssrhr(view);
            } else if (childAdapterPosition == 1) {
                formsgvb(view);
            } else if (childAdapterPosition == 2) {
                sexualgvb(view);
            } else if (childAdapterPosition == 3) {
                poem1(view);
            } else if (childAdapterPosition == 4) {
                poem2(view);
            } else if (childAdapterPosition == 5) {
                poem3(view);
            } else if (childAdapterPosition == 6) {
                drama1(view);
            } else if (childAdapterPosition == 7) {
                drama2(view);
            }
        }

        private void deafaccesssrhr(View view) {
            Intent intent = new Intent(ActivityZimbambwe.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "JlZOf0t4YQU");
            view.getContext().startActivity(intent);
        }

        private void formsgvb(View view) {
            Intent intent = new Intent(ActivityZimbambwe.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "BiAAmgRTZzE");
            view.getContext().startActivity(intent);
        }

        private void sexualgvb(View view) {
            Intent intent = new Intent(ActivityZimbambwe.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "fvIoItIZsRQ");
            view.getContext().startActivity(intent);
        }

        private void poem1(View view) {
            Intent intent = new Intent(ActivityZimbambwe.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "2SBrWkyFXFY");
            view.getContext().startActivity(intent);
        }

        private void poem2(View view) {
            Intent intent = new Intent(ActivityZimbambwe.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "O5MbUREJtN4");
            view.getContext().startActivity(intent);
        }

        private void poem3(View view) {
            Intent intent = new Intent(ActivityZimbambwe.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "7ltM73vvv30");
            view.getContext().startActivity(intent);
        }

        private void drama1(View view) {
            Intent intent = new Intent(ActivityZimbambwe.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "VFG0ja9BuBw");
            view.getContext().startActivity(intent);
        }

        private void drama2(View view) {
            Intent intent = new Intent(ActivityZimbambwe.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "eY73YUiV8Jg");
            view.getContext().startActivity(intent);
        }
    }
}
