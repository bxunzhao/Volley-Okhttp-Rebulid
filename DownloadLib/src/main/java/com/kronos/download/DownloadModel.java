package com.kronos.download;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.kronos.download.adapter.BaseObserveAdapter;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Leif Zhang on 16/9/29.
 * Email leifzhanggithub@gmail.com
 */
public class DownloadModel extends BaseObserveAdapter {
    private String downloadUrl = "";
    private String downloadFolder = Environment.getExternalStorageDirectory().getPath() + "/wallstreetcn/";
    private String sdFile;
    private String fileName;
    private String suffixName;

    public void setSuffixName(String suffixName) {
        this.suffixName = suffixName;
    }

    private int progress;
    private int state;
    private long totalLength;
    private long downloadLength = 0;

    private Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 10:
                    notifyDataChange();
                    break;
            }
            return true;
        }
    });

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSdCardFile() {
        if (TextUtils.isEmpty(sdFile)) {
            sdFile = downloadFolder + downloadUrl.hashCode() + getSuffix(downloadUrl);
        }
        return sdFile;
    }

    public void setDownloadFolder(String downloadFolder) {
        this.downloadFolder = downloadFolder;
    }

    public void addDownloadLength(int length) {
        this.downloadLength += length;
        int curProgress = Math.round(downloadLength * 100f / totalLength);
        if (curProgress != progress) {
            progress = curProgress;
            handler.sendEmptyMessage(10);
        }
    }

    public long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

    public long getDownloadLength() {
        try {
            File file = new File(getSdCardFile());
            downloadLength = !file.exists() ? 0 : file.length();
            return downloadLength;
        } catch (Exception e) {
            e.printStackTrace();
            return downloadLength = 0;
        }
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getProgress() {
        return progress;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private String getSuffix(String url) {
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String suffixes = "avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|txt|html|zip|java|doc";
        Pattern pat = Pattern.compile("[\\w]+[\\.](" + suffixes + ")");//正则判断
        Matcher mc = pat.matcher(url);//条件匹配
        String suffix = "";
        while (mc.find()) {
            suffix = mc.group();//截取文件名后缀名
        }
        if (!TextUtils.isEmpty(suffix)) {
            fileName = TextUtils.isEmpty(fileName) ? suffix.substring(0, suffix.indexOf(".")) : fileName;
            suffix = suffix.substring(suffix.indexOf("."), suffix.length());
        } else {
            fileName = TextUtils.isEmpty(fileName) ? "未知文件" : fileName;
            suffix = ".temp";
        }
        if (TextUtils.isEmpty(suffixName)) {
            return suffix;
        } else {
            return suffixName;
        }
    }

    public boolean check() {
        return getDownloadLength() == getTotalLength() && getTotalLength() != 0;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setDownloadLength(long downloadLength) {
        this.downloadLength = downloadLength;
    }

    public void deleteFile() {
        File file = new File(getSdCardFile());
        file.delete();
    }
}
