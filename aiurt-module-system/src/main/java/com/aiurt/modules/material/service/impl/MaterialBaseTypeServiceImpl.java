package com.aiurt.modules.material.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.mapper.MaterialBaseMapper;
import com.aiurt.modules.material.mapper.MaterialBaseTypeMapper;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.material.service.IMaterialBaseTypeService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class MaterialBaseTypeServiceImpl extends ServiceImpl<MaterialBaseTypeMapper, MaterialBaseType> implements IMaterialBaseTypeService {

    @Autowired
    private MaterialBaseTypeMapper materialBaseTypeMapper;

    @Override
    public List<MaterialBaseType> treeList(List<MaterialBaseType> materialBaseTypeList, String id) {
        if (id == null || "".equals(id)) {
            id = "0";
        }
        return getTreeRes(materialBaseTypeList, id);
    }

    @Override
    public String getCcStr(MaterialBaseType materialBaseType) {
        String res = "";
        String str = cCstr(materialBaseType, "");
        if( !"" .equals(str) ){
            if(str.contains(CommonConstant.SYSTEM_SPLIT_STR)){
                List<String> strings = Arrays.asList(str.split(CommonConstant.SYSTEM_SPLIT_STR));
                Collections.reverse(strings);
                for(String s : strings){
                    res += s + CommonConstant.SYSTEM_SPLIT_STR;
                }
                res = res.substring(0,res.length()-1);
            }else{
                res = str;
            }
        }
        return res;
    }

    List<MaterialBaseType> getTreeRes(List<MaterialBaseType> materialBaseTypeList,String pid){
        List<MaterialBaseType> childList = materialBaseTypeList.stream().filter(materialBaseType -> pid.equals(materialBaseType.getPid())).collect(Collectors.toList());
        if(childList != null && childList.size()>0){
            for (MaterialBaseType materialBaseType : childList) {
                materialBaseType.setMaterialBaseTypeList(getTreeRes(materialBaseTypeList,materialBaseType.getId().toString()));
            }
        }
        return childList;
    }
    String cCstr(MaterialBaseType materialBaseType, String str){
        MaterialBaseType materialBaseTyperes = new MaterialBaseType();
        if(CommonConstant.SYSTEM_SPLIT_PID.equals(materialBaseType.getPid())){
            str += materialBaseType.getBaseTypeCode();
        }else{
            str += materialBaseType.getBaseTypeCode() + "/";
            materialBaseTyperes = materialBaseTypeMapper.selectById(materialBaseType.getPid());
            str = cCstr(materialBaseTyperes, str);
        }
        return str;
    }
}
