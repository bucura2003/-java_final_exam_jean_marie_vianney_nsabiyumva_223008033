package com.transport;

import java.awt.Color;

public class DashboardStat {
    private String title;
    private String value;
    private String icon;
    private Color color;
    
    public DashboardStat(String title, String value, String icon, Color color) {
        this.title = title;
        this.value = value;
        this.icon = icon;
        this.color = color;
    }
    
    public String getTitle() { return title; }
    public String getValue() { return value; }
    public String getIcon() { return icon; }
    public Color getColor() { return color; }
}