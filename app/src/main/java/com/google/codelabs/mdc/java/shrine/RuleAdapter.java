package com.google.codelabs.mdc.java.shrine;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.Arrays;
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
        String v = mData.get(position).getValue();
        value.setText(v);

        String[] myItems= holder.itemView.getResources().getStringArray(R.array.Operators);
        String[] myItemsTwo = add2BeginningOfArray(myItems,mData.get(position).getOperator());
        ArrayAdapter<CharSequence> adapterList = new ArrayAdapter(holder.itemView.getContext(), android.R.layout.simple_spinner_item, myItemsTwo);
        adapterList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner.setAdapter(adapterList);

        if(mData.get(position).isTrue()){
            holder.i.setImageResource(R.drawable.ic_baseline_check_24);
            holder.i.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.green), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.la.setBackgroundResource(R.color.green);
        }

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

    public String getZone(int pos){
        return mData.get(pos).getZoneGroup();
    }

    public String getObject(int pos){
        return mData.get(pos).getObjectGroup();
    }

    public ArrayList<Integer> getMicrobits(int pos){
        return mData.get(pos).getMicrobits();
    }

    public boolean getTrue(int pos){
        return mData.get(pos).isTrue();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher {
        Spinner spinner;
        ImageView i;
        LinearLayout la;

        ViewHolder(View itemView) {
            super(itemView);
            condition = itemView.findViewById(R.id.condition);
            value = itemView.findViewById(R.id.valueEditText);
            i = itemView.findViewById(R.id.ruleIcon);
            la = itemView.findViewById(R.id.xax);

            spinner = (Spinner) itemView.findViewById(R.id.conditions);

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
    public static <T> T[] add2BeginningOfArray(T[] elements, T element)
    {
        T[] newArray = Arrays.copyOf(elements, elements.length + 1);
        newArray[0] = element;
        System.arraycopy(elements, 0, newArray, 1, elements.length);

        return newArray;
    }
    public void updateList(List<Conditions> list){
        mData = list;
        notifyDataSetChanged();
    }
}

