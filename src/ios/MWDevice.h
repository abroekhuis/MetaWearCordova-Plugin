#import <Cordova/CDVPlugin.h>
#import "MBLMetaWear.h"
#import "MBLMetaWearManager.h"


@interface MWDevice : CDVPlugin

- (void)initialize:(CDVInvokedUrlCommand*)command;
- (void)scanForDevices:(CDVInvokedUrlCommand*)command;
- (void)connect:(CDVInvokedUrlCommand*)command;
- (void)readRssi:(CDVInvokedUrlCommand*)command;

@property MBLMetaWear *connectedDevice;

@end
