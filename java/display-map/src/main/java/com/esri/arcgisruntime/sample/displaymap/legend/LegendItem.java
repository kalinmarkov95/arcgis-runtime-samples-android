package com.esri.arcgisruntime.sample.displaymap.legend;

import android.graphics.Bitmap;

public class LegendItem {

    private String label;

    private Bitmap symbol;

    public LegendItem(String label, Bitmap symbol) {

        this.symbol = symbol;
        this.label = label;
    };

    public String getLabel() {

        return label;
    }

    public Bitmap getSymbol() {

        return symbol;
    }
}
