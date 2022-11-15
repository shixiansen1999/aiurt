package com.aiurt.modules.major.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.entity.vo.CsMajorImportVO;
import com.aiurt.modules.major.mapper.CsMajorMapper;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.system.controller.SysDictController;
import com.aiurt.modules.train.utils.DlownTemplateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.TemplateExportParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: cs_major
 * @Author: jeecg-boot
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@Service
public class CsMajorServiceImpl extends ServiceImpl<CsMajorMapper, CsMajor> implements ICsMajorService {
    @Autowired
    private CsMajorMapper csMajorMapper;
    @Autowired
    private SysDictController sysDictController;

    @Value("${jeecg.path.upload}")
    String filepath;

    /**
     * 添加
     *
     * @param csMajor
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(CsMajor csMajor) {
        //专业编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsMajor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsMajor::getMajorCode, csMajor.getMajorCode());
        queryWrapper.eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsMajor> list = csMajorMapper.selectList(queryWrapper);
        if (!list.isEmpty()) {
            return Result.error("专业编码重复，请重新填写！");
        }
        //专业名称不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsMajor> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsMajor::getMajorName, csMajor.getMajorName());
        nameWrapper.eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsMajor> nameList = csMajorMapper.selectList(nameWrapper);
        if (!nameList.isEmpty()) {
            return Result.error("专业名称重复，请重新填写！");
        }
        csMajorMapper.insert(csMajor);
        return Result.OK("添加成功！");
    }

    /**
     * 修改
     *
     * @param csMajor
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(CsMajor csMajor) {
        //专业编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsMajor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsMajor::getMajorCode, csMajor.getMajorCode());
        queryWrapper.eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsMajor> list = csMajorMapper.selectList(queryWrapper);
        if (!list.isEmpty() && !list.get(0).getId().equals(csMajor.getId())) {
            return Result.error("专业编码重复，请重新填写！");
        }
        //专业名称不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsMajor> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsMajor::getMajorName, csMajor.getMajorName());
        nameWrapper.eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsMajor> nameList = csMajorMapper.selectList(nameWrapper);
        if (!nameList.isEmpty() && !nameList.get(0).getId().equals(csMajor.getId())) {
            return Result.error("专业名称重复，请重新填写！");
        }
        csMajorMapper.updateById(csMajor);
        //刷新缓存
        sysDictController.refleshCache();
        return Result.OK("编辑成功！");
    }


    @Override
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        // 错误信息
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0, errorLines = 0;
        String url = null;
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                return imporReturnRes(errorLines, successLines, errorMessage, false,url);
            }
            ImportParams params = new ImportParams();
            params.setTitleRows(1);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<CsMajorImportVO> csMajorList = ExcelImportUtil.importExcel(file.getInputStream(), CsMajorImportVO.class, params);
                List<CsMajor> list = new ArrayList<>();
                for (int i = 0; i < csMajorList.size(); i++) {
                    CsMajorImportVO csMajorImportVO = csMajorList.get(i);
                    String s = decideIsNull(csMajorImportVO);
                    if(ObjectUtil.isNotEmpty(s))
                    {
                        csMajorImportVO.setWrongReason(s);
                        errorLines++;
                    }
                    CsMajor csMajor = new CsMajor();
                    BeanUtils.copyProperties(csMajorImportVO, csMajor);
                    list.add(csMajor);
                    successLines++;

                }
                if (errorLines == 0) {
                    for (CsMajor csMajor : list) {
                        csMajorMapper.insert(csMajor);
                    }

                } else {
                    successLines = 0;
                    URL resource = DlownTemplateUtil.class.getResource("/templates/csMajorImportVO.xlsx");
                    String path = resource.getPath();
                    TemplateExportParams exportParams = new TemplateExportParams(path);
                    Map<String, Object> errorMap = new HashMap<String, Object>();
                    errorMap.put("title", "专业信息导入错误清单");
                    List<Map<String, Object>> listMap = new ArrayList<>();
                    for (CsMajorImportVO dto : csMajorList) {
                        //获取一条排班记录
                        Map<String, Object> lm = new HashMap<String, Object>();
                        //错误报告获取信息
                        lm.put("majorcode", dto.getMajorCode());
                        lm.put("majorname", dto.getMajorName());
                        lm.put("mistake", dto.getWrongReason());
                        listMap.add(lm);
                    }
                    errorMap.put("maplist", listMap);
                    Workbook workbook = ExcelExportUtil.exportExcel(exportParams, errorMap);
                    String filename = "专业信息导入错误清单"+"_" + System.currentTimeMillis()+"."+type;
                    FileOutputStream out = new FileOutputStream(filepath+ File.separator+filename);
                    url =filename;
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
        }
        return imporReturnRes(errorLines, successLines, errorMessage,true,url);

    }


    private String decideIsNull(CsMajorImportVO csMajorImportVO) {
        if (csMajorImportVO.getMajorCode() == null && csMajorImportVO.getMajorName() == null) {
            return "必填字段为空";
        } else if (csMajorImportVO.getMajorCode() != null && csMajorImportVO.getMajorName() == null) {
            CsMajor csMajor = csMajorMapper.selectOne(new QueryWrapper<CsMajor>().lambda().eq(CsMajor::getMajorCode, csMajorImportVO.getMajorCode()).eq(CsMajor::getDelFlag, 0));
            if (csMajor != null) {
                return "必填字段为空;专业编码重复";
            } else {
                return "必填字段为空";
            }
        }
        else if (csMajorImportVO.getMajorCode() == null && csMajorImportVO.getMajorName() != null) {
            CsMajor csMajor = csMajorMapper.selectOne(new QueryWrapper<CsMajor>().lambda().eq(CsMajor::getMajorName, csMajorImportVO.getMajorName()).eq(CsMajor::getDelFlag, 0));
            if (csMajor != null) {
                return "必填字段为空;专业名称重复";
            } else {
                return "必填字段为空";
            }
        }
        else if (csMajorImportVO.getMajorCode() != null && csMajorImportVO.getMajorName() != null) {
            CsMajor csMajorCode = csMajorMapper.selectOne(new QueryWrapper<CsMajor>().lambda().eq(CsMajor::getMajorName, csMajorImportVO.getMajorName()).eq(CsMajor::getDelFlag, 0));
            CsMajor csMajorName = csMajorMapper.selectOne(new QueryWrapper<CsMajor>().lambda().eq(CsMajor::getMajorCode, csMajorImportVO.getMajorCode()).eq(CsMajor::getDelFlag, 0));
            if (csMajorCode != null&&csMajorName!=null) {
                return "专业编码重复;专业名称重复";
            }
            if (csMajorCode != null&&csMajorName==null) {
                return "专业编码重复";
            }
            if (csMajorCode == null&&csMajorName!=null) {
                return "专业名称重复";
            }
        }
        return null;
    }

    public static Result<?> imporReturnRes(int errorLines, int successLines, List<String> errorMessage, boolean isType,String failReportUrl ) throws IOException {
        if (isType) {
            if (errorLines != 0) {
                JSONObject result = new JSONObject(5);
                result.put("isSucceed", false);
                result.put("errorCount", errorLines);
                result.put("successCount", successLines);
                int totalCount = successLines + errorLines;
                result.put("totalCount", totalCount);
                result.put("failReportUrl", failReportUrl);
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
        } else {
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
    public String importErrorExcel(HttpServletResponse response, List<CsMajorImportVO> scheduleDate, String type) {
        //创建导入失败错误报告,进行模板导出


        return null;
    }
}
