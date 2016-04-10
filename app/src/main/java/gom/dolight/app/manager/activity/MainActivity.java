package gom.dolight.app.manager.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.afollestad.materialdialogs.MaterialDialog;
import gom.dolight.app.manager.Constants;
import gom.dolight.app.manager.R;
import gom.dolight.app.manager.adapter.AppViewAdapter;
import gom.dolight.app.manager.interfaces.OnInstalledPackaged;
import gom.dolight.app.manager.list.CategoryItem;
import gom.dolight.app.manager.list.ListItem;
import gom.dolight.app.manager.list.RecyclerItem;
import gom.dolight.app.manager.utils.ApplicationManager;
import gom.dolight.app.manager.utils.RebootDelegator;
import gom.dolight.app.manager.utils.StatusBarColorUtils;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Constants {
    android.support.v7.widget.Toolbar toolbar;
    PackageManager pm;
    ApplicationManager am;
    RecyclerView list;

    AppViewAdapter adapter;
    ArrayList<RecyclerItem> itemSet;
    LinearLayoutManager linearLayoutManager;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarColorUtils.setColor(getWindow());

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbarSetting();

        pm = getPackageManager();
        try {
            am = new ApplicationManager(MainActivity.this);
            am.setOnInstalledPackaged(new OnInstalledPackaged() {
                public void packageInstalled(String packageName, int returnCode) {
                    if (returnCode == ApplicationManager.INSTALL_SUCCEEDED) {
                        RebootDelegator.reboot(MainActivity.this);
                    }
                }
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        list = (RecyclerView) findViewById(R.id.list);
        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        list.setLayoutManager(linearLayoutManager);

        new LoadAPKList().execute();
    }

    public void toolbarSetting() {
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(0xffffffff);
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
            return installedInfo.versionCode != folderInfo.versionCode;
        } catch (Exception e) {
            return false;
        }
    }

    public RecyclerItem generateCategoryItem(String text) {
        RecyclerItem item = new RecyclerItem();
        CategoryItem categoryItem = new CategoryItem();
        categoryItem.setCategoryText(text);
        item.setViewType(1);
        item.setCategoryItem(categoryItem);
        return item;
    }

    public RecyclerItem generateListItem(ListItem listItem) {
        RecyclerItem item = new RecyclerItem();
        item.setViewType(0);
        item.setListItem(listItem);
        return item;
    }

    public class LoadAPKList extends AsyncTask<Void, Void, Void> {
        ArrayList<String> apkFileList = new ArrayList<>();
        MaterialDialog dialog;

        ArrayList<ListItem> updateList;
        ArrayList<ListItem> installList;
        ArrayList<ListItem> deleteList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new MaterialDialog.Builder(MainActivity.this)
                    .content(R.string.loading)
                    .cancelable(false)
                    .progress(true, 0)
                    .show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            itemSet = new ArrayList<>();
            updateList = new ArrayList<>();
            installList = new ArrayList<>();
            deleteList = new ArrayList<>();

            File f = new File(PATH);
            File file[] = f.listFiles();

            for (File fe : file) {
                apkFileList.add(fe.getAbsolutePath());
            }

            for (String apkPath : apkFileList) {
                PackageInfo info = pm.getPackageArchiveInfo(apkPath, 0);
                info.applicationInfo.sourceDir = apkPath;
                info.applicationInfo.publicSourceDir = apkPath;
                CharSequence name = pm.getApplicationLabel(info.applicationInfo);

                ListItem item = new ListItem();
                item.setAppIcon(info.applicationInfo.loadIcon(pm));
                item.setAppPackageName(info.packageName);
                item.setAppTitle(name.toString());
                item.setAppVersion(info.versionName + " (" + info.versionCode + ") ");
                item.setAppPath(apkPath);

                if (isPackageInstalled(info.packageName)) {
                    if (isPackageAvailableUpdate(info.packageName, apkPath)) {
                        updateList.add(item);
                    } else {
                        deleteList.add(item);
                    }
                } else {
                    installList.add(item);
                }
            }

            if (updateList.size() != 0) {
                itemSet.add(generateCategoryItem(getString(R.string.update_list)));
                for (ListItem item : updateList) {
                    itemSet.add(generateListItem(item));
                }
            }

            if (installList.size() != 0) {
                itemSet.add(generateCategoryItem(getString(R.string.install_list)));
                for (ListItem item : installList) {
                    itemSet.add(generateListItem(item));
                }
            }

            if (deleteList.size() != 0) {
                itemSet.add(generateCategoryItem(getString(R.string.delete_list)));
                for (ListItem item : deleteList) {
                    itemSet.add(generateListItem(item));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            adapter = new AppViewAdapter(MainActivity.this, itemSet, pm, am);
            list.setAdapter(adapter);
        }
    }
}