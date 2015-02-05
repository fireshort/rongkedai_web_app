package com.yuexiaohome.framework.file;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

public class FileSize {

    public static final long SIZE_BT = 1024L;

    public static final long SIZE_KB = SIZE_BT * 1024L;

    public static final long SIZE_MB = SIZE_KB * 1024L;

    public static final long SIZE_GB = SIZE_MB * 1024L;

    public static final long SIZE_TB = SIZE_GB * 1024L;

    public static final int SCALE = 2;

    private File file;

    private long longSize;

    public FileSize(File file) {
        this.file = file;
    }

    private void getFileSize() throws RuntimeException, IOException {
        this.longSize = 0;

        if (file.exists() && file.isFile()) {
            this.longSize = file.length();

        } else if (file.exists() && file.isDirectory()) {
            getFileSize(file);
        } else {

        }
    }

    private void getFileSize(File file) throws RuntimeException, IOException {
        File[] fileArray = file.listFiles();
        if (fileArray != null && fileArray.length != 0) {
            for (int i = 0; i < fileArray.length; i++) {
                File fileSI = fileArray[i];
                if (fileSI.isDirectory()) {
                    getFileSize(fileSI);
                }
                if (fileSI.isFile()) {
                    this.longSize += fileSI.length();
                }
            }
        } else {
            this.longSize = 0;
        }
    }

    public String toString() throws RuntimeException {
        try {
            try {
                getFileSize();
            } catch (RuntimeException e) {
                return "";
            }

            return convertSizeToString(this.longSize);

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static String convertSizeToString(long fileSize) {
        if (fileSize >= 0 && fileSize < SIZE_BT) {
            return fileSize + "B";
        } else if (fileSize >= SIZE_BT && fileSize < SIZE_KB) {
            return fileSize / SIZE_BT + "KB";
        } else if (fileSize >= SIZE_KB && fileSize < SIZE_MB) {
            return fileSize / SIZE_KB + "MB";
        } else if (fileSize >= SIZE_MB && fileSize < SIZE_GB) {
            BigDecimal longs = new BigDecimal(Double.valueOf(fileSize + "").toString());
            BigDecimal sizeMB = new BigDecimal(Double.valueOf(SIZE_MB + "").toString());
            String result = longs.divide(sizeMB, SCALE, BigDecimal.ROUND_HALF_UP).toString();
            //double result=this.longSize/(double)SIZE_MB;
            return result + "GB";
        } else {
            BigDecimal longs = new BigDecimal(Double.valueOf(fileSize + "").toString());
            BigDecimal sizeMB = new BigDecimal(Double.valueOf(SIZE_GB + "").toString());
            String result = longs.divide(sizeMB, SCALE, BigDecimal.ROUND_HALF_UP).toString();
            return result + "TB";
        }
    }


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getLongSize() throws RuntimeException {
        try {
            getFileSize();
            return longSize;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }


}
