package com.google.codelabs.mdc.java.shrine;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.codelabs.mdc.java.shrine.network.Device;
import com.google.codelabs.mdc.java.shrine.network.Conditions;
import com.google.codelabs.mdc.java.shrine.staggeredgridlayout.MySingleton;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;
import java.util.concurrent.locks.Condition;

public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.ViewHolder>{

    private List<Conditions> mData;
    private LayoutInflater mInflater;
    private String symbol;
    private RuleAdapter.ItemClickListener mClickListener;
    private AdapterView.OnItemSelectedListener x;
    TextInputEditText value;
    TextView condition;


    // data is passed into the constructor
    RuleAdapter(Context context, List<Conditions> data){
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public RuleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.new_rule_row, parent, false);
        return new RuleAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RuleAdapter.ViewHolder holder, int position) {
        String nameTwo = mData.get(position).getName();
        condition.setText(nameTwo);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public String getListSymbol(int pos){
        return mData.get(pos).getOperator();
    }

    public String getValue(int pos){
        return mData.get(pos).getValue();
    }

    public String getName(int pos){
        return mData.get(pos).getName();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher {
        Spinner spinner;

        ViewHolder(View itemView) {
            super(itemView);
            condition = itemView.findViewById(R.id.condition);
            value = itemView.findViewById(R.id.valueEditText);

            spinner = (Spinner) itemView.findViewById(R.id.conditions);
            ArrayAdapter<CharSequence> adapterList = ArrayAdapter.createFromResource(itemView.getContext(),
                    R.array.Operators, android.R.layout.simple_spinner_item);
            adapterList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapterList);

            itemView.setOnClickListener(this);
            spinner.setOnItemSelectedListener(this);
            value.addTextChangedListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            System.out.println("Selected " + adapterView.getItemAtPosition(i).toString());
            String symbol =  adapterView.getItemAtPosition(i).toString();
            mData.get(getAdapterPosition()).setOperator(symbol);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            mData.get(getAdapterPosition()).setValue(editable.toString());
        }
    }


    // allows clicks events to be caught
    void setClickListener(RuleAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    void setSpinnerListener(AdapterView.OnItemSelectedListener x) {
        this.x = x;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);

    }

    public void updateList(List<Conditions> list){
        mData = list;
        notifyDataSetChanged();
    }
}

