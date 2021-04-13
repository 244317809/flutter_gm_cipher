#import "AppDelegate.h"
#import "GeneratedPluginRegistrant.h"

#import <GMSm2Utils.h>

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application
    didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
  [GeneratedPluginRegistrant registerWithRegistry:self];
  // Override point for customization after application launch.
    
    // TEST SNIPPETS
//
//    NSString *enResult = [GMSm2Utils encryptText:@"abcd1234" publicKey:@"04f602d341e94a200a21c0228f3f1e4ec0559fedcbd6ec0ae9c04638d5d9b999b42bed63087db7b421066d9988234226118223f6ed70e798a8463066608abf75d1"];
//
//    NSArray<NSString *> *c1c3c2 = [GMSm2Utils asn1DecodeToC1C3C2Array:enResult];
//    if (c1c3c2.count != 3) {
//        return nil;
//    }
//
//    NSString *c1c2c3Hex = [NSString stringWithFormat:@"04%@%@%@", c1c3c2[0], c1c3c2[2], c1c3c2[1]];
//
//    NSString *decode = [GMSm2Utils decryptToText:enResult privateKey:@"c9435b1007747ac1e9e111c30560dd5cbf96645f2bacd9564157785c96bee91b"];
//    NSLog(@"%@", enResult);
    
  return [super application:application didFinishLaunchingWithOptions:launchOptions];
}

@end
