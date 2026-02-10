package app.incoder.lawrefbook.storage.channel;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * ChanelDao
 *
 * @author Jerry Xu
 * @since 2025/7/24 22:39
 */
@Dao
public interface ChannelDao {

    /**
     * insertChannel
     *
     * @param channels channels
     */
    @Insert
    void insertChannels(Channel... channels);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Channel> channels);

    /**
     * updateChannel
     *
     * @param channels channels
     */
    @Update
    void updateChannels(Channel... channels);

    /**
     * 根据名称更新 Channel 信息
     *
     * @param sortNo     排序号
     * @param isShow     是否展示
     * @param updateTime 更新时间
     * @return 更新的行数
     */
    @Query("UPDATE channel SET sort_no = :sortNo, is_show = :isShow, update_time= :updateTime WHERE category_name = :categoryName")
    int updateByName(String categoryName, Integer sortNo, boolean isShow, String updateTime);

    /**
     * deleteChannel
     *
     * @param channels channels
     */
    @Delete
    void deleteChannels(Channel... channels);

    /**
     * deleteAllChannel
     */
    @Query("DELETE FROM channel")
    void deleteAllChannels();

    /**
     * deleteAllChannel
     */
    @Query("SELECT * FROM channel")
    LiveData<List<Channel>> queryAll();

    /**
     * deleteAllChannel
     */
    @Query("SELECT * FROM channel WHERE is_show = :isShow")
    LiveData<List<Channel>> getShowChannels(boolean isShow);
}
