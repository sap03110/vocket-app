package com.example.vocket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewAdapter extends BaseAdapter{

    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<ItemData> mItems = new ArrayList<>();

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public ItemData getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_voca, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        TextView id = (TextView) convertView.findViewById(R.id.voca_en);
        TextView name = (TextView) convertView.findViewById(R.id.voca_mean) ;

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        ItemData myItem = getItem(position);

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        id.setText(myItem.getId());
        name.setText(myItem.getName());

        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */
        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(String id, String name, String check) {

        ItemData mItem = new ItemData();

        /* MyItem에 아이템을 setting한다. */
        mItem.setId(id);
        mItem.setName(name);
        mItem.setCkeck(check);

        /* mItems에 MyItem을 추가한다. */
        mItems.add(mItem);

    }

    public void removeItem(int position) {
        mItems.remove(position);
    }
}

class ItemData {

    private String id;
    private String name;
    private String check;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCheck() {
        return check;
    }

    public void setCkeck(String check) {
        this.check = check;
    }

}