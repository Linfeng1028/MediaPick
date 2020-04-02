package com.wlf.mediapick.crash;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * crash日志收集类
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static String sLogDir;

    public CrashHandler(Context context) {
        sLogDir = context.getExternalFilesDir(null) + File.separator + "log" +
                File.separator + "crash";
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Log.e("程序出现异常了", "Thread = " + thread.getName() + "\nThrowable = " + throwable.getMessage());
        String stackTraceInfo = getStackTraceInfo(throwable);
        Log.e("stackTraceInfo", stackTraceInfo);
        saveThrowableMessage(stackTraceInfo);
    }

    private String getStackTraceInfo(final Throwable throwable) {
        Writer writer = new StringWriter();
        try (PrintWriter pw = new PrintWriter(writer)) {
            throwable.printStackTrace(pw);
        } catch (Exception e) {
            return "";
        }
        return writer.toString();
    }

    private void saveThrowableMessage(String errorMessage) {
        if (TextUtils.isEmpty(errorMessage)) {
            return;
        }
        File file = new File(sLogDir);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (mkdirs) {
                writeStringToFile(errorMessage, file);
            }
        } else {
            writeStringToFile(errorMessage, file);
        }
    }

    private void writeStringToFile(final String errorMessage, final File file) {
        new Thread(() -> {
            FileOutputStream outputStream = null;
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(errorMessage.getBytes());
                String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH.mm.ss", Locale.getDefault()).format(new Date());
                outputStream = new FileOutputStream(new File(file, timeStamp + ".txt"));
                int len = 0;
                byte[] bytes = new byte[1024];
                while ((len = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, len);
                }
                outputStream.flush();
                Log.e("程序出异常了", "写入本地文件成功：" + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}