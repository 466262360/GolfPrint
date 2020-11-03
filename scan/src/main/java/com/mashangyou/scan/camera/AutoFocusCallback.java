package com.mashangyou.scan.camera;

/**
 * Created by Administrator on 2016/11/28.
 */
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

final class AutoFocusCallback implements Camera.AutoFocusCallback {
    private static final String TAG = AutoFocusCallback.class.getSimpleName();
    private static final long AUTOFOCUS_INTERVAL_MS = 1500L;
    private Handler autoFocusHandler;
    private int autoFocusMessage;

    AutoFocusCallback() {
    }

    void setHandler(Handler autoFocusHandler, int autoFocusMessage) {
        this.autoFocusHandler = autoFocusHandler;
        this.autoFocusMessage = autoFocusMessage;
    }

    public void onAutoFocus(boolean success, Camera camera) {
        if(this.autoFocusHandler != null) {
            Message message = this.autoFocusHandler.obtainMessage(this.autoFocusMessage, Boolean.valueOf(success));
            Log.d(TAG, "Got auto-focus callback; requesting another");
            this.autoFocusHandler.sendMessageDelayed(message, 1500L);
            this.autoFocusHandler = null;
        } else {
            Log.d(TAG, "Got auto-focus callback, but no handler for it");
        }

    }
}
