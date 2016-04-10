package gom.dolight.app.manager.interfaces;

// 앱을 설치했을 경우 리턴해주는 인터페이스.
public interface OnInstalledPackaged {
	
	public void packageInstalled(String packageName, int returnCode);
}
