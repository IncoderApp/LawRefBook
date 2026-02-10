package app.incoder.lawrefbook.widget.grid;

import lombok.Data;

/**
 * Bean
 *
 * @author Jerry Xu
 * @since 2025/6/7 21:20
 */
@Data
public class Bean {

    public static final int TYPE_GROUP = 0;
    public static final int TYPE_ITEM = 1;

    /*** 当前项的类型 */
    private Integer itemType;
    /*** 是否显示删除图标(TYPE_ITEM 类别) */
    private boolean showClose;
    /*** 标题或其他内容 */
    private String title;

}
