package com.example.touchfilter;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.provider.Settings;
import android.content.Context;
import android.os.Build;
public class TouchFilter extends CordovaPlugin {
   @Override
   public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
       Activity activity = this.cordova.getActivity();
       if (activity == null) {
           callbackContext.error("Activity is null");
           return false;
       }
       if ("enableFilter".equals(action)) {
           View rootView = activity.getWindow().getDecorView();
           applyFilterRecursively(rootView);
           callbackContext.success("Filter enabled");
           return true;
       }
       if ("isOverlayEnabled".equals(action)) {
           boolean result = isOverlayPermissionEnabled(activity);
           callbackContext.success(result ? 1 : 0);
           return true;
       }
       return false;
   }
   private void applyFilterRecursively(View view) {
       if (view == null) return;
       view.setFilterTouchesWhenObscured(true);
       if (view instanceof ViewGroup) {
           ViewGroup group = (ViewGroup) view;
           for (int i = 0; i < group.getChildCount(); i++) {
               applyFilterRecursively(group.getChildAt(i));
           }
       }
   }
   private boolean isOverlayPermissionEnabled(Context context) {
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           return Settings.canDrawOverlays(context);
       }
       return false;
   }
}