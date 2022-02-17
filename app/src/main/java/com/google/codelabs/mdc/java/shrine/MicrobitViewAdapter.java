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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.codelabs.mdc.java.shrine.network.Device;
import com.google.codelabs.mdc.java.shrine.staggeredgridlayout.MySingleton;

import org.json.JSONObject;

import java.util.List;

public class MicrobitViewAdapter extends RecyclerView.Adapter<MicrobitViewAdapter.ViewHolder> {

    private List<Device> mData;
    private int uID;
    private LayoutInflater mInflater;
    private View backView;
    private MicrobitViewAdapter.ItemClickListener mClickListener;
    private MicrobitViewAdapter.ItemLongClickListener mLongClickListener;


    // data is passed into the constructor
    MicrobitViewAdapter(Context context, List<Device> data,int x, View v){
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.uID = x;
        this.backView = v;
    }

    // inflates the row layout from xml when needed
    @Override
    public MicrobitViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.microbit_row, parent, false);
        return new MicrobitViewAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(MicrobitViewAdapter.ViewHolder holder, int position) {
        String nameTwo = mData.get(position).getName();


        String objectName = mData.get(position).getObjectName();
        holder.objectName.setText(nameTwo);
        holder.name.setText(objectName);

        String id = Integer.toString(mData.get(position).getMicrobitID());
        holder.microbitID.setText(id);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView name;
        TextView microbitID;
        TextView objectName;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.m_name);
            microbitID = itemView.findViewById(R.id.microbitID);
            objectName = itemView.findViewById(R.id.m_object);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition(), backView);
        }

        @Override
        public boolean onLongClick(View view) {

            if (uID != 0) {
                System.out.println("Im bot here");
                if (mLongClickListener != null)
                    mLongClickListener.onItemLongClick(view, getAdapterPosition());
                Toast.makeText(view.getContext(), "Position is " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                new MaterialAlertDialogBuilder(view.getContext())
                        .setTitle(R.string.d_tittle)
                        .setMessage(R.string.d_extra_2)
                        .setPositiveButton(R.string.d_remove, (dialog, which) -> {
                            JSONObject json = new JSONObject();

                            try {
                                json.put("mId", getItem(getAdapterPosition()));
                                json.put("uID", uID);
                            } catch (Exception e) {
                            }

                            String url = "https://f074-86-4-178-72.ngrok.io/unass";

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
            } else {
                System.out.println("Im bot here");
                if (mLongClickListener != null)
                    mLongClickListener.onItemLongClick(view, getAdapterPosition());
                Toast.makeText(view.getContext(), "Position is " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                new MaterialAlertDialogBuilder(view.getContext())
                        .setTitle(R.string.d_tittle)
                        .setMessage(R.string.d_extra_3)
                        .setPositiveButton(R.string.d_remove, (dialog, which) -> {
                            JSONObject json = new JSONObject();

                            try {
                                json.put("mId", getItem(getAdapterPosition()));
                            } catch (Exception e) {
                            }

                            String url = "https://f074-86-4-178-72.ngrok.io/removeM";

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
    }

    // convenience method for getting data at click position
    int getItem(int id) {
        return mData.get(id).getMicrobitID();
    }

    // allows clicks events to be caught
    void setClickListener(MicrobitViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    void setLongClickListener(MicrobitViewAdapter.ItemLongClickListener itemClickListener) {
        System.out.println("Im dad here");
        this.mLongClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position, View big);

    }
    public interface ItemLongClickListener{
        void onItemLongClick(View view, int position);
    }

    public void updateList(List<Device> list){
        mData = list;
        notifyDataSetChanged();
    }
}

