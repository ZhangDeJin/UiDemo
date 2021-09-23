package com.zdj.phone.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zdj.phone.R;
import com.zdj.phone.bean.ObjDial;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : dejinzhang
 *     time : 2021/09/13
 *     desc : 拨号盘适配器
 * </pre>
 */
public class DialPadAdapter extends BaseAdapter {
    private Context mContext;
    private List<ObjDial> mList;
    //通话界面
    public static final int SCREEN_UI = 1;
    //电话界面
    public static final int PHONE_UI = 2;
    /**
     * 现在有两套：分别是通话界面（值为1）和电话界面（值为2）
     */
    private int mLayoutStyle = SCREEN_UI;

    /**
     * 注意：集合不用从外面传过来，因为拨号盘所用到的数据集合是固定的，所以我们直接封装在adapter里面即可。
     * 这样子可以避免在有多个引用端的时候，由于造数据导致重复代码的出现。
     * @param context  上下文
     * @param layoutStyle  布局样式
     */
    public DialPadAdapter(Context context, int layoutStyle) {
        mContext = context;
        mLayoutStyle = layoutStyle;
        mList = new ArrayList<>();
        mList.add(new ObjDial("1", " "));
        mList.add(new ObjDial("2", "ABC"));
        mList.add(new ObjDial("3", "DEF"));
        mList.add(new ObjDial("4", "GHI"));
        mList.add(new ObjDial("5", "JKL"));
        mList.add(new ObjDial("6", "MNO"));
        mList.add(new ObjDial("7", "PQRS"));
        mList.add(new ObjDial("8", "TUV"));
        mList.add(new ObjDial("9", "WXYZ"));
        mList.add(new ObjDial("*", "(P)"));
        mList.add(new ObjDial("0", "+"));
        mList.add(new ObjDial("#", "(W)"));
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_dial, null);
            holder = new ViewHolder();
            holder.ll_parent = convertView.findViewById(R.id.ll_parent);
            holder.tv_main_text = convertView.findViewById(R.id.tv_main_text);
            holder.tv_secondary_text = convertView.findViewById(R.id.tv_secondary_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_main_text.setText(mList.get(position).getMainText());
        holder.tv_main_text.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(mList.get(position).getSecondaryText())) {
            holder.tv_secondary_text.setText(mList.get(position).getSecondaryText());
            holder.tv_secondary_text.setVisibility(View.VISIBLE);
        } else {
            holder.tv_secondary_text.setVisibility(View.GONE);
        }

        //tv_main_text、tv_secondary_text的颜色和大小，ll_parent的背景均可以通过后续扩展进行配置，目前无需要，就暂时使用默认的即可。
        if (mLayoutStyle == 2) {
            holder.tv_main_text.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        return convertView;
    }

    public List<ObjDial> getList() {
        return mList;
    }

    public class ViewHolder {
        LinearLayout ll_parent;
        TextView tv_main_text, tv_secondary_text;
    }
}
