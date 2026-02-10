package app.incoder.lawrefbook.storage.channel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import app.incoder.lawrefbook.storage.AppDatabase;
import app.incoder.lawrefbook.util.ConstantManager;

/**
 * ChanelRepository
 *
 * @author Jerry Xu
 * @since 2025/7/24 22:39
 */
public class ChannelRepository {

    private final LiveData<List<Channel>> mChannelLive;
    private final ChannelDao mDao;
    private final AppDatabase database;

    ChannelRepository(Context context) {
        database = AppDatabase.getInstance(context.getApplicationContext());
        mDao = database.getChannelDAO();
        mChannelLive = mDao.queryAll();
    }

    public LiveData<List<Channel>> getAllChannelsLive() {
        return mChannelLive;
    }

    public LiveData<List<Channel>> getShowChannels(boolean isShow) {
        return mDao.getShowChannels(isShow);
    }

    void insertChannels(Channel... channels) {
        ConstantManager.POOL_EXECUTOR.submit(() -> mDao.insertChannels(channels));
    }

    void insertAll(List<Channel> channels) {
        ConstantManager.POOL_EXECUTOR.submit(() -> mDao.insertAll(channels));
    }

    void updateChannels(Channel... channels) {
        ConstantManager.POOL_EXECUTOR.submit(() -> mDao.updateChannels(channels));
    }

    public interface UpdateCallback {
        void onSuccess();

        void onError(Exception e);
    }

    /**
     * 根据名称批量更新 Channel（事务方式）
     *
     * @param channels 要更新的 Channel 列表
     * @param callback 更新结果回调
     */
    public void transactionUpdateByName(List<Channel> channels, UpdateCallback callback) {
        ConstantManager.POOL_EXECUTOR.submit(() -> {
            // 使用 runInTransaction 替代手动事务
            database.runInTransaction(() -> {
                try {
                    for (Channel channel : channels) {
                        // 执行单条更新
                        int rowsUpdated = mDao.updateByName(channel.getCategoryName(), channel.getSortNo(), channel.isShow(), channel.getUpdateTime());
                        // 检查是否成功更新（可选）
                        if (rowsUpdated == 0) {
                            Log.w("Update", "未找到 categoryName 为 " + channel.getCategoryName() + " 的 chanel");
                        }
                    }
                    callback.onSuccess();
                } catch (Exception e) {
                    callback.onError(e);
                    throw e;
                }
            });
        });
    }

    void deleteChannels(Channel... channels) {
        ConstantManager.POOL_EXECUTOR.submit(() -> mDao.deleteChannels(channels));
    }

    void deleteAllChannels() {
        ConstantManager.POOL_EXECUTOR.submit(mDao::deleteAllChannels);
    }
}
