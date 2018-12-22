#import "FlutterLoganPlugin.h"

@implementation FlutterLoganPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"caixin.com/flutter_logan"
            binaryMessenger:[registrar messenger]];
  FlutterLoganPlugin* instance = [[FlutterLoganPlugin alloc] initWithRegistrar];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (instancetype)initWithRegistrar{
    NSData *keydata = [@"0123456789012345" dataUsingEncoding:NSUTF8StringEncoding];
    NSData *ivdata = [@"0123456789012345" dataUsingEncoding:NSUTF8StringEncoding];
    uint64_t file_max = 10 * 1024 * 1024;
    // logan初始化，传入16位key，16位iv，写入文件最大大小(byte)
    loganInit(keydata, ivdata, file_max);
    loganUseASL(YES);
    return self;
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"logan#w" isEqualToString:call.method]) {
      NSDictionary *arguments = [call arguments];
      NSString *msg = arguments[@"msg"];
      NSNumber *type = arguments[@"type"];
      logan([type integerValue],msg);
      result(@YES);
  }
  else if ([@"logan#f" isEqualToString:call.method]) {
      loganFlash();
      result(@YES);
  }
  else if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  }
  else {
    result(FlutterMethodNotImplemented);
  }
}

@end
