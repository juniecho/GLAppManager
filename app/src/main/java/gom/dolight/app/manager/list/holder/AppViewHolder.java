package gom.dolight.app.manager.list.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import gom.dolight.app.manager.R;

public class AppViewHolder extends RecyclerView.ViewHolder {
    public ImageView appIcon;
    public TextView appTitle;
    public TextView appPackageName;
    public TextView appVersion;
    public Button button;

    public AppViewHolder(View itemView) {
        super(itemView);
        appIcon = (ImageView) itemView.findViewById(R.id.appIcon);
        appTitle = (TextView) itemView.findViewById(R.id.appTitle);
        appPackageName = (TextView) itemView.findViewById(R.id.appPackageName);
        appVersion = (TextView) itemView.findViewById(R.id.appVersion);
        button = (Button) itemView.findViewById(R.id.executeButton);
    }
}