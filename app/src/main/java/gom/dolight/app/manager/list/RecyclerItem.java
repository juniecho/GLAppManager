package gom.dolight.app.manager.list;

/**
 * GLAPPManager
 * Class: RecyclerItem
 * Created by WindSekirun on 16. 4. 10..
 */
public class RecyclerItem {
    private int viewType;
    private ListItem listItem;
    private CategoryItem categoryItem;

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public CategoryItem getCategoryItem() {
        return categoryItem;
    }

    public ListItem getListItem() {
        return listItem;
    }

    public void setCategoryItem(CategoryItem categoryItem) {
        this.categoryItem = categoryItem;
    }

    public void setListItem(ListItem listItem) {
        this.listItem = listItem;
    }
}
