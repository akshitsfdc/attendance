package com.enalytix.faceattendance.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enalytix.faceattendance.R;
import com.enalytix.faceattendance.models.AttendanceModel;
import com.enalytix.faceattendance.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class AttendanceViewAdapter extends RecyclerView.Adapter<AttendanceViewAdapter.ViewHolder>{


    private List<AttendanceModel> attendanceModels;
    private Context mContext;


    public AttendanceViewAdapter(Context context, List<AttendanceModel> attendanceModels ) {
        this.attendanceModels = attendanceModels;
        mContext = context;


    }

    @NonNull
    @Override
    public AttendanceViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addtendance_view_recycler_layout, parent, false);
        return new AttendanceViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewAdapter.ViewHolder holder, int position) {

        AttendanceModel attendanceModel = attendanceModels.get(position);

        holder.siteName.setText(attendanceModel.getSiteName());
        holder.dateValue.setText(attendanceModel.getDate());

        String hoursStr;

        if(attendanceModel.getTotalHour() == null || attendanceModel.getTotalHour().length()==0){
            hoursStr = "0 hr";
        }else {
            hoursStr = attendanceModel.getTotalHour()+" hr";
        }

        holder.hoursValue.setText(hoursStr);

        holder.hoursValue.setOnClickListener(v -> {
             UIUtils uiUtils = new UIUtils((Activity) mContext, holder.hoursValue.getId());
            uiUtils.showShortSnakeBar("Total working hours in a day");
        });

    }



    @Override
    public int getItemCount() {
        return attendanceModels.size();
//        return attendanceModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView siteName, dateValue, hoursValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            siteName = itemView.findViewById(R.id.siteName);
            dateValue = itemView.findViewById(R.id.dateValue);
            hoursValue = itemView.findViewById(R.id.hoursValue);



        }
    }
}
