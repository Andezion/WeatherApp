package com.example.lab2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lab2.R;
import com.example.lab2.model.ForecastItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder>
{
    private final List<ForecastItem> forecastList;

    public ForecastAdapter(List<ForecastItem> forecastList) {
        this.forecastList = forecastList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, temp, desc;

        public ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.text_date);
            temp = view.findViewById(R.id.text_temp);
            desc = view.findViewById(R.id.text_desc);
        }
    }

    @NonNull
    @Override
    public ForecastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForecastItem item = forecastList.get(position);
        holder.date.setText(item.date);
        holder.temp.setText(item.temp + "°C");
        holder.desc.setText(item.description);

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd MMM", Locale.getDefault());

        try
        {
            Date date = inputFormat.parse(item.date);
            assert date != null;
            holder.date.setText(outputFormat.format(date));
        }
        catch (Exception e)
        {
            holder.date.setText(item.date);
        }
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }
}
