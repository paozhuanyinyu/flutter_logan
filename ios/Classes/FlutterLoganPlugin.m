#import "FlutterLoganPlugin.h"

@implementation FlutterLoganPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"caixin.com.flutter/logan"
            binaryMessenger:[registrar messenger]];
  FlutterLoganPlugin* instance = [[FlutterLoganPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)initLoganKey:(NSString *)encryptKey withValue:(NSString *)encryptValue{
    NSData *keydata = [encryptKey dataUsingEncoding:NSUTF8StringEncoding];
    NSData *ivdata = [encryptValue dataUsingEncoding:NSUTF8StringEncoding];
    uint64_t file_max = 10 * 1024 * 1024;
    // logan初始化，传入16位key，16位iv，写入文件最大大小(byte)
    loganInit(keydata, ivdata, file_max);
    loganUseASL(YES);
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"init" isEqualToString:call.method]) {
        NSDictionary *arguments = [call arguments];
        NSString *encryptKey = arguments[@"encryptKey"];
        NSString *encryptValue = arguments[@"encryptValue"];
        [self initLoganKey:encryptKey withValue:encryptValue];
        result(@YES);
  }
  else if ([@"w" isEqualToString:call.method]) {
      NSDictionary *arguments = [call arguments];
      NSString *msg = arguments[@"msg"];
      NSNumber *type = arguments[@"type"];
      logan([type integerValue],msg);
      result(@YES);
  }
  else if ([@"f" isEqualToString:call.method]) {
      loganFlush();
      result(@YES);
  }
  else if ([@"getAllFilesInfo" isEqualToString:call.method]) {
      result(loganAllFilesInfo());
  }
  else if ([@"setDebug" isEqualToString:call.method]) {
      NSNumber *debug = [call arguments];
      loganPrintClibLog(debug);
      result(@YES);
  }
  else if ([@"s" isEqualToString:call.method]) {
        NSDictionary *arguments = [call arguments];
        NSString *url = arguments[@"url"];
        NSString *date = arguments[@"date"];
        NSString *appId = arguments[@"appId"];
        NSString *unionId = arguments[@"unionId"];
        NSString *deviceId = arguments[@"deviceId"];
        loganUpload(url,
                date,
                appId,
                unionId,
                deviceId,
                ^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
            NSNumber *code = [NSNumber numberWithInt:0];
            NSString *msg = @"";
            if(error){
                code = [NSNumber numberWithInt:-1];
                msg = error.userInfo.description;
            }
            result([NSDictionary dictionaryWithObjectsAndKeys:msg,@"msg", code,@"code",nil]);
        });
    }
  else {
    result(FlutterMethodNotImplemented);
  }
}

@end
