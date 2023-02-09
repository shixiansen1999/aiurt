package com.aiurt.modules.material.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.ImportExcelUtil;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.material.dto.MaterialBaseTypeDTO;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.mapper.MaterialBaseTypeMapper;
import com.aiurt.modules.material.service.IMaterialBaseTypeService;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.TemplateExportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
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
    @Value("${jeecg.path.upload}")
    private String upLoadPath;

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
        List<MaterialBaseTypeDTO> listMaterial = ExcelImportUtil.importExcel(file.getInputStream(), MaterialBaseTypeDTO.class, params);
        List<String> errorStrs = new ArrayList<>();
        // 去掉 sql 中的重复数据
        Integer errorLines=0;
        Integer successLines=0;
        List<MaterialBaseType> list = new ArrayList<>();
        for (int i = 0; i < listMaterial.size(); i++) {
            try {
                MaterialBaseTypeDTO dto = listMaterial.get(i);
                MaterialBaseType materialBase = new MaterialBaseType();
                materialBase.setMajorName(dto.getMajorName());
                materialBase.setStatus(dto.getStatus());
                materialBase.setBaseTypeCode(dto.getBaseTypeCode());
                materialBase.setBaseTypeName(dto.getBaseTypeName());
                materialBase.setSystemName(dto.getSystemName());
                materialBase.setDelFlag(0);
                materialBase.setPid(id);
                MaterialBaseType materialBaseTyperes = materialBaseTypeMapper.selectById(materialBase.getPid());
                if (null == materialBaseTyperes){
                    materialBase.setPid("0");
                }

                //专业
                String majorCodeName = materialBase.getMajorName()==null?"":materialBase.getMajorName();
                if("".equals(majorCodeName)){
                    errorStrs.add("第 " + i + " 行：专业名称为空，忽略导入。");
                    materialBase.setText("专业名称为空，忽略导入");
                    list.add(materialBase);
                    continue;
                }
                CsMajor csMajor = csMajorService.getOne(new QueryWrapper<CsMajor>().eq("major_name",majorCodeName).eq("del_flag",0));
                if(csMajor == null){
                    errorStrs.add("第 " + i + " 行：无法根据专业名称找到对应数据，忽略导入。");
                    materialBase.setText("无法根据专业名称找到对应数据，忽略导入");
                    list.add(materialBase);
                    continue;
                }else{
                    materialBase.setMajorCode(csMajor.getMajorCode());
                    //子系统
                    String systemCodeName = materialBase.getSystemName()==null?"":materialBase.getSystemName();
                    CsSubsystem csSubsystem = csSubsystemService.getOne(new QueryWrapper<CsSubsystem>().eq("major_code",csMajor.getMajorCode()).eq("system_name",systemCodeName).eq("del_flag",0));
                    if(!"".equals(systemCodeName) && csSubsystem == null){
                        errorStrs.add("第 " + i + " 行：无法根据子系统名称找到对应数据，忽略导入。");
                        materialBase.setText("无法根据子系统名称找到对应数据，忽略导入");
                        list.add(materialBase);
                        continue;
                    }else{
                        if(csSubsystem != null){
                            materialBase.setSystemCode(csSubsystem.getSystemCode());
                        }
                        String typeCodeCc = this.getCcStr(materialBase);
                        materialBase.setTypeCodeCc(typeCodeCc);
                    }
                }
                if(StrUtil.isNotEmpty(materialBase.getBaseTypeCode())){
                   List<MaterialBaseType>  materialBaseType = this.list(new LambdaQueryWrapper<MaterialBaseType>()
                           .eq(MaterialBaseType::getBaseTypeCode,materialBase.getBaseTypeCode())
                           .eq(MaterialBaseType::getMajorCode,materialBase.getMajorCode())
                           .eq(MaterialBaseType::getDelFlag,0));
                   if (materialBaseType.size()>0){
                       errorStrs.add("第 " + i + " 行：分类编码相同，忽略导入。");
                       materialBase.setText("分类编码相同，忽略导入");
                       list.add(materialBase);
                       continue;
                   }
                }
                if(StrUtil.isNotEmpty(materialBase.getBaseTypeName())){
                    List<MaterialBaseType>  materialBaseType = this.list(new LambdaQueryWrapper<MaterialBaseType>()
                            .eq(MaterialBaseType::getBaseTypeName,materialBase.getBaseTypeName())
                            .eq(MaterialBaseType::getMajorCode,majorCodeName)
                            .eq(MaterialBaseType::getDelFlag,0));
                    if (materialBaseType.size()>0){
                        errorStrs.add("第 " + i + " 行：分类名称相同，忽略导入。");
                        materialBase.setText("分类名称相同，忽略导入");
                        list.add(materialBase);
                        continue;
                    }
                }
                if ("".equals(materialBase.getStatus())){
                    errorStrs.add("第 " + i + " 行：没输入分类状态，忽略导入。");
                    materialBase.setText("没输入分类状态，忽略导入");
                    list.add(materialBase);
                    continue;
                }else {
                    if ("启用".equals(materialBase.getStatus())){
                        materialBase.setStatus("1");
                    }else if ("停用".equals(materialBase.getStatus())){
                        materialBase.setStatus("2");
                    }else {
                        errorStrs.add("第 " + i + " 行：分类状态输入错误只有启用和停用，忽略导入。");
                        materialBase.setText("分类状态输入错误只有启用和停用，忽略导入");
                        list.add(materialBase);
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
        if (list.size()>0){
            //创建导入失败错误报告,进行模板导出
            Resource resource = new ClassPathResource("/templates/materialBaseTypeError.xlsx");
            InputStream resourceAsStream = resource.getInputStream();
            //2.获取临时文件
            File fileTemp= new File("/templates/materialBaseTypeError.xlsx");
            try {
                //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
                FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            String path = fileTemp.getAbsolutePath();
            TemplateExportParams exportParams = new TemplateExportParams(path);
            List<Map<String, Object>> mapList = new ArrayList<>();
            list.forEach(l->{
                Map<String, Object> lm = new HashMap<String, Object>();
                lm.put("majorName",l.getMajorName());
                lm.put("status",l.getStatus());
                lm.put("baseTypeCode",l.getBaseTypeCode());
                lm.put("baseTypeName",l.getBaseTypeName());
                lm.put("systemName",l.getSystemName());
                lm.put("text",l.getText());
                mapList.add(lm);
            });
            Map<String, Object> errorMap = new HashMap<String, Object>();
            errorMap.put("maplist", mapList);
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams,errorMap);
            String fileName = "物资分类错误模板"+"_" + System.currentTimeMillis()+".xlsx";
            FileOutputStream out = new FileOutputStream(upLoadPath+ File.separator+fileName);
            String  url = fileName;
            workbook.write(out);
            errorLines+=errorStrs.size();
            successLines+=(listMaterial.size()-errorLines);
            return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorStrs,url);
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
                materialBaseType.setTitle(materialBaseType.getBaseTypeName());
                materialBaseType.setValue(materialBaseType.getBaseTypeCode());
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
