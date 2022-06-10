package com.aiurt.boot.modules.fault.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.common.result.OutsourcingPersonnelResult;
import com.aiurt.boot.common.system.api.ISysBaseAPI;
import com.aiurt.boot.common.util.TokenUtils;
import com.aiurt.boot.modules.fault.dto.OutsourcingPersonnelInput;
import com.aiurt.boot.modules.fault.entity.OutsourcingPersonnel;
import com.aiurt.boot.modules.fault.mapper.OutsourcingPersonnelMapper;
import com.aiurt.boot.modules.fault.param.OutsourcingPersonnelParam;
import com.aiurt.boot.modules.fault.service.IOutsourcingPersonnelService;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description: 委外人员
 * @Author: swsc
 * @Date: 2021-09-18
 * @Version: V1.0
 */
@Service
public class OutsourcingPersonnelServiceImpl extends ServiceImpl<OutsourcingPersonnelMapper, OutsourcingPersonnel> implements IOutsourcingPersonnelService {

    @Resource
    private OutsourcingPersonnelMapper personnelMapper;

    @Resource
    private ISysBaseAPI iSysBaseAPI;

    @Resource
    private ISubsystemService subsystemService;

    /**
     * 新增委外人员
     *
     * @param personnel
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public Result add(OutsourcingPersonnel personnel, HttpServletRequest req) {
        OutsourcingPersonnel outsourcingPersonnel = new OutsourcingPersonnel();
        outsourcingPersonnel.setName(personnel.getName());
        outsourcingPersonnel.setCertificateCode(personnel.getCertificateCode());
        outsourcingPersonnel.setCompany(personnel.getCompany());
        outsourcingPersonnel.setPosition(personnel.getPosition());
        outsourcingPersonnel.setSystemCode(personnel.getSystemCode());
        outsourcingPersonnel.setConnectionWay(personnel.getConnectionWay());
        outsourcingPersonnel.setDelFlag(0);
        String userId = TokenUtils.getUserId(req, iSysBaseAPI);
        outsourcingPersonnel.setCreateBy(userId);
        personnelMapper.insert(outsourcingPersonnel);
        return Result.ok("新增成功");
    }

    /**
     * 查询委外人员
     *
     * @param page
     * @param param
     * @return
     */
    @Override
    public IPage<OutsourcingPersonnelResult> pageList(IPage<OutsourcingPersonnelResult> page, OutsourcingPersonnelParam param) {
        IPage<OutsourcingPersonnelResult> resultIPage = personnelMapper.queryOutsourcingPersonnel(page, param);
        return resultIPage;
    }

    /**
     * 委外人员导出
     *
     * @param param
     * @return
     */
    @Override
    public List<OutsourcingPersonnelResult> exportXls(OutsourcingPersonnelParam param) {
        List<OutsourcingPersonnelResult> list = personnelMapper.exportXls(param);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSerialNumber(i + 1);
        }
        return list;
    }

    /**
     * excel导入
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        String userId = TokenUtils.getUserId(request, iSysBaseAPI);
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            //这里要注意一下
            params.setTitleRows(1);
            params.setHeadRows(2);
            params.setNeedSave(true);
            try {
                //读取Excel中的数据进行转换
                List<OutsourcingPersonnelInput> list = ExcelImportUtil.importExcel(file.getInputStream(), OutsourcingPersonnelInput.class, params);
                if (CollUtil.isEmpty(list)) {
                    return Result.error("Excel转换异常");
                }
                //委外人员批量新增
                for (OutsourcingPersonnelInput input : list) {
                    if (StringUtils.isBlank(input.getName())) {
                        throw new SwscException("人员名称不能为空");
                    }
                    if (StringUtils.isBlank(input.getCompany())) {
                        throw new SwscException("所属单位不能为空");
                    }
                    if (StringUtils.isBlank(input.getCompany())) {
                        throw new SwscException("职位名称不能为空");
                    }
                    if (StringUtils.isNotBlank(input.getSystemName())) {
                        Subsystem one = subsystemService.getOne(new QueryWrapper<Subsystem>().eq(Subsystem.SYSTEM_NAME, input.getSystemName()), false);
                        if (ObjectUtil.isEmpty(one)) {
                            throw new SwscException("输入所属专业系统不规范");
                        }
                        input.setSystemCode(one.getSystemCode());
                    } else {
                        throw new SwscException("所属专业系统不能为空");
                    }
                    if (StringUtils.isBlank(input.getConnectionWay())) {
                        throw new SwscException("联系方式不能为空");
                    }
                    if (StringUtils.isBlank(input.getCertificateCode())) {
                        throw new SwscException("施工证编号不能为空");
                    }
                    OutsourcingPersonnel outsourcingPersonnel = new OutsourcingPersonnel();
                    outsourcingPersonnel.setCreateBy(userId);
                    outsourcingPersonnel.setDelFlag(CommonConstant.DEL_FLAG_0);
                    BeanUtils.copyProperties(input, outsourcingPersonnel);
                    this.save(outsourcingPersonnel);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败" + e);
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入成功");
    }
}
