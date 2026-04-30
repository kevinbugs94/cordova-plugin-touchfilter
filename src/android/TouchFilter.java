package com.example.touchfilter;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import android.view.View;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.app.Activity;

public class TouchFilter extends CordovaPlugin {

    @Override
public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {

    if (action.equals("enable")) {

        cordova.getActivity().runOnUiThread(() -> {
            try {

                View decorView = cordova.getActivity().getWindow().getDecorView();
                View rootView = decorView.getRootView();
                View webView = (View) this.webView.getView();

                // Protección
                webView.setFilterTouchesWhenObscured(true);
                decorView.setFilterTouchesWhenObscured(true);
                rootView.setFilterTouchesWhenObscured(true);

                decorView.setOnTouchListener((v, event) -> {

                    if ((event.getFlags() & MotionEvent.FLAG_WINDOW_IS_OBSCURED) != 0 ||
                        (event.getFlags() & MotionEvent.FLAG_WINDOW_IS_PARTIALLY_OBSCURED) != 0) {

                        // Opcional: log o callback
                        return true; // BLOQUEA
                    }

                    return false; // deja pasar
                });

                callbackContext.success("Protection enabled");

            } catch (Exception e) {
                callbackContext.error("Error: " + e.getMessage());
            }
        });

        return true;
    }

    return false;
}

    private boolean isSuspiciousInteraction() {
        return true;
    }

    private boolean isCriticalZone(float x, float y) {
        return (x > 300 && x < 800 && y > 1200 && y < 1600);
    }
}
