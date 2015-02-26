package com.jjtech.newap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupMenu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends Activity {
    private Timer timer = null;
    private ExecutorService es= Executors.newSingleThreadExecutor();
    private Handler handler = new Handler();
    private boolean playing = false;
    private float max_r;
    private DataView dataView;
    private RelativeLayout menuLayout;
    private LinearLayout backLayout;
    private LinearLayout subLayout;
    private LinearLayout LogLayout;
    private LinearLayout graphLayout;
    private LinearLayout menuText;
    private Button menubtn;
    private Button graphButton;
    private Button logButton;
    private TextView subTitle;
    private ImageView subImage;
    private ListView logList;
    private GraphView graph;
    private myListView m_adapter;
    private myGraphView m_graph;
    private int deviceWidth;
    private int deviceHeight;
    private boolean isLive = true;
    private boolean subOpen = false;
    private int subOpenType;
    private DataPoint[] y_data, t_data;
    private int[] t_cnt ={0,0,0,0,0,0};
    private APsetting m_dns;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

        initLayout();
        initEvent();

        drawView();
        getLiveData();
    }

    public void initLayout(){
        backLayout = (LinearLayout)findViewById(R.id.backLayout);
        subLayout = (LinearLayout)findViewById(R.id.subLayout);
        graphLayout = (LinearLayout)findViewById(R.id.graphLayout);
        LogLayout = (LinearLayout)findViewById(R.id.LogLayout);
        menuText = (LinearLayout)findViewById(R.id.menuText);
        menuLayout = (RelativeLayout)findViewById(R.id.menuLayout);
        menubtn = (Button)findViewById(R.id.menuBtn);
        logButton = (Button)findViewById(R.id.logBtn);
        graphButton = (Button)findViewById(R.id.graphBtn);
        subTitle = (TextView)findViewById(R.id.subTitle);
        subImage = (ImageView)findViewById(R.id.subImage);
        logList = (ListView) findViewById(R.id.listview);
        graph = (GraphView) findViewById(R.id.graph);

        backLayout.setVisibility(View.INVISIBLE);
        subLayout.setVisibility(View.INVISIBLE);

        m_adapter = new myListView(this);
        m_graph = new myGraphView(graph);
        m_dns = new APsetting(this);
        logList.setAdapter(m_adapter);
        m_graph.init();
        //onPopupButtonClick(menubtn);
    }

    public void initEvent(){
        menubtn.setOnClickListener(new View.OnClickListener(){
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                if(!subOpen)
                    onPopupButtonClick(view);
                else
                    closeSubLayout();
            }
        });
        graphButton.setOnClickListener(new View.OnClickListener(){
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                graphLayout.setVisibility(View.VISIBLE);
                LogLayout.setVisibility(View.GONE);
                logButton.setBackground(view.getResources().getDrawable(R.drawable.log));
                graphButton.setBackground(view.getResources().getDrawable(R.drawable.graph_active));
            }
        });
        logButton.setOnClickListener(new View.OnClickListener(){
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                graphLayout.setVisibility(View.GONE);
                LogLayout.setVisibility(View.VISIBLE);
                logButton.setBackground(view.getResources().getDrawable(R.drawable.log_active));
                graphButton.setBackground(view.getResources().getDrawable(R.drawable.graph));
                //getLogData(subOpenType);
            }
        });
        menuLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeSubLayout();
                return false;
            }
        });
        menuText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                closeSubLayout();
            }
        });
    }

    public void drawView(){
        float width;
        float height;
        float radius;
        float margin_x;
        float margin_y;
        final int menuHeight, menuWidth;

        RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
        mainLayout.removeAllViews();

        if(deviceHeight>deviceWidth){
            width = deviceWidth;
            height = (float) ((float)deviceHeight * 0.85);
            radius = (float) (width/6); max_r = radius;
            margin_x = radius + (width/12);
            margin_y = radius + (height/20);

            menuHeight = (int)(deviceHeight * 0.45);
            subLayout.setMinimumHeight(menuHeight);

            backLayout.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getY() < deviceHeight-menuHeight)
                        closeSubLayout();
                    return false;
                }
            });
        }
        else{
            width = deviceWidth;
            height = (float) ((float)deviceHeight * 0.75);
            radius = (float) (height/6); max_r = radius;
            margin_x = radius + (width/8);
            margin_y = radius + (height/12);

            menuHeight = (int)(height);
            subLayout.setMinimumHeight(menuHeight);

            backLayout.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
        }

        dataView = new DataView(this, width, height, margin_x, margin_y, radius);
        mainLayout.addView(new backgroundview(this, width, height, margin_x, margin_y, radius));
        mainLayout.addView(dataView);
        YoYo.with(Techniques.BounceInDown).duration(800).playOn(mainLayout);
    }

    private void setLive(){
        timer.cancel();
        isLive = true;
        setTitle("LIVE");
        setTime();
        getLiveData();
    }

    private void setToday(){
        timer.cancel();
        isLive = false;
        setTitle("TODAY");
        setDay();
        getDailyData();
    }
    private void setTitle(final String name){
        handler.post(new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                TextView title = (TextView)findViewById(R.id.title);
                title.setText(name);
            }
        });
    }

    private void setTime(){
        handler.post(new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                TextView timer = (TextView)findViewById(R.id.date);
                SimpleDateFormat now = new SimpleDateFormat("HH:mm:ss");
                String time = now.format(new Date(System.currentTimeMillis()));
                timer.setText(time);
            }
        });
    }

    private void setDay(){
        handler.post(new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                TextView timer = (TextView)findViewById(R.id.date);
                SimpleDateFormat now = new SimpleDateFormat("yy-MM-dd");
                String time = now.format(new Date(System.currentTimeMillis()));
                timer.setText(time);
            }
        });
    }

    private void getLiveData(){
        es.submit(new getLiveDataTask("http://app.safeap.net/api/realtime"));
    }

    private void getDailyData(){
        es.submit(new getDailyDataTask("http://app.safeap.net/api/realtime"));
    }

    private void getLogData(int type){
        m_adapter.refresh();
        es.submit(new getLogDataTask("http://app.safeap.net/api/recenturl?clsid="+getIds(type)));
    }

    private void getGraphData(int type){
        es.submit(new getGraphDataTask("http://app.safeap.net/api/graph?clsid="+getIds(type)));
    }

    private String getIds(int type){
        String str = "";
        for(int i=0; i< db.CATEGORY[type].length; i++){
            if(i== (db.CATEGORY[type].length - 1))
                str += db.CATEGORY[type][i];
            else {
                str += db.CATEGORY[type][i];
                str += ",";
            }
        }
        return str;
    }

    private class getLiveDataTask implements Runnable{
        private String serverURL;
        public getLiveDataTask(String serverURL) {
            this.serverURL = serverURL;
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    setTime();
                    PasingLiveData(httpRequest(serverURL));
                }
            }, 0, 3000);
        }
    }

    private class getDailyDataTask implements Runnable{
        private String serverURL;
        public getDailyDataTask(String serverURL) {
            this.serverURL = serverURL;
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub
            PasingDailyData();
        }
    }

    private class getLogDataTask implements Runnable{
        private String serverURL;
        public getLogDataTask(String serverURL) {
            this.serverURL = serverURL;
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub
            PasingLogData(httpRequest(serverURL));
        }
    }

    private class getGraphDataTask implements Runnable{
        private String serverURL;
        public getGraphDataTask(String serverURL) {
            this.serverURL = serverURL;
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub
            PasingGraphData(httpRequest(serverURL));
        }
    }

    private void PasingLiveData(String str){
        if(str == null) return;
        try {
            final int[] m_cnt = {0,0,0,0,0,0};

            JSONArray jsonArray = new JSONArray(str);
            for(int i = 0 ; i<jsonArray.length() ; i++){
                JSONObject json = jsonArray.getJSONObject(i);
                int id = Integer.parseInt(json.getString("clsid"));
                int cnt = Integer.parseInt(json.getString("cnt"));
                switch(id){
                    //Category 1 : ����
                    case db.ARTS: case db.BUSINESS: case db.TRANSPORTATION: case db.FORUMS:
                    case db.COMPROMISED: case db.COMPUTERS: case db.EDUCATION: case db.FINANCE:
                    case db.GOVERNMENT: case db.TRANSLATOR: case db.EMAIL: case db.SOFTWARE:
                    case db.SECURITY: case db.IP:
                        m_cnt[db.CATEGORY_1] += cnt; break;
                    //category 2 : �˻�
                    case db.ADVERTISEMENTS: case db.JOBSEARCH: case db.SEARCH: case db.IMAGESHARE: case db.NEWS:
                        m_cnt[db.CATEGORY_2] += cnt; break;
                    //category 4 : �Ҽ�
                    case db.CHAT: case db.DATING: case db.STREAMING: case db.PERSONAL: case db.REALESTATE:
                    case db.RELIGION: case db.RESTAURANTS: case db.SHOPPING: case db.SNS: case db.TRAVEL:
                    case db.FASHION: case db.GREETING: case db.MESSASING:
                        m_cnt[db.CATEGORY_4] += cnt; break;
                    //category 5 : ����
                    case db.DOWNLOAD: case db.ENTERTAINMENT: case db.GAMBLING: case db.GAMES: case db.SPORTS:
                    case db.LEISURE: case db.P2P:
                        m_cnt[db.CATEGORY_5] += cnt; break;
                    //category 6 : ����
                    case db.ALCOHOL: case db.ANONYMIZERS: case db.CRIMINAL: case db.HATE: case db.DRUG:
                    case db.NUDITY: case db.PHISHING: case db.PORNOGRAPHY: case db.SPAM: case db.MALWARE:
                    case db.VIOLENCE: case db.WEAPONS: case db.CULT: case db.HACKING: case db.CHEATING:
                    case db.SEXEDUCATION: case db.CHILDABUSE:
                        m_cnt[db.CATEGORY_6] += cnt; break;
                    //category 3 : ��Ÿ
                    default:
                        m_cnt[db.CATEGORY_3] += cnt;
                }
            }

            handler.post(new Runnable(){
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    for(int i=0; i<6; i++){
                        dataView.setData(i,m_cnt[i], m_cnt[i]*30);
                    }
                }
            });

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
    }

    private void PasingDailyData(){
        for(int i=0; i<6; i++){
            t_cnt[i] = 0;
        }

        for(int i=0; i<6; i++){
            String data = httpRequest("http://app.safeap.net/api/graph?clsid="+getIds(i));
            if(data == null) continue;
            try {
                JSONObject json = new JSONObject(data);
                JSONArray today = json.getJSONArray("today");
                int t_len = today.length();
                for(int j = 0 ; j<t_len; j++){
                    JSONObject obj = today.getJSONObject(j);
                    t_cnt[i] += Integer.parseInt(obj.getString("cnt"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        handler.post(new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                for(int i=0; i<6; i++){
                    dataView.setData(i, t_cnt[i], max_r );
                }
            }
        });
    }

    private void PasingLogData(String str) {
        if (str == null){
            Log.d("PasingLogData", "null");
            return;
        }
        try {

            JSONArray jsonArray = new JSONArray(str);
            for(int i = 0 ; i<jsonArray.length() ; i++){
                if(i>=5) break;
                JSONObject json = jsonArray.getJSONObject(i);
                final String url = json.getString("qname");
                final String time = json.getString("udate");
                handler.post(new Runnable(){
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        m_adapter.add(time, url);
                        m_adapter.notifyDataSetChanged();
                    }
                });
            }
        }catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("PasingLogData", "JSONException");
            return;
        }
    }

    private void PasingGraphData(String str) {
        if (str == null){
            Log.d("PasingGraphData", "null");
            return;
        }
        try {
            JSONObject json = new JSONObject(str);
            JSONArray yesterday = json.getJSONArray("yesterday");
            JSONArray today = json.getJSONArray("today");

            int y_len = yesterday.length();
            int t_len = today.length();
            int max = 0;

            y_data = new DataPoint[y_len];
            t_data = new DataPoint[t_len];

            for(int i=0; i<y_len; i++){
                JSONObject obj = yesterday.getJSONObject(i);
                int cnt = Integer.parseInt(obj.getString("cnt"));
                int hour = Integer.parseInt(obj.getString("uhour"));
                y_data[i] = new DataPoint(hour,cnt);
                if(cnt > max) max = cnt;
            }

            for(int i = 0 ; i<t_len; i++){
                JSONObject obj = today.getJSONObject(i);
                int cnt = Integer.parseInt(obj.getString("cnt"));
                int hour = Integer.parseInt(obj.getString("uhour"));
                t_data[i] = new DataPoint(hour,cnt);
                if(cnt > max) max = cnt;
            }

            final int finalMax = max;
            handler.post(new Runnable(){
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    m_graph.setData(finalMax, y_data, t_data);
                }
            });
        }catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("PasingGraphData", "JSONException");
            return;
        }
    }

    public class backgroundview extends View{
        private Context context;
        private float width;
        private float height;
        private float margin_x;
        private float margin_y;
        private float r;


        public backgroundview(Context context, float width, float height, float margin_x, float margin_y, float r) {
            super(context);
            this.context = context;
            this.width = width;
            this.height = height;
            this.margin_x = margin_x;
            this.margin_y = margin_y;
            this.r = r;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint paint = new Paint();
            paint.setAntiAlias(true);

            if(height>width){
                paint.setARGB(255, 236, 246, 252);
                canvas.drawCircle(margin_x, margin_y, r, paint);
                canvas.drawCircle(margin_x, height/2, r, paint);
                canvas.drawCircle(margin_x, height - margin_y, r, paint);

                paint.setARGB(255, 244, 240, 251);
                canvas.drawCircle(width - margin_x, margin_y, r, paint);
                canvas.drawCircle(width - margin_x, height/2, r, paint);
                canvas.drawCircle(width - margin_x, height - margin_y, r, paint);

                float text_y = r * (float)1.3;
                paint.setARGB(255, 98, 98, 98);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(height/35);

                canvas.drawText("Work", margin_x, margin_y + text_y, paint);
                canvas.drawText("Search", margin_x, height/2 + text_y, paint);
                canvas.drawText("Other", margin_x, height - margin_y + text_y, paint);

                canvas.drawText("SNS", width - margin_x, margin_y + text_y, paint);
                canvas.drawText("Game", width - margin_x, height/2 + text_y, paint);
                canvas.drawText("Adult", width - margin_x, height - margin_y + text_y, paint);

            }else{
                paint.setARGB(255, 236, 246, 252);
                canvas.drawCircle(margin_x, margin_y, r, paint);
                canvas.drawCircle(width/2, margin_y, r, paint);
                canvas.drawCircle(width-margin_x, margin_y, r, paint);

                paint.setARGB(255, 244, 240, 251);
                canvas.drawCircle(margin_x, height-margin_y, r, paint);
                canvas.drawCircle(width/2, height-margin_y, r, paint);
                canvas.drawCircle(width-margin_x, height-margin_y, r, paint);

                float text_y = r * (float)1.4;
                paint.setARGB(255, 98, 98, 98);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(height/20);

                canvas.drawText("Work", margin_x, margin_y + text_y, paint);
                canvas.drawText("Search", width/2, margin_y + text_y, paint);
                canvas.drawText("Other", width-margin_x, margin_y + text_y, paint);

                canvas.drawText("SNS", margin_x, height-margin_y + text_y, paint);
                canvas.drawText("Game", width/2, height-margin_y + text_y, paint);
                canvas.drawText("Adult", width-margin_x, height-margin_y + text_y, paint);
            }
        }
    }



    public class DataView extends View{
        private Context context;
        private float width;
        private float height;
        private float margin_x;
        private float margin_y;
        private Data data[];

        public class Data{
            public int v;
            public float x;
            public float y;
            public float r;
        }

        public DataView(Context context, float width, float height, float margin_x, float margin_y, float r){
            super(context);

            this.context = context;
            this.width = width;
            this.height = height;
            this.margin_x = margin_x;
            this.margin_y = margin_y;
            this.data = new Data[6];

            for(int i=0; i<6; i++){
                data[i] = new Data();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint paint = new Paint();
            paint.setAntiAlias(true);

            if(height>width){
                setDataXY(db.CATEGORY_1, margin_x, margin_y);
                setDataXY(db.CATEGORY_2, margin_x, height/2);
                setDataXY(db.CATEGORY_3, margin_x, height - margin_y);
                setDataXY(db.CATEGORY_4, width - margin_x, margin_y);
                setDataXY(db.CATEGORY_5, width - margin_x, height/2);
                setDataXY(db.CATEGORY_6, width - margin_x, height - margin_y);
            }else{
                setDataXY(db.CATEGORY_1, margin_x, margin_y);
                setDataXY(db.CATEGORY_2, width/2, margin_y);
                setDataXY(db.CATEGORY_3, width-margin_x, margin_y);
                setDataXY(db.CATEGORY_4, margin_x, height-margin_y);
                setDataXY(db.CATEGORY_5, width/2, height-margin_y);
                setDataXY(db.CATEGORY_6, width - margin_x, height - margin_y);
            }

            for(int i=0; i<6; i++){
                switch(i){
                    case db.CATEGORY_1: case db.CATEGORY_2: case db.CATEGORY_3:
                        paint.setARGB(255, 122, 192, 236); break;
                    case db.CATEGORY_4: case db.CATEGORY_5: case db.CATEGORY_6:
                        paint.setARGB(255, 177, 137, 243); break;
                }
                canvas.drawCircle(data[i].x, data[i].y, data[i].r, paint);
                paint.setColor(Color.WHITE);
                paint.setTextAlign(Paint.Align.CENTER);
                if(!isLive){
                    paint.setTextSize(data[i].r/2);
                    canvas.drawText(String.valueOf(data[i].v), data[i].x, data[i].y + data[i].r/8, paint);
                }else{
                    paint.setTextSize(data[i].r);
                    canvas.drawText(String.valueOf(data[i].v), data[i].x, data[i].y + data[i].r/4, paint);
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            if(subOpen) return false;

            float pX, pY;
            pX = event.getX();
            pY = event.getY();
            boolean[] touch = new boolean[6];

            if(event.getAction() == event.ACTION_DOWN) {
                for (int i = 0; i < 6; i++) {
                    touch[i] = (data[i].x > (pX - max_r) && data[i].x < (pX + max_r) && data[i].y > (pY - max_r) && data[i].y < (pY + max_r));
                    if (touch[i] && !subOpen) {
                        openSubLayout(i);
                        final int index = i;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getLogData(index);
                                getGraphData(index);
                            }
                        }, 300);
                        switch (i){
                            case db.CATEGORY_1: subTitle.setText("Work"); subImage.setImageResource(R.drawable.work); break;
                            case db.CATEGORY_2: subTitle.setText("Search"); subImage.setImageResource(R.drawable.search); break;
                            case db.CATEGORY_3: subTitle.setText("Other"); subImage.setImageResource(R.drawable.other);break;
                            case db.CATEGORY_4: subTitle.setText("SNS"); subImage.setImageResource(R.drawable.bad);break;
                            case db.CATEGORY_5: subTitle.setText("Game"); subImage.setImageResource(R.drawable.game); break;
                            case db.CATEGORY_6: subTitle.setText("Adult"); subImage.setImageResource(R.drawable.adult); break;
                        }
                        backLayout.setVisibility(View.VISIBLE);
                        subLayout.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.SlideInUp).duration(250).playOn(subLayout);
                    }
                }
            }
            return false;
        }


        public void setData(int type, int v){

            invalidate();
        }

        public void setData(int type, int v, float r){
            this.data[type].v = v;
            if(r> max_r) this.data[type].r = max_r; else this.data[type].r = r;
            invalidate();
        }

        public void setDataXY(int type, float x, float y){
            this.data[type].x = x;
            this.data[type].y = y;
        }

    }

    public void openSubLayout(){
        subOpen = true;
    }

    public void openSubLayout(int type){
        subOpen = true;
        subOpenType = type;
    }

    public void closeSubLayout(){
        if(subOpen) {
            YoYo.with(Techniques.SlideOutDown).duration(250).playOn(subLayout);
            backLayout.setVisibility(View.INVISIBLE);
            m_graph.clear();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    subOpen = false;
                }
            }, 100);
        }
    }

    private String httpRequest(String serverURL){
        String data = "";
        URL url;
        try {
            url = new URL(serverURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1000);
            conn.setRequestMethod("GET");
            int status = conn.getResponseCode();
            if(status == 200){
                InputStream stream = conn.getInputStream();
                InputStreamReader isReader = new InputStreamReader(stream);
                BufferedReader br = new BufferedReader(isReader);
                String str;
                while ((str = br.readLine()) != null){
                    data+=str;
                }
                br.close();
                isReader.close();
                stream.close();
            }else{
                data = null;
                Log.d("httpRequest", "status");
            }
        } catch (Exception e) {
            e.printStackTrace();
            data = null;
            Log.d("httpRequest", "Exception");
        }
        return data;
    }

    public void onPopupButtonClick(View button) {
        PopupMenu popup = new PopupMenu(this, button);
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.live : setLive(); break;
                    case R.id.today: setToday(); break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onStop(){
        super.onStop();
        timer.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(subOpen)
            closeSubLayout();
        else
            this.finish();
    }
}
