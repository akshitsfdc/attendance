package com.enalytix.faceattendance.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enalytix.faceattendance.R;
import com.enalytix.faceattendance.camera.CameraSourcePreview;
import com.enalytix.faceattendance.camera.GraphicOverlay;
import com.enalytix.faceattendance.faceTracking.BoxDetector;
import com.enalytix.faceattendance.faceTracking.FaceGraphic;
import com.enalytix.faceattendance.helper.FaceCoordinates;
import com.enalytix.faceattendance.models.FaceAttendanceResponse;
import com.enalytix.faceattendance.models.MarkAttendanceResponse;
import com.enalytix.faceattendance.models.RegisterUserRequest;
import com.enalytix.faceattendance.models.RegisterUserResponse;
import com.enalytix.faceattendance.services.AuthService;
import com.enalytix.faceattendance.services.FaceSenderService;
import com.enalytix.faceattendance.services.PermissionsServices;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterEmployeeActivity extends BaseActivity {


    private TextView scanningText;
    private ImageView profilePicture;
    private TextView siteTitle;
    private Button captureButton, cancelButton, saveButton;
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

    private String frontFaceEncoding = "";
    private String leftFaceEncoding = "";
    private String rightFaceEncoding = "";

    private TextView faceTypeLabel;
    private View firstFaceAddImageWrapper, secondFaceAddImageWrapper, thirdFaceAddImageWrapper, cameraWrapper, posResWrapper;
    private ImageView firstPicture, secondPicture, thirdPicture;
    private EditText mobileNumber, nameEditText, empIdEditText;
    private String siteId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_employee);

        super.context = this;
        super.toolbar = findViewById(R.id.toolbar);
        super.setObjects();
        super.setUIUtils(R.id.parent);

//        super.setThumbnail();
//        super.setTitle();
        super.setCurrentDate();



        permissionsServices = new PermissionsServices(this);

        captureButton = findViewById(R.id.captureButton);
        cancelButton = findViewById(R.id.cancelButton);
        labelCard =  findViewById(R.id.labelCard);
        buttonContainer = findViewById(R.id.buttonContainer);
        scanningText = findViewById(R.id.scanningText);
        faceTypeLabel = findViewById(R.id.faceTypeLabel);

        firstPicture = findViewById(R.id.firstPicture);
        secondPicture = findViewById(R.id.secondPicture);
        thirdPicture = findViewById(R.id.thirdPicture);

        mobileNumber = findViewById(R.id.mobileNumber);
        nameEditText = findViewById(R.id.nameEditText);
        empIdEditText = findViewById(R.id.empIdEditText);

        saveButton = findViewById(R.id.saveButton);


        posResWrapper = findViewById(R.id.posResWrapper);
        progressContainer = findViewById(R.id.progressContainer);
        progressIndicator = findViewById(R.id.progressIndicator);
        firstFaceAddImageWrapper = findViewById(R.id.firstFaceAddImageWrapper);
        secondFaceAddImageWrapper = findViewById(R.id.secondFaceAddImageWrapper);
        thirdFaceAddImageWrapper = findViewById(R.id.thirdFaceAddImageWrapper);
        cameraWrapper  = findViewById(R.id.cameraWrapper);
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);


        siteId =  "noida_1645";//(String)routing.getParam("siteId");

        setEventListeners();

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
//        permissionsServices.askForReadPermission();
//        permissionsServices.askForWritePermission();

        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

//        String title = (String)routing.getParam("siteName");
//        if(title != null){
//            super.setCustomTitle(title);
//        }



    }


    private void setEventListeners(){

        captureButton.setOnClickListener(v -> {
            captureClicked();
        });
        cancelButton.setOnClickListener(v -> {
            cancelCapture();
        });
        firstFaceAddImageWrapper.setOnClickListener(v ->{

            if(!validateForm()){
                return;
            }
            cameraWrapper.setVisibility(View.VISIBLE);
            faceTypeLabel.setText("FRONT");

        });
        secondFaceAddImageWrapper.setOnClickListener(v ->{
            if(!validateForm()){
                return;
            }
            cameraWrapper.setVisibility(View.VISIBLE);
            faceTypeLabel.setText("LEFT");
        });
        thirdFaceAddImageWrapper.setOnClickListener(v ->{
            if(!validateForm()){
                return;
            }
            cameraWrapper.setVisibility(View.VISIBLE);
            faceTypeLabel.setText("RIGHT");
        });

        saveButton.setOnClickListener(v -> {
            registerUser();
        });

        mobileNumber.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction()!= KeyEvent.ACTION_DOWN)
                return false;
            if(keyCode == KeyEvent.KEYCODE_ENTER ){
                return true;
            }
            return false;
        });
    }

    private boolean validateEncoding(){

        if(TextUtils.isEmpty(frontFaceEncoding) || TextUtils.isEmpty(leftFaceEncoding) || TextUtils.isEmpty(rightFaceEncoding)){
            uiUtils.showShortSnakeBar("Please upload all images first");
            return false;

        }else {
            return true;
        }
    }

    private boolean validateForm(){
        boolean valid = true;

        String mobileNumber = this.mobileNumber.getText().toString().trim();
        if(TextUtils.isEmpty(mobileNumber) || !TextUtils.isDigitsOnly(mobileNumber) || mobileNumber.length() < 10){
            valid = false;
            this.mobileNumber.setError(getString(R.string.invalid_mobile));
            this.uiUtils.showShortSnakeBar(getString(R.string.invalid_mobile_des));
        }

        String empName = this.nameEditText.getText().toString().trim();
        if(TextUtils.isEmpty(empName)){
            valid = false;
            this.nameEditText.setError("Required");
        }

        String empCode = this.empIdEditText.getText().toString().trim();
        if(TextUtils.isEmpty(empCode)){
            valid = false;
            this.empIdEditText.setError("Required");
        }

        return valid;

    }
    private void cancelCapture() {

        onBackPressed();
//        deactivateScanning();
//        stopScanningTimer();
//        ignoreResult = true;
    }

    @Override
    public void onBackPressed() {
        if(cameraWrapper.getVisibility() == View.VISIBLE){
            cameraWrapper.setVisibility(View.GONE);
            hideButton();
        }else {
            super.onBackPressed();
        }

    }

    private void startScanningImage(){
        activateScanning();
        startScanningTimer();
    }

    private void captureClicked() {



        mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes) {

                startScanningImage();

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Matrix matrix = new Matrix();
                matrix.postRotate(-90);

                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                Bitmap finalBitmap = getMirrorBitmap(rotatedBitmap);

                FirebaseVisionImage image = getVisionImage(finalBitmap);
                FirebaseVisionFaceDetector firebaseVisionFaceDetector = getVisionFaceDetector();

                getDetectTask(firebaseVisionFaceDetector, image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> faces) {
                                        Log.d(TAG, "onSuccess: faces outside D5675 >> "+faces.size());

                                        if (faces.size() > 0) {

                                            Log.d(TAG, "onSuccess: faces inside D5675 >> "+faces.size());

                                            FirebaseVisionFace face = faces.get(0);
                                            // If contour detection was enabled:
                                            List<FirebaseVisionPoint> leftEyeContour =
                                                    face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
                                            List<FirebaseVisionPoint> upperLipBottomContour =
                                                    face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();


                                            try {

                                                int left = face.getBoundingBox().left;
                                                int top = face.getBoundingBox().top;


                                                Bitmap faceBitmap = Bitmap.createBitmap(finalBitmap, left, top,
                                                        face.getBoundingBox().width(), face.getBoundingBox().height());

                                                File file = getSavedFile("xyz", faceBitmap);
                                                if(file != null){
//                                                    recognizeFace(file);\
                                                    String faceType = faceTypeLabel.getText().toString().trim();
                                                    String empId  = empIdEditText.getText().toString().trim();
                                                    registerFace(file, empId, siteId, faceType, faceBitmap);
                                                }else {
                                                    uiUtils.showShortSnakeBar("Opps... We missed your face! Try one more time.");
                                                    stopScanningImage();
                                                }

//                                                ((ImageView)findViewById(R.id.tempImage)).setImageBitmap(faceBitmap);

                                            }catch (Exception e){

                                                e.printStackTrace();

                                                uiUtils.showShortSnakeBar("Opps... We missed your face! Try one more time.");
                                                stopScanningImage();

                                            }

                                        }else {
                                            uiUtils.showShortSnakeBar("Opps... We missed your face! Try one more time.");
                                            stopScanningImage();
                                        }
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        Log.d(TAG, "onFailure: " + e.getMessage());
                                        uiUtils.showShortSnakeBar("Opps... We missed your face! Try one more time.");
                                        stopScanningImage();
                                        // ...
                                    }
                                });
            }
        });
    }

    private Bitmap getMirrorBitmap(Bitmap bitmap)
    {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap dst = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return dst;
    }

    private void activateScanning() {
        captureButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        progressContainer.setVisibility(View.VISIBLE);

    }

    private void deactivateScanning() {
        captureButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
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
//                .setLandmarkType(FaceDetector.CONTOUR_LANDMARKS)
                .setMode(FaceDetector.SELFIE_MODE)
                .setProminentFaceOnly(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setProminentFaceOnly(true)
                .build();

        BoxDetector myFaceDetector = new BoxDetector(detector);


        myFaceDetector.setProcessor(
                new MultiProcessor.Builder<>(new RegisterEmployeeActivity.GraphicFaceTrackerFactory())
                        .build());


        mCameraSource = new CameraSource.Builder(context, myFaceDetector)
                .setRequestedPreviewSize(getDisplayWidth(), getDisplayheight())
//                .setRequestedPreviewSize( getDisplayWidth(), getDisplayWidth())
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setAutoFocusEnabled(true)
                .setRequestedFps(30.0f)
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
            return new RegisterEmployeeActivity.GraphicFaceTracker(mGraphicOverlay);
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


//            if (face.getPosition().x < 0 || face.getPosition().y < 0) {
//                hideButton();
//            }
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

            if(progressContainer.getVisibility() != View.VISIBLE){
                hideButton();
            }

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

    private FirebaseVisionImage getVisionImage(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        return image;
    }

    private FirebaseVisionFaceDetector getVisionFaceDetector() {

        FirebaseVisionFaceDetectorOptions realTimeOpts =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
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

    private void stopScanningImage(){
        stopScanningTimer();
        deactivateScanning();
    }


    private void registerFace(File file, String empId, String siteId, String faceType, Bitmap bitmap) {

        FaceSenderService faceSenderService = new FaceSenderService(this);

//        String title = (String)routing.getParam("siteName");
//        routing.appendParams("siteName", title);
//
//        String siteCode = (String)routing.getParam("siteCode");

        faceSenderService.registerUser(file, empId, siteId)
                .enqueue(new Callback<FaceAttendanceResponse>() {
                    @Override
                    public void onResponse(Call<FaceAttendanceResponse> call, Response<FaceAttendanceResponse> response) {
                        Log.d(TAG, "onResponse: >> AttendanceResponse : ");

                        if(response == null) {
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                            stopScanningTimer();
                            deactivateScanning();
                            return;
                        }

                        if (!response.isSuccessful()) {
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                            stopScanningImage();

                        }else {
                            FaceAttendanceResponse attendanceResult = response.body();
                            if(attendanceResult != null && attendanceResult.isFaceVisible()){
                               setEncoding(bitmap, faceType);
                                uiUtils.showShortSnakeBar("Face uploaded successfully");
                                cameraWrapper.setVisibility(View.GONE);
                                stopScanningImage();
                                hideButton();
                            }else{
//                                routing.appendParams("isSuccess", false);
//                                String siteCode = (String) routing.getParam("siteCode");
//                                routing.appendParams("siteCode", siteCode);
//                                routing.navigate(AttendanceResultActivity.class, false);
//                                stopScanningImage();

                                uiUtils.showShortSnakeBar("Upload failed, since face was not visible");
                                stopScanningImage();
                                hideButton();
                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<FaceAttendanceResponse> call, Throwable t) {
                        Log.d(TAG, "onFailure: >> AttendanceResponse ");
                        uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                        stopScanningImage();
                        t.printStackTrace();
                    }
                });
    }

    private void setEncoding(Bitmap bitmap, String faceType){

        switch (faceType){
            case "FRONT":
                frontFaceEncoding = fileUtils.convert(bitmap);
                updateImageView(faceType);
                break;
            case "LEFT":
                leftFaceEncoding = fileUtils.convert(bitmap);
                updateImageView(faceType);
                break;
            case "RIGHT":
                rightFaceEncoding = fileUtils.convert(bitmap);
                updateImageView(faceType);
                break;
        }
    }
    private void updateImageView(String faceType){
        switch (faceType){
            case "FRONT":
                firstPicture.setImageBitmap(fileUtils.convert(frontFaceEncoding));
                break;
            case "LEFT":
                secondPicture.setImageBitmap(fileUtils.convert(leftFaceEncoding));
                break;
            case "RIGHT":
                thirdPicture.setImageBitmap(fileUtils.convert(rightFaceEncoding));
                break;
        }
    }


    private void registerUser(){

        if(!validateForm() || !validateEncoding()){
            return;
        }
        String mobileNumber = this.mobileNumber.getText().toString().trim();
        String sideCode = this.siteId;
        String empName = this.nameEditText.getText().toString().trim();
        String empCode = this.empIdEditText.getText().toString().trim();

        RegisterUserRequest registerUserRequest = new RegisterUserRequest(mobileNumber, sideCode, empName, empCode
        , frontFaceEncoding, leftFaceEncoding, rightFaceEncoding);

        AuthService authService = new AuthService();
        authService.registerEmployee(registerUserRequest)
                .enqueue(new Callback<List<RegisterUserResponse>>() {
                    @Override
                    public void onResponse(Call<List<RegisterUserResponse>> call, Response<List<RegisterUserResponse>> response) {

                        if (!response.isSuccessful()) {
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                            stopScanningImage();

                            return;
                        }

                        List<RegisterUserResponse> registerUserResponses = response.body();
                        if(registerUserResponses == null){
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                            stopScanningImage();

                            return;
                        }
                        if(registerUserResponses.get(0).getStatus() != null && TextUtils.equals(registerUserResponses.get(0).getStatus().toLowerCase(), getString(R.string.success_status))){
                            Log.d(TAG, "onResponse: status >> "+registerUserResponses.get(0).getStatus());
//                            routing.appendParams("isSuccess", true);
//                            routing.appendParams("attData", registerUserResponses.get(0));
//                            routing.appendParams("attType", attType);

//                            routing.navigate(AttendanceResultActivity.class, false);

//                            stopScanningImage();
                            finalizeObjects();
                        }else {
                            uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                            stopScanningImage();
                        }

//                        removeAttending();


                    }

                    @Override
                    public void onFailure(Call<List<RegisterUserResponse>> call, Throwable t) {
                        Log.d(TAG, "onFailure: attendance failed >> ");
                        uiUtils.showShortSnakeBar(getString(R.string.generic_error));
                        stopScanningImage();
//                        removeAttending();
                    }
                });
    }

    private void finalizeObjects(){


        posResWrapper.setVisibility(View.VISIBLE);
        frontFaceEncoding = "";
        leftFaceEncoding = "";
        rightFaceEncoding = "";

        firstPicture.setImageBitmap(null);
        secondPicture.setImageBitmap(null);
        thirdPicture.setImageBitmap(null);

        mobileNumber.setText("");
        empIdEditText.setText("");
        nameEditText.setText("");
        saveButton.setText("Register Another Employee");
    }
}