#import "FlutterGmCipherPlugin.h"
#import "GMSm2Utils.h"
#import "GMSm3Utils.h"

@implementation FlutterGmCipherPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"flutter_gm_cipher"
            binaryMessenger:[registrar messenger]];
  FlutterGmCipherPlugin* instance = [[FlutterGmCipherPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else if([@"sm2EncryptText" isEqualToString:call.method]) {
      NSString *plainText = call.arguments[@"plainText"];
      NSString *publicKey = call.arguments[@"publicKey"];
      
      //使用公钥加密
      NSString *enResult = [GMSm2Utils encryptText:plainText publicKey:publicKey];
      
      //服务器使用C1C2C3排序，需要重新编排密文
      NSArray<NSString *> *c1c3c2 = [GMSm2Utils asn1DecodeToC1C3C2Array:enResult];
      if (c1c3c2.count != 3) {
          FlutterError *error = [FlutterError errorWithCode:@"重编密文顺序异常" message:@"c1c3c2个数不为3" details:nil];
          result(error);
          return;
      }
      
      NSString *c1c2c3Hex = [NSString stringWithFormat:@"04%@%@%@", c1c3c2[0], c1c3c2[2], c1c3c2[1]];
      result(c1c2c3Hex);
  } else if([@"sm3EncryptText" isEqualToString:call.method]) {
      NSString *plainText = call.arguments[@"plainText"];
      result([GMSm3Utils hashWithString:plainText]);
  } else {
    result(FlutterMethodNotImplemented);
  }
}

@end
