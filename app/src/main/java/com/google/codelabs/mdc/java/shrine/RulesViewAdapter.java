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
import com.google.codelabs.mdc.java.shrine.network.Rules;
import com.google.codelabs.mdc.java.shrine.staggeredgridlayout.MySingleton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RulesViewAdapter extends RecyclerView.Adapter<RulesViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private RulesViewAdapter.ItemClickListener mClickListener;
    private RulesViewAdapter.ItemLongClickListener mLongClickListener;
    private ArrayList<Rules> mData;

    // data is passed into the constructor
    RulesViewAdapter(Context context, ArrayList<Rules> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public RulesViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rules_row, parent, false);
        return new RulesViewAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RulesViewAdapter.ViewHolder holder, int position) {
        String nameTwo = mData.get(position).getName();
        String id = Integer.toString(mData.get(position).getMicrobitID());

        holder.objectName.setText(nameTwo);
        holder.microbitID.setText(id);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView microbitID;
        TextView objectName;

        ViewHolder(View itemView) {
            super(itemView);
            objectName = itemView.findViewById(R.id.username);
            microbitID = itemView.findViewById(R.id.userID);
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
    int getItem(int id) {
        return mData.get(id).getMicrobitID();
    }

    // allows clicks events to be caught
    void setClickListener(RulesViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    void setLongClickListener(RulesViewAdapter.ItemLongClickListener itemClickListener) {
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

    public void updateList(ArrayList<Rules> list){
        mData = list;
        notifyDataSetChanged();
    }
}

