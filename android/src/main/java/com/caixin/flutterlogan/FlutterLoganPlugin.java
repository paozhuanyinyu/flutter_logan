package com.caixin.flutterlogan;

import android.text.TextUtils;
import android.util.Log;
import com.dianping.logan.Logan;
import com.dianping.logan.LoganConfig;
import java.io.File;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterLoganPlugin */
public class FlutterLoganPlugin implements MethodCallHandler {
  private String cachePath;
  private String path;
  private FlutterLoganPlugin(Registrar registrar){
    this.cachePath = registrar.context().getFilesDir().getAbsolutePath();
    this.path = registrar.context().getExternalFilesDir(null).getAbsolutePath() + File.separator + "logan_v1";
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
      Log.d("Logan",msg);
      Logan.w(msg,type);
      result.success(true);
    }else if(call.method.equals("f")){
      Logan.f();
      result.success(true);
    } else {
      result.notImplemented();
    }
  }
}
