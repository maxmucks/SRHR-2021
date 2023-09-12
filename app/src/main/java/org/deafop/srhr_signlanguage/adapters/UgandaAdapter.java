package org.deafop.srhr_signlanguage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import org.deafop.srhr_signlanguage.R;
import org.deafop.srhr_signlanguage.activities.ActivityUganda;
import org.deafop.srhr_signlanguage.models.Uganda;

import java.util.ArrayList;

public class UgandaAdapter extends RecyclerView.Adapter<UgandaAdapter.MyViewHolder> {
    private ArrayList<Uganda> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewIcon;
        TextView textViewName;

        public MyViewHolder(View view) {
            super(view);
            this.textViewName = (TextView) view.findViewById(R.id.textViewName);
            this.imageViewIcon = (ImageView) view.findViewById(R.id.imageView);
        }
    }

    public UgandaAdapter(ArrayList<Uganda> arrayList) {
        this.dataSet = arrayList;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cards_layout, viewGroup, false);
        inflate.setOnClickListener(ActivityUganda.myOnClickListener);
        return new MyViewHolder(inflate);
    }

    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        TextView textView = myViewHolder.textViewName;
        ImageView imageView = myViewHolder.imageViewIcon;
        textView.setText(this.dataSet.get(i).getName());
        imageView.setImageResource(this.dataSet.get(i).getImage());
    }

    public int getItemCount() {
        return this.dataSet.size();
    }
}
