package com.sam_chordas.android.stockhawk.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Brendan on 5/24/2016.
 */
public class TestGraph extends AppCompatActivity {

    ArrayList<Float> closePrices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String symbol = getIntent().getStringExtra("symbol");

        getStockPrice(symbol);

    }


    private void getStockPrice(String symbol){
        Log.d("get stock price", "k");
//        final ArrayList<Double> closePrices = new ArrayList<>();

        StringBuilder builda = new StringBuilder();
        builda.append("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20" + symbol + "\"%20and%20start" +
                "Date%20%3D%20\"2016-04-11\"%20and%20endDate%20%3D%20\"2016-05-11\"&\" +\n" +
                "diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys");
        String hi = builda.toString();

        Log.d("hi url", hi);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").
                authority("query.yahooapis.com").
                appendPath("v1").
                appendPath("public").
                appendEncodedPath("yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20\"" + symbol + "\"%20and%20start" +
                        "Date%20%3D%20\"2016-04-11\"%20and%20endDate%20%3D%20\"2016-05-11\"&\" +\n" +
                        "\"diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys");
        String historyUrl = builder.build().toString();

        String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20" +
                "where%20symbol%20%3D%20%22" + symbol + "%22%20and%20startDate%20%3D%20%222016-04-11%22%20and%20" +
                "endDate%20%3D%20%222016-05-11%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        Log.d("get stock url", url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    Log.d("on response", "k");
                    JSONObject query = jsonObject.getJSONObject("query");
                    int count = query.getInt("count");

                    JSONObject results = query.getJSONObject("results");
                    JSONArray quote = results.getJSONArray("quote");

                    for (int i=0; i < count; i++){
                        JSONObject dayPrice = quote.getJSONObject(i);
                        float closingPrice = (float) dayPrice.getDouble("Close");
                        Log.d("closing price",String.valueOf(String.format("%.2f", closingPrice)));
                        closePrices.add(closingPrice);
                    }
                    Log.d("size of closing prices", String.valueOf(closePrices.size()));
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("on error", "k");
                System.out.println("Something went wrong");
                volleyError.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}
