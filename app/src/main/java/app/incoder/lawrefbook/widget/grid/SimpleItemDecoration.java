package app.incoder.lawrefbook.widget.grid;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * SimpleItemDecoration
 *
 * @author Jerry Xu
 * @since 2025/6/8 21:29
 */
public class SimpleItemDecoration extends RecyclerView.ItemDecoration {

    public int delta;

    public SimpleItemDecoration(int padding) {
        delta = padding;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        RecyclerView.Adapter adapter = parent.getAdapter();
        int viewType = adapter.getItemViewType(position);
        if (viewType == Bean.TYPE_GROUP) {
            return;
        }
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        //列数量
        int cols = layoutManager.getSpanCount();
        //position转为在第几列
        int current = layoutManager.getSpanSizeLookup().getSpanIndex(position, cols);
        //可有可无
        int currentCol = current % cols;

        int bottomPadding = delta / 2;
        if (currentCol == 0) {
            // 第0列左侧贴边
            outRect.left = 0;
            outRect.right = delta / 4;
            outRect.bottom = bottomPadding;
        } else if (currentCol == cols - 1) {
            outRect.left = delta / 4;
            outRect.right = 0;
            outRect.bottom = bottomPadding;
            //最后一列右侧贴边
        } else {
            outRect.left = delta / 4;
            outRect.right = delta / 4;
            outRect.bottom = bottomPadding;
        }
    }
}
