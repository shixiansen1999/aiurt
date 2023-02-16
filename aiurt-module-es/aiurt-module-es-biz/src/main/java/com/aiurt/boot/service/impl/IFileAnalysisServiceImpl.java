package com.aiurt.boot.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.aiurt.boot.constant.EsConstant;
import com.aiurt.modules.search.entity.FileAnalysisData;
import com.aiurt.boot.service.IFileAnalysisService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Slf4j
@Service
public class IFileAnalysisServiceImpl implements IFileAnalysisService {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Override
    public String upload(MultipartFile file, String path) {
        FileAnalysisData analysisData = new FileAnalysisData();
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        String id = snowflake.nextIdStr();
        String fileName = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(fileName);
        try {
            //将文件内容转化为base64编码
            byte[] bytes = file.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);

            analysisData.setId(id);
            analysisData.setName(fileName);
            analysisData.setType(suffix);
            analysisData.setContent(base64);
            analysisData.setAddress(path);

            this.saveData(analysisData);
        } catch (IOException e) {
            log.error("文件解析或保存异常!", e.getMessage());
            e.printStackTrace();
        }
        return id;
    }

    /**
     * 保存文件数据
     *
     * @param analysisData
     * @return
     * @throws IOException
     */
    public IndexResponse saveData(FileAnalysisData analysisData) throws IOException {
        IndexRequest request = new IndexRequest(EsConstant.FILE_DATA_INDEX);
        request.id(analysisData.getId());
        // 上传同时，使用attachment pipline进行提取文件
        request.source(JSON.toJSONString(analysisData), XContentType.JSON);
        request.setPipeline("attachment");
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        return response;
    }
}
