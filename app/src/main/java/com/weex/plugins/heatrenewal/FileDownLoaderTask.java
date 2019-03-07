package com.weex.plugins.heatrenewal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.unisoft.zjc.utdts.R;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

/**
 * Created by mac on 2018/7/30.
 */

public class FileDownLoaderTask
        extends AsyncTask<Void, Integer, Long> {


    private final String TAG = "DownLoaderTask";
    private URL mUrl;
    private File mFile;
    private String mOut;
    private ProgressDialog mDialog;
    private int mProgress = 0;
    String fileName = "";
    private FileDownLoaderTask.ProgressReportingOutputStream mOutputStream;
    private Context mContext;
    ZipExtractorTask.ZipOverListener mZipOverListener;
    Boolean isFill;
    String PATH;
    private String GetFileName(String file) {
        StringTokenizer st = new StringTokenizer(file, "/");
        while (st.hasMoreTokens()) {
            file = st.nextToken();
        }
        return file;
    }

    public FileDownLoaderTask(String url, String out, Context context, ZipExtractorTask.ZipOverListener ZipOverListener,Boolean isFill ) {
        super();
        PATH = Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/";
        this.isFill=isFill;
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
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        //super.onPreExecute();
        if (mDialog != null) {
            mDialog.setTitle("正在下载最新版本...");
            mDialog.setMessage("");
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    cancel(true);
                }
            });
            if(!mDialog.isShowing())
                mDialog.show();
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
        if(result!=0){
            ZipExtractorTask task = new ZipExtractorTask(mOut + "/" + fileName, mOut, mContext, true, mZipOverListener);
            task.execute();
        }else{
            AlertDialog dialog = new AlertDialog.Builder(mContext)
//                    .setIcon(R.mipmap.icon)//设置标题的图片
                    .setTitle("温馨提示")//设置对话框的标题
                    .setMessage("下载热更新文件失败，请检查网络")//设置对话框的内容
                    //设置对话框的按钮

                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(MainActivity.this, "点击了确定的按钮", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            isFill = false;

                            SharedPreferences sp = mContext.getSharedPreferences("User", 0);
                            String value = sp.getString("isVersion", "");
                            String url = "";
                            if (value != null) {
                                if (value.equals("true")) {
                                    url = "https://exeutest.blob.core.chinacloudapi.cn/app/dtsversion.json";
                                } else
                                    url = "http://tt.ab-inbev.cn/TrackApp/AppUpdate/dtsversion.json";
                            } else {
                                url = "http://tt.ab-inbev.cn/TrackApp/AppUpdate/dtsversion.json";
                            }
                            DownLoaderTask task = new DownLoaderTask(url,
                                    PATH, mContext, mZipOverListener, isFill);
                            //DownLoaderTask task = new DownLoaderTask("http://192.168.9.155/johnny/test.h264", getCacheDir().getAbsolutePath()+"/", this);
                            task.execute();
                        }
                    }).create();
            dialog.show();
        }






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
            mOutputStream = new FileDownLoaderTask.ProgressReportingOutputStream(mFile);
            publishProgress(0, length);
            bytesCopied = copy(connection.getInputStream(), mOutputStream);
            if (bytesCopied != length && length != -1) {
                Log.e(TAG, "Download incomplete bytesCopied=" + bytesCopied + ", length" + length);
            }
            mOutputStream.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return 0;
//            e.printStackTrace();
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



}
