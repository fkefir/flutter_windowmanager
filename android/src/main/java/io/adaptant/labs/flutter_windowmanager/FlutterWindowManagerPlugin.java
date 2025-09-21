package io.adaptant.labs.flutter_windowmanager;

import android.app.Activity;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** FlutterWindowManagerPlugin */
public class FlutterWindowManagerPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Activity activity;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_windowmanager");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (activity == null) {
      result.error("NO_ACTIVITY", "flutter_windowmanager requires a foreground activity.", null);
      return;
    }
    
    int flags = 0;
    switch(call.method){
      case "addFlags":
        flags = call.argument("flags");
        activity.getWindow().addFlags(flags);
        result.success(null);
        break;
      case "clearFlags":
        flags = call.argument("flags");
        activity.getWindow().clearFlags(flags);
        result.success(null);
        break;
      case "setFlags":
        flags = call.argument("flags");
        boolean fullscreen = (flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
        
        if (fullscreen) {
            activity.getWindow().setFlags(flags, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        result.success(null);
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  // ActivityAware methods
  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }
}
