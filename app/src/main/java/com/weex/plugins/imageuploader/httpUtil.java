package com.weex.plugins.imageuploader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by xiangyongjie on 2016/10/18.
 */
public class httpUtil {
    /*判断网络是否连接 true表示连接成功，false表示失败*/
    public static boolean isNetWorkConn(Context context) {
        try {
            if (context == null)
                return false;
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info == null) {
                return false;
            } else {
                return info.isConnected();
            }
        } catch (Exception e) {
            return false;
        }

    }

    /*判断网络是否连接 true表示连接成功，false表示失败*/
    public static boolean isNetWorkConn(Context context, Handler handler) {
        try {
            if (context == null)
                return false;
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info == null) {
                Message msg = new Message();
                msg.arg1 = 1;
                handler.sendMessage(msg);
                return false;
            } else {
                return info.isConnected();
            }
        } catch (Exception e) {
            Message msg = new Message();
            msg.arg1 = 1;
            handler.sendMessage(msg);
            return false;
        }
    }

    /**
     * get请求
     */
    public static String getNetWorkResult(String urlString) {
        StringBuffer buf = new StringBuffer();
        InputStream is = null;
        HttpURLConnection connection = null;
        InputStreamReader reader = null;
        try {
            URL url = new URL(urlString);
//            connection  = DownLoadUtil.initSSL(connection, url);// (HttpURLConnection) url.openConnection();
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(50 * 1000);
            connection.setReadTimeout(30 * 1000);
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
                reader = new InputStreamReader(is, "UTF-8");//防止中文出现乱码问题
                int len = 0;
                char[] buffer = new char[1024];

                while ((len = reader.read(buffer)) != -1) {
                    buf.append(new String(buffer, 0, len));
                }

                return buf.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 获取图片 get请求
     */
    public static void getImage(String urlString, File file) {
        FileOutputStream outputStream = null;
        StringBuffer buf = new StringBuffer();
        InputStream is = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            outputStream = new FileOutputStream(file);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(50 * 1000);
//            connection.setReadTimeout(30 * 1000);
            //设置获取图片的方式为GET
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, count);

                }
                outputStream.flush();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 上传数据
     */
    public static String postNetWorkResult(String urlString, String value) {

        StringBuffer buf = new StringBuffer();
        InputStream is = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
//            connection  = DownLoadUtil.initSSL(connection, url);// (HttpURLConnection) url.openConnection();
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(50 * 1000);
            connection.setReadTimeout(30 * 1000);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.connect();
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            out.print(value);//写入输出流
            out.flush();//立即刷新
            out.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) != -1) {
                    buf.append(new String(buffer, 0, count));
                }
                return buf.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 上传图片
     */
    public static String postImage(String urlString, File file) {

        String multipart_form_data = "multipart/form-data";
        String lineStart = "--";
        String boundary = "****************ef5fH38L0hL9DIO";    // 数据分隔符
        String lineEnd = "\r\n";
        HttpURLConnection conn = null;
        DataOutputStream output = null;
        BufferedReader input = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(120000);
            conn.setDoInput(true);        // 允许输入
            conn.setDoOutput(true);        // 允许输出
            conn.setUseCaches(false);    // 不使用Cache
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "text/html, application/xhtml+xml, */*");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", multipart_form_data + "; boundary=" + boundary);
            conn.connect();
            output = new DataOutputStream(conn.getOutputStream());


            StringBuilder split = new StringBuilder();
            split.append(lineStart + boundary + lineEnd);
            split.append("Content-Disposition: form-data; name=\"" + "files" + "\"; filename=\"" + file.getName() + "\"" + lineEnd);
            split.append("Content-Type: " + "image/jpeg" + lineEnd);
            split.append(lineEnd);
            System.out.println("**************result" + split);
            try {
                // 发送图片数据
                output.writeBytes(split.toString());
                FileInputStream fis = new FileInputStream(file);
                byte[] b = new byte[1024];
                int n;
                while ((n = fis.read(b)) != -1) {
                    output.write(b, 0, n);
                }
                fis.close();
                output.writeBytes(lineEnd);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            // 添加图片

            output.writeBytes(lineStart + boundary + lineStart + lineEnd);// 数据结束标志
            output.flush();

            int code = conn.getResponseCode();

            if (code == 200) {

                input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String oneLine;
                while ((oneLine = input.readLine()) != null) {
                    response.append(oneLine + lineEnd);
                }

                String resStr =  response.toString();
                Gson gson = new Gson();
                ImageUploaderResult result = gson.fromJson(resStr, ImageUploaderResult.class);
                if (result.getResult()) {
                    return result.getMessage();
                }
                return null;

            } else {
                return null;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // 统一释放资源
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
