package com.enalytix.faceattendance.faceTracking;

import android.util.SparseArray;

import com.enalytix.faceattendance.activities.RecordAttendanceActivity;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class BoxDetector extends Detector {

    String TAG = "BoxDetector";



    private FaceDetector mDelegate;


    public BoxDetector(FaceDetector delegate) {
        mDelegate = delegate;
//        this.frame = frame;
    }

    public SparseArray<Face> detect(Frame frame) {


        RecordAttendanceActivity.currentFrame = frame;

//        Log.d(TAG, "detect: >> Buffer Image : "+frame.getGrayscaleImageData());
//        Log.d(TAG, "detect: >> Meta Data : getWidth "+frame.getMetadata().getWidth());
//        Log.d(TAG, "detect: >> Meta Data : getHeight "+frame.getMetadata().getHeight());
//        this.imageBitmap = frame.getBitmap();
//        Log.d("SparseArray", "detect: >> imageBitmap >> "+frame.getBitmap());
//        this.imageView.setImageBitmap(frame.getBitmap());
        // *** add your custom frame processing code here
        return mDelegate.detect(frame);
    }

    public boolean isOperational() {
        return mDelegate.isOperational();
    }

    public boolean setFocus(int id) {
        return mDelegate.setFocus(id);
    }

//    public void setImage(){
//
//        runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//
//                // Stuff that updates the UI
//
//            }
//        });
//    }


//    Bitmap getBitmap(ByteBuffer byteBuffer){
//
//        YuvImage yuvimage=new YuvImage(byteBuffer, ImageFormat.NV21, w, h, null);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        yuvimage.compressToJpeg(new Rect(0, 0, w, h), 100, baos); // Where 100 is the quality of the generated jpeg
//        byte[] jpegArray = baos.toByteArray();
//        Bitmap bitmap = BitmapFactory.decodeByteArray(jpegArray, 0, jpegArray.length);
//    }

}
