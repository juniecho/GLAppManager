# GLAppManager
GomdoLight 앱 관리자는 커스텀 펌웨어 개발자가 최종 사용자에게 다수의 번들 앱을 통합된 환경에서 제공하면서 최종 사용자로 하여금 각 번들 앱의 설치 여부를 자유롭게 선택하게 하기 위하여 사용하는 '앱 설치 관리자' 환경입니다. 커스텀 펌웨어 개발자는 자신이 원하는 번들 앱의 APK를 시스템 파티션의 지정된 위치에 삽입하고 (파일시스템에서 read 퍼미션이 부여될 것을 요함) 최종적으로 GomdoLight 앱 관리자를 privileged system app으로서 (즉 /system/priv-app에) 탑재하면 됩니다.

번들 앱의 APK 파일이 위치할 곳은 /app/src/main/java/gom/dolight/app/manager/Constants.java 에 PATH 변수에 지정된 값이며 기본값은 /system/gomdolconfig입니다.

GomdoLight 앱 관리자는 INSTALL_PACKAGES 권한을 사용하여 APK를 강제로 sideload하기 때문에, 최종 사용자가 출처를 알 수 없는 앱의 설치를 허용할 필요가 없으므로 그만큼 사용자 측면의 보안 실수를 줄이는 효과가 있습니다. 또한 Play 스토어를 통하여 설치하거나 업데이트하기 곤란한 패키지도 손쉽게 사용자에게 배포하여 자유롭게 업데이트하도록 할 수 있으므로 번들 앱을 제공하기에는 최적의 인터페이스를 가지고 있습니다.

유용하게 사용하시기 바랍니다.

주의: GomdoLight 앱 관리자는 독자적인 고유의 이용 계약(라이선스)에 따라 이용 권한이 부여됩니다. 반드시 이용 계약의 내용을 먼저 확인하시기 바랍니다. 이용 계약서의 한국어 사본은 http://gomdolight.com/?page_id=2007 에서 제공됩니다.
