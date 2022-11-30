package com.aiurt.boot.materials.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsCategoryMapper;
import com.aiurt.boot.materials.service.IEmergencyMaterialsCategoryService;
import com.aiurt.boot.materials.entity.EmergencyMaterialsCategory;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: emergency_materials_category
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyMaterialsCategoryServiceImpl extends ServiceImpl<EmergencyMaterialsCategoryMapper, EmergencyMaterialsCategory> implements IEmergencyMaterialsCategoryService {

    @Autowired
    private EmergencyMaterialsCategoryMapper emergencyMaterialsCategoryMapper;


    /**
     * 构造树，不固定根节点
     *
     * @param list 全部数据
     * @return 构造好以后的树形
     */
    public static List<EmergencyMaterialsCategory> treeFirst(List<EmergencyMaterialsCategory> list) {
        //这里的Menu是我自己的实体类，参数只需要菜单id和父id即可，其他元素可任意增添
        Map<String, EmergencyMaterialsCategory> map = new HashMap<>(50);
        for (EmergencyMaterialsCategory treeNode : list) {
            map.put(treeNode.getId(), treeNode);
        }
        return addChildren(list, map);
    }


    /**
     * @param list
     * @param map
     * @return
     */
    private static List<EmergencyMaterialsCategory> addChildren(List<EmergencyMaterialsCategory> list, Map<String, EmergencyMaterialsCategory> map) {
        List<EmergencyMaterialsCategory> rootNodes = new ArrayList<>();
        for (EmergencyMaterialsCategory treeNode : list) {
            EmergencyMaterialsCategory parentHave = map.get(treeNode.getPid());
            if (ObjectUtil.isEmpty(parentHave)) {
                rootNodes.add(treeNode);
            } else {
                //当前位置显示实体类中的List元素定义的参数为null，出现空指针异常错误
                if (ObjectUtil.isEmpty(parentHave.getChildren())) {
                    parentHave.setChildren(new ArrayList<EmergencyMaterialsCategory>());
                    parentHave.getChildren().add(treeNode);
                } else {
                    parentHave.getChildren().add(treeNode);
                }
            }
        }
        return rootNodes;
    }

    @Override
    public List<EmergencyMaterialsCategory> selectTreeList() {
        LambdaQueryWrapper<EmergencyMaterialsCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmergencyMaterialsCategory::getDelFlag,0);
        List<EmergencyMaterialsCategory> emergencyMaterialsCategories = emergencyMaterialsCategoryMapper.selectList(queryWrapper);
        return treeFirst(emergencyMaterialsCategories);
    }
}
