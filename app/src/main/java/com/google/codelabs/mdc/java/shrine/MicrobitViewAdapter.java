package com.google.codelabs.mdc.java.shrine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.codelabs.mdc.java.shrine.network.ProductEntry;
import com.google.codelabs.mdc.java.shrine.network.Device;

import java.util.List;

public class MicrobitViewAdapter extends RecyclerView.Adapter<MicrobitViewAdapter.ViewHolder> {

    private List<Device> mData;

    private LayoutInflater mInflater;
    private MicrobitViewAdapter.ItemClickListener mClickListener;


    // data is passed into the constructor
    MicrobitViewAdapter(Context context, List<Device> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView microbitID;
        TextView objectName;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.m_name);
            microbitID = itemView.findViewById(R.id.microbitID);
            objectName = itemView.findViewById(R.id.m_object);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
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

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void updateList(List<Device> list){
        mData = list;
        notifyDataSetChanged();
    }
}

