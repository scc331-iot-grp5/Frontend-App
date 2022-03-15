package com.google.codelabs.mdc.java.shrine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.codelabs.mdc.java.shrine.network.Device;
import com.google.codelabs.mdc.java.shrine.network.Type;
import com.google.codelabs.mdc.java.shrine.staggeredgridlayout.MySingleton;

import org.json.JSONObject;

import java.util.List;

public class ObjectAdapter extends RecyclerView.Adapter<ObjectAdapter.ViewHolder> {

    private List<Type> mData;
    private LayoutInflater mInflater;

    private ObjectAdapter.ItemClickListener mClickListener;
    private ObjectAdapter.ItemLongClickListener mLongClickListener;

    String connection = "https://5f6b-148-88-245-64.ngrok.io";

    // data is passed into the constructor
    ObjectAdapter(Context context, List<Type> data){
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ObjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.objects_row, parent, false);
        return new ObjectAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ObjectAdapter.ViewHolder holder, int position) {
        String name = mData.get(position).getName();
        holder.name.setText(name);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            System.out.println("Im bot here");
            if (mLongClickListener != null)
                mLongClickListener.onItemLongClick(view, getAdapterPosition());
            Toast.makeText(view.getContext(), "Position is " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
            new MaterialAlertDialogBuilder(view.getContext())
                    .setTitle(R.string.d_tittle)
                    .setMessage(R.string.objectMessage)
                    .setPositiveButton(R.string.d_remove, (dialog, which) -> {
                        JSONObject json = new JSONObject();

                        try {
                            json.put("id", getId(getAdapterPosition()));
                        } catch (Exception e) {
                        }

                        String url = connection + "/deleteObject";

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
    String getItem(int id) {
        return mData.get(id).getName();
    }
    int getId(int id) {
        return mData.get(id).getId();
    }

    // allows clicks events to be caught
    void setClickListener(ObjectAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    void setLongClickListener(ObjectAdapter.ItemLongClickListener itemClickListener) {
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

    public void updateList(List<Type> list){
        mData = list;
        notifyDataSetChanged();
    }
}

