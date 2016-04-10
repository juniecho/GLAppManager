package gom.dolight.app.manager.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.afollestad.materialdialogs.MaterialDialog;
import gom.dolight.app.manager.Constants;
import gom.dolight.app.manager.R;
import gom.dolight.app.manager.interfaces.OnInstalledPackaged;
import gom.dolight.app.manager.list.holder.AppViewHolder;
import gom.dolight.app.manager.list.holder.CategoryViewHolder;
import gom.dolight.app.manager.list.item.CategoryItem;
import gom.dolight.app.manager.list.item.ListItem;
import gom.dolight.app.manager.list.item.RecyclerItem;
import gom.dolight.app.manager.utils.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Constants {
    Toolbar toolbar;
    PackageManager pm;
    ApplicationManager am;
    RecyclerView list;

    AppViewAdapter adapter;
    ArrayList<RecyclerItem> itemSet;
    LinearLayoutManager layoutManager;
    MaterialDialog installDialog;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 네비게이션, 상태바 색상 칠하기
        StatusBarColorUtils.setColor(getWindow());

        // 위 제목을 나타나는 위젯. 메뉴 등 넣을 수 있습니다.
        // 일단은 shadow가 적용되는 타이틀바로 대체, 후일 필요하면 toolbar 사용
        // toolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbarSetting();

        pm = getPackageManager();
        try {
            // 숨겨진 installPackages 메소드를 찾기 위한 클래스
            am = new ApplicationManager(MainActivity.this);
            // APK가 설치되었을 때 메소드를 호출합니다.
            am.setOnInstalledPackaged(new OnInstalledPackaged() {
                public void packageInstalled(String packageName, int returnCode) {
                    if (installDialog.isShowing())
                        installDialog.dismiss();
                    if (returnCode == ApplicationManager.INSTALL_SUCCEEDED) {
                        RebootDelegator.reboot(MainActivity.this);
                    }
                }
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        list = (RecyclerView) findViewById(R.id.list);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        list.setLayoutManager(layoutManager);

        // APK 리스트 로드 프로세스 (백그라운드 작업) 실행
        new LoadAPKList().execute();

        installDialog = new MaterialDialog.Builder(MainActivity.this)
                .content(R.string.installing)
                .cancelable(false)
                .progress(true, 0)
                .build();
    }

    /* 상술한 toolbar 관련
    public void toolbarSetting() {
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(0xffffffff);
    } */

    // 패키지가 설치되었는지 확인하는 메소드
    private boolean isPackageInstalled(String packageName) {
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    // 패키지가 업데이트 가능한지 확인하는 메소드
    private boolean isPackageAvailableUpdate(String packageName, String appPath) {
        try {
            PackageInfo installedInfo = pm.getPackageInfo(packageName, 0);
            PackageInfo folderInfo = pm.getPackageArchiveInfo(appPath, 0);
            return installedInfo.versionCode < folderInfo.versionCode;
        } catch (Exception e) {
            return false;
        }
    }

    // 카테고리를 생성하는 메소드
    public RecyclerItem generateCategoryItem(String text) {
        RecyclerItem item = new RecyclerItem();
        CategoryItem categoryItem = new CategoryItem();
        categoryItem.setCategoryText(text);
        item.setViewType(1);
        item.setCategoryItem(categoryItem);
        return item;
    }

    // 앱 리스트를 생성하는 메소드
    public RecyclerItem generateListItem(ListItem listItem) {
        RecyclerItem item = new RecyclerItem();
        item.setViewType(0);
        item.setListItem(listItem);
        return item;
    }

    // 앱 삭제시 삭제 결과를 리턴하는 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 72) {
            if (resultCode == RESULT_OK) {
                // 재부팅 메소드
                RebootDelegator.reboot(MainActivity.this);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("TAG", "onActivityResult: user canceled the (un)install");
            } else if (resultCode == RESULT_FIRST_USER) {
                Log.d("TAG", "onActivityResult: failed to (un)install");
            }
        }
    }

    // APK 로드 프로세스
    public class LoadAPKList extends AsyncTask<Void, Void, Void> {
        ArrayList<String> apkFileList = new ArrayList<>();
        MaterialDialog loadingDialog;

        ArrayList<ListItem> updateList;
        ArrayList<ListItem> installList;
        ArrayList<ListItem> deleteList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // 작업하기 전에 로딩중 다이얼로그 띄움
            // MaterialDialog란 오픈소스 라이브러리를 사용합니다.
            // https://github.com/afollestad/material-dialogs
            loadingDialog = new MaterialDialog.Builder(MainActivity.this)
                    .content(R.string.loading)
                    .cancelable(false)
                    .progress(true, 0)
                    .show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // 리스트 비우기
            itemSet = new ArrayList<>();
            updateList = new ArrayList<>();
            installList = new ArrayList<>();
            deleteList = new ArrayList<>();

            // 설정된 PATH로부터 파일들을 불러옵니다.
            File f = new File(PATH);
            File file[] = f.listFiles();

            // 반복문으로 apk 파일들의 절대 경로를 추가합니다.
            for (File fe : file) {
                apkFileList.add(fe.getAbsolutePath());
            }

            for (String apkPath : apkFileList) {
                // 패키지 정보를 불러옵니다.
                PackageInfo info = pm.getPackageArchiveInfo(apkPath, 0);
                info.applicationInfo.sourceDir = apkPath;
                info.applicationInfo.publicSourceDir = apkPath;
                CharSequence name = pm.getApplicationLabel(info.applicationInfo);

                ListItem item = new ListItem();
                // apk 아이콘
                item.setAppIcon(info.applicationInfo.loadIcon(pm));
                // apk 패키지 이름
                item.setAppPackageName(info.packageName);
                // apk 이름
                item.setAppTitle(name.toString());
                // apk 버전 (버전 네임과 버전 코드를 혼합합니다)
                item.setAppVersion(info.versionName + " (" + info.versionCode + ") ");
                // apk 절대 경로
                item.setAppPath(apkPath);

                if (isPackageInstalled(info.packageName)) {
                    if (isPackageAvailableUpdate(info.packageName, apkPath)) {
                        // 업데이트 가능할 시 업데이트 리스트로 넣습니다.
                        updateList.add(item);
                    } else {
                        // 이미 설치된 것일 경우 삭제 리스트로 넣습니다.
                        deleteList.add(item);
                    }
                } else {
                    // 설치가 안된 경우 설치 리스트로 넣습니다.
                    installList.add(item);
                }
            }

            if (updateList.size() != 0) {
                // 업데이트 리스트가 비어있지 않은 경우 카테고리와 업데이트 리스트애서 앱 리스트를 생성합니다.
                itemSet.add(generateCategoryItem(getString(R.string.update_list)));
                for (ListItem item : updateList) {
                    itemSet.add(generateListItem(item));
                }
                // 메모리 순환
                updateList.clear();
                updateList.trimToSize();
            }

            if (installList.size() != 0) {
                // 설치 리스트가 비어있지 않은 경우 카테고리와 설치 리스트애서 앱 리스트를 생성합니다.
                itemSet.add(generateCategoryItem(getString(R.string.install_list)));
                for (ListItem item : installList) {
                    itemSet.add(generateListItem(item));
                }
                installList.clear();
                installList.trimToSize();
            }

            if (deleteList.size() != 0) {
                // 삭제 리스트가 비어있지 않은 경우 카테고리와 삭제 리스트애서 앱 리스트를 생성합니다.
                itemSet.add(generateCategoryItem(getString(R.string.delete_list)));
                for (ListItem item : deleteList) {
                    itemSet.add(generateListItem(item));
                }
                deleteList.clear();
                deleteList.trimToSize();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadingDialog.dismiss();
            // AppViewAdpater 에 데이터들을 전달합니다.
            adapter = new AppViewAdapter(MainActivity.this, itemSet, pm, am);
            list.setAdapter(adapter);
        }
    }

    public class AppViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Activity c;
        ArrayList<RecyclerItem> dataSet;
        LayoutInflater inflater;
        PackageManager pm;
        ApplicationManager am;

        // 생성 인자 전송
        public AppViewAdapter(Activity a, ArrayList<RecyclerItem> itemSet, PackageManager pm, ApplicationManager am) {
            c = a;
            dataSet = itemSet;
            this.pm = pm;
            this.am = am;
        }

        @SuppressLint("InflateParams")
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v;
            RecyclerView.ViewHolder holder;
            // viewType 에 따라 표시할 객체를 설정합니다.
            switch (viewType) {
                default:
                case 0:
                    v = inflater.inflate(R.layout.row_applist, null);
                    holder = new AppViewHolder(v);
                    break;
                case 1:
                    v = inflater.inflate(R.layout.row_category, null);
                    holder = new CategoryViewHolder(v);
                    break;
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case 0:
                    getAppList(holder, position);
                    break;
                case 1:
                    getCategoryList(holder, position);
                    break;
            }
        }

        // 앱 리스트
        public void getAppList(RecyclerView.ViewHolder hold, int position) {
            AppViewHolder holder = (AppViewHolder) hold;
            final ListItem item = dataSet.get(position).getListItem();

            // 앱 아이콘 설정
            holder.appIcon.setImageDrawable(item.getAppIcon());
            // 앱 이름 설정
            holder.appTitle.setText(item.getAppTitle());
            // 앱 패키지 이름 설정
            holder.appPackageName.setText(item.getAppPackageName());
            // 앱 버전 설정
            holder.appVersion.setText(item.getAppVersion());

            final boolean isInstalled = isPackageInstalled(item.getAppPackageName());
            final boolean isUpdate = isPackageAvailableUpdate(item.getAppPackageName(), item.getAppPath());

            // 설치 / 제거 / 업데이트 문구 설정
            holder.button.setText((isInstalled) ? (isUpdate) ? R.string.update : R.string.delete : R.string.install);

            // 상태에 따라 설치 / 제거 / 업데이트 진행
            holder.button.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("TryWithIdenticalCatches")
                @Override
                public void onClick(View v) {
                    if (isInstalled) {
                        if (isUpdate) {
                            try {
                                installDialog.show();
                                am.installPackage(item.getAppPath());
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                            intent.setData(Uri.parse("package:" + item.getAppPackageName()));
                            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                            c.startActivityForResult(intent, 72);
                        }
                    } else {
                        try {
                            installDialog.show();
                            am.installPackage(item.getAppPath());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        // 카테고리 설정
        public void getCategoryList(RecyclerView.ViewHolder hold, int position) {
            CategoryViewHolder holder = (CategoryViewHolder) hold;
            CategoryItem item = dataSet.get(position).getCategoryItem();
            holder.categoryText.setText(item.getCategoryText());
        }

        // 전체 리스트의 수 리턴
        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        // 해당 위치에 뭐가 들어가야되는지 리턴해주는 메소드
        @Override
        public int getItemViewType(int position) {
            RecyclerItem holder = dataSet.get(position);
            return holder.getViewType();
        }

        private boolean isPackageInstalled(String packageName) {
            try {
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }

        private boolean isPackageAvailableUpdate(String packageName, String appPath) {
            try {
                PackageInfo installedInfo = pm.getPackageInfo(packageName, 0);
                PackageInfo folderInfo = pm.getPackageArchiveInfo(appPath, 0);
                return installedInfo.versionCode < folderInfo.versionCode;
            } catch (Exception e) {
                return false;
            }
        }
    }
}