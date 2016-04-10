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

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarColorUtils.setColor(getWindow());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        layoutManager = new LinearLayoutManager(MainActivity.this);
        list.setLayoutManager(layoutManager);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 72) {
            if (resultCode == RESULT_OK) {
                RebootDelegator.reboot(MainActivity.this);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("TAG", "onActivityResult: user canceled the (un)install");
            } else if (resultCode == RESULT_FIRST_USER) {
                Log.d("TAG", "onActivityResult: failed to (un)install");
            }
        }
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
                updateList.clear();
                updateList.trimToSize();
            }

            if (installList.size() != 0) {
                itemSet.add(generateCategoryItem(getString(R.string.install_list)));
                for (ListItem item : installList) {
                    itemSet.add(generateListItem(item));
                }
                installList.clear();
                installList.trimToSize();
            }

            if (deleteList.size() != 0) {
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
            dialog.dismiss();

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

        public void getAppList(RecyclerView.ViewHolder hold, int position) {
            AppViewHolder holder = (AppViewHolder) hold;
            final ListItem item = dataSet.get(position).getListItem();

            holder.appIcon.setImageDrawable(item.getAppIcon());
            holder.appTitle.setText(item.getAppTitle());
            holder.appPackageName.setText(item.getAppPackageName());
            holder.appVersion.setText(item.getAppVersion());

            final boolean isInstalled = isPackageInstalled(item.getAppPackageName());
            final boolean isUpdate = isPackageAvailableUpdate(item.getAppPackageName(), item.getAppPath());

            holder.button.setText((isInstalled) ? (isUpdate) ? R.string.update : R.string.delete : R.string.install);

            holder.button.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("TryWithIdenticalCatches")
                @Override
                public void onClick(View v) {
                    if (isInstalled) {
                        if (isUpdate) {
                            try {
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

        public void getCategoryList(RecyclerView.ViewHolder hold, int position) {
            CategoryViewHolder holder = (CategoryViewHolder) hold;
            CategoryItem item = dataSet.get(position).getCategoryItem();
            holder.categoryText.setText(item.getCategoryText());
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

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
                return installedInfo.versionCode != folderInfo.versionCode;
            } catch (Exception e) {
                return false;
            }
        }
    }
}