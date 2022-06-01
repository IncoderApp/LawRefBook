package app.incoder.lawrefbook.ui.feed;

import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

import app.incoder.lawrefbook.model.Lawre;

/**
 * FeedDiffCallBack
 *
 * @author : Jerry xu
 * @since : 2022/5/19 00:42
 */
public class FeedDiffCallBack extends DiffUtil.Callback {

    private final Lawre mNewLaws;
    private final Lawre mOldLaws;

    public FeedDiffCallBack(Lawre newLaws, Lawre oldLaws) {
        this.mNewLaws = newLaws;
        this.mOldLaws = oldLaws;
    }

    @Override
    public int getOldListSize() {
        return mOldLaws.getLaws() != null ? mOldLaws.getLaws().size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewLaws.getLaws() != null ? mNewLaws.getLaws().size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldLaws.getLaws().get(oldItemPosition).getName().equals(mNewLaws.getLaws().get(newItemPosition).getName());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Lawre.LawsBean beanOld = mOldLaws.getLaws().get(oldItemPosition);
        Lawre.LawsBean beanNew = mNewLaws.getLaws().get(newItemPosition);
        if (!beanOld.getName().equals(beanNew.getName())) {
            // 如果有内容不同，就返回 false
            return false;
        }
        // 如果有内容不同，就返回 false
        return !Objects.equals(beanOld.getId(), beanNew.getId());
    }
}
