package com.example.iotairpurifier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList record_id, date, preAirQuality, preGasSensor, postAirQuality, postGasSensor;
    int reversePosition;

    public CustomAdapter(Context context, ArrayList record_id, ArrayList date, ArrayList preAirQuality, ArrayList preGasSensor,
                  ArrayList postAirQuality,
                  ArrayList postGasSensor){
        this.context = context;
        this.record_id = record_id;
        this.date = date;
        this.preAirQuality = preAirQuality;
        this.preGasSensor = preGasSensor;
        this.postAirQuality = postAirQuality;
        this.postGasSensor = postGasSensor;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        reversePosition = getItemCount() - position - 1;
        holder.txtDate.setText(String.valueOf(date.get(reversePosition)));
        holder.txtPreAir.setText(String.valueOf(preAirQuality.get(reversePosition)) + " µg/m3");
        holder.txtPreGas.setText(String.valueOf(preGasSensor.get(reversePosition)) + " PPM");
        holder.txtPostAir.setText(String.valueOf(postAirQuality.get(reversePosition)) + " µg/m3");
        holder.txtPostGas.setText(String.valueOf(postGasSensor.get(reversePosition)) + " PPM");
    }

    @Override
    public int getItemCount() {
        return record_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtDate, txtPreAir, txtPreGas, txtPostAir, txtPostGas;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.recordDateTimeFinal);
            txtPreAir = itemView.findViewById(R.id.recordPreAirQualityFinal);
            txtPreGas = itemView.findViewById(R.id.recordPreGasFinal);
            txtPostAir = itemView.findViewById(R.id.recordPostAirQualityFinal);
            txtPostGas = itemView.findViewById(R.id.recordPostGasFinal);
        }
    }
}
