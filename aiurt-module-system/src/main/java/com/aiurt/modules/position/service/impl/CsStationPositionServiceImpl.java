package com.aiurt.modules.position.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.ImportExcelUtil;
import com.aiurt.modules.position.controller.CsLineController;
import com.aiurt.modules.position.controller.CsStationController;
import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.aiurt.modules.position.mapper.CsLineMapper;
import com.aiurt.modules.position.mapper.CsStationMapper;
import com.aiurt.modules.position.mapper.CsStationPositionMapper;
import com.aiurt.modules.position.service.ICsStationPositionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.TemplateExportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: cs_station_position
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class CsStationPositionServiceImpl extends ServiceImpl<CsStationPositionMapper, CsStationPosition> implements ICsStationPositionService {
    @Autowired
    private CsStationPositionMapper csStationPositionMapper;
    @Autowired
    private CsStationMapper csStationMapper;
    @Autowired
    private CsLineMapper csLineMapper;
    @Autowired
    @Lazy
    private CsLineController csLineController;
    @Autowired
    @Lazy
    private CsStationController csStationController;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    /**
     * 查询列表
     * @param page
     * @return
     */
    @Override
    public List<CsStationPosition> readAll(Page<CsStationPosition> page, CsStationPosition csStationPosition){
        List<CsStationPosition> csStationPositions = csStationPositionMapper.queryCsStationPositionAll(page, csStationPosition);
        if (CollUtil.isNotEmpty(csStationPositions)) {
            for (CsStationPosition stationPosition : csStationPositions) {
                if (stationPosition.getLevel().equals(2)) {
                    CsStationPosition result = csStationPositionMapper.getById(stationPosition.getId());
                    stationPosition.setPhoneNum(result.getPhoneNum());
                }
            }
        }
        return csStationPositions;
    }
    /**
     * 添加
     *
     * @param csStationPosition
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(CsStationPosition csStationPosition) {
        /*编码不能重复，判断数据库中是否存在，如不存在则可继续添加*/
        List<CsLine> list = csLineMapper.selectCode(csStationPosition.getPositionCode());
        if (!list.isEmpty()) {
            return Result.error("编码重复，请重新填写！");
        }
        //根据Station_code查询所属线路code
        LambdaQueryWrapper<CsStation> stationWrapper = new LambdaQueryWrapper<>();
        stationWrapper.eq(CsStation::getStationCode,csStationPosition.getStaionCode());
        stationWrapper.eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0);
        CsStation sta = csStationMapper.selectOne(stationWrapper);
        csStationPosition.setLineCode(sta.getLineCode());
        //拼接position_code_cc
        csStationPosition.setPositionCodeCc("/"+sta.getLineCode()+"/"+csStationPosition.getStaionCode()+"/"+csStationPosition.getPositionCode());
        csStationPosition.setUpdateTime(new Date());
        csStationPositionMapper.insert(csStationPosition);
        return Result.OK("添加成功！");
    }
    /**
     * 修改
     *
     * @param csStationPosition
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(CsStationPosition csStationPosition) {
        //编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        List<CsLine> list = csLineMapper.selectCode(csStationPosition.getPositionCode());
        if (!list.isEmpty() && !list.get(0).getId().equals(csStationPosition.getId())) {
            return Result.error("编码重复，请重新填写！");
        }
        csStationPositionMapper.updateById(csStationPosition);
        return Result.OK("编辑成功！");
    }

    @Override
    public Result<?> importExcelMaterial(MultipartFile file, ImportParams params) throws Exception {
        List<CsStationPosition> listMaterial = ExcelImportUtil.importExcel(file.getInputStream(), CsStationPosition.class, params);
        listMaterial =  listMaterial.stream().filter(l-> l.getLevelName()!=null && l.getPositionTypeName()!=null && l.getPositionCode()!=null && l.getPositionName() !=null)
                                             .collect(Collectors.toList());
        List<String> errorStrs = new ArrayList<>();
        // 去掉 sql 中的重复数据
        Integer errorLines = 0;
        Integer successLines = 0;
        List<CsStationPosition> list = new ArrayList<>();
        for (int i = 0; i < listMaterial.size(); i++) {
            try {
                CsStationPosition csStationPosition = listMaterial.get(i);
                  if (StrUtil.isEmpty(csStationPosition.getLevelName())){
                      errorStrs.add("第 " + i + " 行：请选择层级类型，忽略导入。");
                      list.add(csStationPosition.setText("请选择层级类型，忽略导入"));
                      continue;
                  }else if ("二级".equals(csStationPosition.getLevelName())){
                      if(StrUtil.isNotEmpty(csStationPosition.getPUrl())){
                       CsLine csLine = csLineMapper.selectOne(new LambdaQueryWrapper<CsLine>()
                               .eq(CsLine::getLineName,csStationPosition.getPUrl())
                               .eq(CsLine::getDelFlag,0));
                       if (ObjectUtil.isEmpty(csLine)){
                           errorStrs.add("第 " + i + " 行：输入的上级节点找不到！请核对后输出，忽略导入。");
                           list.add(csStationPosition.setText("输入的上级节点找不到！请核对后输出，忽略导入"));
                           continue;
                       }else {
                           csStationPosition.setLineCode(csLine.getLineCode());
                           csStationPosition.setLineName(csLine.getLineName());
                           csStationPosition.setPid(csLine.getId());
                       }
                      }else {
                          errorStrs.add("第 " + i + " 行：请输入上级节点，忽略导入。");
                          list.add(csStationPosition.setText("请输入上级节点，忽略导入"));
                          continue;
                      }
                      if (StrUtil.isNotEmpty(csStationPosition.getPositionTypeName())){
                          List<DictModel> dictItems = sysBaseAPI.getDictItems("station_level_two");
                          dictItems.forEach(s -> {
                              if (s.getText().equals(csStationPosition.getPositionTypeName())) {
                                  csStationPosition.setPositionType(Integer.valueOf(s.getValue()));
                              }
                          });
                          if (ObjectUtil.isEmpty(csStationPosition.getPositionType())){
                              errorStrs.add("第 " + i + " 行：二级位置类型识别不出，忽略导入。");
                              list.add(csStationPosition.setText("二级位置类型识别不出，忽略导入"));
                              continue;
                          }
                      if(StrUtil.isNotEmpty(csStationPosition.getPositionName())){
                          List<CsStation> csStations = csStationMapper.selectList(new LambdaQueryWrapper<CsStation>()
                                  .eq(CsStation::getLineCode,csStationPosition.getLineCode())
                                  .eq(CsStation::getStationName,csStationPosition.getPositionName())
                                  .eq(CsStation::getDelFlag,0));
                          if (!csStations.isEmpty()){
                              errorStrs.add("第 " + i + " 行：二级节点位置名称重复，忽略导入。");
                              list.add(csStationPosition.setText("二级位置节点名称重复，忽略导入"));
                              continue;
                          }
                      }
                    }
                  }else if ("三级".equals(csStationPosition.getLevelName())) {
                          if (StrUtil.isNotEmpty(csStationPosition.getPUrl())) {
                              List<String> list1 = Arrays.asList(csStationPosition.getPUrl().split("-"));
                              if (list1.size()==2) {
                                  CsStation csStation = csStationMapper.selectOne(new LambdaQueryWrapper<CsStation>()
                                          .eq(CsStation::getLineName, list1.get(0))
                                          .eq(CsStation::getStationName, list1.get(1))
                                          .eq(CsStation::getDelFlag, 0));
                                  if (ObjectUtil.isEmpty(csStation)) {
                                      errorStrs.add("第 " + i + " 行：输入的上级节点找不到！请核对后输出，忽略导入。");
                                      list.add(csStationPosition.setText("输入的上级节点找不到！请核对后输出，忽略导入"));
                                      continue;
                                  } else {
                                      csStationPosition.setLineCode(csStation.getLineCode());
                                      csStationPosition.setLineName(csStation.getLineName());
                                      csStationPosition.setStaionCode(csStation.getStationCode());
                                      csStationPosition.setPid(csStation.getId());
                                  }
                              }else {
                                  errorStrs.add("第 " + i + " 行：三级上级节点请按规范输入，忽略导入。");
                                  list.add(csStationPosition.setText("三级上级节点请按规范输入，忽略导入"));
                                  continue;
                              }
                          } else {
                              errorStrs.add("第 " + i + " 行：请输入上级节点，忽略导入。");
                              list.add(csStationPosition.setText("请输入上级节点，忽略导入"));
                              continue;
                          }
                          if (StrUtil.isNotEmpty(csStationPosition.getPositionTypeName())) {
                              List<DictModel> dictItems = sysBaseAPI.getDictItems("station_level_three");
                              dictItems.forEach(s -> {
                                  if (s.getText().equals(csStationPosition.getPositionTypeName())) {
                                      csStationPosition.setPositionType(Integer.valueOf(s.getValue()));
                                  }
                              });
                              if (ObjectUtil.isEmpty(csStationPosition.getPositionType())) {
                                  errorStrs.add("第 " + i + " 行：三级位置类型识别不出，忽略导入。");
                                  list.add(csStationPosition.setText("三级位置类型识别不出，忽略导入"));
                                  continue;
                              }
                              if (StrUtil.isNotEmpty(csStationPosition.getPositionName())) {
                                  List<CsStationPosition> csStations = csStationPositionMapper.selectList(new LambdaQueryWrapper<CsStationPosition>()
                                          .eq(CsStationPosition::getLineCode, csStationPosition.getLineCode())
                                          .eq(CsStationPosition::getStaionCode, csStationPosition.getStaionCode())
                                          .eq(CsStationPosition::getPositionName, csStationPosition.getPositionName())
                                          .eq(CsStationPosition::getDelFlag, 0));
                                  if (!csStations.isEmpty()) {
                                      errorStrs.add("第 " + i + " 行：三级节点位置名称重复，忽略导入。");
                                      list.add(csStationPosition.setText("三级节点位置名称重复，忽略导入"));
                                      continue;
                                  }
                              }
                           }
                          } else if ("一级".equals(csStationPosition.getLevelName())) {
                              if (StrUtil.isNotEmpty(csStationPosition.getPositionTypeName())) {
                                  List<DictModel> dictItems = sysBaseAPI.getDictItems("station_level_one");
                                  dictItems.forEach(s -> {
                                      if (s.getText().equals(csStationPosition.getPositionTypeName())) {
                                          csStationPosition.setPositionType(Integer.valueOf(s.getValue()));
                                      }
                                  });
                                  if (ObjectUtil.isEmpty(csStationPosition.getPositionType())) {
                                      errorStrs.add("第 " + i + " 行：一级位置类型识别不出，忽略导入。");
                                      list.add(csStationPosition.setText("一级位置类型识别不出，忽略导入"));
                                      continue;
                                  }
                              } else {
                                  errorStrs.add("第 " + i + " 行：位置类型为空，忽略导入。");
                                  list.add(csStationPosition.setText("位置类型为空，忽略导入"));
                                  continue;
                              }
                              if (StrUtil.isNotEmpty(csStationPosition.getPositionName())) {
                                  List<CsLine> csStations = csLineMapper.selectList(new LambdaQueryWrapper<CsLine>()
                                          .eq(CsLine::getLineName, csStationPosition.getPositionName())
                                          .eq(CsLine::getDelFlag, 0));
                                  if (!csStations.isEmpty()) {
                                      errorStrs.add("第 " + i + " 行：一级节点位置名称重复，忽略导入。");
                                      list.add(csStationPosition.setText("一级节点位置名称重复，忽略导入"));
                                      continue;
                                  }
                              }
                          }
                          if (StrUtil.isNotEmpty(csStationPosition.getPositionCode())) {
                              List<CsLine> csLine = csLineMapper.selectCode(csStationPosition.getPositionCode());
                              if (!csLine.isEmpty()) {
                                  errorStrs.add("第 " + i + " 行：输入的位置编码重复，忽略导入。");
                                  list.add(csStationPosition.setText("输入的位置编码重复，忽略导入"));
                                  continue;
                              }
                          }
                          if ("一级".equals(csStationPosition.getLevelName())){
                              CsLine csLine = csLineController.entityChange(csStationPosition);
                              csLineMapper.insert(csLine);
                          }else if ("二级".equals(csStationPosition.getLevelName())){
                              CsStation csStation = csStationController.entityChange(csStationPosition);
                              csStationMapper.insert(csStation);
                          }else {
                              csStationPositionMapper.insert(csStationPosition);
                          }
                     } catch (Exception e) {
                             e.printStackTrace();
                }
            }
        if (list.size()>0){
            //创建导入失败错误报告,进行模板导出
            Resource resource = new ClassPathResource("templates/csStationPositionError.xlsx");
            InputStream resourceAsStream = resource.getInputStream();
            //2.获取临时文件
            File fileTemp= new File("templates/csStationPositionError.xlsx");
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
                lm.put("levelName",l.getLevelName());
                lm.put("positionName",l.getPositionName());
                lm.put("pUrl",l.getPUrl());
                lm.put("positionCode",l.getPositionCode());
                lm.put("positionTypeName",l.getPositionTypeName());
                lm.put("latitude",l.getLatitude());
                lm.put("longitude",l.getLongitude());
                lm.put("sort",l.getSort());
                lm.put("text",l.getText());
                mapList.add(lm);
            });
            Map<String, Object> errorMap = new HashMap<String, Object>();
            errorMap.put("maplist", mapList);
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams,errorMap);
            String fileName = "位置导入错误模板"+"_" + System.currentTimeMillis()+".xlsx";
            FileOutputStream out = new FileOutputStream(upLoadPath+ File.separator+fileName);
            String  url = fileName;
            workbook.write(out);
            errorLines+=errorStrs.size();
            successLines+=(listMaterial.size()-errorLines);
            return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorStrs,url);
        }
        errorLines += errorStrs.size();
        successLines += (listMaterial.size() - errorLines);
        return ImportExcelUtil.imporReturnRes(errorLines, successLines, errorStrs,null);
    }

}
