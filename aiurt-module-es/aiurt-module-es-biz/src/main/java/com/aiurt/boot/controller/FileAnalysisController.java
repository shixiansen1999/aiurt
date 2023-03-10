package com.aiurt.boot.controller;

import com.aiurt.boot.mapper.EsMapper;
import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.service.IFileAnalysisService;
import com.aiurt.boot.utils.ElasticsearchClientUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author cgkj
 * @Title:
 * @Description:
 * @date 2023/2/159:11
 */
@Api(tags = "文件数据解析保存")
@RestController
@RequestMapping("/file")
public class FileAnalysisController {

    @Autowired
    private IFileAnalysisService fileAnalysisService;


    @ApiOperation(value = "解析并保存上传文件数据", notes = "解析并保存上传文件数据")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Result<?> upload(@RequestParam @ApiParam(name = "file", value = "文件") MultipartFile file,
                            @ApiParam(name = "path", value = "文件存储地址") String path,
                            @ApiParam(name = "typeId", value = "文件类型ID") String typeId) throws IOException {
        String id = fileAnalysisService.upload(file, path, typeId);
        return Result.OK("保存成功！", id);
    }

    /**
     * 同步规范知识库的数据和文档数据
     */
    @ApiOperation(value = "同步规范知识库的数据和文档数据", notes = "同步规范知识库的数据和文档数据")
    @RequestMapping(value = "/syncCanonicalKnowledgeBase", method = RequestMethod.POST)
    public Result<?> syncCanonicalKnowledgeBase(HttpServletRequest request, HttpServletResponse response) {
        List<String> list = fileAnalysisService.syncCanonicalKnowledgeBase(request, response);
        if (CollUtil.isEmpty(list)) {
            return Result.ok("同步完成！");
        }
        return Result.OK("存在以下记录的文件同步失败！", list);
    }

    @ApiOperation(value = "同步数据到es", notes = "同步数据到es")
    @RequestMapping(value = "/syncData", method = RequestMethod.GET)
    public Result<?> syncData(String index) throws IOException {
        fileAnalysisService.syncData(index);
        return Result.OK("保存成功！");
    }

}
