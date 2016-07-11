package com.sam_chordas.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brendan on 5/10/2016.
 */
public class StockWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockRemoteViewsFactory(this.getApplicationContext(), intent);
    }


    class StockRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{
    // RemoteViewsFactory is a thin wrapper around adapter, responsible for making a RemoteViews object
    // for each data item in a more collection view (ListView, etc)

        private Context mContext;
        private int mAppWidgetId;
        List<String> mSymbols = new ArrayList<>();
        List<Double> mBidPriceList = new ArrayList<>();
        Cursor cursor;


        public StockRemoteViewsFactory(Context context, Intent intent){

            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.d("intent get", String.valueOf(mAppWidgetId));
        }

        @Override
        public void onCreate() {
            Log.d("1", "");
//            for (int i = 0; i < 10; i ++){
//                mSymbols.add(i + "!");
//                Log.d("2", mSymbols.get(i));
//            }
//            if (cursor != null){
//                cursor.close();
//            }
            String[] mDataColumns = {com.sam_chordas.android.stockhawk.data.QuoteDatabase.QUOTES + "." + QuoteColumns._ID,
                    QuoteColumns.SYMBOL,
                    QuoteColumns.BIDPRICE,
                    QuoteColumns.PERCENT_CHANGE,
                    QuoteColumns.CHANGE,
                    QuoteColumns.ISUP};
            cursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI, mDataColumns, QuoteColumns.ISCURRENT + "=?", new String[]{"1"}, null);
            Log.d("cursor number", String.valueOf(cursor.getCount()));
            Log.d("after 3 a", String.valueOf(cursor.getColumnIndex(QuoteColumns.BIDPRICE)));
            cursor.moveToFirst();
            for (int i = 0; i <= cursor.getCount() && !cursor.isAfterLast(); i++){
                Log.d("after 3", String.valueOf(cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL))));
                String symbol = cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL));
                mSymbols.add(symbol);

                double bidPrice = cursor.getDouble(cursor.getColumnIndex(QuoteColumns.BIDPRICE));
                mBidPriceList.add(bidPrice);
                cursor.moveToNext();
            }

        }

        @Override
        public void onDataSetChanged() {
            Log.d("4 on dataset", "");
            if (cursor != null){
                Log.d("odsc not null", "not null");
                cursor.close();
            }

            final long token = Binder.clearCallingIdentity();
            try {
                cursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI, new String[]
                        {QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE}, QuoteColumns.ISCURRENT + " = ?", new String[]{"1"}, null);

                cursor.moveToFirst();
                for (int i = 0; i <= cursor.getCount() && !cursor.isAfterLast(); i++) {
                    Log.d("after 3 dat set changed", String.valueOf(cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL))));
                    String symbol = cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL));
                    mSymbols.add(symbol);

                    double bidPrice = cursor.getDouble(cursor.getColumnIndex(QuoteColumns.BIDPRICE));
                    mBidPriceList.add(bidPrice);
                    cursor.moveToNext();
                }

            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        @Override
        public void onDestroy() {
            Log.d("on destroy", "");

        }

        @Override
        public int getCount() {
            Log.d("7", String.valueOf(cursor.getCount()));
            return (cursor.getCount() > 0) ? cursor.getCount() : 1;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Log.d("8", "");
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
//            String stockSymbol = cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL));
            rv.setTextViewText(R.id.widget_stock_symbol, mSymbols.get(position));
            rv.setTextViewText(R.id.widget_price, String.valueOf(mBidPriceList.get(position)));
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            Log.d("6", "");
            return null;
        }

        @Override
        public int getViewTypeCount() {
            Log.d("5", "");
            return 1;
        }

        @Override
        public long getItemId(int position) {
            Log.d("get item id", "");
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
