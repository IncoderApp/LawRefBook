package app.incoder.lawrefbook.ui.toc;

import lombok.Data;

/**
 * FileBean
 *
 * @author : Jerry xu
 * @since : 2022/5/3 08:53
 */
@Data
public class FileBean {

    @TreeNodeId
    private int id;
    @TreeNodePid
    private int parentId;
    @TreeNodeLabel
    private String name;
    private long length;
    private String desc;

    public FileBean(int id, int parentId, String name) {
        super();
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

}
