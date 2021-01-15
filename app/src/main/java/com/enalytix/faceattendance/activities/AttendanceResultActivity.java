package com.enalytix.faceattendance.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.io.File;
import java.util.Calendar;
import java.util.List;

public class AttendanceResultActivity extends BaseActivity {


    private ImageView posBigImage;
    private ImageView posSmallImage;
    private ImageView negPosBigImage;
    private RelativeLayout resultNegativeWrapper;
    private RelativeLayout resultPositiveWrapper;
    private Button ngRegisterNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_result);

        super.context = this;
        super.toolbar = findViewById(R.id.toolbar);
        super.setObjects();
        super.setUIUtils(R.id.attParent);

        super.setThumbnail();
        super.setTitle();
        super.setCurrentDate();

        posBigImage = findViewById(R.id.posBigImage);
        posSmallImage = findViewById(R.id.posSmallImage);
        resultNegativeWrapper = findViewById(R.id.resultNegativeWrapper);
        resultPositiveWrapper = findViewById(R.id.resultPositiveWrapper);
        negPosBigImage = findViewById(R.id.negPosBigImage);
        ngRegisterNow = findViewById(R.id.ngRegisterNow);


        boolean isSuccess = (boolean)routing.getParam("isSuccess");
        if(isSuccess){
            resultNegativeWrapper.setVisibility(View.GONE);
            resultPositiveWrapper.setVisibility(View.VISIBLE);
            setBitmap("xyz", true);
        }else {
            resultNegativeWrapper.setVisibility(View.VISIBLE);
            resultPositiveWrapper.setVisibility(View.GONE);
            setBitmap("xyz", false);
        }

        ngRegisterNow.setOnClickListener(v -> {
            super.routing.navigate(RegisterAgainResponseActivity.class, false);
        });

    }

    private void setBitmap(String fileName, boolean isPositive){

        File folder = fileUtils.getFolder("offline_images");

        final File file = fileUtils.getFile(folder, fileName);


        if(file.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            if(myBitmap != null){
                if(isPositive){
                    posBigImage.setImageBitmap(myBitmap);
                    posSmallImage.setImageBitmap((myBitmap));
                }else {
                    negPosBigImage.setImageBitmap(myBitmap);
                }

            }

        }
    }
}