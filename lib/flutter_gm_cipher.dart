
import 'dart:async';

import 'package:flutter/services.dart';

class FlutterGmCipher {
  static const MethodChannel _channel =
      const MethodChannel('flutter_gm_cipher');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> sm2EncryptText(String plainText, String publicKeyHex) async {
    final String encryptText = await _channel.invokeMethod('sm2EncryptText', {'plainText': plainText, 'publicKey': publicKeyHex});
    return encryptText;
  }
}
