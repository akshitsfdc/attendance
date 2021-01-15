package com.enalytix.faceattendance.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.enalytix.faceattendance.R;
import com.enalytix.faceattendance.camera.CameraSourcePreview;
import com.enalytix.faceattendance.camera.GraphicOverlay;
import com.enalytix.faceattendance.faceTracking.BoxDetector;

import com.enalytix.faceattendance.faceTracking.FaceGraphic;
import com.enalytix.faceattendance.models.AttendanceResponse;
import com.enalytix.faceattendance.models.Site;
import com.enalytix.faceattendance.services.FaceSenderService;
import com.enalytix.faceattendance.services.PermissionsServices;
import com.enalytix.faceattendance.utils.FileUtils;
import com.enalytix.faceattendance.utils.Routing;
import com.enalytix.faceattendance.utils.UIUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordAttendanceActivity extends BaseActivity {

    private static final String TAG = "RecordAttendance";

    private ImageView profilePicture;
    private TextView siteTitle;
    private Button captureButton, cancelButton;
    private RelativeLayout progressContainer;
    private LinearProgressIndicator progressIndicator;
    private Timer scanningTimer;



    private CameraSource mCameraSource = null;

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    boolean hasBlinked = false;

    public Face myFace;
    private boolean assignFace = true;
    public static Frame currentFrame;


    private PermissionsServices permissionsServices;


    private boolean ignoreResult = false;

    private CardView labelCard;
    private RelativeLayout buttonContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_attendance);

        super.context = this;
        super.toolbar = findViewById(R.id.toolbar);
        super.setObjects();
        super.setUIUtils(R.id.preview);

        super.setThumbnail();
        super.setTitle();

        permissionsServices = new PermissionsServices(this);

        captureButton = findViewById(R.id.captureButton);
        cancelButton = findViewById(R.id.cancelButton);
        labelCard =  findViewById(R.id.labelCard);
        buttonContainer = findViewById(R.id.buttonContainer);

        progressContainer = findViewById(R.id.progressContainer);
        progressIndicator = findViewById(R.id.progressIndicator);




        captureButton.setOnClickListener(v -> {
            ignoreResult = false;
            captureClicked();
        });
        cancelButton.setOnClickListener(v -> {
            ignoreResult = true;
            cancelCapture();
        });


        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        permissionsServices.askForReadPermission();
        permissionsServices.askForWritePermission();

        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void cancelCapture() {

        deactivateScanning();
        stopScanningTimer();
        ignoreResult = true;
    }

    private void captureClicked() {

        mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes) {

                activateScanning();
                startScanningTimer();
                setImage();

            }
        });
    }

    private void activateScanning() {
        captureButton.setVisibility(View.GONE);
        progressContainer.setVisibility(View.VISIBLE);

    }

    private void deactivateScanning() {
        captureButton.setVisibility(View.VISIBLE);
        progressContainer.setVisibility(View.GONE);
    }

    private void startScanningTimer() {

        scanningTimer = new Timer();

        scanningTimer.scheduleAtFixedRate(new TimerTask() {

                                              @Override
                                              public void run() {
                                                  setScanningProgress();
                                              }
                                          },
                0,
                50);
    }

    private void stopScanningTimer() {

        progressIndicator.setProgress(0);

        if (scanningTimer != null) {
            try {
                scanningTimer.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    private void setScanningProgress() {

        runOnUiThread(() -> {
            int newProgress = progressIndicator.getProgress() + 1;
            if (newProgress == 100) {
                newProgress = 1;

                //temp
            }
            progressIndicator.setProgress(newProgress);
        });
    }





    private void createCameraSource() {

        Context context = getApplicationContext();


        FaceDetector detector = new FaceDetector.Builder(context).setTrackingEnabled(true) //can be false too
                .setLandmarkType(FaceDetector.CONTOUR_LANDMARKS)
                .setMode(FaceDetector.SELFIE_MODE)
                .setProminentFaceOnly(true)
//                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
//                .setProminentFaceOnly(true)
                .build();

        BoxDetector myFaceDetector = new BoxDetector(detector);


        myFaceDetector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());


        mCameraSource = new CameraSource.Builder(context, myFaceDetector)
                .setRequestedPreviewSize(getDisplayWidth(), getDisplayheight())
//                .setRequestedPreviewSize( 1280, 1024)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setAutoFocusEnabled(true)
                .setRequestedFps(40.0f)
                .build();


    }

    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }


        if (mCameraSource != null) {
            try {
                //mCameraSource.start();
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private void requestCameraPermission() {

        Log.d(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    private int getDisplayWidth() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.widthPixels;
    }

    private int getDisplayheight() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.heightPixels;
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {


        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {

            mFaceGraphic.setId(faceId);
           // showButton();
//            Log.d(TAG, "onNewItem: left >> face entered");
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {


            if (face.getPosition().x < 0 || face.getPosition().y < 0) {
                hideButton();
            }
            if (assignFace) {
                myFace = face;
            }


            //showButton();
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face); //


            if (face.getIsRightEyeOpenProbability() <= 0.03 || face.getIsLeftEyeOpenProbability() <= 0.03) {

//               mp.start();
//                mpAlert.start();

                hasBlinked = true;
                showButton();
//                Log.d(TAG, "onUpdate: >> getPreviewSize >> h"+mCameraSource.getPreviewSize().getHeight());
//                Log.d(TAG, "onUpdate: >> getPreviewSize >> w"+mCameraSource.getPreviewSize().getWidth());


            }
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {

            mOverlay.remove(mFaceGraphic);
            hideButton();
//            mpAlert.start();
//            Log.d(TAG, "onNewItem: left >> onMissing gone");
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {

            mOverlay.remove(mFaceGraphic);

//            mpAlert.start();
//            Log.d(TAG, "onDone: left >> onDone complete >> gone");

            //takeScreenshot();

        }
    }


    private void setImage() {

        Bitmap bitmap = getBitmap(currentFrame.getGrayscaleImageData());

        if (bitmap == null) {
            deactivateScanning();
            stopScanningTimer();
            return;
        }
        try {
            assignFace = false;
//            screenImage.setImageBitmap(bitmap);
            assignFace = true;
        } catch (Exception e) {
            Toast.makeText(this, "It looks like you went out of frame !",
                    Toast.LENGTH_LONG).show();
            deactivateScanning();
            stopScanningTimer();
            assignFace = true;
        }
        FirebaseVisionImage image = getVisionImage(bitmap);
        FirebaseVisionFaceDetector firebaseVisionFaceDetector = getVisionFaceDetector();

        getDetectTask(firebaseVisionFaceDetector, image)
                .addOnSuccessListener(
                        new OnSuccessListener<List<FirebaseVisionFace>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionFace> faces) {
//                                Log.d(TAG, "onSuccess: faces >> "+faces.size());

                                if (faces.size() > 0) {

                                    FirebaseVisionFace face = faces.get(0);

                                    // If contour detection was enabled:
                                    List<FirebaseVisionPoint> leftEyeContour =
                                            face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
                                    List<FirebaseVisionPoint> upperLipBottomContour =
                                            face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();
//
//                                    Log.d(TAG, "onSuccess:  >>> leftEyeContour >> "+leftEyeContour.toString());
//                                    Log.d(TAG, "onSuccess:  >>> upperLipBottomContour >> "+upperLipBottomContour.toString());
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                Log.d(TAG, "onFailure: " + e.getMessage());
                                deactivateScanning();
                                stopScanningTimer();
                                // ...
                            }
                        });
        File file = getSavedFile("xyz", bitmap);
        makeAttendanceCall(file, bitmap);
    }

    Bitmap getBitmap(ByteBuffer byteBuffer) {


        Face face = myFace;

        Bitmap rotatedBitmap;

        int left = (int) face.getPosition().x;
        int top = (int) face.getPosition().y;
        int right = (int) face.getWidth() + left;
        int bottom = (int) face.getHeight() + top;

        int height = currentFrame.getMetadata().getHeight();
        int width = currentFrame.getMetadata().getWidth();

//        Log.d(TAG, "getBitmap: left >>"+left+" top >> "+top+" right >> "+" bottom >>"+bottom);
//
//        Log.d(TAG, "getBitmap: currentFrame >> height >> "+height+" width >> "+width);


        try {


            YuvImage yuvimage = new YuvImage(byteBuffer.array(), ImageFormat.NV21, width,
                    height, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvimage.compressToJpeg(new Rect(left, top, right,
                    bottom), 100, baos); // Where 100 is the quality of the generated jpeg
            byte[] jpegArray = baos.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(jpegArray, 0, jpegArray.length);

            Matrix matrix = new Matrix();

            matrix.postRotate(-90);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);


            rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            uiUtils.showShortSnakeBar(getString(R.string.re_att_face_not_visible));
            deactivateScanning();
            stopScanningTimer();
            return null;
        }
        return rotatedBitmap;
    }

    private FirebaseVisionImage getVisionImage(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        return image;
    }

    private FirebaseVisionFaceDetector getVisionFaceDetector() {

        FirebaseVisionFaceDetectorOptions realTimeOpts =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .build();

        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(realTimeOpts);

        return detector;
    }

    private Task<List<FirebaseVisionFace>> getDetectTask(FirebaseVisionFaceDetector detector, FirebaseVisionImage image) {

        Task<List<FirebaseVisionFace>> result =
                detector.detectInImage(image);
//
        return result;
    }

    void hideButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonContainer.setVisibility(View.GONE);
                labelCard.setVisibility(View.VISIBLE);
            }
        });
    }

    void showButton(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(hasBlinked){
                    labelCard.setVisibility(View.GONE);
                    buttonContainer.setVisibility(View.VISIBLE);
                    hasBlinked = false;
                }

            }
        });
    }

    private File getSavedFile(String fileName, Bitmap bitmap) {

        File folder = fileUtils.getFolder("offline_images");

        final File file = fileUtils.getFile(folder, fileName);


        if (file.exists()) {
            try {
                file.getAbsoluteFile().delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {

            OutputStream stream = null;

            stream = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            stream.flush();

            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file.exists()) {
            Log.d(TAG, "getSavedFile: file exists");
            return file;
        }

        return null;
    }

    private void makeAttendanceCall(File file, Bitmap bitmap) {

        FaceSenderService faceSenderService = new FaceSenderService(this);

        faceSenderService.registerUser(file, "8888985133_rajput", "noida_2565")
        .enqueue(new Callback<AttendanceResponse>() {
            @Override
            public void onResponse(Call<AttendanceResponse> call, Response<AttendanceResponse> response) {
                Log.d(TAG, "onResponse: >> AttendanceResponse : ");

                if(ignoreResult){
                    return;
                }

                if(response == null) {
                    uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                    return;
                }

                if (!response.isSuccessful()) {
                    uiUtils.showShortSnakeBar(getString(R.string.generic_error));

                }else {
                    AttendanceResponse attendanceResult = response.body();
                    if(attendanceResult != null && attendanceResult.isFaceVisible()){
                        routing.appendParams("isSuccess", true);
                        routing.navigate(AttendanceResultActivity.class, false);
                        stopScanningTimer();
                        deactivateScanning();
                    }else{
                        routing.appendParams("isSuccess", false);
                        routing.navigate(AttendanceResultActivity.class, false);
                        stopScanningTimer();
                        deactivateScanning();
                    }
//                    Log.d(TAG, "onResponse: >> response attendanceResult : "+attendanceResult.toString());
//                    Log.d(TAG, "onResponse: >> response attendanceResult : "+attendanceResult.isFaceVisible());
//                    Log.d(TAG, "onResponse: >> response attendanceResult : "+attendanceResult.getId());
//                    Log.d(TAG, "onResponse: >> response attendanceResult : "+attendanceResult.getError());

                }
            }

            @Override
            public void onFailure(Call<AttendanceResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: >> AttendanceResponse ");
                uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                stopScanningTimer();
                deactivateScanning();
                t.printStackTrace();
            }
        });
    }

}