package com.example.lab2.model;

// Наша модель

public class ForecastItem
{
    public String date;
    public String temp;
    public String description;
    public String iconCode;

    public ForecastItem(String date, String temp, String description, String iconCode)
    {
        this.date = date;
        this.temp = temp;
        this.description = description;
        this.iconCode = iconCode;
    }
}
