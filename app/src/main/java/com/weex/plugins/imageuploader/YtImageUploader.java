package com.weex.plugins.imageuploader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.unisoft.zjc.utdts.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by zhangjiacheng on 2018/6/13.
 */
public class YtImageUploader extends WXModule {


    /**
     * 把batmap 转file
     * @param bitmap
     * @param filepath
     */
    public static File saveBitmapFile(Bitmap bitmap, String filepath) {
        File file = new File(filepath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    private Bitmap compressPixel(String filePath){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只解析图片边沿，获取宽高
        BitmapFactory.decodeFile(filePath, options);
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);

    }



    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    @JSMethod
    public void uploadImage(HashMap<String, String> param, final JSCallback jsCallback) {

        final String imagePath = param.get("imagePath");
        final String serverUrl =  param.get("serverUrl");


        if(imagePath == null || serverUrl == null ) {

            HashMap<String, Object> param1 = new HashMap<>();
            param.put("msg", "参数错误");
            HashMap<String, Object> o = new HashMap<String, Object>();
            o.put("error", param1);
            jsCallback.invoke(o);
        }
        else {
            Thread th = new Thread(){
                @Override
                public void run() {
                    //super.run();
                    String result = httpUtil.postImage(serverUrl, saveBitmapFile(compressPixel(imagePath),imagePath));
                    if (result == null) {
                        HashMap<String, Object> param1 = new HashMap<>();
                        param1.put("msg", "上传图片失败");
                        HashMap<String, Object> o = new HashMap<String, Object>();
                        o.put("error", param1);
                        jsCallback.invoke(o);
                    }
                    else {
                        HashMap<String, Object> o = new HashMap<String, Object>();
                        o.put("imageName", result);
                        jsCallback.invoke(o);
                    }

                }
            };
            th.start();

        }
    }
}
