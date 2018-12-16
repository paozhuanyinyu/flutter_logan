import 'dart:async';

import 'package:flutter/services.dart';

class Logan {
  static const MethodChannel _channel =
      const MethodChannel('flutter_logan');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
  static Future<void> w(String msg,int type) async{
    var argument = {
      'msg': msg,
      'type': type
    };
    await _channel.invokeMethod('logan',argument);
  }
}
