package com.aiurt.modules.sparepart.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;

import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.aiurt.modules.sparepart.entity.vo.SparePartStockInfoImportExcelVO;
import com.aiurt.modules.sparepart.mapper.SparePartStockInfoMapper;
import com.aiurt.modules.sparepart.service.ISparePartStockInfoService;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.service.ISysDepartService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: spare_part_stock_info
 * @Author: aiurt
 * @Date:   2022-07-20
 * @Version: V1.0
 */
@Slf4j
@Service
public class SparePartStockInfoServiceImpl extends ServiceImpl<SparePartStockInfoMapper, SparePartStockInfo> implements ISparePartStockInfoService {
    @Autowired
    private SparePartStockInfoMapper sparePartStockInfoMapper;
    @Autowired
    private ISysDepartService iSysDepartService;

    @Autowired
    private ISysBaseAPI sysBaseApi;
    /**
     * 添加
     *
     * @param sparePartStockInfo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(SparePartStockInfo sparePartStockInfo) {
        //判断编码是否重复
        LambdaQueryWrapper<SparePartStockInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SparePartStockInfo::getWarehouseCode, sparePartStockInfo.getWarehouseCode());
        queryWrapper.eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SparePartStockInfo> list = sparePartStockInfoMapper.selectList(queryWrapper);
        if (!list.isEmpty()) {
            return Result.error("备件仓库编号重复，请重新填写！");
        }
        //判断名称是否重复
        LambdaQueryWrapper<SparePartStockInfo> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(SparePartStockInfo::getWarehouseName, sparePartStockInfo.getWarehouseName());
        nameWrapper.eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SparePartStockInfo> nameList = sparePartStockInfoMapper.selectList(nameWrapper);
        if (!nameList.isEmpty()) {
            return Result.error("备件仓库名称重复，请重新填写！");
        }
        //判断一个仓库仅能所属一个机构
        LambdaQueryWrapper<SparePartStockInfo> deptWrapper = new LambdaQueryWrapper<>();
        deptWrapper.eq(SparePartStockInfo::getOrganizationId, sparePartStockInfo.getOrganizationId());
        deptWrapper.eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SparePartStockInfo> deptList = sparePartStockInfoMapper.selectList(deptWrapper);
        if (!deptList.isEmpty()) {
            return Result.error("已存在该组织机构的备件仓库！");
        }
        String organizationId = sparePartStockInfo.getOrganizationId();
        SysDepart sysDepart = iSysDepartService.getById(organizationId);
        sparePartStockInfo.setOrgCode(sysDepart.getOrgCode());
        sparePartStockInfoMapper.insert(sparePartStockInfo);
        return Result.OK("添加成功！");
    }
    /**
     * 修改
     *
     * @param sparePartStockInfo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(SparePartStockInfo sparePartStockInfo) {
        SparePartStockInfo stockInfo = getById(sparePartStockInfo.getId());
        //判断编码是否重复
        LambdaQueryWrapper<SparePartStockInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SparePartStockInfo::getWarehouseCode, sparePartStockInfo.getWarehouseCode());
        queryWrapper.eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SparePartStockInfo> list = sparePartStockInfoMapper.selectList(queryWrapper);
        if (!list.isEmpty() && list.get(0).equals(sparePartStockInfo.getId())) {
            return Result.error("备件仓库编号重复，请重新填写！");
        }
        //判断名称是否重复
        LambdaQueryWrapper<SparePartStockInfo> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(SparePartStockInfo::getWarehouseName, sparePartStockInfo.getWarehouseName());
        nameWrapper.eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SparePartStockInfo> nameList = sparePartStockInfoMapper.selectList(nameWrapper);
        if (!nameList.isEmpty() && nameList.get(0).equals(sparePartStockInfo.getId())) {
            return Result.error("备件仓库名称重复，请重新填写！");
        }
        //判断一个仓库仅能所属一个机构
        LambdaQueryWrapper<SparePartStockInfo> deptWrapper = new LambdaQueryWrapper<>();
        deptWrapper.eq(SparePartStockInfo::getOrganizationId, sparePartStockInfo.getOrganizationId());
        deptWrapper.eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SparePartStockInfo> deptList = sparePartStockInfoMapper.selectList(deptWrapper);
        if (!deptList.isEmpty() && deptList.get(0).equals(sparePartStockInfo.getId())) {
            return Result.error("已存在该组织机构的备件仓库！");
        }
        String organizationId = sparePartStockInfo.getOrganizationId();
        SysDepart sysDepart = iSysDepartService.getById(organizationId);
        sparePartStockInfo.setOrgCode(sysDepart.getOrgCode());
        sparePartStockInfoMapper.updateById(sparePartStockInfo);
        return Result.OK("编辑成功！");
    }

    @Override
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile file = multipartRequest.getFile("file");
            if (file == null){
                throw new AiurtBootException("导入文件不能为空!");
            }
            String fileName = file.getOriginalFilename();
            boolean isErrorFile = StrUtil.isEmpty(fileName) || (!fileName.contains(".xls") && !fileName.contains("xlsx"));
            if (isErrorFile) {
                throw new AiurtBootException("只能导入excel文件");
            }
            ImportParams importParams = new ImportParams();
            // 表头行在第三行(索引从0开始)
            importParams.setTitleRows(2);
            // 读取excel的数据，并转化为SparePartStockInfoImportExcelVO类对象的列表
            List<SparePartStockInfoImportExcelVO> list = ExcelImportUtil.importExcel(file.getInputStream(), SparePartStockInfoImportExcelVO.class, importParams);
            // 数据验证
            verifyData(list);

            // 看有没有错误信息
            int errorCount = (int) list.stream().filter(vo -> StrUtil.isNotEmpty(vo.getErrorMessage())).count();
            if (errorCount > 0){
                // TODO: 错误模板
                throw new AiurtBootException("有错误");
            }

            // 转成实体类，并进行保存
            List<SparePartStockInfo> sparePartStockInfoList = list.stream().map(vo -> {
                SparePartStockInfo sparePartStockInfo = new SparePartStockInfo();
                BeanUtils.copyProperties(vo, sparePartStockInfo);
                return sparePartStockInfo;
            }).collect(Collectors.toList());

            // this.saveBatch(sparePartStockInfoList);
            log.info("list: {}", list);

            JSONObject result = new JSONObject(5);
            result.put("isSucceed", true);
            result.put("errorCount", errorCount);
            result.put("successCount", list.size());
            result.put("totalCount", errorCount + list.size());
            return Result.OK("文件导入成功！", result);

        }catch (Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }


    }

    /**
     * 根据用户名查询管理的仓库
     *
     * @param userName
     * @return
     */
    @Override
    public SparePartStockInfo getSparePartStockInfoByUserName(String userName) {
        if (StrUtil.isBlank(userName)) {
            return null;
        }
        LoginUser loginUser = sysBaseApi.getUserByName(userName);

        if (Objects.isNull(loginUser)) {
            return null;
        }

        String orgId = loginUser.getOrgId();

        if (StrUtil.isBlank(orgId)) {
            log.info("该用户没绑定机构：{}-{}", loginUser.getRealname(), loginUser.getUsername());
            return null;
        }
        // 查询仓库
        LambdaQueryWrapper<SparePartStockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SparePartStockInfo::getOrganizationId, orgId).last("limit 1");
        SparePartStockInfo stockInfo = baseMapper.selectOne(wrapper);
        return stockInfo;
    }

    /**
     * 导入excel数据时，验证数据的正确性以及有效性
     * @param list 要验证的数据
     */
    public void verifyData(List<SparePartStockInfoImportExcelVO> list){
        // 查出导入的组织机构，并合成一个orgMap,key是orgCode，value是orgId
        String orgCodes = list.stream().filter(vo -> StrUtil.isNotEmpty(vo.getOrgCode()))
                .map(SparePartStockInfoImportExcelVO::getOrgCode)
                .collect(Collectors.joining(","));

        List<JSONObject> jsonObjectList = sysBaseApi.queryDepartsByOrgcodes(orgCodes);
        Map<String, String> orgMap = jsonObjectList.stream().map(jsonObject -> JSON.toJavaObject(jsonObject, SysDepart.class))
                .collect(Collectors.toMap(SysDepart::getOrgCode, SysDepart::getId, (oldValue, newValue) -> newValue));

        // 获取所有的仓库
        LambdaQueryWrapper<SparePartStockInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SparePartStockInfo> allSparePartStockInfoList = sparePartStockInfoMapper.selectList(queryWrapper);

        // 备件仓库所有的仓库编号
        List<String> warehouseCodeList = allSparePartStockInfoList.stream().map(SparePartStockInfo::getWarehouseCode).collect(Collectors.toList());
        // 备件仓库所有的仓库名称
        List<String> warehouseNameList = allSparePartStockInfoList.stream().map(SparePartStockInfo::getWarehouseName).collect(Collectors.toList());
        // 备件仓库所有的组织机构，一个组织机构只能有一个仓库
        List<String> warehouseOrgCodeList = allSparePartStockInfoList.stream().map(SparePartStockInfo::getOrgCode).collect(Collectors.toList());

        // 验证数据
        list.forEach(vo->{
            // stringBuilder用来存储错误信息
            StringBuilder stringBuilder = new StringBuilder();
            //判断编码是否重复
            if (warehouseCodeList.contains(vo.getWarehouseCode())) {
                stringBuilder.append("备件仓库编号重复或库中已存在");
                stringBuilder.append(";");
            }
            //判断名称是否重复
            if (warehouseNameList.contains(vo.getWarehouseName())) {
                stringBuilder.append("备件仓库名称重复或库中已存在");
                stringBuilder.append(";");
            }
            // 判断组织机构是否存在
            String orgId = orgMap.get(vo.getOrgCode());
            if (StrUtil.isEmpty(orgId)){
                stringBuilder.append("组织机构不存在");
                stringBuilder.append(";");
            }else {
                vo.setOrganizationId(orgId);
            }

            //判断一个仓库仅能所属一个机构
            if (warehouseOrgCodeList.contains(vo.getOrgCode())) {
                stringBuilder.append("备件仓库组织机构重复或库中已存在");
                stringBuilder.append(";");
            }

            //判断后把编码、名称、组织机构加入判断，后面的和前面的也不能重复
            warehouseCodeList.add(vo.getWarehouseCode());
            warehouseNameList.add(vo.getWarehouseName());
            warehouseOrgCodeList.add(vo.getOrgCode());

            // 如果错误信息不为空，加入到vo的错误信息里
            String errorMessage = stringBuilder.toString();
            if (StrUtil.isNotEmpty(errorMessage)) {
                vo.setErrorMessage(errorMessage);
            }

        });

    }


}
