package com.enalytix.faceattendance.activities;

import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.enalytix.faceattendance.R;
import com.enalytix.faceattendance.adapters.AttendanceViewAdapter;
import com.enalytix.faceattendance.models.AttendanceModel;
import com.enalytix.faceattendance.models.MyAttendanceResponse;
import com.enalytix.faceattendance.services.AuthService;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceViewActivity extends BaseActivity {

    private static String TAG = "AttendanceView";


    private TextView monthLabel;

    private RecyclerView recyclerView;
    private ImageView profileImage;
    private TextView employeeName, empIdValue, hoursValue;
    private String month;
    private String year;
    private String mobileNumber = "9717030887";
    private TextView empIdHeader;
    private Spinner monthSpinner;
    private Spinner yearSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_view);

        super.context = this;
        super.toolbar = findViewById(R.id.toolbar);
        super.setObjects();
        super.setUIUtils(R.id.parent);
        super.setTitle();
        super.setThumbnail();

        mobileNumber = MainActivity.USER_DATA.getMobileNumber().trim();

        monthLabel = findViewById(R.id.monthLabel);
        recyclerView = findViewById(R.id.recyclerView);
        profileImage = findViewById(R.id.profile_image);
        employeeName = findViewById(R.id.siteName);
        empIdValue = findViewById(R.id.dateValue);
        hoursValue = findViewById(R.id.hoursValue);
        monthSpinner = findViewById(R.id.monthSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);

//        empIdHeader = findViewById(R.id.empIdHeader);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addItemDecoration( new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        populateList(new ArrayList<AttendanceModel>());
        //setMonthLabel("MAY");


        fileUtils.setImage(profileImage, MainActivity.USER_DATA.getThumbnail());
        empIdValue.setText(MainActivity.USER_DATA.getEmpCode().length()==0?"__":MainActivity.USER_DATA.getEmpCode());


        selectDefaultSpinners();

        hoursValue.setOnClickListener(v -> {
            uiUtils.showShortSnakeBar("Total working hours in a month");
        });
        empIdValue.setOnClickListener(v -> {
            uiUtils.showShortSnakeBar("Employee ID");
        });




        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                month = monthSpinner.getSelectedItem().toString();
                year = yearSpinner.getSelectedItem().toString();

                loadAttendance(mobileNumber, month+"-"+year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setMonthSpinnerAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }


    private void selectDefaultSpinners(){

        setYearSpinnerAdapter();
        setMonthSpinnerAdapter();

        Calendar calendar = Calendar.getInstance();

        month = uiUtils.getMonthFromIndex(calendar.get(Calendar.MONTH));
        year = String.valueOf(calendar.get(Calendar.YEAR));

        monthSpinner.setSelection(uiUtils.getMonthArrayToMonths(11).indexOf(month));
        yearSpinner.setSelection(uiUtils.getYearArray().indexOf(year));

//        loadAttendance(mobileNumber, month+"-"+year);
    }
    private void setMonthSpinnerAdapter(){

        ArrayList<String> list;

        Calendar calendar = Calendar.getInstance();
        String selectedYear =  yearSpinner.getSelectedItem().toString();
        String currentYear = String.valueOf(calendar.get(Calendar.YEAR));
        if(!TextUtils.equals(selectedYear, currentYear)){
            list = uiUtils.getMonthArrayToMonths(11);
        }else {
            list = uiUtils.getMonthArrayToMonths(calendar.get(Calendar.MONTH));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout,list);
        monthSpinner.setAdapter(adapter);
    }
    private void setYearSpinnerAdapter(){
        ArrayList<String> yearArray = uiUtils.getYearArray();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout,yearArray);
        yearSpinner.setAdapter(adapter);
    }
    private void setMonthLabel(String month){
        String finalText = "MONTH: "+month;
        monthLabel.setText(finalText);
    }

    private void populateList(List<AttendanceModel> attendanceModels){
        AttendanceViewAdapter adapter = new AttendanceViewAdapter(this, attendanceModels);
        recyclerView.setAdapter(adapter);
    }


    private void loadAttendance(String mobileNumber, String monthYear){
        AuthService authService = new AuthService();
        showLoading();
        authService.getMyAttendance(mobileNumber, monthYear)
                .enqueue(new Callback<MyAttendanceResponse>() {
                    @Override
                    public void onResponse(Call<MyAttendanceResponse> call, Response<MyAttendanceResponse> response) {

                        hideLoading();
                        if (!response.isSuccessful()) {
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                            return;
                        }


                        MyAttendanceResponse myAttendanceResponse = response.body();
                        if(myAttendanceResponse == null){
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                            return;
                        }
                        if(myAttendanceResponse.getError() == null){

                            sanitizeList(myAttendanceResponse.getDaily());
                            populateList(myAttendanceResponse.getDaily());
                            setMonthlyView(myAttendanceResponse.getMonthly());


                        }else{
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                        }


                    }

                    @Override
                    public void onFailure(Call<MyAttendanceResponse> call, Throwable t) {
                        uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                        hideLoading();
                    }
                });
    }

    private void setMonthlyView(AttendanceModel attendanceModel){

        String hrValue = attendanceModel.getTotalHour()+" hr";
        employeeName.setText(attendanceModel.getEmpName());
        hoursValue.setText(hrValue);
    }
    private void sanitizeList(List<AttendanceModel> attendanceModels){

        for (AttendanceModel attendanceModel: attendanceModels) {
            attendanceModel.setDateValue(calculateDateValue(attendanceModel.getDate()));
        }
        Collections.sort(attendanceModels, Collections.reverseOrder());
    }

    private int calculateDateValue(String date){
        if(date == null || date.length() == 0 ){
            return 0;
        }
        String[] parts = date.split("-");
        if(parts.length == 3){
            return Integer.parseInt(parts[2]);
        }else {
            return 0;
        }
    }

}