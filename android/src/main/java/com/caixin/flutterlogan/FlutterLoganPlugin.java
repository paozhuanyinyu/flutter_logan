package com.caixin.flutterlogan;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.dianping.logan.Logan;
import com.dianping.logan.LoganConfig;
import com.dianping.logan.SendLogCallback;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterLoganPlugin */
public class FlutterLoganPlugin implements MethodCallHandler {
  private String cachePath;
  private String path;
  private String versionName;
  private String versionCode;

  private FlutterLoganPlugin(Registrar registrar){
    this.cachePath = registrar.context().getFilesDir().getAbsolutePath();
    this.path = registrar.context().getExternalFilesDir(null).getAbsolutePath() + File.separator + "logan_v1";
    try {
      PackageInfo pInfo = registrar.context().getPackageManager().getPackageInfo(registrar.context().getPackageName(), 0);
      versionName = pInfo.versionName;
      versionCode = String.valueOf(pInfo.versionCode);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void initLogan(String cachePath,String path, String encryptKey,String encryptValue) {
    LoganConfig config = new LoganConfig.Builder()
            .setCachePath(cachePath)
            .setPath(path)
            .setEncryptKey16(encryptKey.getBytes())
            .setEncryptIV16(encryptValue.getBytes())
            .build();
    Logan.init(config);
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "caixin.com.flutter/logan");
    channel.setMethodCallHandler(new FlutterLoganPlugin(registrar));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if(call.method.equals("init")){
      String cachePath = call.argument("cachePath");
      String path = call.argument("path");
      String encryptKey = call.argument("encryptKey");
      String encryptValue = call.argument("encryptValue");
      if(!TextUtils.isEmpty(cachePath)){
        this.cachePath = cachePath;
      }
      if(!TextUtils.isEmpty(path)){
        this.path = path;
      }
      initLogan(this.cachePath,this.path,encryptKey,encryptValue);
      result.success(true);
    } else if(call.method.equals("w")){
      String msg = call.argument("msg");
      int type = call.argument("type");
      Logan.w(msg,type);
      result.success(true);
    }else if(call.method.equals("f")){
      Logan.f();
      result.success(true);
    }else if(call.method.equals("s")){
      String url = call.argument("url");
      String date = call.argument("date");
      String appId = call.argument("appId");
      String unionId = call.argument("unionId");
      String deviceId = call.argument("deviceId");
      String versionCode = call.argument("versionCode");
      String versionName = call.argument("versionName");
      if(!TextUtils.isEmpty(versionCode)){
        this.versionCode = versionCode;
      }
      if(!TextUtils.isEmpty(versionName)){
        this.versionName = versionName;
      }
      loganSendByDefault(result,url,date,appId,unionId,deviceId,this.versionCode,this.versionName);

    }else if(call.method.equals("getAllFilesInfo")){
      result.success(Logan.getAllFilesInfo());
    }else if(call.method.equals("setDebug")){
      Logan.setDebug((boolean)call.arguments);
      result.success(true);
    } else {
      result.notImplemented();
    }
  }

  private void loganSendByDefault(final Result result, String url, String date, String appId, String unionId, String deviceId,String versionCode,String versionName) {
    Logan.s(url, date, appId, unionId, deviceId, versionCode, versionName, new SendLogCallback() {
      @Override
      public void onLogSendCompleted(int statusCode, byte[] data) {
        final String resultData = data != null ? new String(data) : "";
        Map<String,Object> map = new HashMap<>();
        map.put("code",statusCode);
        map.put("msg",resultData);
        MainThreadResult mainThreadResult = new MainThreadResult(result);
        mainThreadResult.success(map);
      }
    });
  }

  private static class MainThreadResult implements MethodChannel.Result {
    private MethodChannel.Result result;
    private Handler handler;

    MainThreadResult(MethodChannel.Result result) {
      this.result = result;
      handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void success(final Object data) {
      handler.post(
              new Runnable() {
                @Override
                public void run() {
                  result.success(data);
                }
              });
    }

    @Override
    public void error(
            final String errorCode, final String errorMessage, final Object errorDetails) {
      handler.post(
              new Runnable() {
                @Override
                public void run() {
                  result.error(errorCode, errorMessage, errorDetails);
                }
              });
    }

    @Override
    public void notImplemented() {
      handler.post(
              new Runnable() {
                @Override
                public void run() {
                  result.notImplemented();
                }
              });
    }
  }
}
