package com.aiurt.modules.tree;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fgw
 */
@Data
public class TreeFilter implements Serializable, ITreeFilter<Integer> {
    private static final long serialVersionUID = 4808891276574194437L;

    public TreeFilter(Integer id , Integer parentId) {
        this.id = id;
        this.pid = parentId;
    }

    public TreeFilter() {
    }


    /**
     *
     */
    private Integer id;

    /**
     *
     */
    private Integer pid;

    /**
     *
     */
    private List<TreeFilter> children;


    /**
     * 获取主键
     *
     * @return
     */
    @Override
    public Integer getPrimaryKey() {
        return this.id;
    }

    /**
     * 获取父节点的id
     *
     * @return
     */
    @Override
    public Integer getParentKey() {
        return this.pid;
    }

    @Override
    public List<? extends ITreeFilter> getChildrenList() {
        return this.children;
    }


    public static void main(String[] args) {
        TreeFilter treeNode = initTreeNode();
        // 预设 命中的节点
        List<Integer> list = new ArrayList<>();

        list.add(7);

        ITreeFilter node = TreeNodeUtil.filterTree(treeNode, list);
        System.out.println(node);
    }

    /**
     * 初始化树状结构
     *                              1
     *                           /  |  \
     *                          2   3   4
     *                        / \   /\
     *                       5  6  7  8
     *                      /     /\
     *                    11     9 10
     *                   / \
     *                 12  13
     */
    private static TreeFilter initTreeNode() {
        TreeFilter root = new TreeFilter(1,-1);
        List<TreeFilter> rootChildren = new ArrayList<>();
        TreeFilter TreeNode2= new TreeFilter(2, 1);
        TreeFilter TreeNode3= new TreeFilter(3, 1);
        TreeFilter TreeNode4= new TreeFilter(4, 1);
        rootChildren.add(TreeNode2);
        rootChildren.add(TreeNode3);
        rootChildren.add(TreeNode4);
        root.setChildren(rootChildren);

        TreeFilter TreeNode5= new TreeFilter(5, 2);
        TreeFilter TreeNode11= new TreeFilter(11, 5);
        List<TreeFilter> TreeNode5Children = new ArrayList<>();
        TreeNode5Children.add(TreeNode11);
        TreeNode5.setChildren(TreeNode5Children);

        TreeFilter TreeNode12= new TreeFilter(12, 11);
        TreeFilter TreeNode13= new TreeFilter(13, 11);
        List<TreeFilter> TreeNode11Children = new ArrayList<>();
        TreeNode11Children.add(TreeNode12);
        TreeNode11Children.add(TreeNode13);
        TreeNode11.setChildren(TreeNode11Children);

        TreeFilter TreeNode6= new TreeFilter(6, 2);

        List<TreeFilter> TreeNode2Children = new ArrayList<>();
        TreeNode2Children.add(TreeNode5);
        TreeNode2Children.add(TreeNode6);
        TreeNode2.setChildren(TreeNode2Children);

        TreeFilter TreeNode7= new TreeFilter(7, 2);
        TreeFilter TreeNode8= new TreeFilter(8, 2);

        List<TreeFilter> TreeNode3Children = new ArrayList<>();
        TreeNode3Children.add(TreeNode7);
        TreeNode3Children.add(TreeNode8);
        TreeNode3.setChildren(TreeNode3Children);

        TreeFilter TreeNode9= new TreeFilter(9, 7);
        TreeFilter TreeNode10= new TreeFilter(10, 7);

        List<TreeFilter> TreeNode7Children = new ArrayList<>();
        TreeNode7Children.add(TreeNode9);
        TreeNode7Children.add(TreeNode10);
        TreeNode7.setChildren(TreeNode7Children);
        return root;
    }


}
