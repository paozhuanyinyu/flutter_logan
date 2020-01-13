#import "FlutterLoganPlugin.h"

@implementation FlutterLoganPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"caixin.com/flutter_logan"
            binaryMessenger:[registrar messenger]];
  FlutterLoganPlugin* instance = [[FlutterLoganPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)initLoganKey:(NSString *)encryptKey withValue:(NSString *)encryptValue{
    NSData *keydata = [@"0123456789012345" dataUsingEncoding:NSUTF8StringEncoding];
    NSData *ivdata = [@"0123456789012345" dataUsingEncoding:NSUTF8StringEncoding];
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
  else {
    result(FlutterMethodNotImplemented);
  }
}

@end
