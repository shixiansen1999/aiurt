package com.aiurt.modules.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.ImportExcelUtil;
import com.aiurt.modules.system.entity.SysRole;
import com.aiurt.modules.system.mapper.SysRoleMapper;
import com.aiurt.modules.system.mapper.SysUserMapper;
import com.aiurt.modules.system.service.ISysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.TemplateExportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {
    @Autowired
    SysRoleMapper sysRoleMapper;
    @Autowired
    SysUserMapper sysUserMapper;
    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @Override
    public Result importExcelCheckRoleCode(MultipartFile file, ImportParams params) throws Exception {
        List<SysRole> listSysRoles = ExcelImportUtil.importExcel(file.getInputStream(), SysRole.class, params);
        int totalCount = listSysRoles.size();
        List<String> errorStrs = new ArrayList<>();
        List<SysRole> list = new ArrayList<>();
        // 去掉 sql 中的重复数据
        Integer errorLines=0;
        Integer successLines=0;
        // 去除 listSysRoles 中重复的数据
        for (int i = 0; i < listSysRoles.size(); i++) {
            SysRole sysRole =listSysRoles.get(i);
            String roleCodeI =sysRole.getRoleCode();
            if (StrUtil.isEmpty(roleCodeI)){
                errorStrs.add("第 " + i + " 行的角色编码未输入，忽略导入");
                sysRole.setText("角色编码未输入，忽略导入");
                list.add(listSysRoles.get(i));
                continue;
            }
            List<SysRole> list1 = baseMapper.selectList(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode,roleCodeI));
            if (CollectionUtil.isNotEmpty(list1)){
                errorStrs.add("第 " + i + " 行的 roleCode 值：" + roleCodeI + " 已存在，忽略导入");
                sysRole.setText("角色编码重复,忽略导入");
                list.add(listSysRoles.get(i));
                continue;
            }
            if (StrUtil.isEmpty(sysRole.getRoleName())){
                errorStrs.add("第 " + i + " 行的角色名称未输入，忽略导入");
                sysRole.setText("角色名称未输入，忽略导入");
                list.add(listSysRoles.get(i));
                continue;
            }
            int save = baseMapper.insert(sysRole);
            if (save <= 0) {
                throw new Exception(CommonConstant.SQL_INDEX_UNIQ_SYS_ROLE_CODE);
            }
        }
        if (list.size() > 0) {
            //创建导入失败错误报告,进行模板导出
            Resource resource = new ClassPathResource("templates/sysRoleError.xlsx");
            InputStream resourceAsStream = resource.getInputStream();
            //2.获取临时文件
            File fileTemp = new File("templates/sysRoleError.xlsx");
            try {
                //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
                FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            String path = fileTemp.getAbsolutePath();
            TemplateExportParams exportParams = new TemplateExportParams(path);
            List<Map<String, Object>> mapList = new ArrayList<>();
            list.forEach(l -> {
                Map<String, Object> lm = new HashMap<String, Object>();
                lm.put("name", l.getRoleName());
                lm.put("code", l.getRoleCode());
                lm.put("description",l.getDescription());
                lm.put("text", l.getText());
                mapList.add(lm);
            });
            Map<String, Object> errorMap = new HashMap<String, Object>();
            errorMap.put("maplist", mapList);
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, errorMap);
            String fileName = "角色导入错误模板" + "_" + System.currentTimeMillis() + ".xlsx";
            FileOutputStream out = new FileOutputStream(upLoadPath + File.separator + fileName);
            String url = fileName;
            workbook.write(out);
            errorLines += errorStrs.size();
            successLines += (listSysRoles.size() - errorLines);
            return ImportExcelUtil.imporReturnRes(errorLines, successLines, errorStrs, url);
        }
        errorLines+=list.size();
        successLines+=(listSysRoles.size()-errorLines);
        return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorStrs,null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(String roleid) {
        //1.删除角色和用户关系
        sysRoleMapper.deleteRoleUserRelation(roleid);
        //2.删除角色和权限关系
        sysRoleMapper.deleteRolePermissionRelation(roleid);
        //3.删除角色
        this.removeById(roleid);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatchRole(String[] roleIds) {
        //1.删除角色和用户关系
        sysUserMapper.deleteBathRoleUserRelation(roleIds);
        //2.删除角色和权限关系
        sysUserMapper.deleteBathRolePermissionRelation(roleIds);
        //3.删除角色
        this.removeByIds(Arrays.asList(roleIds));
        return true;
    }
}
