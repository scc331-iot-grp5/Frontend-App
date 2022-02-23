package com.google.codelabs.mdc.java.shrine;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.codelabs.mdc.java.shrine.network.ImageRequester;
import com.google.codelabs.mdc.java.shrine.network.ProductEntry;
import com.google.codelabs.mdc.java.shrine.staggeredgridlayout.MySingleton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<ProductEntry> mData;

    private LayoutInflater mInflater;
    private ImageRequester imageRequester;
    private View backView;

    private MyRecyclerViewAdapter.ItemClickListener mClickListener;
    private MyRecyclerViewAdapter.ItemLongClickListener mLongClickListener;

    String connection = "https://6e66-148-88-245-146.ngrok.io";


    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<ProductEntry> data, View v) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.backView = v;
        imageRequester = ImageRequester.getInstance();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animal = mData.get(position).getName();
        holder.myTextView.setText(animal);
        String image = mData.get(position).getURL();
        String id = Integer.toString(mData.get(position).getUserID());
        holder.login.setText(id);
        imageRequester.setImageFromUrl(holder.productImage, image);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView myTextView;
        TextView login;
        NetworkImageView productImage;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.username);
            login = itemView.findViewById(R.id.userID);
            productImage = itemView.findViewById(R.id.product_image);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition(), backView);
        }
        @Override
        public boolean onLongClick(View view) {
            System.out.println("Im bot here");
            if (mLongClickListener != null) mLongClickListener.onItemLongClick(view, getAdapterPosition());
            Toast.makeText(view.getContext(), "Position is " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
            new MaterialAlertDialogBuilder(view.getContext())
                    .setTitle(R.string.d_tittle)
                    .setMessage(R.string.d_extra_2)
                    .setPositiveButton(R.string.d_remove, (dialog, which) -> {
                        JSONObject json = new JSONObject();

                        try {
                            json.put("mId", getItem(getAdapterPosition()));
                        } catch (Exception e) { }

                        String url = connection + "/removeUser";

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                (Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // TODO: Handle error
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO: Handle error

                                    }
                                });

                        MySingleton.getInstance(view.getContext()).addToRequestQueue(jsonObjectRequest);
                    })
                    .setNegativeButton(R.string.d_cancel, (dialog, which) -> {

                    })
                    .show();
            return true;
        }
    }

    // convenience method for getting data at click position
    int getItem(int id) {
        return mData.get(id).getUserID();
    }

    // allows clicks events to be caught
    void setClickListener(MyRecyclerViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    void setLongClickListener(MyRecyclerViewAdapter.ItemLongClickListener itemClickListener) {
        this.mLongClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position, View big);
    }
    public interface ItemLongClickListener{
        void onItemLongClick(View view, int position);
    }

    public void updateList(List<ProductEntry> list){
        mData = list;
        notifyDataSetChanged();
    }
}
