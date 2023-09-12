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
import org.deafop.srhr_signlanguage.adapters.UgandaAdapter;
import org.deafop.srhr_signlanguage.models.UgData;
import org.deafop.srhr_signlanguage.models.Uganda;
import org.deafop.srhr_signlanguage.utils.Constant;

import java.util.ArrayList;

public class ActivityUganda extends AppCompatActivity {
    private static RecyclerView.Adapter adapter;
    private static ArrayList<Uganda> data;
    public static View.OnClickListener myOnClickListener;
    /* access modifiers changed from: private */
    public static RecyclerView uganda;
    private RecyclerView.LayoutManager layoutManager;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_uganda);
        myOnClickListener = new MyOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_Zimbambwe);
        uganda = recyclerView;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.layoutManager = linearLayoutManager;
        uganda.setLayoutManager(linearLayoutManager);
        uganda.setItemAnimator(new DefaultItemAnimator());
        data = new ArrayList<>();
        for (int i = 0; i < UgData.nameArray.length; i++) {
            data.add(new Uganda(UgData.nameArray[i], UgData.id_[i].intValue(), UgData.drawableArray[i].intValue()));
        }
        UgandaAdapter ugandaAdapter = new UgandaAdapter(data);
        adapter = ugandaAdapter;
        uganda.setAdapter(ugandaAdapter);
        uganda.setAdapter(adapter);
    }

    private class MyOnClickListener implements View.OnClickListener {
        private final Context context;

        private MyOnClickListener(Context context2) {
            this.context = context2;
        }

        public void onClick(View view) {
            int childAdapterPosition = ActivityUganda.uganda.getChildAdapterPosition(view);
            if (childAdapterPosition == 0) {
                digitaldeaf(view);
            } else if (childAdapterPosition == 1) {
                srhrpt1(view);
            } else if (childAdapterPosition == 2) {
                srhrcont(view);
            } else if (childAdapterPosition == 3) {
                teenspreg(view);
            } else if (childAdapterPosition == 4) {
                antenatalcare1(view);
            } else if (childAdapterPosition == 5) {
                antenatalcarecont(view);
            } else if (childAdapterPosition == 6) {
                postnatal(view);
            } else if (childAdapterPosition == 7) {
                hivtrans(view);
            } else if (childAdapterPosition == 8) {
                hivtprev(view);
            } else if (childAdapterPosition == 9) {
                sti(view);
            } else if (childAdapterPosition == 10) {
                stincont(view);
            }
        }

        private void digitaldeaf(View view) {
            Intent intent = new Intent(ActivityUganda.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "FZ8DUWs6eA4");
            view.getContext().startActivity(intent);
        }

        private void srhrpt1(View view) {
            Intent intent = new Intent(ActivityUganda.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "3qvUp7SdrDA");
            view.getContext().startActivity(intent);
        }

        private void srhrcont(View view) {
            Intent intent = new Intent(ActivityUganda.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "xdF2-oZ9IJ0");
            view.getContext().startActivity(intent);
        }

        private void teenspreg(View view) {
            Intent intent = new Intent(ActivityUganda.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "BJd3hlwVnBw");
            view.getContext().startActivity(intent);
        }

        private void antenatalcare1(View view) {
            Intent intent = new Intent(ActivityUganda.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "VrZgLcdUJds");
            view.getContext().startActivity(intent);
        }

        private void antenatalcarecont(View view) {
            Intent intent = new Intent(ActivityUganda.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "qNi-rctj7e8");
            view.getContext().startActivity(intent);
        }

        private void postnatal(View view) {
            Intent intent = new Intent(ActivityUganda.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "9fntTCXCtWg");
            view.getContext().startActivity(intent);
        }

        private void hivtrans(View view) {
            Intent intent = new Intent(ActivityUganda.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "IWJl6pTJ_Tg");
            view.getContext().startActivity(intent);
        }

        private void hivtprev(View view) {
            Intent intent = new Intent(ActivityUganda.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "KIynSlvp49E");
            view.getContext().startActivity(intent);
        }

        private void sti(View view) {
            Intent intent = new Intent(ActivityUganda.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "XbCeY8UgleI");
            view.getContext().startActivity(intent);
        }

        private void stincont(View view) {
            Intent intent = new Intent(ActivityUganda.this, ActivityYoutubePlayer.class);
            intent.putExtra(Constant.KEY_VIDEO_ID, "mjVCnp8VDPg");
            view.getContext().startActivity(intent);
        }
    }
}
