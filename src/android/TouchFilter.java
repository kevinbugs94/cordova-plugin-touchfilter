package com.example.touchfilter;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

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

                if (protectionEnabled) {
                    callbackContext.success("Protection already enabled");
                    return;
                }

                View decorView = cordova.getActivity().getWindow().getDecorView();

                //------------------------------------------
                // Aplicar protección a todas las Views
                //------------------------------------------

                int protectedViews = applyTouchFilter(decorView);

                Log.i(TAG, "Protected Views: " + protectedViews);

                //------------------------------------------
                // Listener de seguridad
                //------------------------------------------

                securityTouchListener = (v, event) -> {

                    if (isOverlayDetected(event)) {

                        Log.w(TAG,
                                "Touch blocked. Flags="
                                        + event.getFlags()
                                        + " Action="
                                        + event.getAction());

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
     * Recorre todo el árbol de Views aplicando
     * setFilterTouchesWhenObscured(true)
     */
    private int applyTouchFilter(View view) {

        if (view == null) {
            return 0;
        }

        view.setFilterTouchesWhenObscured(true);

        int count = 1;

        if (view instanceof ViewGroup) {

            ViewGroup group = (ViewGroup) view;

            for (int i = 0; i < group.getChildCount(); i++) {

                count += applyTouchFilter(group.getChildAt(i));

            }

        }

        return count;
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

        if (!protectionEnabled) {
            return;
        }

        cordova.getActivity().runOnUiThread(() -> {

            try {

                View decorView = cordova.getActivity().getWindow().getDecorView();

                int protectedViews = applyTouchFilter(decorView);

                Log.i(TAG,
                        "Touch protection reapplied. Protected Views: "
                                + protectedViews);

            } catch (Exception e) {

                Log.e(TAG, "Failed to reapply TouchFilter", e);

            }

        });

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
