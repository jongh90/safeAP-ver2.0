package com.jjtech.newap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 2015-02-03.
 */
public class myListView extends BaseAdapter {
    private ArrayList<String> times = new ArrayList<String>();
    private ArrayList<String> urls = new ArrayList<String>();
    private TextView time, url;
    private Button link;
    private Context mContext = null;
    private CustomHolder holder = null;

    public myListView(Context mContext){
        super();
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object getItem(int i) {
        return urls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview, null);
            time = (TextView) view.findViewById(R.id.LogTime);
            url = (TextView) view.findViewById(R.id.LogUrl);
            link = (Button) view.findViewById(R.id.LogLink);
            holder = new CustomHolder(time, url, link);
            view.setTag(holder);
        }else{
            holder = (CustomHolder) view.getTag();
        }

        final String url = urls.get(i);
        final String newUrl;

        if(url.length()> 23)
            newUrl = url.substring(0,23) + "...";
        else
            newUrl = url;

        holder.time.setText(times.get(i));
        holder.url.setText(newUrl);
        holder.link.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+url));
                mContext.startActivity(intent);
            }
        });
        return view;
    }

    public void add(String time, String url){
        String ftime;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
            Date date = format.parse(time);
            ftime = format1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            ftime = "00:00:00";
        }
        times.add(ftime);
        urls.add(url);
    }

    public void refresh() {
        times.clear();
        urls.clear();
    }

    private class CustomHolder{
        TextView 	 time;
        TextView	 url;
        Button       link;
        public CustomHolder(TextView time, TextView url, Button link){
            this.time = time;
            this.url = url;
            this.link = link;
        }
    }
}
