package com.esri.arcgisruntime.sample.displaymap.legend;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.List;

public class Legend extends View {

    private String title;

    private List<LegendItem> legendItems;

    private int xPosition;

    private int yPosition;

    private final int itemSize = 70;

    public Legend(Context context, String title, List<LegendItem> legendItems, int xPosition, int yPosition) {

        super(context);
        this.title = title;
        this.legendItems = legendItems;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    };

    @Override
    protected void onDraw(Canvas canvas) {

        int xPosition = this.xPosition;
        int yPosition = this.yPosition;

        Paint titleStyle = new Paint();
        titleStyle.setColor(Color.BLUE);
        titleStyle.setFakeBoldText(true);
        titleStyle.setTextSize(48);

        Paint labelStyle = new Paint();
        labelStyle.setColor(Color.BLACK);
        labelStyle.setFakeBoldText(true);
        labelStyle.setTextSize(24);

        Paint legendBoxStyle = new Paint();
        legendBoxStyle.setStyle(Paint.Style.FILL);
        legendBoxStyle.setColor(Color.WHITE);

        canvas.drawRect(xPosition, yPosition - itemSize/2, xPosition + 250, yPosition + (itemSize * 8), legendBoxStyle);

        canvas.drawText(title.substring(0, title.indexOf("\n")), xPosition, yPosition + itemSize/2, titleStyle);
        yPosition = yPosition + itemSize;
        canvas.drawText(title.substring(title.indexOf("\n") + 1), xPosition, yPosition + itemSize/2, titleStyle);

        for(LegendItem legendItem : legendItems) {

            yPosition = yPosition + itemSize;
            canvas.drawBitmap(legendItem.getSymbol(), xPosition, yPosition, new Paint());
            canvas.drawText(legendItem.getLabel(), xPosition + 70, yPosition + itemSize/2, labelStyle);
        }
    }
}
