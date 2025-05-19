package com.example.lab2.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lab2.R;
import com.example.lab2.model.ForecastItem;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ForecastItem item = forecastList.get(position);
        holder.date.setText(item.date);
        holder.temp.setText(item.temp + "°C");
        holder.desc.setText(item.description);

        ImageView icon = holder.itemView.findViewById(R.id.image_icon);
        String iconUrl = "https://openweathermap.org/img/wn/" + item.iconCode + "@2x.png";
        Glide.with(holder.itemView.getContext()).load(iconUrl).into(icon);

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
