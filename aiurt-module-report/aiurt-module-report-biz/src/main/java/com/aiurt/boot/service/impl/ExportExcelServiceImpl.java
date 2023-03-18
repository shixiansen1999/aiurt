package com.aiurt.boot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.aiurt.boot.dto.ExcelExportDTO;
import com.aiurt.boot.entity.HeadInfo;
import com.aiurt.boot.service.ExportExcelService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @Author CJB
 * @Description: Excel导出
 */
@Slf4j
@Service
public class ExportExcelServiceImpl implements ExportExcelService {

    @Override
    public void exportExcel(HttpServletRequest request, HttpServletResponse response, ExcelExportDTO excelExportDTO) {
        List<HeadInfo> headInfos = excelExportDTO.getHeadInfos();
        LinkedHashMap<String, String> fieldMap = new LinkedHashMap<>();
        List<List<String>> buildHead = this.buildHead(headInfos, new Stack<>(), new LinkedList<>(), fieldMap);
        String excelName = excelExportDTO.getExcelName();
        if (StrUtil.isEmpty(excelName)) {
            excelName = "download";
        }

        String dataStr = this.getUrlData(excelExportDTO);
        List<LinkedList<String>> exportData = new LinkedList<>();
        Result result = JSONObject.parseObject(dataStr, Result.class);
        Object obj = result.getResult();
        if (Integer.valueOf(200).equals(result.getCode()) && ObjectUtil.isNotEmpty(obj)) {
            JSONArray jsonArray = new JSONArray();
            if (obj instanceof JSONArray) {
                // 返回结果是列表
                jsonArray = (JSONArray) obj;
            } else {
                // 返回结果是分页列表
                JSONObject parseObject = JSONObject.parseObject(JSONObject.toJSONString(obj));
                if (0 < Optional.ofNullable(parseObject.getInteger("size")).orElse(0)) {
                    jsonArray = Optional.ofNullable(parseObject.getJSONArray("records")).orElseGet(JSONArray::new);
                }
            }
            for (Object data : jsonArray) {
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(data));
                exportData.add(new LinkedList<String>() {
                    {
                        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
                            Object obj = jsonObject.get(entry.getKey());
                            String value = ObjectUtil.isNotEmpty(obj) ? String.valueOf(obj) : "";
                            add(value);
                        }
                    }
                });
            }
        }
        try {
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            // 防止中文乱码
            String fileName = URLEncoder.encode(excelName, "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream()).head(buildHead).sheet(excelExportDTO.getSheetName()).doWrite(exportData);
        } catch (IOException e) {
            log.error("导出Excel文档发生异常！", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 请求数据地址获取数据
     *
     * @param excelExportDTO
     * @return
     */
    private String getUrlData(ExcelExportDTO excelExportDTO) {
        String dataUrl = excelExportDTO.getDataUrl();
        String url = this.urlHandling(dataUrl);
        HttpRequest httpRequest = HttpUtil.createGet(url);
        if (RequestMethod.POST.name().equalsIgnoreCase(excelExportDTO.getMethod())) {
            httpRequest = HttpUtil.createPost(excelExportDTO.getDataUrl());
            // GET请求消息中的有效负载（即 body）没有定义的语义；在GET请求上发送有效负载主体可能会导致某些现有实现拒绝该请求。
            if (StrUtil.isNotEmpty(excelExportDTO.getReqBody())) {
                httpRequest.body(excelExportDTO.getReqBody());
            }
        }
        httpRequest.header("Content-Type", "application/json")
                .header("X-Access-Token", excelExportDTO.getToken());
        String dataStr = httpRequest.execute().body();
        return dataStr;
    }

    /**
     * 地址处理
     *
     * @param dataUrl
     * @return
     */
    private String urlHandling(String dataUrl) {
        // 处理前端传过来为空或空字符串的参数
        String[] url = StrUtil.split(dataUrl, "?");
        if (StrUtil.isBlank(url[1])) {
            return url[0];
        }
        //处理前端传多个？号和重复参数的情况
        IdentityHashMap<String, Object> map = new IdentityHashMap<>(16);
        //Map<String, Object> map = new HashMap<String, Object>(16);
        for (int k=1;k< url.length;k++){
            String[] params = url[k].split("&");
            for (int i = 0; i < params.length; i++) {
                String[] param = params[i].split("=");
                if (2 == param.length) {
                    map.put(param[0], param[1]);
                }
            }
        }
        String urlParam = URLUtil.buildQuery(map, Charset.defaultCharset());
        String fullPath = url[0] + "?" + urlParam;
        return fullPath;
    }

    /**
     * 表头构造
     *
     * @param headInfos
     * @param stack
     * @param list
     * @return
     */
    private List<List<String>> buildHead(List<HeadInfo> headInfos,
                                         Stack<String> stack,
                                         LinkedList<List<String>> list,
                                         LinkedHashMap<String, String> map) {
        for (HeadInfo headInfo : headInfos) {
            stack.push(StrUtil.join("-", headInfo.getTitle(), headInfo.getDataIndex()));
            List<HeadInfo> children = headInfo.getChildren();
            if (CollectionUtil.isNotEmpty(children)) {
                buildHead(children, stack, list, map);
            }
            if (CollectionUtil.isEmpty(headInfo.getChildren())) {
                LinkedList<String> stacks = new LinkedList<>();
                for (String info : stack) {
                    String[] split = StrUtil.split(info, "-");
                    stacks.add(split[0]);
                    map.put(split[1], split[0]);
                }
                list.add(stacks);
            }
            stack.pop();
        }
        return list;
    }
}
