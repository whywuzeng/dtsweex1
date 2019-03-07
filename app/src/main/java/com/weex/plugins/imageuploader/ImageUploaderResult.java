package com.weex.plugins.imageuploader;

/**
 * Created by zhangjiacheng on 2018/6/13.
 */

public class ImageUploaderResult {
    private boolean Result;
    private String Message;
    private String Info;
    private String HandleDate;

    public  boolean getResult() {
        return Result;
    }
    public  void  setResult(boolean result) {
        this.Result = result;
    }

    public  String getMessage() {
        return Message;
    }
    public  void  setMessage(String message) {
        this.Message = message;
    }

    public  String getInfo() {
        return Info;
    }
    public  void  setInfo(String info) {
        this.Info = info;
    }

    public  String getHandleDate() {
        return HandleDate;
    }
    public  void  setHandleDate(String handleDate) {
        this.HandleDate = handleDate;
    }

}
