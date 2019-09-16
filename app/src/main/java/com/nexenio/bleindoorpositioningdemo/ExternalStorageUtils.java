package com.nexenio.bleindoorpositioningdemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

/**
 * Created by steppschuh on 04/11/2016.
 */

public abstract class ExternalStorageUtils {

    /**
     * Checks if external storage is available for read and write
     */
    public static boolean isExternalStorageWritable() {
        String state;
        try {
            state = Environment.getExternalStorageState();
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Checks if external storage is available to at least read
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getDocumentsDirectory(String subFolder) {
        File file;
        if (subFolder != null) {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), subFolder);
        } else {
            file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        }
        file.mkdirs();
        return file;
    }

    public static File getCacheDirectory(Context context, String subFolder) {
        File file;
        if (subFolder != null) {
            file = new File(context.getExternalCacheDir(), subFolder);
        } else {
            file = context.getExternalCacheDir();
        }
        file.mkdirs();
        return file;
    }

    public static long getFileSize(@NonNull File file) {
        long size = file.length();
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (f.isDirectory()) {
                    size += getFileSize(f);
                }
                size += f.length();
            }
        }
        return size;
    }

    /**
     * Emits all {@link File}s (that are not directories) in the specified directory. Also includes
     * files in sub directories.
     */
    public static List<File> getFilesInDirectory(@NonNull File directory) {
        List<File> files = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                files.addAll(getFilesInDirectory(file));
            } else {
                files.add(file);
            }
        }
        return files;
    }

    /**
     * Returns a list of {@link File}s which have the .json extension in the specified directory.
     * Also includes files in subfolders.
     */
    public static List<File> getJsonFilesInDirectory(File directory) {
        ArrayList<File> jsonFiles = new ArrayList<>();
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                jsonFiles.addAll(getJsonFilesInDirectory(file));
            } else {
                if (file.getName().endsWith(".json")) {
                    jsonFiles.add(file);
                }
            }
        }
        return jsonFiles;
    }

    /**
     * Removes the given files from the external storage.
     */
    public static boolean removeFiles(List<File> files) {
        boolean allFilesRemoved = true;
        for (File file : files) {
            allFilesRemoved &= file.delete();
        }
        return allFilesRemoved;
    }

    /**
     * Invokes an implicit share intent for the specified file.
     */
    public static void shareFile(File file, Context context) {
        if (file == null || !file.canRead()) {
            return;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        Uri contentUri = FileProvider.getUriForFile(context, "com.nexenio.bleindoorpositioning.fileprovider", file);

        shareIntent.setTypeAndNormalize(context.getContentResolver().getType(contentUri));
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, file.getName());
        shareIntent.putExtra(Intent.EXTRA_TEXT, file.getName());
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);

        Intent chooserIntent = Intent.createChooser(shareIntent, "Share file");
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (shareIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(chooserIntent);
        } else {
            Toast.makeText(context, "Unable to share file", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Writes the contents of a string to the specified file.
     *
     * @param data   the string that the file should contain
     * @param file   the file that the string should be written to
     * @param append if false, file contents will be overwritten
     */
    public static void writeStringToFile(String data, File file, boolean append) throws IOException {
        try (FileWriter fw = new FileWriter(file, append)) {
            fw.write(data);
        }
    }

    /**
     * Returns the amount of bytes that are available in the specified directory.
     */
    public static long getAvailableMemory(@NonNull File directory) {
        StatFs stats = new StatFs(directory.getAbsolutePath());
        return stats.getAvailableBlocksLong() * stats.getBlockSizeLong();
    }

}
