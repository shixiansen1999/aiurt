package com.aiurt.modules.robot.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.robot.dto.AreaPointDTO;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wgp
 * @Title:
 * @Description: 巡检区域点位工具类
 * @date 2022/9/2710:38
 */
public class AreaPointTreeUtils {

    /**
     * 构造树，不固定根节点
     *
     * @param list 全部数据
     * @return 构造好以后的树形
     */
    public static List<AreaPointDTO> treeFirst(List<AreaPointDTO> list) {
        Map<String, AreaPointDTO> map = new HashMap<>(128);
        for (AreaPointDTO treeNode : list) {
            map.put(treeNode.getId(), treeNode);
        }
        return addChildren(list, map);
    }

    /**
     * 递归子节点
     *
     * @param list
     * @param map
     * @return
     */
    private static List<AreaPointDTO> addChildren(List<AreaPointDTO> list, Map<String, AreaPointDTO> map) {
        List<AreaPointDTO> rootNodes = new ArrayList<>();
        for (AreaPointDTO treeNode : list) {
            AreaPointDTO parentHave = map.get(treeNode.getPid());
            if (ObjectUtil.isEmpty(parentHave)) {
                rootNodes.add(treeNode);
            } else {
                // 当前位置显示实体类中的List元素定义的参数为null，出现空指针异常错误
                if (ObjectUtil.isEmpty(parentHave.getChildren())) {
                    parentHave.setChildren(new ArrayList<>());
                    parentHave.getChildren().add(treeNode);
                } else {
                    parentHave.getChildren().add(treeNode);
                }
            }
        }
        return rootNodes;
    }


    /**
     * 查询节点的全部父节点
     *
     * @param nodeId   查询节点
     * @param treeList 全部节点数据
     * @param result   结果（该查询节点对应的全部父节点）
     * @return
     */
    public static Set<AreaPointDTO> selectNode(String nodeId, List<AreaPointDTO> treeList, Set<AreaPointDTO> result) {
        if (CollUtil.isEmpty(result)) {
            result = CollUtil.newHashSet();
        }
        // 树为空，或者nodeId为空，直接返回
        if (nodeId == null || CollUtil.isEmpty(treeList)) {
            return result;
        }

        // 查询是否有匹配的节点
        List<AreaPointDTO> nodeList = treeList.stream().filter(node -> nodeId.equals(node.getId())).collect(Collectors.toList());

        // 没有则直接返回
        if (CollUtil.isEmpty(nodeList)) {
            return result;
        }

        // 有则加入
        result.addAll(nodeList);

        // 继续递归
        return selectNode(nodeList.get(0).getPid(), treeList, result);
    }

    public static void main(String[] args) {
        List<AreaPointDTO> treeList = new ArrayList<>();
        AreaPointDTO a1 = new AreaPointDTO();
        a1.setId("2");
        a1.setPid("1");
        AreaPointDTO a2 = new AreaPointDTO();
        a2.setId("3");
        a2.setPid("2");
        AreaPointDTO a3 = new AreaPointDTO();
        a3.setId("4");
        a3.setPid("3");
        AreaPointDTO a4 = new AreaPointDTO();
        a4.setId("5");
        a4.setPid("1");
        treeList.add(a1);
        treeList.add(a2);
        treeList.add(a3);
        treeList.add(a4);
        Set<AreaPointDTO> areaPointDTOS = selectNode("3", treeList, CollUtil.newHashSet());

        System.out.println(areaPointDTOS);
    }

    /**
     * 判断IP地址的合法性，这里采用了正则表达式的方法来判断
     * return true，合法
     * */
    public static boolean ipCheck(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."+
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (text.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        return false;
    }
}
