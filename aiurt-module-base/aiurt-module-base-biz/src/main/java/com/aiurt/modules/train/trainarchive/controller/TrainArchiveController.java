package com.aiurt.modules.train.trainarchive.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.train.trainarchive.dto.TrainArchiveDTO;
import com.aiurt.modules.train.trainarchive.entity.TrainArchive;
import com.aiurt.modules.train.trainarchive.service.ITrainArchiveService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: train_archive
 * @Author: aiurt
 * @Date: 2023-06-25
 * @Version: V1.0
 */
@Api(tags = "培训档案")
@RestController
@RequestMapping("/trainarchive/trainarchive")
@Slf4j
public class TrainArchiveController extends BaseController<TrainArchive, ITrainArchiveService> {
    @Autowired
    private ITrainArchiveService trainArchiveService;

    /**
     * 分页列表查询
     *
     * @param trainArchiveDTO 查询参数
     * @param pageNo          页码
     * @param pageSize        页数
     * @return list
     */
    @AutoLog(value = "培训档案-分页列表查询")
    @ApiOperation(value = "培训档案-分页列表查询", notes = "培训档案-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<TrainArchiveDTO>> queryPageList(TrainArchiveDTO trainArchiveDTO,
                                                        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<TrainArchiveDTO> page = new Page<>(pageNo, pageSize);
        IPage<TrainArchiveDTO> pageList = trainArchiveService.pageList(page, trainArchiveDTO);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param trainArchiveDTO 添加的数据
     * @return 结果集
     */
    @AutoLog(value = "培训档案-添加")
    @ApiOperation(value = "培训档案-添加", notes = "培训档案-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody TrainArchiveDTO trainArchiveDTO) {
        return trainArchiveService.add(trainArchiveDTO);
    }

    /**
     * 编辑
     *
     * @param trainArchiveDTO 编辑的数据
     * @return 结果集
     */
    @AutoLog(value = "培训档案-编辑")
    @ApiOperation(value = "培训档案-编辑", notes = "培训档案-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody TrainArchiveDTO trainArchiveDTO) {
        return trainArchiveService.edit(trainArchiveDTO);
    }

    /**
     * 冻结
     *
     * @param id 编辑的数据
     * @return 结果集
     */
    @AutoLog(value = "培训档案-冻结")
    @ApiOperation(value = "培训档案-冻结", notes = "培训档案-冻结")
    @PutMapping(value = "/freeze")
    public Result<String> freeze(String id) {
        TrainArchive trainArchive = trainArchiveService.getById(id);
        trainArchive.setStatus(2);
        trainArchiveService.updateById(trainArchive);
        return Result.OK("冻结成功!");
    }

    /**
     * 删除
     *
     * @param id 删除的id
     * @return 结果集
     */
    @AutoLog(value = "培训档案-通过id删除")
    @ApiOperation(value = "培训档案-通过id删除", notes = "培训档案-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id") String id) {
        return trainArchiveService.delete(id);
    }

    /**
     * 批量删除
     *
     * @param ids 档案ids
     * @return 结果集
     */
    @AutoLog(value = "train_archive-批量删除")
    @ApiOperation(value = "train_archive-批量删除", notes = "train_archive-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids") String ids) {
        this.trainArchiveService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id 档案id
     * @return 档案信息
     */
    @AutoLog(value = "培训档案-通过id查询")
    @ApiOperation(value = "培训档案-通过id查询", notes = "培训档案-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<TrainArchiveDTO> queryById(@RequestParam(name = "id") String id) {
        return trainArchiveService.queryById(id);
    }
    /**
     * 下载模板
     *
     * @param response 响应参数
     */
    @AutoLog(value = "培训档案-下载模板")
    @ApiOperation(value = "培训档案-下载模板", notes = "培训档案-下载模板")
    @RequestMapping(value = "/downloadTemple", method = RequestMethod.GET)
    public void downloadTemple(HttpServletResponse response) throws IOException {
        //获取输入流，原始模板位置
        Resource resource = new ClassPathResource("/templates/trainarchivetemple.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/trainarchivetemple.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
            String path = fileTemp.getAbsolutePath();
            TemplateExportParams exportParams = new TemplateExportParams(path);
            Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
            Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
            String fileName = "个人培训档案.xlsx";
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setHeader("Content-Disposition", "attachment;filename=" + "个人培训档案导入模板.xlsx");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
    /**
     * 导出excel
     *
     * @param request 请求参数
     * @param response 响应参数
     * @param archiveDTO 查询
     */
    @AutoLog(value = "培训档案-导出")
    @ApiOperation(value = "培训档案-导出", notes = "培训档案-导出")
    @RequestMapping(value = "/exportXls")
    public Result<String> exportXls(HttpServletRequest request,HttpServletResponse response, TrainArchiveDTO archiveDTO) throws IOException {
        return trainArchiveService.exportXls(request,response, archiveDTO);
    }

    /**
     * 通过excel导入数据
     *
     * @param request 请求参数
     * @param response 响应参数
     * @return 结果集
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return trainArchiveService.importExcel(request, response);
    }

}
