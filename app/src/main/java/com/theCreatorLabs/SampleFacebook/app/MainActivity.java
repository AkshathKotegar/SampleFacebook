package com.theCreatorLabs.SampleFacebook.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ShareOnFacebook";
    LoginButton btnFbLogin;
    TextView tvStatus;
    CallbackManager callbackManager;
    Button btnPost, btnPostImage;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.theCreatorLabs.SampleFacebook.app",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        img = (ImageView) findViewById(R.id.img);
        btnFbLogin = (LoginButton) findViewById(R.id.btn_fb_login);
        tvStatus = (TextView) findViewById(R.id.login_status);
        btnPost = (Button) findViewById(R.id.btn_post);
        btnPostImage = (Button) findViewById(R.id.btn_post_Image);
        callbackManager = CallbackManager.Factory.create();
        btnFbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                tvStatus.setText("Login Success\n" +
                        loginResult.getAccessToken().getUserId() + "\n" +
                        loginResult.getAccessToken().getToken());

            }

            @Override
            public void onCancel() {
                tvStatus.setText("Login Cancelled");
            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOnWall();
            }
        });

        btnPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = null;
                File f = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "IMG_" + "123" + ".jpg");
                bitmap = writeTextOnDrawable(f.toString(), "Mantechser");
                postFB(bitmap);
            }
        });
    }

    void shareOnWall() {
        ShareDialog shareDialog = new ShareDialog(this);
        callbackManager = CallbackManager.Factory.create();
        shareDialog.registerCallback(callbackManager, new

                FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Log.d(TAG, "onSuccess: ");
                        Toast.makeText(MainActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "onCancel: ");
                        Toast.makeText(MainActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "onError: ");
                        Toast.makeText(MainActivity.this, "onError" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Hello Facebook")
                    .setContentDescription("The 'Hello Facebook' sample  showcases simple Facebook integration")
                    .setContentUrl(Uri.parse("http://mantechser.com/"))
                    //.setImageUrl(Uri.parse("http://mantechser.com/images/IMG-20160503-WA0002.png"))
                    .build();

            shareDialog.show(linkContent);
        }
    }

    private void postFB(Bitmap bm) {
        SharePhoto photo = new SharePhoto.Builder().setBitmap(bm).build();
        SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
        ShareDialog dialog = new ShareDialog(this);
        if (dialog.canShow(SharePhotoContent.class)) {
            dialog.show(content);
        } else {
            Log.d("Activity", "you cannot share photos :(");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap writeTextOnDrawable(String path, String text) {

        Bitmap bm = BitmapFactory.decodeFile(path).copy(Bitmap.Config.ARGB_8888, true);
        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(getApplicationContext(), 20));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        if (textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(getApplicationContext(), 7));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 1.1) - ((paint.descent() + paint.ascent()) / 2));

        canvas.drawText(text, xPos, yPos, paint);
         //img.setImageBitmap(bm);
        return bm;
    }


    public static int convertToPixels(Context context, int nDP) {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f);

    }

}


   /* Bitmap bitmap = null;
    File f = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "IMG_" + "123" + ".jpg");
    BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        try {
                        bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
                        } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        }
                        postFB(bitmap);*/