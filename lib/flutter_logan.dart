import 'dart:async';

import 'package:flutter/services.dart';

class Logan{
  static const MethodChannel _channel =
      const MethodChannel('caixin.com/flutter_logan');

  /// 初始化方法,在使用之前必须滴啊用初始化方法
  /// encryptKey key值的加密秘钥
  /// encryptValue value值得加密秘钥
  /// cachePath(可选) 缓存日志文件的存储地址，如若不传使用Logan的默认存储地址
  /// path(可选) 日志文件的存储地址，如若不传使用Logan的默认存储地址
  static Future<bool> init(String encryptKey,String encryptValue,{String cachePath,String path}) async{
    var argument = {
      'cachePath': cachePath,
      'path': path,
      'encryptKey': encryptKey,
      'encryptValue': encryptValue
    };
    return await _channel.invokeMethod('init',argument);
  }
  /// 写入方法
  /// msg 写入的值
  /// type 写入的值得类型
  static Future<bool> w(String msg,int type) async{
    var argument = {
      'msg': msg,
      'type': type
    };
    return await _channel.invokeMethod('w',argument);
  }
  /// 刷新方法，完成写入到文件的操作
  static Future<bool> f() async{
    return await _channel.invokeMethod('f');
  }

  /// 上传日志文件到服务器，使用Logan内置的上传方式
  /// url 接收日志的服务器地址
  /// date 日志日期 格式：yyyy-MM-dd
  /// appId 当前应用的唯一标识,在多App时区分日志来源App
  /// unioniId 当前用户的唯一标识,用来区分日志来源用户
  /// deviceId 设备id
  static Future<Map> s(String url,String date,String appId, String unioniId, String deviceId) async{
    var argument = {
      'url': url,
      'date': date,
      'appId': appId,
      'unioniId': unioniId,
      'deviceId': deviceId
    };
    Map map = await _channel.invokeMethod('s',argument);
    return map;
  }
}
