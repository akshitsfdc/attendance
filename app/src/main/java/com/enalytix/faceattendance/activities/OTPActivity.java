package com.enalytix.faceattendance.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.enalytix.faceattendance.R;
import com.enalytix.faceattendance.helper.GenericTextWatcher;

public class OTPActivity extends AppCompatActivity {


    EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five;
    Button verify_otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);

        otp_textbox_one = findViewById(R.id.otp_edit_box1);
        otp_textbox_two = findViewById(R.id.otp_edit_box2);
        otp_textbox_three = findViewById(R.id.otp_edit_box3);
        otp_textbox_four = findViewById(R.id.otp_edit_box4);
        otp_textbox_five = findViewById(R.id.otp_edit_box5);
//        verify_otp = findViewById(R.id.verify_otp_btn);


        EditText[] edit = {otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five};

        otp_textbox_one.addTextChangedListener(new GenericTextWatcher(otp_textbox_one, edit));
        otp_textbox_two.addTextChangedListener(new GenericTextWatcher(otp_textbox_two, edit));
        otp_textbox_three.addTextChangedListener(new GenericTextWatcher(otp_textbox_three, edit));
        otp_textbox_four.addTextChangedListener(new GenericTextWatcher(otp_textbox_four, edit));
        otp_textbox_five.addTextChangedListener(new GenericTextWatcher(otp_textbox_five, edit));


    }
}