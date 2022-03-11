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
import com.google.codelabs.mdc.java.shrine.network.Contact;
import com.google.codelabs.mdc.java.shrine.network.Device;
import com.google.codelabs.mdc.java.shrine.network.ImageRequester;
import com.google.codelabs.mdc.java.shrine.network.Message;
import com.google.codelabs.mdc.java.shrine.network.Reading;
import com.google.codelabs.mdc.java.shrine.network.Rules;
import com.google.codelabs.mdc.java.shrine.staggeredgridlayout.MySingleton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessagesView extends RecyclerView.Adapter<MessagesView.ViewHolder> {

    private LayoutInflater mInflater;

    private MessagesView.ItemClickListener mClickListener;
    private MessagesView.ItemLongClickListener mLongClickListener;
    private ArrayList<Contact> mData;
    private ImageRequester imageRequester;

    // data is passed into the constructor
    MessagesView(Context context, ArrayList<Contact> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        imageRequester = ImageRequester.getInstance();
    }

    // inflates the row layout from xml when needed
    @Override
    public MessagesView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.contact, parent, false);
        return new MessagesView.ViewHolder(view);

    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(MessagesView.ViewHolder holder, int position) {
        String name = mData.get(position).getName();
        String recentMessage = mData.get(position).getRecentMessage();
        String date = mData.get(position).getDate();
        String url = mData.get(position).getUrl();

        holder.name.setText(name);
        holder.recentMessage.setText(recentMessage);
        holder.date.setText(date);
        imageRequester.setImageFromUrl(holder.userImage, url);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    int getID(int id) {
        System.out.println(mData.get(id).getId());
        return mData.get(id).getId();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView name;
        TextView recentMessage;
        TextView date;
        NetworkImageView userImage;

        ViewHolder(View itemView) {
            super(itemView);
            name  = itemView.findViewById(R.id.username);
            recentMessage = itemView.findViewById(R.id.recentMessage);
            date = itemView.findViewById(R.id.date);
            userImage = itemView.findViewById(R.id.product_image);


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
    void setClickListener(MessagesView.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    void setLongClickListener(MessagesView.ItemLongClickListener itemClickListener) {
        this.mLongClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);

    }
    public interface ItemLongClickListener{
        void onItemLongClick(View view, int position);
    }

    public void updateList(ArrayList<Contact> list){
        mData = list;
        notifyDataSetChanged();
    }
}

