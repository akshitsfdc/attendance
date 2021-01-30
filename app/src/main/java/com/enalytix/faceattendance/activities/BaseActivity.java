package com.enalytix.faceattendance.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.enalytix.faceattendance.R;
import com.enalytix.faceattendance.models.Site;
import com.enalytix.faceattendance.utils.FileUtils;
import com.enalytix.faceattendance.utils.Routing;
import com.enalytix.faceattendance.utils.UIUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BaseActivity extends AppCompatActivity {

    protected static String TAG = "BaseActivity";

    protected Routing routing;
    protected FileUtils fileUtils;
    protected UIUtils uiUtils;
    protected Activity context;
    protected Toolbar toolbar;

    long delay = 6000; // delay in milliseconds
    private LoopTask timerTask;
    private Timer timeChangerTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timeChangerTimer = new Timer("timeChanger");
        timerTask= new LoopTask();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.app_background));
        }else{
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if(getSupportActionBar() != null){
//            requestFeature()
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getSupportActionBar().hide();
        }




    }
    protected void setObjects(){

        if(toolbar != null){
            toolbar.setOverflowIcon(ContextCompat.getDrawable(context, R.drawable.ic_menu_overflow));
            toolbar.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                switch (item.getItemId()) {
                    case R.id.logout:
                        fileUtils.saveUserDataInSharedPre(null);
                        routing.navigateAndClear(LoginActivity.class);
                        break;

                }
                return false;
            });
        }
        TAG = context.getLocalClassName();
        routing = new Routing(context);
        fileUtils = new FileUtils(context);
    }
    protected void setUIUtils(int parent){
        uiUtils = new UIUtils(context, parent);
    }


    protected void setThumbnail(){

        Glide.with(context).load(MainActivity.USER_DATA.getThumbnail())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //                holder.progress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .error(R.drawable.ic_log_in).fallback(R.drawable.ic_log_out)
                .into((ImageView) context.findViewById(R.id.profilePicture));
    }

    protected void setTitle(){

        String siteTitle = "";

        List<Site> sites = MainActivity.USER_DATA.getSites();

        for(Site site : sites){
            siteTitle = siteTitle+" || "+site.getSiteName();
        }
        if(siteTitle.length() > 0){
            StringBuilder str  = new StringBuilder(siteTitle);
//            str.deleteCharAt(0);
            if(str.length() >= 4){
                str.delete(0, 3);
            }
            siteTitle = str.toString().trim();
        }else {
            siteTitle = "No Sites";
        }

        ((TextView)context.findViewById(R.id.siteTitle)).setText(siteTitle);

    }

    protected void setCustomTitle(String title){
        ((TextView)context.findViewById(R.id.siteTitle)).setText(title);
    }

    protected void setCurrentDate(){

        Date executionDate = new Date(); // no params = now
        timeChangerTimer.scheduleAtFixedRate(timerTask, executionDate, delay);

        Calendar calendar = Calendar.getInstance();
        String date = String.valueOf(calendar.get(Calendar.DATE));
        String month = uiUtils.getMonthFromIndex(calendar.get(Calendar.MONTH));
        String year = String.valueOf(calendar.get(Calendar.YEAR));

        int hours = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);


        String hr="", mi="";
        if(hours < 10){
            hr = "0"+hours;
        }else {
            hr = ""+hours;
        }
        if (minutes < 10){
            mi = "0"+minutes;
        }else {
            mi = ""+minutes;
        }

        String amPm = uiUtils.getAmOrPm(calendar.get(Calendar.AM_PM));

        String finalTime = date+" "+month+" "+year+", "+hr+":"+mi+" "+amPm;

        ((TextView)context.findViewById(R.id.timeLabel)).setText(finalTime);



    }

    protected void timeChangerTask(){
        Calendar calendar = Calendar.getInstance();
        String date = String.valueOf(calendar.get(Calendar.DATE));
        String month = uiUtils.getMonthFromIndex(calendar.get(Calendar.MONTH));
        String year = String.valueOf(calendar.get(Calendar.YEAR));

        int hours = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);


        String hr="", mi="";
        if(hours < 10){
            hr = "0"+hours;
        }else {
            hr = ""+hours;
        }
        if (minutes < 10){
            mi = "0"+minutes;
        }else {
            mi = ""+minutes;
        }

        String amPm = uiUtils.getAmOrPm(calendar.get(Calendar.AM_PM));

        String finalTime = date+" "+month+" "+year+", "+hr+":"+mi+" "+amPm;

        ((TextView)context.findViewById(R.id.timeLabel)).setText(finalTime);
    }
    protected void showLoading(){
        context.findViewById(R.id.loadingIndicator).setVisibility(View.VISIBLE);
    }
    protected void hideLoading(){
        context.findViewById(R.id.loadingIndicator).setVisibility(View.GONE);
    }

    private class LoopTask extends TimerTask {
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timeChangerTask();
                }
            });

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(timeChangerTimer != null){
            try {
                timeChangerTimer.cancel();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menues, menu);
        return true;
    }
        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: "+item);
            return  true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            View view = getCurrentFocus();
            if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
                int scrcoords[] = new int[2];
                view.getLocationOnScreen(scrcoords);
                float x = ev.getRawX() + view.getLeft() - scrcoords[0];
                float y = ev.getRawY() + view.getTop() - scrcoords[1];
                if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                    ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
            }
            return super.dispatchTouchEvent(ev);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

}