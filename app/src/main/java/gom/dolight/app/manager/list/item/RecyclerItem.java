package gom.dolight.app.manager.list.item;

/**
 * GLAPPManager
 * Class: RecyclerItem
 * Created by WindSekirun on 16. 4. 10..
 */
// 리스트에 복합 객체를 표시하기 위해 viewType를 담은 객체입니다
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
