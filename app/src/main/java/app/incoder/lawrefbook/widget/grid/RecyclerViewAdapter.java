package app.incoder.lawrefbook.widget.grid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.incoder.lawrefbook.R;

/**
 * RecyclerViewAdapter
 *
 * @author Jerry Xu
 * @since 2025/6/7 21:08
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchCallback {

    private List<Bean> mDataList;

    public RecyclerViewAdapter() {
        mDataList = new ArrayList<>();
    }

    public void setDataList(List<Bean> dataList) {
        this.mDataList.clear();
        if (dataList == null || dataList.isEmpty()) {
            notifyDataSetChanged();
            return;
        }
        this.mDataList.addAll(dataList);
        int max = Math.max(this.mDataList.size(), dataList.size());
        notifyItemRangeChanged(0, max);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Bean.TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_chips, parent, false);
            return new RecyclerViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chanel_title, parent, false);
        return new GroupRecyclerViewHolder(view);

//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//
//        if (viewType == Bean.TYPE_GROUP) {
//            View view = inflater.inflate(R.layout.item_group, parent, false);
//            return new GroupViewHolder(view);
//        } else {
//            View view = inflater.inflate(R.layout.item_normal, parent, false);
//            return new ItemViewHolder(view);
//        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).getItemType();
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder bindHolder, final int position) {
        if (bindHolder instanceof RecyclerViewHolder) {
            RecyclerViewHolder holder = (RecyclerViewHolder) bindHolder;
            holder.mTextView.setText(mDataList.get(position).getTitle());
//            holder.mBitmapView.setImageBitmap(mDataList.get(position).getBitmap());
//            holder.itemView.setBackgroundColor(mDataList.get(position).getIconResId());
        }
        if (bindHolder instanceof GroupRecyclerViewHolder) {
            GroupRecyclerViewHolder holder = (GroupRecyclerViewHolder) bindHolder;
            holder.mTextView.setText(mDataList.get(position).getTitle());
//            holder.itemView.setBackgroundColor(mDataList.get(position).getIconResId());
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        // 为了保证UI和数据一致，我们要修复数据，当然，本篇的Recycler列表是不可以滑动的，问题不明显
        mDataList.add(toPosition, mDataList.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemRemove(int position) {
        mDataList.remove(position); // 移除数据
        notifyItemRemoved(position);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
//        ImageView mBitmapView;
        TextView mTextView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.chip_name);
//            mBitmapView = itemView.findViewById(R.id.bitmap);
        }
    }

    public static class GroupRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public GroupRecyclerViewHolder(View itemView) {
            super(itemView);
//            mTextView = itemView.findViewById(R.id.textView);
        }
    }
}
