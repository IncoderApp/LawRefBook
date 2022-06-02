/*
 * Copyright (C) 2022 The Jerry xu Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.incoder.lawrefbook.toc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * TreeListViewAdapter
 *
 * @author : Jerry xu
 * @since : 2022/5/3 08:56
 */
public abstract class TreeListViewAdapter<T> extends BaseAdapter {

    protected Context mContext;
    /**
     * 存储所有可见的Node
     */
    protected List<Node> mNodes;
    protected LayoutInflater mInflater;
    /**
     * 存储所有的Node
     */
    protected List<Node> mAllNodes;

    /**
     * 点击的回调接口
     */
    private OnTreeNodeClickListener onTreeNodeClickListener;

    public interface OnTreeNodeClickListener {
        void onClick(Node node, int position);
    }

    public void setOnTreeNodeClickListener(OnTreeNodeClickListener onTreeNodeClickListener) {
        this.onTreeNodeClickListener = onTreeNodeClickListener;
    }

    /**
     * @param mTree              listview
     * @param context            context
     * @param data               data
     * @param defaultExpandLevel 默认展开几级树
     * @throws IllegalArgumentException e
     * @throws IllegalAccessException   e
     */
    public TreeListViewAdapter(ListView mTree, Context context, List<T> data, int defaultExpandLevel)
            throws IllegalArgumentException, IllegalAccessException {
        mContext = context;
        /*
         * 对所有的Node进行排序
         */
        mAllNodes = TreeHelper.getSortedNodes(data, defaultExpandLevel);
        /*
         * 过滤出可见的Node
         */
        mNodes = TreeHelper.filterVisibleNode(mAllNodes);
        mInflater = LayoutInflater.from(context);
        /*
         * 设置节点点击时，可以展开以及关闭；并且将ItemClick事件继续往外公布
         */
        mTree.setOnItemClickListener((parent, view, position, id) -> {
            expandOrCollapse(position);
            if (onTreeNodeClickListener != null) {
                onTreeNodeClickListener.onClick(mNodes.get(position), position);
            }
        });

    }

    /**
     * 相应ListView的点击事件 展开或关闭某节点
     *
     * @param position position
     */
    public void expandOrCollapse(int position) {
        Node n = mNodes.get(position);
        if (n != null) {
            // 排除传入参数错误异常
            if (!n.isLeaf()) {
                n.setExpand(!n.isExpand());
                mNodes = TreeHelper.filterVisibleNode(mAllNodes);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public int getCount() {
        return mNodes.size();
    }

    @Override
    public Object getItem(int position) {
        return mNodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Node node = mNodes.get(position);
        convertView = getConvertView(node, position, convertView, parent);
        // 设置内边距
        convertView.setPadding(node.getLevel() * 40, convertView.getPaddingTop(), 0, convertView.getPaddingBottom());
        return convertView;
    }

    public abstract View getConvertView(Node node, int position, View convertView, ViewGroup parent);

}