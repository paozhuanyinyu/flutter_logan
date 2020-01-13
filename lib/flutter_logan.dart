import 'dart:async';

import 'package:flutter/services.dart';

class Logan{
  static const MethodChannel _channel =
      const MethodChannel('caixin.com/flutter_logan');
  static Future<bool> init(String encryptKey,String encryptValue,{String cachePath,String path}) async{
    var argument = {
      'cachePath': cachePath,
      'path': path,
      'encryptKey': encryptKey,
      'encryptValue': encryptValue
    };
    return await _channel.invokeMethod('init',argument);
  }
  static Future<bool> w(String msg,int type) async{
    var argument = {
      'msg': msg,
      'type': type
    };
    return await _channel.invokeMethod('w',argument);
  }
  static Future<bool> f() async{
    return await _channel.invokeMethod('f');
  }
}
