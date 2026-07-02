package com.example.touchfilter;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

public class TouchFilter extends CordovaPlugin {

    private static final String TAG = "TouchFilter";

    private boolean protectionEnabled = false;

    private View.OnTouchListener securityTouchListener;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {

        if (!"enable".equals(action)) {
            return false;
        }

        cordova.getActivity().runOnUiThread(() -> {

            try {

                View decorView = cordova.getActivity().getWindow().getDecorView();
                View rootView = decorView.getRootView();
                View webView = (View) this.webView.getView();

                // Evitar registrar nuevamente
                if (protectionEnabled) {
                    callbackContext.success("Protection already enabled");
                    return;
                }

                //------------------------------------------
                // Protección nativa Android
                //------------------------------------------

                decorView.setFilterTouchesWhenObscured(true);
                rootView.setFilterTouchesWhenObscured(true);
                webView.setFilterTouchesWhenObscured(true);

                //------------------------------------------
                // Listener de seguridad
                //------------------------------------------

                securityTouchListener = (v, event) -> {

                    if (isOverlayDetected(event)) {

                        Log.w(TAG,
                                "Touch blocked. Flags=" + event.getFlags()
                                        + " Action=" + event.getAction());

                        // En futuras versiones aquí se puede enviar
                        // un callback hacia JavaScript.

                        return true;
                    }

                    return false;
                };

                decorView.setOnTouchListener(securityTouchListener);

                protectionEnabled = true;

                Log.i(TAG, "Touch protection enabled");

                callbackContext.success("Protection enabled");

            } catch (Exception e) {

                Log.e(TAG, "Error enabling protection", e);

                callbackContext.error(e.getMessage());

            }

        });

        return true;
    }

    /**
     * Detecta overlays completos y parciales.
     */
    private boolean isOverlayDetected(MotionEvent event) {

        int flags = event.getFlags();

        if ((flags & MotionEvent.FLAG_WINDOW_IS_OBSCURED) != 0) {

            Log.w(TAG, "FLAG_WINDOW_IS_OBSCURED detected");

            return true;
        }

        if ((flags & MotionEvent.FLAG_WINDOW_IS_PARTIALLY_OBSCURED) != 0) {

            Log.w(TAG, "FLAG_WINDOW_IS_PARTIALLY_OBSCURED detected");

            return true;
        }

        return false;
    }

    @Override
    public void onResume(boolean multitasking) {

        super.onResume(multitasking);

        if (protectionEnabled) {
            Log.i(TAG, "Application resumed with TouchFilter enabled");
        }

    }

    @Override
    public void onPause(boolean multitasking) {

        super.onPause(multitasking);

        if (protectionEnabled) {
            Log.i(TAG, "Application paused");
        }

    }

    @Override
    public void onDestroy() {

        protectionEnabled = false;
        securityTouchListener = null;

        Log.i(TAG, "TouchFilter destroyed");

        super.onDestroy();

    }

}
