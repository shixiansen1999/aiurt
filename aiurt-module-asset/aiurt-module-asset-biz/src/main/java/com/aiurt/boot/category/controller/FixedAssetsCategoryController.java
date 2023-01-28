package com.aiurt.boot.category.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.asset.entity.FixedAssets;
import com.aiurt.boot.asset.service.IFixedAssetsService;
import com.aiurt.boot.category.dto.FixedAssetsCategoryDTO;
import com.aiurt.boot.category.entity.FixedAssetsCategory;
import com.aiurt.boot.category.service.IFixedAssetsCategoryService;
import com.aiurt.boot.check.entity.FixedAssetsCheckCategory;
import com.aiurt.boot.check.service.IFixedAssetsCheckCategoryService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: fixed_assets_category
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
@Api(tags = "资产分类")
@RestController
@RequestMapping("/category/fixedAssetsCategory")
@Slf4j
public class FixedAssetsCategoryController extends BaseController<FixedAssetsCategory, IFixedAssetsCategoryService> {
    @Autowired
    private IFixedAssetsCategoryService fixedAssetsCategoryService;
    @Autowired
    private IFixedAssetsService fixedAssetsService;
    @Autowired
    private IFixedAssetsCheckCategoryService checkCategoryService;

    /**
     * 分页列表查询
     *
     * @param fixedAssetsCategory
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "资产分类-分页列表查询")
    @ApiOperation(value = "资产分类-分页列表查询", notes = "资产分类-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<FixedAssetsCategoryDTO>> queryPageList(FixedAssetsCategoryDTO fixedAssetsCategory,
                                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                               HttpServletRequest req) {
        Page<FixedAssetsCategoryDTO> pageList = new Page<>(pageNo, pageSize);
        Page<FixedAssetsCategoryDTO> list = fixedAssetsCategoryService.pageList(pageList, fixedAssetsCategory);
        return Result.OK(list);
    }

    /**
     * 资产分类-树形
     *
     * @return
     */
    @AutoLog(value = "资产分类-树形")
    @ApiOperation(value = "资产分类-树形", notes = "资产分类-树形")
    @GetMapping(value = "/getCategoryTreeList")
    public Result<List<FixedAssetsCategoryDTO>> getCategoryTree() {
        List<FixedAssetsCategoryDTO> list = fixedAssetsCategoryService.getCategoryTree();
        return Result.OK(list);
    }

    /**
     * 添加
     *
     * @param fixedAssetsCategory
     * @return
     */
    @AutoLog(value = "资产分类-添加")
    @ApiOperation(value = "资产分类-添加", notes = "资产分类-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody FixedAssetsCategory fixedAssetsCategory) {
        LambdaQueryWrapper<FixedAssetsCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FixedAssetsCategory::getCategoryCode, fixedAssetsCategory.getParentCode());
        if ("0".equals(fixedAssetsCategory.getParentCode())) {
            fixedAssetsCategory.setPid("0");
            fixedAssetsCategory.setLevel("1");
        } else {
            FixedAssetsCategory assetsCategory = fixedAssetsCategoryService.getOne(queryWrapper);
            fixedAssetsCategory.setPid(assetsCategory.getId());
            if(!"0".equals(assetsCategory.getPid())){
                fixedAssetsCategory.setLevel("3");
            }else {
                fixedAssetsCategory.setLevel("2");
            }
        }
        fixedAssetsCategoryService.save(fixedAssetsCategory);
        return Result.OK("添加成功！");
    }

    /**
     * 校验分类编码、分类名称
     *
     * @param fixedAssetsCategory
     * @return
     */
    @AutoLog(value = "资产分类-校验")
    @ApiOperation(value = "资产分类-校验", notes = "资产分类-校验")
    @PostMapping(value = "/checkCodeName")
    public Result<String> checkCodeName(@RequestBody FixedAssetsCategoryDTO fixedAssetsCategory) {
        return fixedAssetsCategoryService.checkCodeName(fixedAssetsCategory);
    }

    /**
     * 编辑
     *
     * @param fixedAssetsCategory
     * @return
     */
    @AutoLog(value = "资产分类-编辑")
    @ApiOperation(value = "资产分类-编辑", notes = "资产分类-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody FixedAssetsCategory fixedAssetsCategory) {
        fixedAssetsCategoryService.updateById(fixedAssetsCategory);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "资产分类-删除")
    @ApiOperation(value = "资产分类-删除", notes = "资产分类-删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id, @RequestParam(name = "code", required = true) String code) {
        List<FixedAssets> list = fixedAssetsService.list(new LambdaQueryWrapper<FixedAssets>().eq(FixedAssets::getCategoryCode, code));
        //是否被资产引用
        List<FixedAssetsCategory> categoryList = fixedAssetsCategoryService.list(new LambdaQueryWrapper<FixedAssetsCategory>().eq(FixedAssetsCategory::getPid, id));
        //是否被盘点任务引用
        List<FixedAssetsCheckCategory> checkCategoryList = checkCategoryService.list(new LambdaQueryWrapper<FixedAssetsCheckCategory>().eq(FixedAssetsCheckCategory::getCategoryCode, code));
        if (CollUtil.isNotEmpty(list)) {
            return Result.error("分类被引用不允许删除!");
        }
        if (CollUtil.isNotEmpty(categoryList)) {
            return Result.error("该分类下有子节点，请删除后再操作!");
        }
        if (CollUtil.isNotEmpty(checkCategoryList)) {
            return Result.error("分类被引用不允许删除!");
        }
        fixedAssetsCategoryService.removeById(id);
        return Result.OK("删除成功!");
    }

//	/**
//	 *  批量删除
//	 *
//	 * @param ids
//	 * @return
//	 */
//	@AutoLog(value = "资产分类-批量删除")
//	@ApiOperation(value="资产分类-批量删除", notes="资产分类-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.fixedAssetsCategoryService.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.OK("批量删除成功!");
//	}
//
//	/**
//	 * 通过id查询
//	 *
//	 * @param id
//	 * @return
//	 */
//	//@AutoLog(value = "fixed_assets_category-通过id查询")
//	@ApiOperation(value="fixed_assets_category-通过id查询", notes="fixed_assets_category-通过id查询")
//	@GetMapping(value = "/queryById")
//	public Result<FixedAssetsCategory> queryById(@RequestParam(name="id",required=true) String id) {
//		FixedAssetsCategory fixedAssetsCategory = fixedAssetsCategoryService.getById(id);
//		if(fixedAssetsCategory==null) {
//			return Result.error("未找到对应数据");
//		}
//		return Result.OK(fixedAssetsCategory);
//	}

    /**
     * 导出excel
     *
     * @param request
     * @param categoryDTO
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FixedAssetsCategoryDTO categoryDTO) {
        List<FixedAssetsCategoryDTO> list = fixedAssetsCategoryService.getCategoryList(categoryDTO);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "固定资产分类");
        mv.addObject(NormalExcelConstants.CLASS, FixedAssetsCategoryDTO.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("固定资产分类", "固定资产分类导出信息", ExcelType.XSSF));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response)throws IOException {
        return fixedAssetsCategoryService.importExcel(request, response);
    }

    /**
     * 下载模板
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/downloadTemple", method = RequestMethod.GET)
    public void downloadTemple(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取输入流，原始模板位置
        Resource resource = new ClassPathResource("/templates/fixedAssetsCategory.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/fixedAssetsCategory.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        String path = fileTemp.getAbsolutePath();
        TemplateExportParams exportParams = new TemplateExportParams(path);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
        Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
        String fileName = "固定资产分类导入模板.xlsx";
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setHeader("Content-Disposition", "attachment;filename=" + "固定资产分类导入模板.xlsx");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
