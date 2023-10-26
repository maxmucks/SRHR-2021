package org.deafop.srhr_signlanguage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import org.deafop.srhr_signlanguage.R;
import org.deafop.srhr_signlanguage.activities.ActivityZimbambwe;
import org.deafop.srhr_signlanguage.models.Zimbambwe;

import java.util.ArrayList;
public class ZimbambweAdapter extends RecyclerView.Adapter<ZimbambweAdapter.MyViewHolder> {
    private ArrayList<Zimbambwe> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewIcon;
        TextView textViewName;

        public MyViewHolder(View view) {
            super(view);
            this.textViewName = view.findViewById(R.id.textViewName);
            this.imageViewIcon = view.findViewById(R.id.imageView);
        }
    }

    public ZimbambweAdapter(ArrayList<Zimbambwe> arrayList) {
        this.dataSet = arrayList;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cards_layout, viewGroup, false);
        inflate.setOnClickListener(ActivityZimbambwe.myOnClickListener);
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
