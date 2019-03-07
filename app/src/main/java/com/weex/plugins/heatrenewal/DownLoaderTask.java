package com.weex.plugins.heatrenewal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.unisoft.zjc.utdts.SplashActivity;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.StringTokenizer;

/**
 * Created by mac on 2018/7/30.
 */

public class DownLoaderTask extends AsyncTask<Void, Integer, Long> {


    private final String TAG = "DownLoaderTask";
    private URL mUrl;
    private File mFile;
    private String mOut;
    private ProgressDialog mDialog;
    private int mProgress = 0;
    String fileName = "";
    private ProgressReportingOutputStream mOutputStream;
    private Context mContext;
    ZipExtractorTask.ZipOverListener mZipOverListener;
    Boolean isFill;

    private String GetFileName(String file) {
        StringTokenizer st = new StringTokenizer(file, "/");
        while (st.hasMoreTokens()) {
            file = st.nextToken();
        }
        return file;
    }

    public DownLoaderTask(String url, String out, Context context, ZipExtractorTask.ZipOverListener ZipOverListener, Boolean isFill) {
        super();
        this.isFill = isFill;
        if (context != null) {
            mDialog = new ProgressDialog(context);
            mContext = context;
        } else {
            mDialog = null;
        }

        try {
            mZipOverListener = ZipOverListener;
            mUrl = new URL(url);
            mOut = out;
            fileName = GetFileName(url);
            mFile = getFilePath(mOut, fileName);
            Log.d(TAG, "out=" + out + ", name=" + fileName + ",mUrl.getFile()=" + mUrl.getFile());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public File getFilePath(String filePath,
                            String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }

    public void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onPreExecute()
    {
        // TODO Auto-generated method stub
        //super.onPreExecute();
        if (mDialog != null) {
            mDialog.setTitle("加载中...");
            mDialog.setMessage("");
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    cancel(true);
                }
            });
//            if (!mDialog.isShowing())
//                mDialog.show();
        }
    }

    @Override
    protected Long doInBackground(Void... params) {
        // TODO Auto-generated method stub
        return download();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // TODO Auto-generated method stub
        //super.onProgressUpdate(values);
        if (mDialog == null)
            return;
        if (values.length > 1) {
            int contentLength = values[1];
            if (contentLength == -1) {
                mDialog.setIndeterminate(true);
            } else {
                mDialog.setMax(contentLength);
            }
        } else {
            mDialog.setProgress(values[0].intValue());
        }
    }

    @Override
    protected void onPostExecute(Long result) {
        // TODO Auto-generated method stub
        //super.onPostExecute(result);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
//            mDialog.cancel();
//            mDialog=null;
        }
        if (isCancelled())
            return;
        //((MainActivity) mContext).showUnzipDialog();


//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
        /**
         *要执行的操作
         */


            readFile(mOut + "dtsversion.json");


//            }
//        }, 1000);//3秒后执行Runnable中的run方法

    }

    private long download() {
        URLConnection connection = null;
        int bytesCopied = 0;
        try {
            connection = mUrl.openConnection();
            int length = connection.getContentLength();
//            if (mFile.exists() && length == mFile.length()) {
//                Log.d(TAG, "file " + mFile.getName() + " already exits!!");
//                return 0l;
//            }
//            fileName = connection.getHeaderField("Content-Disposition");
//            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
//            ;// 通过Content-Disposition获取文件名，这点跟
//            mFile = getFilePath(mOut, fileName);
            mOutputStream = new ProgressReportingOutputStream(mFile);
            publishProgress(0, length);
            bytesCopied = copy(connection.getInputStream(), mOutputStream);
            if (bytesCopied != length && length != -1) {
                Log.e(TAG, "Download incomplete bytesCopied=" + bytesCopied + ", length" + length);
            }
            mOutputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytesCopied;
    }

    private int copy(InputStream input, OutputStream output) {
        byte[] buffer = new byte[1024 * 8];
        BufferedInputStream in = new BufferedInputStream(input, 1024 * 8);
        BufferedOutputStream out = new BufferedOutputStream(output, 1024 * 8);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, 1024 * 8)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return count;
    }

    private final class ProgressReportingOutputStream extends FileOutputStream {

        public ProgressReportingOutputStream(File file)
                throws FileNotFoundException {
            super(file);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void write(byte[] buffer, int byteOffset, int byteCount)
                throws IOException {
            // TODO Auto-generated method stub
            super.write(buffer, byteOffset, byteCount);
            mProgress += byteCount;
            publishProgress(mProgress);
        }

    }


    //读取热更新文件
    private String readFile(String filename) {
        String reads = "";
        try {
            File file = new File(filename);
            FileInputStream fis = new FileInputStream(file);

            byte[] b = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (fis.read(b) != -1) {
                baos.write(b, 0, b.length);
            }
            fis.close();
            baos.flush();
            baos.close();
            reads = baos.toString();
            Log.d("chenzhu", "chenzhu--->read file" + reads);
            JSONObject jsonObject = new JSONObject(reads);
            String url = jsonObject.getString("bundelUrl");
            SharedPreferences sp = mContext.getSharedPreferences("User", mContext.MODE_PRIVATE);
            String isVersion = sp.getString("isVersion", "");
            SharedPreferences.Editor edit = sp.edit();
            String current = sp.getString("current", "");
            if (current==null||current.equals("")){
                current=isVersion;
                edit.putString("current", isVersion);
                edit.commit();
            }
            String value;
            if (isVersion.equals("true")) {
                value = sp.getString("isValueTest", "");
            } else
                value = sp.getString("isValue", "");
            if (url != null)
                if (value == null || value == "") {
                    //获取到edit对象

                    //通过editor对象写入数据
                    if (isVersion.equals("true")) {
                        edit.putString("isLoaderValueText", jsonObject.getString("bundleVersion") + "");
                    } else
                        edit.putString("isLoaderValue", jsonObject.getString("bundleVersion") + "");
                    //通过editor对象写入数据

                    //提交数据存入到xml文件中
                    edit.commit();

                    FileDownLoaderTask task = new FileDownLoaderTask(url,
                            mOut, mContext, mZipOverListener, isFill);
                    //DownLoaderTask task = new DownLoaderTask("http://192.168.9.155/johnny/test.h264", getCacheDir().getAbsolutePath()+"/", this);
                    task.execute();
                    return "Y";
                } else if (compareVersion(value,jsonObject.getString("bundleVersion"))==-1 || !current.equals(isVersion)) {




                    //通过editor对象写入数据
                    if (isVersion.equals("true")) {
                        edit.putString("isLoaderValueText", jsonObject.getString("bundleVersion") + "");
                    } else
                        edit.putString("isLoaderValue", jsonObject.getString("bundleVersion") + "");
                    //提交数据存入到xml文件中
                    edit.commit();
                    Toast.makeText(mContext,"有新的版本自动更新中...",Toast.LENGTH_LONG).show();
                    if (isFill) {
                        Intent intent=new Intent(mContext, SplashActivity.class);
                        mContext.startActivity(intent);
                        Activity activity = (Activity) mContext;
                        activity.finish();
                        return "";
                    } else
                    {
                        try {
                            FileDownLoaderTask task = new FileDownLoaderTask(url,
                                    mOut, mContext, mZipOverListener ,isFill);
                            //DownLoaderTask task = new DownLoaderTask("http://192.168.9.155/johnny/test.h264", getCacheDir().getAbsolutePath()+"/", this);
                            task.execute();
                            edit.putString("current", isVersion);
                            edit.commit();
                        }catch (Exception e){}


                    }


                }else{
                    if (mZipOverListener != null)
                        mZipOverListener.zipOver();
                }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("chenzhu", "chenzhu--->read file FileNotFoundException" + e);
            e.printStackTrace();
        }
        return reads;
    }
    /**
     * 版本号比较
     *
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersion(String version1, String version2) {
        if (version1.equals(version2)) {
            return 0;
        }
        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");
        Log.d("HomePageActivity", "version1Array=="+version1Array.length);
        Log.d("HomePageActivity", "version2Array=="+version2Array.length);
        int index = 0;
        // 获取最小长度值
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;
        // 循环判断每位的大小
        Log.d("HomePageActivity", "verTag2=2222="+version1Array[index]);
        while (index < minLen
                && (diff = Integer.parseInt(version1Array[index])
                - Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            // 如果位数不一致，比较多余位数
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }


}
