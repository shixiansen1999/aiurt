package com.aiurt.modules.subsystem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
        int successLines = 0, errorLines = 0,errorUsers=0;;
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                return imporReturnRes(errorLines, successLines, errorMessage,false);
            }
            ImportParams params = new ImportParams();
            params.setTitleRows(1);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<CsSubsystemDTO> csSubsystemDTOList = ExcelImportUtil.importExcel(file.getInputStream(), CsSubsystemDTO.class, params);
                csSubsystemDTOList = csSubsystemDTOList.stream().filter(c->c.getSystemName()!=null).collect(Collectors.toList());
                List<CsSubsystem> list = new ArrayList<>();
                for (int i = 0; i < csSubsystemDTOList.size(); i++) {
                    CsSubsystemDTO csSubsystemDTO = csSubsystemDTOList.get(i);
                    boolean error = true;
                    if (ObjectUtil.isNull(csSubsystemDTO.getMajorCode())) {
                        errorMessage.add("专业编码为必填项，忽略导入");
                        errorLines++;
                        error=false;

                    }
                    else
                    {
                        CsMajor csMajor = csMajorService.getOne(new QueryWrapper<CsMajor>().lambda().eq(CsMajor::getMajorCode, csSubsystemDTO.getMajorCode()).eq(CsMajor::getDelFlag, 0));
                        if (csMajor == null) {
                            errorMessage.add(csSubsystemDTO.getMajorName() + "专业名称不存在，忽略导入");
                            if(error)
                            {
                                errorLines++;
                                error=false;
                            }
                        }
                        else
                        {
                            csSubsystemDTO.setMajorCode(csMajor.getMajorCode());
                        }

                    }
                    if (ObjectUtil.isNull(csSubsystemDTO.getSystemCode())) {
                        errorMessage.add("系统编码为必填项，忽略导入");
                        if(error)
                        {
                            errorLines++;
                            error=false;
                        }
                    }
                    else
                    {
                        CsSubsystem csSubsystem = csSubsystemMapper.selectOne(new QueryWrapper<CsSubsystem>().lambda().eq(CsSubsystem::getSystemCode, csSubsystemDTO.getSystemCode()).eq(CsSubsystem::getDelFlag, 0));
                        if (csSubsystem != null) {
                            errorMessage.add(csSubsystemDTO.getMajorCode() + "系统编码已经存在，忽略导入");
                            if(error)
                            {
                                errorLines++;
                                error=false;
                            }
                        }
                    }

                    if (ObjectUtil.isNull(csSubsystemDTO.getSystemName())) {
                        errorMessage.add("系统名称为必填项，忽略导入");
                        if(error)
                        {
                            errorLines++;
                            error=false;
                        }
                    }
                    else {
                        CsSubsystem csSubsystem = csSubsystemMapper.selectOne(new QueryWrapper<CsSubsystem>().lambda().eq(CsSubsystem::getSystemName, csSubsystemDTO.getSystemName()).eq(CsSubsystem::getDelFlag, 0));
                        if (csSubsystem != null) {
                            errorMessage.add(csSubsystem.getMajorCode() + "系统名称已经存在，忽略导入");
                            if(error)
                            {
                                errorLines++;
                                error=false;
                            }
                        }
                    }
                    CsSubsystem csSubsystem = new CsSubsystem();
                    BeanUtils.copyProperties(csSubsystemDTO, csSubsystem);
                    Integer lineUser=0;
                    if(ObjectUtil.isNotEmpty(csSubsystemDTO.getSystemUserName()))
                    {
                        String[] arr = csSubsystemDTO.getSystemUserName().split(",");

                        for(String userName :arr){
                            //判断是否存在
                            List<SysUser> users = sysUserService.list(new LambdaQueryWrapper<SysUser>().eq(SysUser::getRealname, userName));
                            if(CollUtil.isEmpty(users))
                            {
                                errorMessage.add(csSubsystem.getMajorCode() + "技术人不存在，忽略导入");
                                if(error)
                                {
                                    errorLines++;
                                    error=false;
                                }
                                lineUser++;
                            }
                            else
                            {
                                List<SysUser> userList  = sysUserService.list(new LambdaQueryWrapper<SysUser>().eq(SysUser::getRealname, userName));
                                csSubsystem.setSystemUsers(userList);
                            }

                        }
                    }
                    if(lineUser!=0)
                    {
                        if(error)
                        {
                            errorLines++;
                        }
                        errorUsers++;
                    }
                    list.add(csSubsystem);
                    successLines++;
                }

                if(errorLines==0&&errorUsers==0)
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
                }
                else
                {
                    successLines =0;
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
        }
        return imporReturnRes(errorLines, successLines, errorMessage,true);
    }
    public static Result<?> imporReturnRes(int errorLines,int successLines,List<String> errorMessage,boolean isType) throws IOException {
        if(isType)
        {
            if (errorLines != 0) {
                JSONObject result = new JSONObject(5);
                result.put("isSucceed", false);
                result.put("errorCount", errorLines);
                result.put("successCount", successLines);
                int totalCount = successLines + errorLines;
                result.put("totalCount", totalCount);
                result.put("failReportUrl", "");
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
