import 'dart:async';

import 'package:flutter/services.dart';

class Logan {
  static const MethodChannel _channel =
      const MethodChannel('caixin.com/flutter_logan');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
  static Future<bool> w(String msg,int type) async{
    var argument = {
      'msg': msg,
      'type': type
    };
    return await _channel.invokeMethod('logan#w',argument);
  }
  static Future<bool> f() async{
    return await _channel.invokeMethod('logan#f');
  }
}
