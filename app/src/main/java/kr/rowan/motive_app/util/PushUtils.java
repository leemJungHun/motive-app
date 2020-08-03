package kr.rowan.motive_app.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

public class PushUtils {
    private static PowerManager.WakeLock mWakeLock;
    @SuppressLint("InvalidWakeLockTag")
    public static void acquireWakeLock(Context context){
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        mWakeLock = pm.newWakeLock( PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WAKEUP" );
        mWakeLock.acquire(3000);
    }
    public static void releaseWakeLock(){
        if(mWakeLock != null){
            mWakeLock.release();
            mWakeLock = null;
        }
    }
}
