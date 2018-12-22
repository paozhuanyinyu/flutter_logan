package com.caixin.flutterlogan;

import android.content.Context;
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
  private FlutterLoganPlugin(MethodChannel channel, Registrar registrar){
    initLogan(registrar.context());
  }

  private void initLogan(Context context) {
    LoganConfig config = new LoganConfig.Builder()
            .setCachePath(context.getFilesDir().getAbsolutePath())
            .setPath(context.getExternalFilesDir(null).getAbsolutePath()
                    + File.separator + "logan_v1")
            .setEncryptKey16("0123456789012345".getBytes())
            .setEncryptIV16("0123456789012345".getBytes())
            .build();
    Logan.init(config);
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "caixin.com/flutter_logan");
    channel.setMethodCallHandler(new FlutterLoganPlugin(channel,registrar));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if(call.method.equals("logan#w")){
      String msg = call.argument("msg");
      int type = call.argument("type");
      Log.d("Logan",msg);
      Logan.w(msg,type);
      result.success(true);
    }else if(call.method.equals("logan#f")){
      Logan.f();
      result.success(true);
    }
    else if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }
}
