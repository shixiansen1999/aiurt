package com.aiurt.modules.material.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.ImportExcelUtil;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.mapper.MaterialBaseMapper;
import com.aiurt.modules.material.mapper.MaterialBaseTypeMapper;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.material.service.IMaterialBaseTypeService;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
    @Autowired
    private ICsMajorService csMajorService;
    @Autowired
    private ICsSubsystemService csSubsystemService;

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

    @Override
    public Result importExcelMaterial(MultipartFile file, ImportParams params, String id) throws Exception {
        List<MaterialBaseType> listMaterial = ExcelImportUtil.importExcel(file.getInputStream(), MaterialBase.class, params);
        List<String> errorStrs = new ArrayList<>();
        // 去掉 sql 中的重复数据
        Integer errorLines=0;
        Integer successLines=0;
        for (int i = 0; i < listMaterial.size(); i++) {
            try {
                MaterialBaseType materialBase = listMaterial.get(i);
                String finalstr = "";
                materialBase.setDelFlag(0);
                materialBase.setPid(id);
                materialBase.setStatus("1");
                //专业
                String majorCodeName = materialBase.getMajorName()==null?"":materialBase.getMajorName();
                if("".equals(majorCodeName)){
                    errorStrs.add("第 " + i + " 行：专业名称为空，忽略导入。");
                    continue;
                }
                CsMajor csMajor = csMajorService.getOne(new QueryWrapper<CsMajor>().eq("major_name",majorCodeName).eq("del_flag",0));
                if(csMajor == null){
                    errorStrs.add("第 " + i + " 行：无法根据专业名称找到对应数据，忽略导入。");
                    continue;
                }else{
                    materialBase.setMajorCode(csMajor.getMajorCode());
                    //子系统
                    String systemCodeName = materialBase.getSystemName()==null?"":materialBase.getSystemName();
                    CsSubsystem csSubsystem = csSubsystemService.getOne(new QueryWrapper<CsSubsystem>().eq("major_code",csMajor.getMajorCode()).eq("system_name",systemCodeName).eq("del_flag",0));
                    if(!"".equals(systemCodeName) && csSubsystem == null){
                        errorStrs.add("第 " + i + " 行：无法根据子系统名称找到对应数据，忽略导入。");
                        continue;
                    }else{
                        if(csSubsystem != null){
                            materialBase.setSystemCode(csSubsystem.getSystemCode());
                        }
                        String typeCodeCc = this.getCcStr(materialBase);
                        materialBase.setTypeCodeCc(typeCodeCc);
                    }
                }
                if ("".equals(materialBase.getStatus())){
                    errorStrs.add("第 " + i + " 行：没输入分类状态，忽略导入。");
                    continue;
                }else {
                    if ("启用".equals(materialBase.getStatus())){
                        materialBase.setStatus("1");
                    }else if ("停用".equals(materialBase.getStatus())){
                        materialBase.setStatus("2");
                    }else {
                        errorStrs.add("第 " + i + " 行：分类状态输入错误只有启用和停用，忽略导入。");
                        continue;
                    }
                }
                LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                materialBase.setSysOrgCode(user.getOrgCode());
                int save = materialBaseTypeMapper.insert(materialBase);
                if(save<=0){
                    throw new Exception(CommonConstant.SQL_INDEX_UNIQ_MATERIAL_BASE_CODE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        errorLines+=errorStrs.size();
        successLines+=(listMaterial.size()-errorLines);
        return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorStrs);
    }

    List<MaterialBaseType> getTreeRes(List<MaterialBaseType> materialBaseTypeList,String pid){
        String status = "";
        List<MaterialBaseType> childList = materialBaseTypeList.stream().filter(materialBaseType -> pid.equals(materialBaseType.getPid())).collect(Collectors.toList());
        if(childList != null && childList.size()>0){
            for (MaterialBaseType materialBaseType : childList) {
                if(!CommonConstant.SYSTEM_SPLIT_PID.equals(pid)){
                    MaterialBaseType materialBaseTypeFather = materialBaseTypeList.stream().filter(m -> pid.equals(m.getId())).collect(Collectors.toList())==null?new MaterialBaseType():materialBaseTypeList.stream().filter(m -> pid.equals(m.getId())).collect(Collectors.toList()).get(0);
                    if(CommonConstant.MATERIAL_BASE_TYPE_STATUS_0.toString().equals(materialBaseTypeFather.getPStatus()) || CommonConstant.MATERIAL_BASE_TYPE_STATUS_0.toString().equals(materialBaseTypeFather.getStatus())){
                        status = CommonConstant.MATERIAL_BASE_TYPE_STATUS_0.toString();
                    }else{
                        status = CommonConstant.MATERIAL_BASE_TYPE_STATUS_1.toString();
                    }
                }else{
                    status = CommonConstant.MATERIAL_BASE_TYPE_STATUS_1.toString();
                }
                materialBaseType.setPStatus(status);
                materialBaseType.setMaterialBaseTypeList(getTreeRes(materialBaseTypeList,materialBaseType.getId()));
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
