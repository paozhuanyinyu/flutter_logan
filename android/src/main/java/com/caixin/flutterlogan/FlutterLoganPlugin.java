package com.caixin.flutterlogan;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import com.dianping.logan.Logan;
import com.dianping.logan.LoganConfig;
import com.dianping.logan.SendLogCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
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
  private String buildVersion;
  private String appVersion;
  private FlutterLoganPlugin(Registrar registrar){
    this.cachePath = registrar.context().getFilesDir().getAbsolutePath();
    this.path = registrar.context().getExternalFilesDir(null).getAbsolutePath() + File.separator + "logan_v1";
    try {
      PackageInfo pInfo = registrar.context().getPackageManager().getPackageInfo(registrar.context().getPackageName(), 0);
      appVersion = pInfo.versionName;
      buildVersion = String.valueOf(pInfo.versionCode);
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
  private void initLogan(String encryptKey,String encryptValue) {
    initLogan(cachePath,path,encryptKey,encryptValue);
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "caixin.com/flutter_logan");
    channel.setMethodCallHandler(new FlutterLoganPlugin(registrar));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if(call.method.equals("init")){
      String cachePath = call.argument("cachePath");
      String path = call.argument("path");
      String encryptKey = call.argument("encryptKey");
      String encryptValue = call.argument("encryptValue");
      if(!TextUtils.isEmpty(cachePath) && !TextUtils.isEmpty(path)){
        initLogan(cachePath,path,encryptKey,encryptValue);
      }else{
        initLogan(encryptKey,encryptValue);
      }
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
      loganSendByDefault(result,
              (String)call.argument("url"),
              (String)call.argument("date"),
              (String)call.argument("appId"),
              (String)call.argument("unionId"),
              (String)call.argument("deviceId")
      );
    }else if(call.method.equals("getAllFilesInfo")){
      result.success(Logan.getAllFilesInfo());
    }else if(call.method.equals("setDebug")){
      Logan.setDebug((boolean)call.arguments);
      result.success(true);
    } else {
      result.notImplemented();
    }
  }

  private void loganSendByDefault(final Result result, String url, String date, String appId, String unionId, String deviceId) {
    Logan.s(url, date, appId, unionId, deviceId, buildVersion, appVersion, new SendLogCallback() {
      @Override
      public void onLogSendCompleted(int statusCode, byte[] data) {
        final String resultData = data != null ? new String(data) : "";
        Map<String,Object> map = new HashMap<>();
        map.put("code",statusCode);
        map.put("msg",resultData);
        result.success(map);
      }
    });
  }
}
