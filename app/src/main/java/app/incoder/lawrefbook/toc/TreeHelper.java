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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import app.incoder.lawrefbook.R;

/**
 * TreeHelper
 *
 * @author : Jerry xu
 * @since : 2022/5/3 08:58
 */
public class TreeHelper {

    /**
     * 传入我们的普通bean，转化为我们排序后的Node
     *
     * @param data               data
     * @param defaultExpandLevel level
     * @return List
     * @throws IllegalArgumentException i
     * @throws IllegalAccessException   i
     */
    public static <T> List<Node> getSortedNodes(List<T> data, int defaultExpandLevel)
            throws IllegalArgumentException, IllegalAccessException {
        List<Node> result = new ArrayList<>(data.size());
        List<Node> nodes = convertData2Node(data);
        List<Node> rootNodes = getRootNodes(nodes);
        for (Node node : rootNodes) {
            addNode(result, node, defaultExpandLevel, 1);
        }
        return result;
    }

    /**
     * 过滤出所有可见的 Node
     *
     * @param nodes nodes
     * @return List
     */
    public static List<Node> filterVisibleNode(List<Node> nodes) {
        List<Node> result = new ArrayList<>(nodes.size());
        for (Node node : nodes) {
            // 如果为根节点，或者上层目录为展开状态
            if (node.isRoot() || node.isParentExpand()) {
                setNodeIcon(node);
                result.add(node);
            }
        }
        return result;
    }

    /**
     * 将我们的数据转化为树的节点
     *
     * @param data data
     * @return List
     * @throws IllegalAccessException   i
     * @throws IllegalArgumentException i
     */
    private static <T> List<Node> convertData2Node(List<T> data)
            throws IllegalArgumentException, IllegalAccessException {
        List<Node> nodes = new ArrayList<>(data.size());
        Node node;
        for (T t : data) {
            int id = -1;
            int pId = -1;
            String label = null;
            Class<?> clazz = t.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field f : declaredFields) {
                if (f.getAnnotation(TreeNodeId.class) != null) {
                    f.setAccessible(true);
                    id = f.getInt(t);
                }
                if (f.getAnnotation(TreeNodePid.class) != null) {
                    f.setAccessible(true);
                    pId = f.getInt(t);
                }
                if (f.getAnnotation(TreeNodeLabel.class) != null) {
                    f.setAccessible(true);
                    label = (String) f.get(t);
                }
                if (id != -1 && pId != -1 && label != null) {
                    break;
                }
            }
            node = new Node(id, pId, label);
            nodes.add(node);
        }
        // 先按照 pid 排序，再按照 id 排序
        nodes.sort(Comparator.comparingInt(Node::getPId).thenComparingInt(Node::getId));

        /*
         * 设置Node间，父子关系;让每两个节点都比较一次，即可设置其中的关系
         */
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                Node m = nodes.get(j);
                if (m.getPId() == n.getId()) {
                    n.getChildren().add(m);
                    m.setParent(n);
                } else if (m.getId() == n.getPId()) {
                    m.getChildren().add(n);
                    n.setParent(m);
                }
            }
        }

        for (Node n : nodes) {
            setNodeIcon(n);
        }
        return nodes;
    }

    private static List<Node> getRootNodes(List<Node> nodes) {
        List<Node> root = new ArrayList<>(nodes.size());
        for (Node node : nodes) {
            if (node.isRoot()) {
                root.add(node);
            }
        }
        return root;
    }

    /**
     * 把一个节点上的所有的内容都挂上去
     */
    private static void addNode(List<Node> nodes, Node node, int defaultExpandLevel, int currentLevel) {
        nodes.add(node);
        if (defaultExpandLevel >= currentLevel) {
            node.setExpand(true);
        }

        if (node.isLeaf()) {
            return;
        }
        for (int i = 0; i < node.getChildren().size(); i++) {
            addNode(nodes, node.getChildren().get(i), defaultExpandLevel, currentLevel + 1);
        }
    }

    /**
     * 设置节点的图标
     *
     * @param node node
     */
    private static void setNodeIcon(Node node) {
        if (node.getChildren().size() > 0 && node.isExpand()) {
            node.setIcon(R.drawable.ic_baseline_arrow_drop_down_24);
        } else if (node.getChildren().size() > 0 && !node.isExpand()) {
            node.setIcon(R.drawable.ic_baseline_arrow_right_24);
        } else {
            node.setIcon(-1);
        }
    }
}