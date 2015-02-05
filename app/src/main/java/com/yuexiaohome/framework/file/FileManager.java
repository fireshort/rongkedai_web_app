package com.yuexiaohome.framework.file;

import android.os.Environment;
import com.yuexiaohome.framework.util.L;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static final String AVATAR_LARGE = "avatar_large";

    private static final String PICTURE_THUMBNAIL = "picture_thumbnail";

    private static final String PICTURE_BMIDDLE = "picture_bmiddle";

    private static final String PICTURE_LARGE = "picture_large";

    private static final String WEBVIEW_FAVICON = "favicon";

    private static final String LOG = "log";

    private static String getSdCardCachePath() {
        String path = null;
//        if (isExternalStorageMounted()) {
//            File extDir = GlobalApp.getInstance().getExternalCacheDir();
//            if (extDir != null) {
//                path = extDir.getAbsolutePath();
//            } else {
//
//            }
//        }
        return path;
    }


    public static boolean isExternalStorageMounted() {

        boolean canRead = Environment.getExternalStorageDirectory().canRead();
        boolean onlyRead = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED_READ_ONLY);
        boolean unMounted = Environment.getExternalStorageState().equals(
                Environment.MEDIA_UNMOUNTED);

        return !(!canRead || onlyRead || unMounted);
    }

    public static String getUploadPicTempFile() {

        if (!isExternalStorageMounted()) {
            return "";
        } else {
            return getSdCardCachePath() + File.separator + "upload.jpg";
        }
    }


    public static File createNewFileInSDCard(String absolutePath) {
        if (!isExternalStorageMounted()) {
            L.e("sdcard unavailiable");
            return null;
        }

        File file = new File(absolutePath);
        if (file.exists()) {
            return file;
        } else {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try {
                if (file.createNewFile()) {
                    return file;
                }
            } catch (IOException e) {
                L.d(e.getMessage());
                return null;
            }
        }
        return null;
    }


    public static String getCacheSize() {
        if (isExternalStorageMounted()) {
            String path = getSdCardCachePath() + File.separator;
            FileSize size = new FileSize(new File(path));
            return size.toString();
        }
        return "0MB";
    }

    public static List<String> getCachePath() {
        List<String> path = new ArrayList<String>();
        if (isExternalStorageMounted()) {
            String thumbnailPath = getSdCardCachePath() + File.separator + PICTURE_THUMBNAIL;
            String middlePath = getSdCardCachePath() + File.separator + PICTURE_BMIDDLE;
            String oriPath = getSdCardCachePath() + File.separator + PICTURE_LARGE;
            String largeAvatarPath = getSdCardCachePath() + File.separator + AVATAR_LARGE;

            path.add(thumbnailPath);
            path.add(middlePath);
            path.add(oriPath);
            path.add(largeAvatarPath);
        }
        return path;
    }

    public static String getPictureCacheSize() {
        long size = 0L;
        if (isExternalStorageMounted()) {
            String thumbnailPath = getSdCardCachePath() + File.separator + PICTURE_THUMBNAIL;
            String middlePath = getSdCardCachePath() + File.separator + PICTURE_BMIDDLE;
            String oriPath = getSdCardCachePath() + File.separator + PICTURE_LARGE;
            size += new FileSize(new File(thumbnailPath)).getLongSize();
            size += new FileSize(new File(middlePath)).getLongSize();
            size += new FileSize(new File(oriPath)).getLongSize();

        }
        return FileSize.convertSizeToString(size);
    }

    public static boolean deleteCache() {
        String path = getSdCardCachePath() + File.separator;
        return deleteDirectory(new File(path));
    }

    public static boolean deletePictureCache() {
        String thumbnailPath = getSdCardCachePath() + File.separator + PICTURE_THUMBNAIL;
        String middlePath = getSdCardCachePath() + File.separator + PICTURE_BMIDDLE;
        String oriPath = getSdCardCachePath() + File.separator + PICTURE_LARGE;

        deleteDirectory(new File(thumbnailPath));
        deleteDirectory(new File(middlePath));
        deleteDirectory(new File(oriPath));

        return true;
    }

    private static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    public static boolean saveToPicDir(String path, String ext) {
        if (!isExternalStorageMounted()) {
            return false;
        }

        File file = new File(path);
        String name = file.getName();
        String newPath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + name;
        if (ext != null) {
            newPath += ext;
        }
        try {
            FileManager.createNewFileInSDCard(newPath);
            copyFile(file, new File(newPath));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean saveToPicDir(String path) {
        return saveToPicDir(path, null);
    }


    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            outBuff.flush();
        } finally {
            if (inBuff != null) {
                inBuff.close();
            }
            if (outBuff != null) {
                outBuff.close();
            }
        }
    }
}
