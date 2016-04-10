package gom.dolight.app.manager.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import gom.dolight.app.manager.Constants;

// 앱 재부팅 코드
public class RebootDelegator implements Constants {

    // 재부팅후 메인으로 가게 합니다.
    public static void reboot(Context context) {
        reboot(context, context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName()));
    }

    // 재부팅후 설정한 액티비티로 가게 합니다.
    public static void reboot(Context context, Class<? extends Activity> gotoActivityName) {
        reboot(context, new Intent(context, gotoActivityName));
    }

    public static void reboot(Context context, @NonNull Intent restartIntent) {
        if (isDebug) {
            Log.e("RebootDelegator", "rebooting app");
        }
        // 재부팅 후 예전 있던 프로세스는 자동 종료
        restartIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (context instanceof Activity) {
            context.startActivity(restartIntent);
        } else {
            // 액티비티가 실행중이지 않을 때도 재부팅하도록 설정
            restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(restartIntent);
        }

        if (context instanceof Activity) {
            // 앱 재부팅
            finishAffinity((Activity) context);
        }
    }

    private static void finishAffinity(Activity activity) {
        activity.setResult(Activity.RESULT_CANCELED);
        // 젤리빈부터 추가된 재부팅 코드로 처리
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.finishAffinity();
        } else {
            // 그 이하는 호환성 라이브러리 사용
            ActivityCompat.finishAffinity(activity);
        }
    }
}