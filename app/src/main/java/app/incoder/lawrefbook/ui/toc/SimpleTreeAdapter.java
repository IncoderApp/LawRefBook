package app.incoder.lawrefbook.ui.toc;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import app.incoder.lawrefbook.R;

/**
 * SimpleTreeAdapter
 * <a herf="https://blog.csdn.net/lmj623565791/article/details/40212367">Android 打造任意层级树形控件 考验你的数据结构和设计</a>
 *
 * @author : Jerry xu
 * @since : 2022/5/3 08:53
 */
public class SimpleTreeAdapter<T> extends TreeListViewAdapter<T> {

    public SimpleTreeAdapter(ListView mTree, Context context, List<T> data, int defaultExpandLevel)
            throws IllegalArgumentException, IllegalAccessException {
        super(mTree, context, data, defaultExpandLevel);
    }

    @Override
    public View getConvertView(Node node, int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_toc, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = convertView.findViewById(R.id.iv_label);
            viewHolder.label = convertView.findViewById(R.id.tv_label);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (node.getIcon() == -1) {
            viewHolder.icon.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.icon.setVisibility(View.VISIBLE);
            viewHolder.icon.setImageResource(node.getIcon());
        }
        viewHolder.label.setText(node.getName());
        return convertView;
    }

    private static final class ViewHolder {
        ImageView icon;
        TextView label;
    }

}
