package com.aiurt.boot.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.EsFileAPI;
import com.aiurt.boot.constant.EsConstant;
import com.aiurt.boot.mapper.EsMapper;
import com.aiurt.boot.mapper.FileAnalysisMapper;
import com.aiurt.boot.service.IFileAnalysisService;
import com.aiurt.boot.utils.ElasticsearchClientUtil;
import com.aiurt.boot.utils.ElasticsearchClientUtil;
import com.aiurt.modules.search.dto.FaultKnowledgeBaseDTO;
import com.aiurt.common.constant.SymbolConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.MinioUtil;
import com.aiurt.modules.basic.entity.SysAttachment;
import com.aiurt.modules.search.dto.FaultKnowledgeBaseDTO;
import com.aiurt.modules.search.dto.FileDataDTO;
import com.aiurt.modules.search.entity.FileAnalysisData;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * @author cgkj
 * @Title:
 * @Description:
 * @date 2023/2/159:11
 */
@Slf4j
@Service
public class FileAnalysisServiceImpl implements IFileAnalysisService, EsFileAPI {

    @Value(value = "${jeecg.path.upload}")
    private String uploadPath;

    @Autowired
    private FileAnalysisMapper fileAnalysisMapper;

    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;
    @Autowired
    private ElasticsearchClientUtil elasticsearchClientUtil;
    @Autowired
    private EsMapper esMapper;

    @Override
    public String upload(MultipartFile file, String path, String typeId) {
        FileAnalysisData analysisData = new FileAnalysisData();
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        String id = snowflake.nextIdStr();
        String fileName = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(fileName);
        try {
            //将文件内容转化为base64编码
            byte[] bytes = file.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            // 去除空格换行符等
//            String content = StrUtil.removeAllLineBreaks(new String(bytes));
//            String base64Content = Base64.getEncoder().encodeToString(StrUtil.bytes(content));

            analysisData.setId(id);
            analysisData.setName(fileName);
            analysisData.setFormat(suffix);
            analysisData.setTypeId(typeId);
            analysisData.setAddress(path);
            analysisData.setContent(base64);

            this.saveData(analysisData);
        } catch (IOException e) {
            log.error("文件解析或保存异常!", e.getMessage());
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public List<String> syncCanonicalKnowledgeBase(HttpServletRequest request, HttpServletResponse response) {
        // 查询数据库获取规范知识库的记录
        List<FileDataDTO> files = fileAnalysisMapper.syncCanonicalKnowledgeBase();
        List<String> error = new ArrayList<>();
        // 根据记录的文件地址获取文件并解析
        FileAnalysisData analysis = null;
        for (FileDataDTO file : files) {
            analysis = new FileAnalysisData();
            analysis.setId(file.getId());
            analysis.setAddress(file.getAddress());
            analysis.setTypeId(file.getTypeId());
            analysis.setName(file.getName());
            analysis.setFormat(file.getFormat());
            String content = null;
            try {
                if (Arrays.asList("xls", "xlsx", "doc", "docx", "pdf", "txt").contains(file.getFormat().toLowerCase())) {
                    byte[] bytes = this.getBytes(file);
                    content = this.byteEncodeToString(bytes);
                    analysis.setContent(content);
                } else {
                    content = this.byteEncodeToString("".getBytes());
                }
            } catch (Exception e) {
                content = this.byteEncodeToString("".getBytes());
            }
            analysis.setContent(content);
            IndexRequest indexRequest = this.extractingFiles(analysis);
            IndexResponse indexResponse = null;
            try {
                client.index(indexRequest, RequestOptions.DEFAULT);
            } catch (Exception e) {
                try {
                    // 文件内容存在问题时，保存其他字段数据
                    analysis.setContent(this.byteEncodeToString("".getBytes()));
                    client.index(this.extractingFiles(analysis), RequestOptions.DEFAULT);
                } catch (Exception ex) {
                }
                log.debug("记录的文件异常！ID:{}", file.getId());
                error.add(file.getId());
            }
        }
        return error;
    }

    private byte[] getBytes(FileDataDTO fileData) throws IOException {
        byte[] bytes = null;
        String attName = null;
        String filePath = null;
        String url = fileData.getAddress();
        filePath = StrUtil.subBefore(url, "?", false);

        filePath = filePath.replace("..", "").replace("../", "");
        if (filePath.endsWith(SymbolConstant.COMMA)) {
            filePath = filePath.substring(0, filePath.length() - 1);
        }
        SysAttachment sysAttachment = iSysBaseAPI.getFilePath(filePath);
        InputStream inputStream = null;

        if (Objects.isNull(sysAttachment)) {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new AiurtBootException("文件不存在..");
            }
            if (StrUtil.isBlank(fileData.getName())) {
                attName = file.getName();
            } else {
                attName = fileData.getName();
            }
            inputStream = new BufferedInputStream(new FileInputStream(filePath));
            bytes = StreamUtils.copyToByteArray(inputStream);
            //关闭流
            inputStream.close();
        } else {
            if (StrUtil.equalsIgnoreCase("minio", sysAttachment.getType())) {
                inputStream = MinioUtil.getMinioFile("platform", sysAttachment.getFilePath());
            } else {
                String imgPath = uploadPath + File.separator + sysAttachment.getFilePath();
                File file = new File(imgPath);
                if (!file.exists()) {
                    throw new RuntimeException("文件[" + imgPath + "]不存在..");
                }
                inputStream = new BufferedInputStream(new FileInputStream(imgPath));
            }
            bytes = StreamUtils.copyToByteArray(inputStream);
            //关闭流
            inputStream.close();
        }
        return bytes;
    }

    @Override
    public void syncData(String index) {
        List<FaultKnowledgeBaseDTO> list = esMapper.selectList();
        elasticsearchClientUtil.createBulkDocument(index,list);
    }

    /**
     * 保存文件数据
     *
     * @param analysisData
     * @return
     * @throws IOException
     */
    public IndexResponse saveData(FileAnalysisData analysisData) throws IOException {
        IndexRequest request = this.extractingFiles(analysisData);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        return response;
    }

    @Override
    public IndexResponse saveFileData(FileDataDTO fileDataDTO) {
        byte[] fileBytes = fileDataDTO.getFileBytes();
        String content = this.byteEncodeToString(fileBytes);

        FileAnalysisData analysisData = new FileAnalysisData();
        analysisData.setId(fileDataDTO.getId());
        analysisData.setAddress(fileDataDTO.getAddress());
        analysisData.setTypeId(fileDataDTO.getTypeId());
        analysisData.setName(fileDataDTO.getName());
        analysisData.setFormat(fileDataDTO.getFormat());
        analysisData.setContent(content);
        IndexRequest request = this.extractingFiles(analysisData);
        IndexResponse response = null;
        try {
            response = client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 更新文件数据
     *
     * @param fileDataDTO
     * @return
     * @throws IOException
     */
    @Override
    public UpdateResponse updateFileData(FileDataDTO fileDataDTO) {
        FileAnalysisData analysisData = new FileAnalysisData();
        byte[] fileBytes = fileDataDTO.getFileBytes();
        String content = this.byteEncodeToString(fileBytes);

        analysisData.setId(fileDataDTO.getId());
        analysisData.setAddress(fileDataDTO.getAddress());
        analysisData.setTypeId(fileDataDTO.getTypeId());
        analysisData.setName(fileDataDTO.getName());
        analysisData.setFormat(fileDataDTO.getFormat());
        analysisData.setContent(content);

        IndexRequest indexRequest = this.extractingFiles(analysisData);
        UpdateRequest request = new UpdateRequest(EsConstant.FILE_DATA_INDEX, analysisData.getId())
                .doc(indexRequest);
        UpdateResponse response = null;
        try {
            response = client.update(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }


    private String byteEncodeToString(byte[] bytes) {
//        // 去除空格换行符等
//        String content = StrUtil.removeAllLineBreaks(new String(bytes));
//        String base64Content = Base64.getEncoder().encodeToString(StrUtil.bytes(content));
        String base64Content = Base64.getEncoder().encodeToString(bytes);
        return base64Content;
    }

    /**
     * 使用attachment pipline进行提取文件
     *
     * @param analysisData
     */
    private IndexRequest extractingFiles(FileAnalysisData analysisData) {
        IndexRequest indexRequest = new IndexRequest(EsConstant.FILE_DATA_INDEX);
//        IndexRequest indexRequest = new IndexRequest("regulation_knowledge_base");
        indexRequest.id(analysisData.getId());
        // 使用attachment pipline进行提取文件
        indexRequest.source(JSON.toJSONString(analysisData), XContentType.JSON);
        indexRequest.setPipeline("attachment");
        return indexRequest;
    }
}
