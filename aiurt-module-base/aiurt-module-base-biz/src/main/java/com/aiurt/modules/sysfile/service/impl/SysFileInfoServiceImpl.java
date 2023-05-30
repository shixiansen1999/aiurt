package com.aiurt.modules.sysfile.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.sysfile.constant.SysFileConstant;
import com.aiurt.modules.sysfile.entity.SysFileInfo;
import com.aiurt.modules.sysfile.mapper.SysFileInfoMapper;
import com.aiurt.modules.sysfile.param.SysFileInfoParam;
import com.aiurt.modules.sysfile.service.ISysFileInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zwl
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysFileInfoServiceImpl extends ServiceImpl<SysFileInfoMapper, SysFileInfo> implements ISysFileInfoService {

    @Resource
    private ISysBaseAPI sysBaseApi;

    @Override
    public SysFileInfo addDownload(SysFileInfo sysFileInfo) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("请重新登录");
        }
        sysFileInfo.setId(null);
        sysFileInfo.setUserName(loginUser.getRealname());
        sysFileInfo.setDepartmentCode(loginUser.getOrgCode());
        sysFileInfo.setDownloadTime(new Date());
        sysFileInfo.setDelFlag(CommonConstant.DEL_FLAG_0);

        this.save(sysFileInfo);
        return sysFileInfo;
    }

    @Override
    public Page<SysFileInfo> queryPageDownloadList(Page<SysFileInfo> page, SysFileInfoParam sysFileInfoParam) {
        //查询条件拼接
        LambdaQueryWrapper<SysFileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (ObjectUtil.isNotNull(sysFileInfoParam.getFileId())) {
            lambdaQueryWrapper.eq(SysFileInfo::getFileId, sysFileInfoParam.getFileId());
        }
        if (ObjectUtil.isNotNull(sysFileInfoParam.getStartTime()) && ObjectUtil.isNotNull(sysFileInfoParam.getEndTime())) {
            lambdaQueryWrapper.ge(SysFileInfo::getDownloadTime, DateUtil.beginOfDay(sysFileInfoParam.getStartTime()));
            lambdaQueryWrapper.le(SysFileInfo::getDownloadTime, DateUtil.endOfDay(sysFileInfoParam.getEndTime()));
        }
        if (StrUtil.isNotBlank(sysFileInfoParam.getUserName())) {
            lambdaQueryWrapper.like(SysFileInfo::getUserName, sysFileInfoParam.getUserName());
        }
        if (ObjectUtil.isNotNull(sysFileInfoParam.getDownloadStatus())) {
            lambdaQueryWrapper.eq(SysFileInfo::getDownloadStatus, sysFileInfoParam.getDownloadStatus());
        }
        if (StrUtil.isNotBlank(sysFileInfoParam.getOrgCode())) {
            lambdaQueryWrapper.eq(SysFileInfo::getDepartmentCode, sysFileInfoParam.getOrgCode());
        }

        lambdaQueryWrapper.eq(SysFileInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
        lambdaQueryWrapper.orderByDesc(SysFileInfo::getCreateTime);

        page = this.page(page, lambdaQueryWrapper);

        Map<String, String> downloadStatusDictListMap = getDownloadStatusDictListMap();

        page.getRecords().forEach(sysFileInfo -> processDownloadRecord(sysFileInfo, downloadStatusDictListMap));

        return page;
    }

    @Override
    public ModelAndView reportExportDownloadList(Long fileId) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());

        //查询条件拼接
        LambdaQueryWrapper<SysFileInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (ObjectUtil.isNotNull(fileId)) {
            lambdaQueryWrapper.eq(SysFileInfo::getFileId, fileId);
        }
        lambdaQueryWrapper.eq(SysFileInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
        lambdaQueryWrapper.orderByDesc(SysFileInfo::getCreateTime);

        List<SysFileInfo> sysFileInfoList = this.list(lambdaQueryWrapper);

        Map<String, String> downloadStatusDictListMap = getDownloadStatusDictListMap();

        sysFileInfoList.forEach(sysFileInfo -> processDownloadRecord(sysFileInfo, downloadStatusDictListMap));

        if (CollUtil.isNotEmpty(sysFileInfoList)) {
            //导出文件名称
            mv.addObject(NormalExcelConstants.FILE_NAME, "下载记录报表");
            //excel注解对象Class
            mv.addObject(NormalExcelConstants.CLASS, SysFileInfo.class);
            //自定义导出字段
            mv.addObject(NormalExcelConstants.EXPORT_FIELDS, SysFileConstant.DOWNLOAD_RECORD_EXPORT_FIELD);
            //自定义表格参数
            mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("下载记录报表", "下载记录报表"));
            //导出数据列表
            mv.addObject(NormalExcelConstants.DATA_LIST, sysFileInfoList);
        }
        return mv;

    }

    /**
     * 获取下载状态字典映射
     *
     * @return 映射的 Map，键为周期类型值，值为周期类型文本描述
     */
    private Map<String, String> getDownloadStatusDictListMap() {
        List<DictModel> dictItems = sysBaseApi.queryEnableDictItemsByCode(SysFileConstant.DOWNLOAD_STATUS);
        if (dictItems == null || dictItems.isEmpty()) {
            return Collections.emptyMap();
        }

        return dictItems.stream()
                .filter(dictModel -> dictModel.getValue() != null && dictModel.getText() != null)
                .collect(Collectors.toMap(DictModel::getValue, DictModel::getText, (v1, v2) -> v1));
    }

    /**
     * 处理下载记录的文件名和下载状态名称
     *
     * @param sysFileInfo               下载记录对象
     * @param downloadStatusDictListMap 下载状态字典列表映射
     */
    private void processDownloadRecord(SysFileInfo sysFileInfo, Map<String, String> downloadStatusDictListMap) {
        String fileName = sysFileInfo.getFileName();

        if (ObjectUtil.isNotNull(sysFileInfo.getDownloadStatus())) {
            sysFileInfo.setDownloadStatusName(downloadStatusDictListMap.get(String.valueOf(sysFileInfo.getDownloadStatus())));
        }

        if (StrUtil.isNotBlank(fileName)) {
            sysFileInfo.setFileName("《" + fileName + "》");
        }
    }

}
