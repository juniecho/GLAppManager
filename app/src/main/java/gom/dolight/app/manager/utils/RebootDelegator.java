package gom.dolight.app.manager.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import gom.dolight.app.manager.Constants;

public class RebootDelegator implements Constants {

    public static void reboot(Context context) {
        reboot(context, context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName()));
    }

    public static void reboot(Context context, Class<? extends Activity> gotoActivityName) {
        reboot(context, new Intent(context, gotoActivityName));
    }

    public static void reboot(Context context, @NonNull Intent restartIntent) {
        if (isDebug) {
            Log.e("RebootDelegator", "rebooting app");
        }

        restartIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (context instanceof Activity) {
            context.startActivity(restartIntent);
        } else {
            restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(restartIntent);
        }

        if (context instanceof Activity) {
            finishAffinity((Activity) context);
        }
    }

    private static void finishAffinity(Activity activity) {
        //finishing Activity
        activity.setResult(Activity.RESULT_CANCELED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.finishAffinity();
        } else {
            ActivityCompat.finishAffinity(activity);
        }
    }
}