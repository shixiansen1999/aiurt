package com.aiurt.modules.subsystem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.subsystem.dto.*;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.entity.CsSubsystemUser;
import com.aiurt.modules.subsystem.mapper.CsSubsystemMapper;
import com.aiurt.modules.subsystem.mapper.CsSubsystemUserMapper;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.mapper.CsUserSubsystemMapper;
import com.aiurt.modules.system.service.ISysUserService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.TemplateExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: cs_subsystem
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class CsSubsystemServiceImpl extends ServiceImpl<CsSubsystemMapper, CsSubsystem> implements ICsSubsystemService {
    @Autowired
    private CsSubsystemMapper csSubsystemMapper;
    @Autowired
    private CsSubsystemUserMapper csSubsystemUserMapper;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ICsMajorService csMajorService;
    @Autowired
    private CsUserSubsystemMapper csUserSubsystemMapper;
    @Value("${jeecg.path.upload}")
    String filepath;
    /**
     * 添加
     *
     * @param csSubsystem
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(CsSubsystem csSubsystem) {
        //子系统编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsSubsystem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsSubsystem::getSystemCode, csSubsystem.getSystemCode());
        queryWrapper.eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsSubsystem> list = csSubsystemMapper.selectList(queryWrapper);
        if (!list.isEmpty()) {
            return Result.error("子系统编码重复，请重新填写！");
        }
        //子系统名称不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsSubsystem> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsSubsystem::getMajorCode, csSubsystem.getMajorCode());
        nameWrapper.eq(CsSubsystem::getSystemName, csSubsystem.getSystemName());
        nameWrapper.eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsSubsystem> nameList = csSubsystemMapper.selectList(nameWrapper);
        if (!nameList.isEmpty()) {
            return Result.error("子系统名称重复，请重新填写！");
        }
        csSubsystemMapper.insert(csSubsystem);
        //插入子系统人员表
        insertSystemUser(csSubsystem);
        return Result.OK("添加成功！");
    }
    /**
     * 修改
     *
     * @param csSubsystem
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(CsSubsystem csSubsystem) {
        //删除原子系统人员表
        QueryWrapper<CsSubsystemUser> userQueryWrapper = new QueryWrapper<CsSubsystemUser>();
        userQueryWrapper.eq("subsystem_id", csSubsystem.getId());
        csSubsystemUserMapper.delete(userQueryWrapper);
        //子系统编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsSubsystem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsSubsystem::getSystemCode, csSubsystem.getSystemCode());
        queryWrapper.eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsSubsystem> list = csSubsystemMapper.selectList(queryWrapper);
        if (!list.isEmpty() && !list.get(0).getId().equals(csSubsystem.getId())) {
            return Result.error("子系统编码重复，请重新填写！");
        }
        //子系统名称不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsSubsystem> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsSubsystem::getMajorCode, csSubsystem.getMajorCode());
        nameWrapper.eq(CsSubsystem::getSystemName, csSubsystem.getSystemName());
        nameWrapper.eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsSubsystem> nameList = csSubsystemMapper.selectList(nameWrapper);
        if (!nameList.isEmpty() && !nameList.get(0).getId().equals(csSubsystem.getId())) {
            return Result.error("子系统名称重复，请重新填写！");
        }
        csSubsystemMapper.updateById(csSubsystem);
        //插入子系统人员表
        insertSystemUser(csSubsystem);
        return Result.OK("编辑成功！");
    }

    @Override
    public Page<SubsystemFaultDTO> getSubsystemFailureReport(Page<SubsystemFaultDTO> page, String time,SubsystemFaultDTO subsystemCode, List<String> deviceTypeCode) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SubsystemFaultDTO> subSystemCodes = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(subsystemCode.getSystemCode())){
            subSystemCodes.add(csUserSubsystemMapper.selectSubSystem(subsystemCode));
        }else {
            subSystemCodes = csUserSubsystemMapper.selectByUserId(page,sysUser.getId());
        }
        List<SubsystemFaultDTO> subsystemFaultDtos = new ArrayList<>();
        subSystemCodes.forEach(s -> {
            SubsystemFaultDTO subDTO = csUserSubsystemMapper.getSubsystemFaultDTO(time,s.getSystemCode());
            subDTO.setFailureNum(subDTO.getCommonFaultNum()+subDTO.getSeriousFaultNum());
            subDTO.setSystemCode(s.getSystemCode());subDTO.setSystemName(s.getSystemName());subDTO.setId(s.getId());
            subDTO.setCode(subDTO.getSystemCode());subDTO.setName(subDTO.getSystemName());
            subDTO.setFailureDuration(new BigDecimal((1.0 * ( subDTO.getNum()) / 60)).setScale(2, BigDecimal.ROUND_HALF_UP));
            List<SubsystemFaultDTO> list = csUserSubsystemMapper.getSubsystemByDeviceTypeCode(s.getSystemCode(),deviceTypeCode);
            List<SubsystemFaultDTO> deviceTypeList = new ArrayList<>();
            list.forEach(l->{
                SubsystemFaultDTO deviceType = csUserSubsystemMapper.getSubsystemByDeviceType(time,s.getSystemCode(),l.getDeviceTypeCode());
                Long num = csUserSubsystemMapper.getNum(time,s.getSystemCode(),l.getDeviceTypeCode());
                deviceType.setFailureNum(deviceType.getCommonFaultNum()+deviceType.getSeriousFaultNum());
                deviceType.setFailureDuration(new BigDecimal((1.0 * (num==null?0:num) / 60)).setScale(2, BigDecimal.ROUND_HALF_UP));
                deviceType.setDeviceTypeCode(l.getDeviceTypeCode());
                deviceType.setDeviceTypeName(l.getDeviceTypeName());
                deviceType.setName(l.getDeviceTypeName());deviceType.setCode(l.getDeviceTypeCode());deviceType.setId(l.getId());
                deviceTypeList.add(deviceType);
            });
            subDTO.setDeviceTypeList(deviceTypeList);
            subsystemFaultDtos.add(subDTO);
        });

        return page.setRecords(subsystemFaultDtos);
    }

    @Override
    public List<YearFaultDTO> yearFault() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<YearFaultDTO> yearFaultDtos = csUserSubsystemMapper.getYearNumFault(sysUser.getId());
        yearFaultDtos.forEach(y->{
            List<SubsystemFaultDTO> list = csUserSubsystemMapper.getSubsystemByDeviceTypeCode(y.getCode(),null);
            List<YearFaultDTO> years = new ArrayList<>();
            list.forEach(l->{
                YearFaultDTO year = csUserSubsystemMapper.getDeviceTypeYearFault(y.getCode(),l.getDeviceTypeCode());
                year.setId(l.getId());
                year.setCode(l.getDeviceTypeCode());
                year.setName(l.getDeviceTypeName());
                years.add(year);
            });
            y.setYearFaultDtos(years);
        });
        return yearFaultDtos;
    }

    @Override
    public List<SubsystemFaultDTO> deviceTypeCodeByNameDTO(String subsystemCode) {
        List<SubsystemFaultDTO> list = csUserSubsystemMapper.getSubsystemByDeviceTypeCode(subsystemCode,null);
        return list;
    }

    @Override
    public List<YearFaultDTO> yearMinuteFault() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Page<SubsystemFaultDTO> page = new Page<SubsystemFaultDTO>(1, 999);
        List<SubsystemFaultDTO> strings = csUserSubsystemMapper.selectByUserId(page, sysUser.getId());
        List<YearFaultDTO> yearFaultDtos = new ArrayList<>();
          strings.forEach(s->{
              List<ListDTO> system = csUserSubsystemMapper.sysTemYearFault(s.getSystemCode());
              YearFaultDTO yearFaultDTO = new YearFaultDTO();
              yearFaultDTO.setId(s.getId());
              yearFaultDTO.setName(s.getSystemName());yearFaultDTO.setCode(s.getSystemCode());
              system.forEach(sys->{
                  if (ObjectUtils.isNotEmpty(sys.getMonth())) {
                      if (sys.getMonth() == 1) {
                          yearFaultDTO.setJanuary(new BigDecimal((1.0 * (sys.getNum() == null ? 0 : sys.getNum()))).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                      } else if (sys.getMonth() == 2) {
                          yearFaultDTO.setFebruary(new BigDecimal((1.0 * (sys.getNum() == null ? 0 : sys.getNum()))).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                      } else if (sys.getMonth() == 3) {
                          yearFaultDTO.setMarch(new BigDecimal((1.0 * (sys.getNum() == null ? 0 : sys.getNum()) )).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                      } else if (sys.getMonth() == 4) {
                          yearFaultDTO.setApril(new BigDecimal((1.0 * (sys.getNum() == null ? 0 : sys.getNum()) )).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                      } else if (sys.getMonth() == 5) {
                          yearFaultDTO.setMay(new BigDecimal((1.0 * (sys.getNum() == null ? 0 : sys.getNum()) )).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                      } else if (sys.getMonth() == 6) {
                          yearFaultDTO.setJune(new BigDecimal((1.0 * (sys.getNum() == null ? 0 : sys.getNum()) )).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                      } else if (sys.getMonth() == 7) {
                          yearFaultDTO.setJuly(new BigDecimal((1.0 * (sys.getNum() == null ? 0 : sys.getNum()) )).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                      } else if (sys.getMonth() == 8) {
                          yearFaultDTO.setAugust(new BigDecimal((1.0 * (sys.getNum() == null ? 0 : sys.getNum()) )).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                      } else if (sys.getMonth() == 9) {
                          yearFaultDTO.setSeptember(new BigDecimal((1.0 * (sys.getNum() == null ? 0 : sys.getNum()) )).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                      } else if (sys.getMonth() == 10) {
                          yearFaultDTO.setOctober(new BigDecimal((1.0 * (sys.getNum() == null ? 0 : sys.getNum()) )).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                      } else if (sys.getMonth() == 11) {
                          yearFaultDTO.setNovember(new BigDecimal((1.0 * (sys.getNum() == null ? 0 : sys.getNum()) )).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                      } else if (sys.getMonth() == 12) {
                          yearFaultDTO.setDecember(new BigDecimal((1.0 * (sys.getNum() == null ? 0 : sys.getNum()) )).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                      }
                  }
              });
              List<SubsystemFaultDTO> list = csUserSubsystemMapper.getSubsystemByDeviceTypeCode(s.getSystemCode(),null);
              List<YearFaultDTO> yearFaultDTOList =new ArrayList<>();
              list.forEach(l->{
                  YearFaultDTO devDTO = new YearFaultDTO();
                  devDTO.setId(l.getId());
                  devDTO.setCode(l.getDeviceTypeCode());devDTO.setName(l.getDeviceTypeName());
                  List<ListDTO> listDtos = csUserSubsystemMapper.deviceTypeFault(s.getSystemCode(),l.getDeviceTypeCode());
                  listDtos.forEach(ld->{
                      if (ObjectUtils.isNotEmpty(ld.getMonth())) {
                          if (ld.getMonth() == 1) {
                              devDTO.setJanuary(new BigDecimal(Long.valueOf(devDTO.getJanuary())).add( new BigDecimal((1.0 * (ld.getNum() == null ? 0 : ld.getNum()) ))).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                          } else if (ld.getMonth() == 2) {
                              devDTO.setFebruary(new BigDecimal(Long.valueOf(devDTO.getFebruary())).add( new BigDecimal((1.0 * (ld.getNum() == null ? 0 : ld.getNum()) ))).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                          } else if (ld.getMonth() == 3) {
                              devDTO.setMarch(new BigDecimal(Long.valueOf(devDTO.getMarch())).add( new BigDecimal((1.0 * (ld.getNum() == null ? 0 : ld.getNum()) ))).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                          } else if (ld.getMonth() == 4) {
                              devDTO.setApril(new BigDecimal(Long.valueOf(devDTO.getApril())).add( new BigDecimal((1.0 * (ld.getNum() == null ? 0 : ld.getNum()) ))).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                          } else if (ld.getMonth() == 5) {
                              devDTO.setMay(new BigDecimal(Long.valueOf(devDTO.getMay())).add( new BigDecimal((1.0 * (ld.getNum() == null ? 0 : ld.getNum()) ))).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                          } else if (ld.getMonth() == 6) {
                              devDTO.setJune(new BigDecimal(Long.valueOf(devDTO.getJune())).add( new BigDecimal((1.0 * (ld.getNum() == null ? 0 : ld.getNum()) ))).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                          } else if (ld.getMonth() == 7) {
                              devDTO.setJuly(new BigDecimal(Long.valueOf(devDTO.getJuly())).add( new BigDecimal((1.0 * (ld.getNum() == null ? 0 : ld.getNum()) ))).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                          } else if (ld.getMonth() == 8) {
                              devDTO.setAugust(new BigDecimal(Long.valueOf(devDTO.getAugust())).add( new BigDecimal((1.0 * (ld.getNum() == null ? 0 : ld.getNum()) ))).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                          } else if (ld.getMonth() == 9) {
                              devDTO.setSeptember(new BigDecimal(Long.valueOf(devDTO.getSeptember())).add( new BigDecimal((1.0 * (ld.getNum() == null ? 0 : ld.getNum()) ))).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                          } else if (ld.getMonth() == 10) {
                              devDTO.setOctober(new BigDecimal(Long.valueOf(devDTO.getOctober())).add( new BigDecimal((1.0 * (ld.getNum() == null ? 0 : ld.getNum()) ))).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                          } else if (ld.getMonth() == 11) {
                              devDTO.setNovember(new BigDecimal(Long.valueOf(devDTO.getNovember())).add( new BigDecimal((1.0 * (ld.getNum() == null ? 0 : ld.getNum()) ))).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                          } else if (ld.getMonth() == 12) {
                              devDTO.setDecember(new BigDecimal(Long.valueOf(devDTO.getDecember())).add( new BigDecimal((1.0 * (ld.getNum() == null ? 0 : ld.getNum()) ))).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                          }
                      }
                  });
                  yearFaultDTOList.add(devDTO);
              });
              yearFaultDTO.setYearFaultDtos(yearFaultDTOList);
              yearFaultDtos.add(yearFaultDTO);
          });
        return yearFaultDtos;
    }

    @Override
    public ModelAndView reportSystemExport(HttpServletRequest request, SubsystemFaultDTO subsystemCode, List<String> deviceTypeCode, String time, String exportField) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        Page<SubsystemFaultDTO> page = new Page<SubsystemFaultDTO>(1, 9999);
        IPage<SubsystemFaultDTO> failureReportList = this.getSubsystemFailureReport(page,time, subsystemCode, deviceTypeCode);
        List<SubsystemFaultDTO> failureReports = failureReportList.getRecords();
        List<SubsystemFaultDTO> dtos = new ArrayList<>();
        for (SubsystemFaultDTO faultDTO : failureReports) {
            dtos.add(faultDTO);
            List<SubsystemFaultDTO> deviceTypeList = faultDTO.getDeviceTypeList();
            List<SubsystemFaultDTO> dtoNameList = new ArrayList<>();
            for (SubsystemFaultDTO dto : deviceTypeList) {
                SubsystemFaultDTO subsystemFaultDTO = new SubsystemFaultDTO();
                BeanUtil.copyProperties(dto,subsystemFaultDTO);
                dtoNameList.add(subsystemFaultDTO);
            }
            if (CollUtil.isNotEmpty(dtoNameList)) {
                dtos.addAll(dtoNameList);
            }
        }
        if (CollectionUtil.isNotEmpty(failureReports)) {
            //导出文件名称
            mv.addObject(NormalExcelConstants.FILE_NAME, "子系统分析报表");
            //excel注解对象Class
            mv.addObject(NormalExcelConstants.CLASS, SubsystemFaultDTO.class);
            //自定义导出字段
            mv.addObject(NormalExcelConstants.EXPORT_FIELDS,exportField);
            //自定义表格参数
            mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("统计分析-子系统分析报表", "子系统分析报表"));
            //导出数据列表
            mv.addObject(NormalExcelConstants.DATA_LIST, dtos);
        }
        return mv;
    }

    @Override
    public SystemByCodeDTO csSubsystemByCode(String subsystemCode) {
        SystemByCodeDTO systemByCodeDTO= csUserSubsystemMapper.getSystemByCodeDTO(subsystemCode);
        systemByCodeDTO.setReplacementNum(csUserSubsystemMapper.getReplacementNum(subsystemCode));
        return systemByCodeDTO;
    }

    public void insertSystemUser(CsSubsystem csSubsystem){
        if(null!=csSubsystem.getSystemUserList()){
            String[] arr = csSubsystem.getSystemUserList().split(",");
            for(String userName :arr){
                CsSubsystemUser systemUser = new CsSubsystemUser();
                systemUser.setSubsystemId(csSubsystem.getId()+"");
                systemUser.setUsername(userName);
                LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.in(SysUser::getUsername, userName);
                List<SysUser> userList = sysUserService.list(queryWrapper);
                if(!userList.isEmpty()){
                    systemUser.setUserId(userList.get(0).getId());
                }
                csSubsystemUserMapper.insert(systemUser);
            }
        }
    }
    @Override
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        // 错误信息
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0, errorLines = 0;
        String url = null;
            // 获取上传文件对象
        MultipartFile file = multipartRequest.getFile("file");
        InputStream inputStream = file.getInputStream();//获取后缀名
        String nameAndType[] = file.getOriginalFilename().split("\\.");
        String type = nameAndType[1];
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                return imporReturnRes(errorLines, successLines, errorMessage,false,null);
            }
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<CsSubsystemImportDTO> csSubsystemDTOList = ExcelImportUtil.importExcel(file.getInputStream(), CsSubsystemImportDTO.class, params);
                csSubsystemDTOList = csSubsystemDTOList.parallelStream().filter(c->c.getMajorCode()!=null||c.getSystemName()!=null||c.getSystemCode()!=null||c.getSystemUserName()!=null||c.getGeneralSituation()!=null).collect(Collectors.toList());
                List<CsSubsystem> list = new ArrayList<>();
                if(CollUtil.isEmpty(csSubsystemDTOList))
                {
                    throw new AiurtBootException("该文件无数据，请填写再导入");
                }
                for (int i = 0; i < csSubsystemDTOList.size(); i++) {
                    CsSubsystemImportDTO csSubsystemDTO = csSubsystemDTOList.get(i);
                    List<CsSubsystemImportDTO> csSubsystemCodes = csSubsystemDTOList.stream().filter(c -> c.getSystemCode().equals(csSubsystemDTO.getSystemCode())).collect(Collectors.toList());
                    List<CsSubsystemImportDTO> csSubsystemNames = csSubsystemDTOList.stream().filter(c -> c.getSystemName().equals(csSubsystemDTO.getSystemName())).collect(Collectors.toList());
                    CsSubsystem csSubsystem = new CsSubsystem();
                     String s = decideIsNull(csSubsystemDTO);
                    if(ObjectUtil.isNotEmpty(s))
                    {
                        if(csSubsystemCodes.size()==1&&csSubsystemNames.size()==1)
                        {
                            csSubsystemDTO.setWrongReason(s);
                        }
                        if(csSubsystemCodes.size()!=1&&csSubsystemNames.size()==1)
                        {
                            csSubsystemDTO.setWrongReason(s+",子系统编码重复");
                        }
                        if(csSubsystemCodes.size()!=1&&csSubsystemNames.size()!=1)
                        {
                            csSubsystemDTO.setWrongReason(s+",子系统编码和子系统名称重复");
                        }
                        if(csSubsystemCodes.size()==1&&csSubsystemNames.size()!=1)
                        {
                            csSubsystemDTO.setWrongReason(s+",子系统名称重复");
                        }
                        errorLines++;
                    }
                    else
                    {
                        if(csSubsystemCodes.size()!=1||csSubsystemNames.size()!=1)
                        {
                            if(csSubsystemCodes.size()!=1&&csSubsystemNames.size()==1)
                            {
                                csSubsystemDTO.setWrongReason("子系统编码重复");
                            }
                            if(csSubsystemCodes.size()!=1&&csSubsystemNames.size()!=1)
                            {
                                csSubsystemDTO.setWrongReason("子系统编码和子系统名称重复");
                            }
                            if(csSubsystemCodes.size()==1&&csSubsystemNames.size()!=1)
                            {
                                csSubsystemDTO.setWrongReason("子系统名称重复");
                            }
                            errorLines++;
                        }
                    }
                    BeanUtils.copyProperties(csSubsystemDTO, csSubsystem);
                    if(ObjectUtil.isNotEmpty(csSubsystemDTO.getSystemUserName()))
                    {
                        List<SysUser> userList = isNUllUsers(csSubsystemDTO.getSystemUserName());
                        csSubsystem.setSystemUsers(userList);
                    }

                    list.add(csSubsystem);
                }

                if(errorLines==0)
                {
                    for (CsSubsystem csSubsystem : list) {
                        csSubsystemMapper.insert(csSubsystem);
                        List<SysUser> systemUserList = csSubsystem.getSystemUsers();
                        if(CollUtil.isNotEmpty(systemUserList))
                        {
                            for (SysUser sysUser : systemUserList) {
                                CsSubsystemUser csSubsystemUser = new CsSubsystemUser();
                                csSubsystemUser.setSubsystemId(csSubsystem.getId());
                                csSubsystemUser.setUserId(sysUser.getId());
                                csSubsystemUser.setUsername(sysUser.getUsername());
                                csSubsystemUserMapper.insert(csSubsystemUser);
                            }

                        }
                    }
                    successLines =csSubsystemDTOList.size();
                }
                else
                {
                    successLines =csSubsystemDTOList.size()-errorLines;
                    //1.获取文件流
                    Resource resource = new ClassPathResource("/templates/csSubsystemImportDTO.xlsx");
                    InputStream resourceAsStream = resource.getInputStream();

                    //2.获取临时文件
                    File fileTemp= new File("/templates/csmajorexcel.xlsx");
                    try {
                        //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
                        FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                    String path = fileTemp.getAbsolutePath();
                    TemplateExportParams exportParams = new TemplateExportParams(path);
                    Map<String, Object> errorMap = new HashMap<String, Object>();
                    errorMap.put("title", "子系统导入失败错误清单");
                    List<Map<String, Object>> listMap = new ArrayList<>();
                    for (CsSubsystemImportDTO dto : csSubsystemDTOList) {
                        //获取一条排班记录
                        Map<String, Object> lm = new HashMap<String, Object>();
                        CsMajor csMajor = csMajorService.getBaseMapper().selectOne(new LambdaQueryWrapper<CsMajor>().eq(CsMajor::getMajorCode, dto.getMajorCode()).eq(CsMajor::getDelFlag, 0));
                        //错误报告获取信息
                        if(ObjectUtil.isNotEmpty(csMajor))
                        {
                            lm.put("major", csMajor.getMajorName());
                            lm.put("mistake", dto.getWrongReason());
                        }
                        else
                        {
                            lm.put("major", dto.getMajorCode());
                            lm.put("mistake", dto.getWrongReason()==null?"所属专业不存在":dto.getWrongReason()+";所属专业不存在");
                        }
                        lm.put("systemcode", dto.getSystemCode());
                        lm.put("systemname", dto.getSystemName());
                        lm.put("systemusername", dto.getSystemUserName());
                        lm.put("generalsituation", dto.getGeneralSituation());

                        listMap.add(lm);
                    }
                    errorMap.put("maplist", listMap);
                    Workbook workbook = ExcelExportUtil.exportExcel(exportParams, errorMap);
                    List<CsMajor> scheduleItems = csMajorService.getBaseMapper().selectList(new LambdaQueryWrapper<CsMajor>().eq(CsMajor::getDelFlag, 0));
                    List<String> names = scheduleItems.stream().map(CsMajor::getMajorName).collect(Collectors.toList());
                    ExcelSelectListUtil.selectList(workbook, 1, 1, names);
                    String fileName = "子系统导入失败错误清单"+ "_" + System.currentTimeMillis()+"."+type;
                    FileOutputStream out = new FileOutputStream(filepath+ File.separator+fileName);
                    url =fileName;
                    workbook.write(out);
                }
            } catch (Exception e) {
                errorMessage.add("发生异常：" + e.getMessage());
                log.error(e.getMessage(), e);
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        return imporReturnRes(errorLines, successLines, errorMessage,true,url);
    }

    public static final class ExcelSelectListUtil {
        /**
         * firstRow 開始行號 根据此项目，默认为3(下标0开始)
         * lastRow  根据此项目，默认为最大65535
         * firstCol 区域中第一个单元格的列号 (下标0开始)
         * lastCol 区域中最后一个单元格的列号
         * strings 下拉内容
         * */
        public static void selectList(Workbook workbook,int firstCol,int lastCol,List<String >majorName ){
            if (CollectionUtil.isNotEmpty(majorName)) {
                Sheet sheet = workbook.getSheetAt(0);
                //将新建的sheet页隐藏掉, 下拉值太多，需要创建隐藏页面
                int sheetTotal = workbook.getNumberOfSheets();
                String hiddenSheetName = "专业" + "_hiddenSheet";
                Sheet hiddenSheet = workbook.getSheet(hiddenSheetName);
                if (hiddenSheet == null) {
                    hiddenSheet = workbook.createSheet(hiddenSheetName);
                    //写入下拉数据到新的sheet页中
                    for (int i = 0; i < majorName.size(); i++) {
                        Row hiddenRow = hiddenSheet.createRow(i);
                        Cell hiddenCell = hiddenRow.createCell(0);
                        hiddenCell.setCellValue(majorName.get(i));
                    }
                    workbook.setSheetHidden(sheetTotal, true);
                }

                // 下拉数据
                CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(3, 65535, 0, 0);
                //  生成下拉框内容名称
                String strFormula = hiddenSheetName + "!$A$1:$A$65535";
                // 根据隐藏页面创建下拉列表
                XSSFDataValidationConstraint constraint = new XSSFDataValidationConstraint(DataValidationConstraint.ValidationType.LIST, strFormula);
                XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) hiddenSheet);
                DataValidation validation = dvHelper.createValidation(constraint, cellRangeAddressList);
                //  对sheet页生效
                sheet.addValidationData(validation);
            }
        }
    }
    private String decideIsNull(CsSubsystemImportDTO csSubsystemDTO) {
        List<SysUser> nUllUsers = new ArrayList<>();
        Integer size= 0;
        if(ObjectUtil.isNotEmpty(csSubsystemDTO.getSystemUserName()))
        {

             nUllUsers = isNUllUsers(csSubsystemDTO.getSystemUserName());
        }
        if(CollUtil.isNotEmpty(nUllUsers))
        {

            size = nUllUsers.size();
        }
        if (csSubsystemDTO.getMajorCode() == null && csSubsystemDTO.getSystemName() == null && csSubsystemDTO.getSystemCode()==null) {
            return "必填字段为空";
        }
        else if (csSubsystemDTO.getMajorCode() == null && csSubsystemDTO.getSystemName() != null && csSubsystemDTO.getSystemCode()==null) {
            CsSubsystem csSubsystem = csSubsystemMapper.selectOne(new QueryWrapper<CsSubsystem>().lambda().eq(CsSubsystem::getSystemName, csSubsystemDTO.getSystemName()).eq(CsSubsystem::getDelFlag, 0));
            if (csSubsystem != null&&size==0) {
                return "必填字段为空;子系统名称重复;技术员不存在";
            }
            if (csSubsystem != null&&size!=0) {
                return "必填字段为空;子系统名称重复";
            }
            if (csSubsystem == null&&size!=0)
            {
                return "必填字段为空";
            }
        }
        else if (csSubsystemDTO.getMajorCode() == null && csSubsystemDTO.getSystemName() == null && csSubsystemDTO.getSystemCode()!=null) {
            CsSubsystem csSubsystem = csSubsystemMapper.selectOne(new QueryWrapper<CsSubsystem>().lambda().eq(CsSubsystem::getSystemCode, csSubsystemDTO.getSystemCode()).eq(CsSubsystem::getDelFlag, 0));
            if (csSubsystem != null&&size==0) {
                return "必填字段为空;子系统编码重复;技术员不存在";
            }
            if (csSubsystem != null&&size!=0) {
                return "必填字段为空;子系统编码重复";
            }
            if (csSubsystem == null&&size!=0)
            {
                return "必填字段为空";
            }
        }
        else if (csSubsystemDTO.getMajorCode() != null && csSubsystemDTO.getSystemName() != null && csSubsystemDTO.getSystemCode()!=null) {
            CsSubsystem csSubsystemName = csSubsystemMapper.selectOne(new QueryWrapper<CsSubsystem>().lambda().eq(CsSubsystem::getSystemName, csSubsystemDTO.getSystemName()).eq(CsSubsystem::getDelFlag, 0));
            CsSubsystem csSubsystemCode = csSubsystemMapper.selectOne(new QueryWrapper<CsSubsystem>().lambda().eq(CsSubsystem::getSystemCode, csSubsystemDTO.getSystemCode()).eq(CsSubsystem::getDelFlag, 0));

            if (csSubsystemName != null&&csSubsystemCode!=null&&size!=0) {
                return "子系统编码重复;子系统名称重复";
            }
            if (csSubsystemName != null&&csSubsystemCode!=null&&size==0) {
                return "子系统编码重复;子系统名称重复;技术员不存在";
            }
            if (csSubsystemCode != null&&csSubsystemName==null&&size!=0) {
                return "子系统编码重复";
            }
            if (csSubsystemCode != null&&csSubsystemName==null&&size==0) {
                return "子系统编码重复;技术员不存在";
            }
            if (csSubsystemCode == null&&csSubsystemName!=null&&size!=0) {
                return "子系统名称重复";
            }
            if (csSubsystemCode == null&&csSubsystemName!=null&&size==0) {
                return "子系统名称重复;技术员不存在";
            }
        }
        return null;
    }
    private List<SysUser> isNUllUsers(String  realName) {
        if(ObjectUtil.isNotEmpty(realName))
        {
            String[] arr = realName.split(",");
            for(String userName :arr){
                //判断是否存在
                List<SysUser> users = sysUserService.list(new LambdaQueryWrapper<SysUser>().eq(SysUser::getRealname, userName));
                if(CollUtil.isNotEmpty(users))
                {
                    List<SysUser> userList  = sysUserService.list(new LambdaQueryWrapper<SysUser>().eq(SysUser::getRealname, userName));
                    return  userList;
                }
                else
                {
                   return  null;
                }
            }
        }
        return  null;
        }
    public static Result<?> imporReturnRes(int errorLines,int successLines,List<String> errorMessage,boolean isType,String url) throws IOException {
        if(isType)
        {
            if (errorLines != 0) {
                JSONObject result = new JSONObject(5);
                result.put("isSucceed", false);
                result.put("errorCount", errorLines);
                result.put("successCount", successLines);
                int totalCount = successLines + errorLines;
                result.put("totalCount", totalCount);
                result.put("failReportUrl", url);
                Result res = Result.ok(result);
                res.setMessage("文件失败，数据有错误。");
                res.setCode(200);
                return res;
            } else {
                //是否成功
                JSONObject result = new JSONObject(5);
                result.put("isSucceed", true);
                result.put("errorCount", errorLines);
                result.put("successCount", successLines);
                int totalCount = successLines + errorLines;
                result.put("totalCount", totalCount);
                Result res = Result.ok(result);
                res.setMessage("文件导入成功！");
                res.setCode(200);
                return res;
            }
        }
        else
        {
            JSONObject result = new JSONObject(5);
            result.put("isSucceed", false);
            result.put("errorCount", errorLines);
            result.put("successCount", successLines);
            int totalCount = successLines + errorLines;
            result.put("totalCount", totalCount);
            Result res = Result.ok(result);
            res.setMessage("导入失败，文件类型不对。");
            res.setCode(200);
            return res;
        }

    }
}
