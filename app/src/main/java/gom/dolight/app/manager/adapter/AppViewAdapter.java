package gom.dolight.app.manager.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import gom.dolight.app.manager.R;
import gom.dolight.app.manager.list.CategoryItem;
import gom.dolight.app.manager.list.ListItem;
import gom.dolight.app.manager.list.RecyclerItem;
import gom.dolight.app.manager.list.holder.AppViewHolder;
import gom.dolight.app.manager.list.holder.CategoryViewHolder;
import gom.dolight.app.manager.utils.ApplicationManager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * GLAPPManager
 * Class: AppViewAdapter
 * Created by WindSekirun on 16. 4. 10..
 */
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
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        c.startActivity(intent);
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