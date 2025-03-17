package com.app.kiyalearning.util;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class UtilClass {


         public static Boolean isClassTimeExpired(Long classTimeStamp) {
            long current = System.currentTimeMillis();
            int thirtyMinutes = 1 * 60 * 60 * 1000 ;
            long classTime = (classTimeStamp * 1000) + (thirtyMinutes);
            return classTime < current;
        }

    public static void mToast(Context context,String msg) {
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }


    public static void copyText(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
    }

    public static String getCurrentDate() {
        int year, month, day;
        Calendar c = Calendar.getInstance();

        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        String date = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(day);
        return date;
    }

    public static String getCurrentDateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, EEE");
        int year, month, day;
        Calendar c = Calendar.getInstance();

        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        Date currentDate = c.getTime();
        //String date = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(day);
        return sdf.format(currentDate);
    }

    public static Long getCurrentTimeStamp() {
        //return System.currentTimeMillis() / 1000 ;
        return System.currentTimeMillis() / 1000;
    }


    public static void setStatusBarProperty(Activity activity) {
        //transparent status bar
        Window window = activity.getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    public static String getFileName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }


    public static String currentPhotoPath;
    public static String aadharPhotoPath;
    public static String panPhotoPath;


    public static String getPathFromURI(Uri contentUri, Context context) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public static byte[] getBytes(Uri uri, Activity activity) {
        try {
            InputStream inputStream = activity
                    .getApplicationContext().getContentResolver().openInputStream(uri);
            return readBytes(inputStream);

        } catch (Exception ex) {
            Log.d("MyTag", "could not get byte stream");
        }
        return null;
    }

    private static byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1 * 1024 * 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    public static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public static boolean hasPlayServices(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(activity);

        if (result == ConnectionResult.SUCCESS)
            return true;
        else if (googleApiAvailability.isUserResolvableError(result)) {
            Dialog dialog = googleApiAvailability.getErrorDialog(activity, result, 9002,
                    task -> Toast.makeText(activity, "Dialog is cancelled by the user", Toast.LENGTH_LONG).show());
            assert dialog != null;
            dialog.show();
            return false;
        } else {
            Toast.makeText(activity, "play services are required to run application", Toast.LENGTH_LONG).show();
            return false;
        }


    }


    public static Long checkFileSize(File file) {
        long fileSizeInBytes = file.length();
        long fileSizeInKB = fileSizeInBytes / 1024;
        long fileSizeInMB = fileSizeInKB / 1024;
        return fileSizeInMB;
    }




   /* public static boolean hasPlayServices(Activity activity)
    {
        GoogleApiAvailability googleApiAvailability=GoogleApiAvailability.getInstance ();
        int result=googleApiAvailability.isGooglePlayServicesAvailable ( activity );

        if(result== ConnectionResult.SUCCESS )
            return true;
        else if(googleApiAvailability.isUserResolvableError ( result ))
        {
            Dialog dialog=googleApiAvailability.getErrorDialog ( activity,result,9002 ,
                    task-> Toast.makeText ( activity,"Dialog is cancelled by the user",Toast.LENGTH_LONG ).show ());
            assert dialog != null;
            dialog.show ();
            return false;
        }else
        {
            Toast.makeText ( activity,"play services are required to run application",Toast.LENGTH_LONG ).show ();
            return false;
        }

    }*/


}
