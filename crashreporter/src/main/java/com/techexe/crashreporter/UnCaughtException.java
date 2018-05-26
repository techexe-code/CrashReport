package com.techexe.crashreporter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.android.volley.Request.Method.POST;

public class UnCaughtException implements Thread.UncaughtExceptionHandler {

    private String packageName;
    private HashMap<String, String> customParameters = new HashMap<String, String>();
    private Context context;
    public UnCaughtException(Context ctx) {
        context = ctx;
    }


    private StatFs getStatFs() {
        File path = Environment.getDataDirectory();
        return new StatFs(path.getPath());
    }

    private long getAvailableInternalMemorySize(StatFs stat) {
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    private long getTotalInternalMemorySize(StatFs stat) {
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }


    private String addInformation() {

        StringBuilder message = new StringBuilder();

        message.append("Locale: ").append(Locale.getDefault()).append('\n');
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi;
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            message.append("Version: ").append(pi.versionName).append('\n');
            message.append("Package: ").append(pi.packageName).append('\n');
        } catch (Exception e) {
            Log.e("CustomExceptionHandler", "Error", e);
            message.append("Could not get Version information for ").append(
                    context.getPackageName());
        }
        message.append("Phone Model: ").append(android.os.Build.MODEL)
                .append('\n');
        message.append("Android Version: ")
                .append(android.os.Build.VERSION.RELEASE).append('\n');
        message.append("Board: ").append(android.os.Build.BOARD).append('\n');
        message.append("Brand: ").append(android.os.Build.BRAND).append('\n');
        message.append("Device: ").append(android.os.Build.DEVICE).append('\n');
        message.append("Manufacturer: ").append(Build.MANUFACTURER).append('\n');
        message.append("Host: ").append(android.os.Build.HOST).append('\n');
        message.append("ID: ").append(android.os.Build.ID).append('\n');
        message.append("Model: ").append(android.os.Build.MODEL).append('\n');
        message.append("Product: ").append(android.os.Build.PRODUCT)
                .append('\n');
        message.append("Type: ").append(android.os.Build.TYPE).append('\n');
        message.append("User: ").append(Build.USER).append('\n');
        message.append("Total Internal memory: ")
                .append(getTotalInternalMemorySize()+" MB").append('\n');
        message.append("Available Internal memory: ")
                .append(getAvailableInternalMemorySize()+" MB").append('\n');

        return message.toString();
    }




    @Override
    public void uncaughtException(Thread t, Throwable e) {
//        showLog("====uncaughtException");

        StringBuilder reportStringBuffer = new StringBuilder();
        reportStringBuffer.append("Error Report collected on : ").append(new Date().toString());
        reportStringBuffer.append("\n\nInformations :\n==============");
        reportStringBuffer.append(addInformation());
        String customInfo = createCustomInfoString();
        if(!customInfo.equals("")) {
            reportStringBuffer.append("\n\nCustom Informations :\n==============\n");
            reportStringBuffer.append(customInfo);
        }

        reportStringBuffer.append("\n\nStack :\n==============\n");
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        reportStringBuffer.append(result.toString());

        reportStringBuffer.append("\nCause :\n==============");
        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        Throwable cause = e.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            reportStringBuffer.append(result.toString());
            cause = cause.getCause();
        }
        printWriter.close();
        reportStringBuffer.append("\n\n**** End of current Report ***");

//        sendReport(reportStringBuffer.toString());

        Intent intent = new Intent(context, ErrorHandling.class);
        intent.putExtra("message",reportStringBuffer.toString());
        intent.putExtra("packageName",context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);



        //previousHandler.uncaughtException(t, e);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);

    }


    private String createCustomInfoString() {
        String customInfo = "";
        for (Object currentKey : customParameters.keySet()) {
            String currentVal = customParameters.get(currentKey);
            customInfo += currentKey + " = " + currentVal + "\n";
        }
        return customInfo;
    }

    private long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return (availableBlocks * blockSize)/(1024*1024);
    }

    private long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return (totalBlocks * blockSize)/(1024*1024);
    }




}
