package com.google.codelabs.mdc.java.shrine;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.codelabs.mdc.java.shrine.network.Device;
import com.google.codelabs.mdc.java.shrine.network.ImageRequester;
import com.google.codelabs.mdc.java.shrine.network.Message;
import com.google.codelabs.mdc.java.shrine.network.Reading;
import com.google.codelabs.mdc.java.shrine.network.Rules;
import com.google.codelabs.mdc.java.shrine.staggeredgridlayout.MySingleton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private LayoutInflater mInflater;

    private MessageAdapter.ItemClickListener mClickListener;
    private MessageAdapter.ItemLongClickListener mLongClickListener;
    private ArrayList<Message> mData;

    // data is passed into the constructor
    MessageAdapter(Context context, ArrayList<Message> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.message, parent, false);
        return new MessageAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(MessageAdapter.ViewHolder holder, int position) {
        String message = mData.get(position).getMessage();
        String time = mData.get(position).getTime();
        if(mData.get(position).getIsYourMessage() == 1){
            holder.messR.setText(message);
            holder.timeR.setText(time);
            holder.messL.setVisibility(View.GONE);
            holder.timeL.setVisibility(View.GONE);


        }
        else{
            holder.messL.setText(message);
            holder.timeL.setText(time);
            holder.messR.setVisibility(View.GONE);
            holder.timeR.setVisibility(View.GONE);
        }


    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView messL;
        TextView messR;
        TextView timeR;
        TextView timeL;
        CardView cv;

        ViewHolder(View itemView) {
            super(itemView);
            messL = itemView.findViewById(R.id.messageLeft);
            messR = itemView.findViewById(R.id.messageRight);
            timeR = itemView.findViewById(R.id.createdAtRight);
            timeL = itemView.findViewById(R.id.createdAtLeft);


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



    // allows clicks events to be caught
    void setClickListener(MessageAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    void setLongClickListener(MessageAdapter.ItemLongClickListener itemClickListener) {
        this.mLongClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);

    }
    public interface ItemLongClickListener{
        void onItemLongClick(View view, int position);
    }

    public void updateList(ArrayList<Message> list){
        mData = list;
        notifyDataSetChanged();
    }
}

