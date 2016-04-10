package gom.dolight.app.manager.utils;

import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

/**
 * MidoriBot
 * Class: StatusBarColorUtils
 * Created by WindSekirun on 2016. 2. 24..
 */
public class StatusBarColorUtils {

    public static void setColor(Window w) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 색상은 16진수로 설정합니다. 0xffRRGGBB
            w.setStatusBarColor(0xff009688);
            w.setNavigationBarColor(0xff009688);
        }
    }
}
