package com.google.codelabs.mdc.java.shrine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.codelabs.mdc.java.shrine.network.Device;
import com.google.codelabs.mdc.java.shrine.network.ImageRequester;
import com.google.codelabs.mdc.java.shrine.network.Reading;
import com.google.codelabs.mdc.java.shrine.network.Rules;
import com.google.codelabs.mdc.java.shrine.staggeredgridlayout.MySingleton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReadingsAdapter extends RecyclerView.Adapter<ReadingsAdapter.ViewHolder> {

    private LayoutInflater mInflater;

    private ReadingsAdapter.ItemClickListener mClickListener;
    private ReadingsAdapter.ItemLongClickListener mLongClickListener;
    private ArrayList<Reading> mData;

    // data is passed into the constructor
    ReadingsAdapter(Context context, ArrayList<Reading> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ReadingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.readings_row, parent, false);
        return new ReadingsAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ReadingsAdapter.ViewHolder holder, int position) {
        String nameTwo = mData.get(position).getTemp();
        String image = mData.get(position).getAcc();
        String id = mData.get(position).getCompass();

        holder.temp.setText(nameTwo);
        holder.acc.setText(image);
        holder.compass.setText(id);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView temp;
        TextView acc;
        TextView compass;

        ViewHolder(View itemView) {
            super(itemView);
            temp = itemView.findViewById(R.id.temp);
            acc = itemView.findViewById(R.id.acc);
            compass = itemView.findViewById(R.id.compass);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            return false;
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id).getTemp();
    }

    // allows clicks events to be caught
    void setClickListener(ReadingsAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    void setLongClickListener(ReadingsAdapter.ItemLongClickListener itemClickListener) {
        System.out.println("Im dad here");
        this.mLongClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);

    }
    public interface ItemLongClickListener{
        void onItemLongClick(View view, int position);
    }

    public void updateList(ArrayList<Reading> list){
        mData = list;
        notifyDataSetChanged();
    }
}

