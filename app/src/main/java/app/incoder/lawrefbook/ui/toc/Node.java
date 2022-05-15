package app.incoder.lawrefbook.ui.toc;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Node
 *
 * @author : Jerry xu
 * @since : 2022/5/3 08:54
 */
@Data
@AllArgsConstructor
public class Node {

    private int id;

    /**
     * 根节点pId为0
     */
    private int pId;

    private String name;

    /**
     * 当前的级别
     */
    private int level;

    /**
     * 是否展开
     */
    private boolean isExpand = false;

    private int icon;

    /**
     * 下一级的子Node
     */
    private List<Node> children = new ArrayList<>();

    /**
     * 父Node
     */
    private Node parent;

    public Node(int id, int pId, String name) {
        super();
        this.id = id;
        this.pId = pId;
        this.name = name;
    }

    /**
     * 是否为跟节点
     *
     * @return boolean
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * 判断父节点是否展开
     *
     * @return boolean
     */
    public boolean isParentExpand() {
        if (parent == null) {
            return false;
        }
        return parent.isExpand();
    }

    /**
     * 是否是叶子节点
     *
     * @return boolean
     */
    public boolean isLeaf() {
        return children.size() == 0;
    }

    /**
     * 获取 level
     */
    public int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    /**
     * 设置展开
     *
     * @param isExpand isExpand
     */
    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
        if (!isExpand) {
            for (Node node : children) {
                node.setExpand(isExpand);
            }
        }
    }

}