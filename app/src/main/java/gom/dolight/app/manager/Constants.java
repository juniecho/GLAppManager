package gom.dolight.app.manager;

import android.os.Environment;

/**
 * GLAPPManager
 * Class: Constants
 * Created by WindSekirun on 16. 4. 10..
 */
public interface Constants {
    boolean isDebug = false;

    // apk 파일이 위치할 폴더의 경로를 설정합니다.
    // Environment.getExternalStorageDirectory() 는 SD카드 경로, 즉 /storage/0/emulate 를 리턴합니다. (폰마다 다름)
    // 시스템 경로로 할 계획이시면 systemPATH 처럼 절대 경로로 설정해주세요.
    String PATH = Environment.getExternalStorageDirectory() + "/apps/";
    String systemPATH = "/system/apps/priv-app/";
}
