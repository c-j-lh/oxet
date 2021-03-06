package com.example.s.oxet;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.nononsenseapps.filepicker.FilePickerFragment;

import java.io.File;

public class ImageFilePickerFragment extends FilePickerFragment {

    /**
     *
     * @param file
     * @return The file extension. If file has no extension, it returns null.
     */
    public static String getExtension(@NonNull File file) {
        String path = file.getPath();
        int i = path.lastIndexOf(".");
        if (i < 0) {
            return null;
        } else {
            return path.substring(i);
        }
    }

    @Override
    protected boolean isItemVisible(final File file) {
        return true;
//        boolean ret = super.isItemVisible(file);
//        if (ret && !isDir(file) && (mode == MODE_FILE || mode == MODE_FILE_AND_DIR)) {
//            String ext = getExtension(file);
//            return ext != null && (".txt".equalsIgnoreCase(ext)||".gam".equalsIgnoreCase(ext)||".lang".equalsIgnoreCase(ext));
//        }
//        return ret;
    }

}