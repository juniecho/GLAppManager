package android.content.pm;

// 이 클래스는 Android 내 PackageManager 속에 숨겨져 있는 클래스입니다.
// 별도 설명은 하지 않습니다.
public interface IPackageInstallObserver extends android.os.IInterface {
	
	public abstract static class Stub extends android.os.Binder implements android.content.pm.IPackageInstallObserver {
		public Stub() {
			throw new RuntimeException("Stub!");
		}

		public static android.content.pm.IPackageInstallObserver asInterface(android.os.IBinder obj) {
			throw new RuntimeException("Stub!");
		}

		public android.os.IBinder asBinder() {
			throw new RuntimeException("Stub!");
		}

		public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags)
				throws android.os.RemoteException {
			throw new RuntimeException("Stub!");
		}
	}

	public abstract void packageInstalled(java.lang.String packageName, int returnCode)
			throws android.os.RemoteException;
}