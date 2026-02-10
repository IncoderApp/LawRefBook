package app.incoder.lawrefbook.storage.channel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.Data;

/**
 * Channel
 *
 * @author Jerry Xu
 * @since 2025/7/24 22:31
 */
@Data
@Entity(tableName = "channel")
public class Channel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "category_name")
    private String categoryName;
    @ColumnInfo(name = "sort_no")
    private Integer sortNo;
    @ColumnInfo(name = "is_show")
    private boolean isShow;
    @ColumnInfo(name = "create_time")
    private String createTime;
    @ColumnInfo(name = "update_time")
    private String updateTime;

}
