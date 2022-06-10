package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.common.enums.MaterialTypeEnum;
import com.aiurt.boot.common.enums.ProductiveTypeEnum;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.modules.device.entity.DeviceSmallType;
import com.aiurt.boot.modules.device.entity.DeviceType;
import com.aiurt.boot.modules.device.service.IDeviceSmallTypeService;
import com.aiurt.boot.modules.device.service.IDeviceTypeService;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.MaterialBase;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.MaterialBaseInput;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.MaterialBaseResult;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.MaterialBaseMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IMaterialBaseService;
import com.aiurt.boot.modules.secondLevelWarehouse.vo.MaterialBaseParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description: 物资基础信息
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Service
public class MaterialBaseServiceImpl extends ServiceImpl<MaterialBaseMapper, MaterialBase> implements IMaterialBaseService {

    @Resource @Lazy
    private IMaterialBaseService materialBaseService;

    @Resource
    private MaterialBaseMapper materialBaseMapper;

    @Resource
    private ISubsystemService subsystemService;

    @Resource
    private IDeviceTypeService deviceTypeService;

    @Resource
    private IDeviceSmallTypeService deviceSmallTypeService;


    @Override
    public Integer getTypeByMaterialCode(String code) {
        MaterialBase materialBase = materialBaseService.
                getOne(new QueryWrapper<MaterialBase>().eq(MaterialBase.CODE, code), false);
        if(ObjectUtil.isNotEmpty(materialBase)){
            return materialBase.getType();
        }else{
            return null;
        }
    }

    /**
     * 分页列表查询
     * @param page
     * @param param
     * @return
     */
    @Override
    public IPage<MaterialBaseResult> pageList(IPage<MaterialBaseResult> page, MaterialBaseParam param) {
        IPage<MaterialBaseResult> materialBaseResultIPage = materialBaseMapper.queryMaterialBase(page, param);
        for (int i = 0; i < materialBaseResultIPage.getRecords().size(); i++) {
            //添加类型
            materialBaseResultIPage.getRecords().get(i).setTypeName(MaterialTypeEnum.getNameByCode(materialBaseResultIPage.getRecords().get(i).getType()));
        }
        return materialBaseResultIPage;
    }

    /**
     * excel导入
     * @param request
     * @param response
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            //这里要注意一下
            params.setTitleRows(0);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                //读取Excel中的数据进行转换
                List<MaterialBaseInput> list = ExcelImportUtil.importExcel(file.getInputStream(), MaterialBaseInput.class, params);
                if(CollUtil.isEmpty(list)){
                    return Result.error("Excel转换异常");
                }

                //物资基础批量新增
                for (MaterialBaseInput input : list) {
                    if (StringUtils.isNotBlank(input.getBigTypeName())) {
                        DeviceType one = deviceTypeService.getOne(new QueryWrapper<DeviceType>().eq(DeviceType.NAME, input.getBigTypeName()), false);
                        if (ObjectUtil.isNotEmpty(one)) {
                            input.setTypeCode(one.getCode());
                        }else {
                            throw new SwscException("输入物资大类不规范");
                        }

                        if (StringUtils.isNotBlank(input.getSmallTypeName())) {
                            DeviceSmallType smallType = deviceSmallTypeService.lambdaQuery().eq(DeviceSmallType::getDeviceTypeId,one.getId()).eq(DeviceSmallType::getName,input.getSmallTypeName()).last("limit 1").one();
                            if (ObjectUtil.isNotEmpty(smallType)) {
                                input.setSmallTypeCode(smallType.getCode());
                            }else {
                                throw new SwscException("输入物资小类不规范");
                            }
                        }else {
                            throw new SwscException("物资小类不能为空");
                        }
                    }else {
                        throw new SwscException("物资大类不能为空");
                    }


                    if (StringUtils.isNotBlank(input.getSystem())) {
                        Subsystem one = subsystemService.getOne(new QueryWrapper<Subsystem>().eq(Subsystem.SYSTEM_NAME, input.getSystem()), false);
                        if (ObjectUtil.isNotEmpty(one)){
                            input.setSystemCode(one.getSystemCode());
                        }else {
                            throw new SwscException("输入系统名称不规范");
                        }
                    }else {
                        throw new SwscException("系统名称不能为空");
                    }
                    if (StringUtils.isNotBlank(input.getTypeName())) {
                        if (ProductiveTypeEnum.SCLX.getMessage().equals(input.getTypeName())){
                            input.setType(ProductiveTypeEnum.SCLX.getCode());
                        }else if (ProductiveTypeEnum.FSCLX.getMessage().equals(input.getTypeName())) {
                            input.setType(ProductiveTypeEnum.FSCLX.getCode());
                        }else {
                            throw new SwscException("输入物资类型不规范");
                        }
                    }else {
                        throw new SwscException("物资类型不能为空");
                    }
                    if (StringUtils.isBlank(input.getCode())) {
                        throw new SwscException("物资编号不能为空");
                    }
                    if (StringUtils.isBlank(input.getUnit())) {
                        throw new SwscException("单位不能为空");
                    }
                    List<MaterialBase> code = materialBaseMapper.selectList(new QueryWrapper<MaterialBase>().eq(MaterialBase.CODE, input.getCode()));
                    if (CollUtil.isNotEmpty(code)) {
                        throw new SwscException("操作失败,该物资编号已存在，请重新输入");
                    }
                    MaterialBase materialBase = new MaterialBase();
                    materialBase.setCreateBy(user.getId());
                    BeanUtils.copyProperties(input,materialBase);
                    materialBaseService.save(materialBase);
                }
                return Result.ok("文件导入成功！数据行数:" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                throw new SwscException("文件导入失败:"+e);
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入成功！");
    }
}
