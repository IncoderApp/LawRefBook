package app.incoder.lawrefbook.storage.channel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * ChanelViewModel
 *
 * @author Jerry Xu
 * @since 2025/7/24 22:39
 */
public class ChannelViewModel extends AndroidViewModel {

    private final ChannelRepository mCategoryChannelRepository;

    public ChannelViewModel(@NonNull Application application) {
        super(application);
        mCategoryChannelRepository = new ChannelRepository(getApplication());
    }

    public LiveData<List<Channel>> getAllChannelsLive() {
        return mCategoryChannelRepository.getAllChannelsLive();
    }

    public LiveData<List<Channel>> getShowChannels(boolean isShow, String category) {
        return mCategoryChannelRepository.getShowChannels(isShow);
    }

    public void insertAction(Channel... channels) {
        mCategoryChannelRepository.insertChannels(channels);
    }

    public void insertAll(List<Channel> channels) {
        mCategoryChannelRepository.insertAll(channels);
    }

    public void updateChannels(Channel... channels) {
        mCategoryChannelRepository.updateChannels(channels);
    }

    public void updateAll(List<Channel> channels, ChannelRepository.UpdateCallback callback) {
        mCategoryChannelRepository.transactionUpdateByName(channels, callback);
    }

    public void delete(Channel... channels) {
        mCategoryChannelRepository.deleteChannels(channels);
    }

    public void deleteAllChannels() {
        mCategoryChannelRepository.deleteAllChannels();
    }

}
