package com.sam_chordas.android.stockhawk.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;


/**
 * Created by asheph on 3/27/16.
 */
public class StockWidgetIntentService extends IntentService {

    private static final String[] QUOTE_COLUMNS = {


    };

    public StockWidgetIntentService() {
        super("StockWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                QuoteWidgetProvider.class));

        Cursor data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{ QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        if(data == null) {
            return;
        }

        if(!data.moveToFirst()) {
            data.close();
            return;
        }
        data.moveToFirst();
        String symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
        String percentChange = data.getString(data.getColumnIndex(QuoteColumns.PERCENT_CHANGE));
        String price = data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));
        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {

            int layoutId = R.layout.widget_large;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);
            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Add the data to the RemoteViews
            views.setTextViewText(R.id.stock_symbol, symbol);
            views.setTextViewText(R.id.change, percentChange);
            views.setTextViewText(R.id.bid_price, price);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

}

