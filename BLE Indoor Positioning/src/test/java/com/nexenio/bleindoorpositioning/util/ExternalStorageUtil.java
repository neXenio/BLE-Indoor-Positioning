package com.nexenio.bleindoorpositioning.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import io.reactivex.Observable;

public class ExternalStorageUtil {

    /**
     * Returns a list of {@link File}s which have the .json extension in the specified directory.
     * Also includes files in subfolders.
     */
    public static Observable<File> getObservableForJsonFilesInDirectory(File directory) {
        return getObservableForFilesInDirectory(directory).filter(file -> file.getName().endsWith(".json"));
    }

    public static Observable<File> getObservableForFilesInDirectory(File directory) {
        return Observable.create(emitter -> {
            List<File> files = getFilesInDirectory(directory);
            for (File file : files) {
                emitter.onNext(file);
            }
            emitter.onComplete();
        });
    }

    public static List<File> getFilesInDirectory(File directory) {
        ArrayList<File> files = new ArrayList<>();
        File[] f = directory.listFiles();
        if (f == null) {
            return files;
        }
        for (File file : f) {
            if (file.isDirectory()) {
                files.addAll(getFilesInDirectory(file));
            } else {
                files.add(file);
            }
        }
        return files;
    }

    public static String readFile(File file) throws IOException {
        return new Scanner(file).useDelimiter("\\Z").next();
    }

}
