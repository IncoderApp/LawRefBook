package app.incoder.lawrefbook.widget.grid;

/**
 * ItemTouchCallback
 *
 * @author Jerry Xu
 * @since 2025/6/7 21:13
 */
public interface ItemTouchCallback {
    /**
     * 拖拽 item 时回调，用于交换数据位置
     *
     * @param fromPosition 起始位置
     * @param toPosition   目标位置
     * @return 是否处理成功
     */
    boolean onItemMove(int fromPosition, int toPosition);

    /**
     * 滑动删除 item 时回调
     *
     * @param position 要删除的位置
     */
    void onItemRemove(int position);
}
