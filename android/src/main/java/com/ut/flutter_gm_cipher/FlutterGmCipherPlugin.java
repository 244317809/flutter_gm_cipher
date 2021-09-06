package com.ut.flutter_gm_cipher;

import androidx.annotation.NonNull;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.jcajce.provider.digest.SM3;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import ut.pcmspf.common.security.security.SM3Utils;

/** FlutterGmCipherPlugin */
public class FlutterGmCipherPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_gm_cipher");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("sm2EncryptText")) {
      String plainText = call.argument("plainText");
      String publicKey = call.argument("publicKey");
      String enResult;
      System.out.println("plainText: " + plainText);
      System.out.println("publicKey: " + publicKey);
      try {
        Sm2Engine sm2Engine = new Sm2Engine();
        enResult = sm2Engine.encodeDataWithPublicKey(plainText, publicKey);

        result.success(enResult);
      } catch (NoSuchAlgorithmException | InvalidCipherTextException e) {
        e.printStackTrace();
        result.error("SM2加密异常", e.getLocalizedMessage(), e.getCause());
      }
    } else if (call.method.equals("sm3EncryptText")) {
      String plainText = call.argument("plainText");;
      String enResult = SM3Utils.encrypt(plainText);

      result.success(enResult);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
