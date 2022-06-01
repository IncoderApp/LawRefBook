package app.incoder.lawrefbook.ui.content;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

/**
 * ContentDiffCallBack
 *
 * @author : Jerry xu
 * @since : 2022/5/19 23:31
 */
public class ContentDiffCallBack extends DiffUtil.Callback {

    private final List<String> mNewContent;
    private final List<String> mOldContent;

    public ContentDiffCallBack(List<String> newContent, List<String> oldContent) {
        this.mNewContent = newContent;
        this.mOldContent = oldContent;
    }

    @Override
    public int getOldListSize() {
        return mOldContent != null ? mOldContent.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewContent != null ? mNewContent.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return !mOldContent.get(oldItemPosition).equals(mNewContent.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        String oldContent = mOldContent.get(oldItemPosition);
        String newContent = mNewContent.get(newItemPosition);
        return oldContent.equals(newContent);
    }
}
