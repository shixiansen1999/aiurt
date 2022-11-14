package com.aiurt.modules.manufactor.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.manufactor.entity.vo.CsManuFactorImportVo;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.manufactor.mapper.CsManufactorMapper;
import com.aiurt.modules.manufactor.service.ICsManufactorService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: cs_manufactor
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class CsManufactorServiceImpl extends ServiceImpl<CsManufactorMapper, CsManufactor> implements ICsManufactorService {
    @Autowired
    private CsManufactorMapper csManufactorMapper;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;
    /**
     * 添加
     *
     * @param csManufactor
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(CsManufactor csManufactor) {
        //判断厂商编码是否重复
		LambdaQueryWrapper<CsManufactor> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CsManufactor::getCode, csManufactor.getCode());
        queryWrapper.eq(CsManufactor::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<CsManufactor> list = csManufactorMapper.selectList(queryWrapper);
		if (!list.isEmpty()) {
			return Result.error("厂商编码重复，请重新填写！");
		}
        //判断厂商名称是否重复
        LambdaQueryWrapper<CsManufactor> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsManufactor::getName, csManufactor.getName());
        queryWrapper.eq(CsManufactor::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsManufactor> nameList = csManufactorMapper.selectList(nameWrapper);
        if (!nameList.isEmpty()) {
            return Result.error("厂商名称重复，请重新填写！");
        }
        csManufactorMapper.insert(csManufactor);
        return Result.OK("添加成功！");
    }
    /**
     * 修改
     *
     * @param csManufactor
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(CsManufactor csManufactor) {
        CsManufactor manufactor = getById(csManufactor.getId());
        //如果附件修改了，则删除附件表
        if(null!=csManufactor.getFilePath() && !manufactor.getFilePath().equals(csManufactor.getFilePath())){
            //删除附件表 todo
        }
        //判断厂商编码是否重复
        LambdaQueryWrapper<CsManufactor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsManufactor::getCode, csManufactor.getCode());
        queryWrapper.eq(CsManufactor::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsManufactor> list = csManufactorMapper.selectList(queryWrapper);
        if (!list.isEmpty() && list.get(0).equals(csManufactor.getId())) {
            return Result.error("厂商编码重复，请重新填写！");
        }
        //判断厂商名称是否重复
        LambdaQueryWrapper<CsManufactor> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsManufactor::getName, csManufactor.getName());
        queryWrapper.eq(CsManufactor::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsManufactor> nameList = csManufactorMapper.selectList(nameWrapper);
        if (!nameList.isEmpty() && nameList.get(0).equals(csManufactor.getId())) {
            return Result.error("厂商名称重复，请重新填写！");
        }
        csManufactorMapper.updateById(csManufactor);
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
                return imporReturnRes(errorLines, successLines, errorMessage,false,url);
            }
            ImportParams params = new ImportParams();
            params.setTitleRows(1);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<CsManuFactorImportVo> csList = ExcelImportUtil.importExcel(file.getInputStream(), CsManuFactorImportVo.class, params);
                List<CsManuFactorImportVo> csManuFactorList = csList.stream().filter(item -> existFieldNotEmpty(item)).collect(Collectors.toList());

                List<CsManufactor> list = new ArrayList<>();
                for (int i = 0; i < csManuFactorList.size(); i++) {
                    CsManuFactorImportVo csManuFactorImportVo = csManuFactorList.get(i);
                    boolean error = true;
                    StringBuffer sb = new StringBuffer();
                    if (ObjectUtil.isNull(csManuFactorImportVo.getName())) {
                        errorMessage.add("厂商名称为必填项，忽略导入");
                        sb.append("厂商名称为必填项;");
                        errorLines++;
                        error = false;
                    }else {
                        CsManufactor csManufactor = csManufactorMapper.selectOne(new QueryWrapper<CsManufactor>().lambda().eq(CsManufactor::getCode, csManuFactorImportVo.getCode()).eq(CsManufactor::getDelFlag, 0));
                        if (csManufactor != null) {
                            errorMessage.add(csManuFactorImportVo.getCode() + "厂商编码已经存在，忽略导入");
                            sb.append("厂商编码已经存在;");
                            if(error) {
                                errorLines++;
                                error=false;
                            }
                        }
                    }
                    if (ObjectUtil.isNull(csManuFactorImportVo.getLevel())) {
                        errorMessage.add("厂商等级为必填项，忽略导入");
                        sb.append("厂商等级为必填项");
                        if(error){
                            errorLines++;
                        }
                    }
                    csManuFactorImportVo.setErrorCause(String.valueOf(sb));
                    CsManufactor csManufactor = new CsManufactor();
                    BeanUtils.copyProperties(csManuFactorImportVo, csManufactor);
                    list.add(csManufactor);
                    successLines++;
                }
                if(errorLines==0) {
                    for (CsManufactor csManufactor : list) {
                        csManufactorMapper.insert(csManufactor);
                    }
                } else {
                    successLines =0;
                    List<CsManuFactorImportVo> exportList =new ArrayList<>();
                    ModelAndView model = new ModelAndView(new JeecgEntityExcelView());
                    model.addObject(NormalExcelConstants.FILE_NAME, "下载错误模板");
                    //excel注解对象Class
                    model.addObject(NormalExcelConstants.CLASS, CsManuFactorImportVo.class);
                    //自定义表格参数
                    ExportParams  exportParams =new ExportParams("错误清单模板", "错误清单模板");
                    model.addObject(NormalExcelConstants.PARAMS,exportParams );
                    //导出数据列表
                    model.addObject(NormalExcelConstants.DATA_LIST, csManuFactorList);
                    Map<String, Object> model1 = model.getModel();
                    // 生成错误excel
                    Workbook workbook = ExcelExportUtil.exportExcel((ExportParams)model1.get("params"), (Class)model1.get("entity"), (Collection)model1.get("data"));
                    // 写到文件中
                    String filename = "厂商信息导入错误清单"+"_" + System.currentTimeMillis()+"."+type;
                    FileOutputStream out = new FileOutputStream(upLoadPath+ File.separator+filename);
                    workbook.write(out);
                    url =filename;
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

    /**
     * 校验字段属性是否存在不为空字段
     *
     * @param
     * @return
     */
    private static <T> boolean existFieldNotEmpty(T t) {
        if (ObjectUtil.isEmpty(t)) {
            return false;
        }
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (ObjectUtil.isNotEmpty(field.get(t))) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static Result<?> imporReturnRes(int errorLines,int successLines,List<String> errorMessage,boolean isType,String failReportUrl) throws IOException {
        if (isType) {
            if (errorLines != 0) {
                JSONObject result = new JSONObject(5);
                result.put("isSucceed", false);
                result.put("errorCount", errorLines);
                result.put("successCount", successLines);
                int totalCount = successLines + errorLines;
                result.put("totalCount", totalCount);
                result.put("failReportUrl",failReportUrl);
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
}
