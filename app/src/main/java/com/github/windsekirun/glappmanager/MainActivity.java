package com.github.windsekirun.glappmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public String PATH = Environment.getExternalStorageDirectory() + "/apps/";
    // public String PATH = "/system/apps/priv-app";
    public ListAdapter adapter;
    public ArrayList<ListItem> itemSet;
    ListView list;
    android.support.v7.widget.Toolbar toolbar;
    PackageManager pm;

    int UNINSTALL_REQUEST_CODE = 72;
    int INSTALL_REQUEST_CODE = 78;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarColorUtils.setColor(getWindow());

        list = (ListView) findViewById(R.id.list);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(0xffffffff);

        new LoadAPKList().execute();
        pm = getPackageManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INSTALL_REQUEST_CODE)
            adapter.notifyDataSetChanged();
        if (requestCode == UNINSTALL_REQUEST_CODE)
            adapter.notifyDataSetChanged();
    }


    public class LoadAPKList extends AsyncTask<Void, Void, Void> {
        ArrayList<String> apkFileList = new ArrayList<>();
        MaterialDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new MaterialDialog.Builder(MainActivity.this)
                    .content("Loading...")
                    .cancelable(false)
                    .progress(true, 0)
                    .show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            itemSet = new ArrayList<>();
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
                itemSet.add(item);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            adapter = new ListAdapter(MainActivity.this, itemSet);
            list.setAdapter(adapter);
        }
    }

    public class ListAdapter extends ArrayAdapter<ListItem> {
        Context c;
        ArrayList<ListItem> itemSet;
        ViewHolder holder;
        LayoutInflater inflater;

        public ListAdapter(Context context, ArrayList<ListItem> o) {
            super(context, 0, o);
            c = context;
            itemSet = o;
            inflater = (LayoutInflater) c.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_applist, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ListItem item = itemSet.get(position);
            holder.appIcon.setImageDrawable(item.getAppIcon());
            holder.appTitle.setText(item.getAppTitle());
            holder.appPackageName.setText(item.getAppPackageName());
            holder.appVersion.setText(item.getAppVersion());

            final boolean isInstalled = isPackageInstalled(item.getAppPackageName());
            final boolean isUpdate = isPackageAvailableUpdate(item.getAppPackageName(), item.getAppPath());

            holder.button.setText((isInstalled) ? (isUpdate) ? R.string.update : R.string.delete: R.string.install);

            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isInstalled) {
                        if (isUpdate) {
                            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                            intent.setData(Uri.fromFile(new File(item.getAppPath())));
                            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(intent, INSTALL_REQUEST_CODE);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                            intent.setData(Uri.parse("package:" + item.getAppPackageName()));
                            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
                        }
                    } else {
                        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                        intent.setData(Uri.fromFile(new File(item.getAppPath())));
                        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, INSTALL_REQUEST_CODE);
                    }
                }
            });
            return convertView;
        }
    }

    public class ListItem {
        private Drawable appIcon;
        private String appTitle;
        private String appPackageName;
        private String appVersion;
        private String appPath;

        public String getAppPath() {
            return appPath;
        }

        public void setAppPath(String appPath) {
            this.appPath = appPath;
        }

        public Drawable getAppIcon() {
            return appIcon;
        }

        public void setAppIcon(Drawable appIcon) {
            this.appIcon = appIcon;
        }

        public String getAppPackageName() {
            return appPackageName;
        }

        public void setAppPackageName(String appPackageName) {
            this.appPackageName = appPackageName;
        }

        public String getAppTitle() {
            return appTitle;
        }

        public void setAppTitle(String appTitle) {
            this.appTitle = appTitle;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }
    }

    public class ViewHolder {
        public ImageView appIcon;
        public TextView appTitle;
        public TextView appPackageName;
        public TextView appVersion;
        public Button button;

        public ViewHolder(View itemView) {
            appIcon = (ImageView) itemView.findViewById(R.id.appIcon);
            appTitle = (TextView) itemView.findViewById(R.id.appTitle);
            appPackageName = (TextView) itemView.findViewById(R.id.appPackageName);
            appVersion = (TextView) itemView.findViewById(R.id.appVersion);
            button = (Button) itemView.findViewById(R.id.executeButton);
        }
    }
}
