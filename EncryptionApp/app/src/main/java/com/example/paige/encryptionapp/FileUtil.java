package com.example.paige.encryptionapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;


public class FileUtil {

    private FileUtil() {
    }

    protected static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    protected static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    protected static boolean isExternalStorageRoot(File directory) {
        if (directory == null) {
            throw new IllegalArgumentException("Argument 'directory' cannot be null.");
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Argument 'directory' should be a directory.");
        }
        return directory.equals(Environment.getExternalStorageDirectory());
    }


    protected static Intent createOpenFileIntent(File file) throws IOException {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Log.d("PATH", MimeTypeMap.getFileExtensionFromUrl(file.toString()));
        if (isDocument(file)) {
            intent.setDataAndType(uri, "application/msword");
        } else if (isPDF(file)) {
            intent.setDataAndType(uri, "application/pdf");
        } else if (isPowerPoint(file)) {
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (isExcel(file)) {
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (file.toString().contains(".zip") || file.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (file.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (isMusic(file)) {
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (file.toString().contains(".gif")) {
            intent.setDataAndType(uri, "image/gif");
        } else if (isImage(file)) {
            intent.setDataAndType(uri, "image/jpeg");
        } else if (file.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (isVideo(file)) {
            intent.setDataAndType(uri, "video/*");
        } else if (isAPK(file)) {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        } else if (isCalendar(file)) {
            intent.setDataAndType(uri, "text/calendar");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static int getFileDrawableResource(File file) {
        if (isDocument(file)) {
            return R.drawable.ic_file;
        } else if (file.toString().contains(".pdf")) {
            return R.drawable.ic_pdf;
        } else if (isPowerPoint(file)) {
            return R.drawable.ic_ppt;
        } else if (isExcel(file)) {
            return R.drawable.ic_xls;
        } else if (isMusic(file)) {
            return R.drawable.ic_music;
        } else if (file.toString().contains(".gif")) {
            return R.drawable.ic_gif;
        } else if (isImage(file)) {
            return R.drawable.ic_image;
        } else if (file.toString().contains(".txt")) {
            return R.drawable.ic_txt;
        } else if (isVideo(file)) {
            return R.drawable.ic_video;
        } else if (isAPK(file)) {
            return R.drawable.ic_apk;
        } else if (isCalendar(file)) {
            return R.drawable.ic_calendar;
        }
        return R.drawable.ic_file;
    }

    private static boolean isDocument(File file) {
        return file.toString().contains(".doc") || file.toString().contains(".docx");
    }

    private static boolean isPowerPoint(File file) {
        return file.toString().contains(".ppt") || file.toString().contains(".pptx");
    }

    private static boolean isExcel(File file) {
        return file.toString().contains(".xls") || file.toString().endsWith(".xlsx");
    }

    private static boolean isMusic(File file) {
        return file.toString().contains(".wav") || file.toString().contains(".mp3");
    }

    private static boolean isImage(File file) {
        return file.toString().contains(".jpg") || file.toString().contains(".jpeg")
                || file.toString().contains(".png");
    }

    private static boolean isVideo(File file) {
        return file.toString().contains(".3gp") || file.toString().contains(".mpg")
                || file.toString().contains(".mpeg") || file.toString().contains(".mpe")
                || file.toString().contains(".mp4") || file.toString().contains(".avi");
    }

    private static boolean isAPK(File file) {
        return file.toString().contains(".apk");
    }

    private static boolean isCalendar(File file) {
        return file.toString().contains(".ics");
    }

    private static boolean isPDF(File file) {
        return file.toString().contains(".pdf");
    }

}
