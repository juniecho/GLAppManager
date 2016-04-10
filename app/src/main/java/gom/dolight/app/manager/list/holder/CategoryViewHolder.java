package gom.dolight.app.manager.list.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import gom.dolight.app.manager.R;

/**
 * GLAPPManager
 * Class: CategoryViewHolder
 * Created by WindSekirun on 16. 4. 10..
 */
public class CategoryViewHolder extends RecyclerView.ViewHolder {

    public TextView categoryText;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        categoryText = (TextView) itemView.findViewById(R.id.categoryText);
    }
}
