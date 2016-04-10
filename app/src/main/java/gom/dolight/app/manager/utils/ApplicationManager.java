package gom.dolight.app.manager.utils;

import android.content.Context;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.RemoteException;
import gom.dolight.app.manager.interfaces.OnInstalledPackaged;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// 숨겨진 메소드를 찾기 위해 생성된 클래스입니다.
public class ApplicationManager {

    public static final int INSTALL_SUCCEEDED = 1;
    public static final int INSTALL_FAILED_ALREADY_EXISTS = -1;
    public static final int INSTALL_FAILED_INVALID_APK = -2;
    public static final int INSTALL_FAILED_INVALID_URI = -3;
    public static final int INSTALL_FAILED_INSUFFICIENT_STORAGE = -4;
    public static final int INSTALL_FAILED_DUPLICATE_PACKAGE = -5;
    public static final int INSTALL_FAILED_NO_SHARED_USER = -6;
    public static final int INSTALL_FAILED_UPDATE_INCOMPATIBLE = -7;
    public static final int INSTALL_FAILED_SHARED_USER_INCOMPATIBLE = -8;
    public static final int INSTALL_FAILED_MISSING_SHARED_LIBRARY = -9;
    public static final int INSTALL_FAILED_REPLACE_COULDNT_DELETE = -10;
    public static final int INSTALL_FAILED_DEXOPT = -11;
    public static final int INSTALL_FAILED_OLDER_SDK = -12;
    public static final int INSTALL_FAILED_CONFLICTING_PROVIDER = -13;
    public static final int INSTALL_FAILED_NEWER_SDK = -14;
    public static final int INSTALL_FAILED_TEST_ONLY = -15;
    public static final int INSTALL_FAILED_CPU_ABI_INCOMPATIBLE = -16;
    public static final int INSTALL_FAILED_MISSING_FEATURE = -17;
    public static final int INSTALL_FAILED_CONTAINER_ERROR = -18;
    public static final int INSTALL_FAILED_INVALID_INSTALL_LOCATION = -19;
    public static final int INSTALL_FAILED_MEDIA_UNAVAILABLE = -20;
    public static final int INSTALL_PARSE_FAILED_NOT_APK = -100;
    public static final int INSTALL_PARSE_FAILED_BAD_MANIFEST = -101;
    public static final int INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION = -102;
    public static final int INSTALL_PARSE_FAILED_NO_CERTIFICATES = -103;
    public static final int INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES = -104;
    public static final int INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING = -105;
    public static final int INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME = -106;
    public static final int INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID = -107;
    public static final int INSTALL_PARSE_FAILED_MANIFEST_MALFORMED = -108;
    public static final int INSTALL_PARSE_FAILED_MANIFEST_EMPTY = -109;
    public static final int INSTALL_FAILED_INTERNAL_ERROR = -110;
    // 각종 return code들
    public final int INSTALL_REPLACE_EXISTING = 2;
    private PackageInstallObserver observer;
    private PackageManager pm;
    private Method method;

    private OnInstalledPackaged onInstalledPackaged;

    public ApplicationManager(Context context) throws SecurityException, NoSuchMethodException {
        observer = new PackageInstallObserver();
        pm = context.getPackageManager();

        // 파라미터 설정
        Class<?>[] types = new Class[]{Uri.class, IPackageInstallObserver.class, int.class, String.class};
        // installPackage 이름 붙은거와 위 types 라는 파라미터를 담은 메소드를 찾음
        method = pm.getClass().getMethod("installPackage", types);
    }

    public void setOnInstalledPackaged(OnInstalledPackaged onInstalledPackaged) {
        // 인터페이스 설정
        this.onInstalledPackaged = onInstalledPackaged;
    }

    public void installPackage(String apkFile) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        // apk 경로로 패키지 설치
        installPackage(new File(apkFile));
    }

    public void installPackage(File apkFile) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        // 파일이 존재하지 않을 경우 IllegalArgumentException 투척
        if (!apkFile.exists()) throw new IllegalArgumentException();
        Uri packageURI = Uri.fromFile(apkFile);
        // URL로 패키지 설치
        installPackage(packageURI);
    }

    @SuppressWarnings("RedundantArrayCreation")
    public void installPackage(Uri apkFile) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        // 메소드 실행!
        method.invoke(pm, new Object[]{apkFile, observer, INSTALL_REPLACE_EXISTING, null});
    }

    class PackageInstallObserver extends IPackageInstallObserver.Stub {

        // observer에서 설치되었다고 알려주면 OnInstallPackaged 전송
        public void packageInstalled(String packageName, int returnCode) throws RemoteException {
            if (onInstalledPackaged != null) {
                onInstalledPackaged.packageInstalled(packageName, returnCode);
            }
        }
    }

}
