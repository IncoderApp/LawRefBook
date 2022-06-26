/*
 * Copyright (C) 2022 The Jerry xu Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.incoder.lawrefbook.toc;

import java.util.ArrayList;
import java.util.List;


/**
 * Node
 *
 * @author : Jerry xu
 * @since : 2022/5/3 08:54
 */
@lombok.NoArgsConstructor
@lombok.Data
public class Node {
    /**
     * id
     */
    private int id;
    /**
     * 根节点 pId 为 0
     */
    private int pId;
    /**
     * name
     */
    private String name;
    /**
     * position
     */
    private int position;
    /**
     * 当前的级别
     */
    private int level;
    /**
     * 是否展开
     */
    private boolean isExpand = false;
    /**
     * icon
     */
    private int icon;
    /**
     * 下一级的子 Node
     */
    private List<Node> children = new ArrayList<>();
    /**
     * 父Node
     */
    private Node parent;

    public Node(int id, int pId, String name, int position) {
        super();
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.position = position;
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
                node.setExpand(false);
            }
        }
    }

}