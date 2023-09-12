package org.deafop.srhr_signlanguage.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.deafop.srhr_signlanguage.R;
import org.deafop.srhr_signlanguage.adapters.TitleAdapter;
import org.deafop.srhr_signlanguage.models.DataModel;
import org.deafop.srhr_signlanguage.models.MyData;
import org.deafop.srhr_signlanguage.models.Video;
import org.deafop.srhr_signlanguage.utils.Tools;

import java.util.ArrayList;

public class ActivityCountries extends AppCompatActivity {


    private static RecyclerView.Adapter adapter;
    /* access modifiers changed from: private */
    public static RecyclerView countries;
    private static ArrayList<DataModel> data;
    public static View.OnClickListener myOnClickListener;
    private static Video post;
    private RecyclerView.LayoutManager layoutManager;
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Tools.getTheme(this);
        setContentView(R.layout.activity_countries);
        post = (Video) getIntent().getSerializableExtra("key.EXTRA_OBJC");
        myOnClickListener = new MyOnClickListener(this);
        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        countries = recyclerView;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.layoutManager = linearLayoutManager;
        countries.setLayoutManager(linearLayoutManager);
        countries.setItemAnimator(new DefaultItemAnimator());
        data = new ArrayList<>();
        for (int i = 0; i < MyData.nameArray.length; i++) {
            data.add(new DataModel(MyData.nameArray[i], MyData.id_[i].intValue(), MyData.drawableArray[i].intValue()));
        }
        TitleAdapter titleAdapter = new TitleAdapter(data);
        adapter = titleAdapter;
        countries.setAdapter(titleAdapter);
    }

    private static class MyOnClickListener implements View.OnClickListener {
        private final Context context;

        private MyOnClickListener(Context context2) {
            this.context = context2;
        }

        public void onClick(View view) {
            int childAdapterPosition = ActivityCountries.countries.getChildAdapterPosition(view);
            if (childAdapterPosition == 0) {
                uganda(view);
            } else if (childAdapterPosition == 1) {
                zimbambwe(view);
            }
        }

        private void uganda(View view) {
            view.getContext().startActivity(new Intent(view.getContext(), ActivityUganda.class));
        }

        private void zimbambwe(View view) {
            view.getContext().startActivity(new Intent(view.getContext(), ActivityZimbambwe.class));
        }
    }
}