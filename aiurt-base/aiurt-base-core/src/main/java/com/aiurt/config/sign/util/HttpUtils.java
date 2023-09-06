package com.aiurt.config.sign.util;

import cn.hutool.core.map.MapUtil;
import com.aiurt.common.constant.SymbolConstant;
import com.aiurt.common.util.oConvertUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * http 工具类 获取请求中的参数
 *
 * @author jeecg
 * @date 20210621
 */
@Slf4j
public class HttpUtils {

    /**
     * 将URL的参数和body参数合并
     *
     * @author jeecg
     * @date 20210621
     * @param request
     */
    public static SortedMap<String, String> getAllParams(HttpServletRequest request) throws IOException {

        SortedMap<String, String> result = new TreeMap<>();
        // 获取URL上最后带逗号的参数变量 sys/dict/getDictItems/sys_user,realname,username
        String pathVariable = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
        if (pathVariable.contains(SymbolConstant.COMMA)) {
            log.info(" pathVariable: {}",pathVariable);
            String deString = URLDecoder.decode(pathVariable, "UTF-8");
            log.info(" pathVariable decode: {}",deString);
            result.put(SignUtil.X_PATH_VARIABLE, deString);
        }
        // 获取URL上的参数
        Map<String, String> urlParams = getUrlParams(request);
        for (Map.Entry entry : urlParams.entrySet()) {
            result.put((String)entry.getKey(), (String)entry.getValue());
        }
        Map<String, String> allRequestParam = new HashMap<>(16);
        // get请求不需要拿body参数
        if (!HttpMethod.GET.name().equals(request.getMethod())) {
            allRequestParam = getAllRequestParam(request);
        }
        // 将URL的参数和body参数进行合并
        if (allRequestParam != null) {
            for (Map.Entry entry : allRequestParam.entrySet()) {
                result.put((String)entry.getKey(), (String)entry.getValue());
            }
        }
        return result;
    }

    /**
     * 将URL的参数和body参数合并
     *
     * @author jeecg
     * @date 20210621
     * @param queryString
     */
    public static SortedMap<String, String> getAllParams(String url, String queryString, byte[] body, String method)
        throws IOException {

        SortedMap<String, String> result = new TreeMap<>();
        // 获取URL上最后带逗号的参数变量 sys/dict/getDictItems/sys_user,realname,username
        String pathVariable = url.substring(url.lastIndexOf("/") + 1);
        if (pathVariable.contains(SymbolConstant.COMMA)) {
            log.info(" pathVariable: {}",pathVariable);
            String deString = URLDecoder.decode(pathVariable, "UTF-8");
            log.info(" pathVariable decode: {}",deString);
            result.put(SignUtil.X_PATH_VARIABLE, deString);
        }
        // 获取URL上的参数
        Map<String, String> urlParams = getUrlParams(queryString);
        for (Map.Entry entry : urlParams.entrySet()) {
            result.put((String)entry.getKey(), (String)entry.getValue());
        }
        Map<String, String> allRequestParam = new HashMap<>(16);
        // get请求不需要拿body参数
        if (!HttpMethod.GET.name().equals(method)) {
            allRequestParam = getAllRequestParam(body);
        }
        // 将URL的参数和body参数进行合并
        if (allRequestParam != null) {
            for (Map.Entry entry : allRequestParam.entrySet()) {
                result.put((String)entry.getKey(), (String)entry.getValue());
            }
        }
        return result;
    }

    /**
     * 获取 Body 参数
     *
     * @date 15:04 20210621
     * @param request
     */
    public static Map<String, String> getAllRequestParam(final HttpServletRequest request) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String str = "";
        StringBuilder wholeStr = new StringBuilder();
        // 一行一行的读取body体里面的内容；
        while ((str = reader.readLine()) != null) {
            wholeStr.append(str);
        }
        // 转化成json对象
        return JSONObject.parseObject(wholeStr.toString(), Map.class);
    }

    /**
     * 获取 Body 参数
     *
     * @date 15:04 20210621
     * @param body
     */
    public static Map<String, String> getAllRequestParam(final byte[] body) throws IOException {
        if(body==null){
            return null;
        }
        String wholeStr = new String(body);
        // 转化成json对象
        return JSONObject.parseObject(wholeStr.toString(), Map.class);
    }

    /**
     * 将URL请求参数转换成Map
     *
     * @param request
     */
    public static Map<String, String> getUrlParams(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>(16);
        if (oConvertUtils.isEmpty(request.getQueryString())) {
            return result;
        }
        String param = "";
        try {
            param = URLDecoder.decode(request.getQueryString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] params = param.split("&");
        for (String s : params) {
            int index = s.indexOf("=");
            result.put(s.substring(0, index), s.substring(index + 1));
        }
        return result;
    }

    /**
     * 将URL请求参数转换成Map
     *
     * @param queryString
     */
    public static Map<String, String> getUrlParams(String queryString) {
        Map<String, String> result = new HashMap<>(16);
        if (oConvertUtils.isEmpty(queryString)) {
            return result;
        }
        String param = "";
        try {
            param = URLDecoder.decode(queryString, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] params = param.split("&");
        for (String s : params) {
            int index = s.indexOf("=");
            result.put(s.substring(0, index), s.substring(index + 1));
        }
        return result;
    }

    /**
     * 预先检测 HTTP 请求是否可行
     * @param url
     * @return
     */
    public static Boolean checkUrl(String url) {
        // 检测url是否可达
        RestTemplate restTemplate = new RestTemplate();

        try {
            // Perform a HEAD request to check feasibility

            ResponseEntity<Void> exchange = restTemplate.exchange(
                    url,
                    HttpMethod.HEAD,
                    null,
                    Void.class
            );
            // Check if the response code indicates a successful connection
            if (exchange.getStatusCode().is2xxSuccessful()) {
               log.info("HTTP request is feasible. Proceed with the full request.");
                // Now you can proceed with the actual HTTP request using restTemplate.getForObject(), restTemplate.exchange(), etc.
                return true;
            } else {
                log.info("HTTP request is not feasible (Response code: " + exchange.getStatusCode() + ")");
            }
        }catch (Exception e) {
            log.error("Error occurred during precheck: " + e.getMessage());
        }
        return false;
    }

    /**
     * 发送HTTP GET请求并获取响应内容。
     *
     * @param url     请求的URL地址
     * @param headers 请求头参数，可为null
     * @param params  请求参数，可为null
     * @return 从服务器返回的响应内容
     */
    public static String sendGetRequest(String url, Map<String, String> headers, Map<String, String> params) {
        StringBuilder response = new StringBuilder();
       /* if (!checkUrl(url)) {
            return "";
        }*/
        try {
            // 构建参数字符串
            if (MapUtil.isNotEmpty(params)) {
                log.info("request params: {}",params);
                url = String.format("%s%s%s", url, url.contains("?") ? "&" : "?", buildQueryString(params));
            }
            // 设置请求头
            HttpHeaders requestHeaders = new HttpHeaders();
            if (MapUtil.isNotEmpty(headers)) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    requestHeaders.set(entry.getKey(), entry.getValue());
                }
            }
            HttpEntity requestEntity = new HttpEntity(requestHeaders);


            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Result> exchange = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    Result.class
            );
            Result result = exchange.getBody();
            log.info("请求接口接口：{}", JSON.toJSONString(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    /**
     * 构建HTTP请求参数字符串。
     *
     * @param params 请求参数，以键值对的形式传入
     * @return 构建的参数字符串，形如 "key1=value1&key2=value2"
     * @throws UnsupportedEncodingException 如果URL编码时出现不支持的字符集异常
     */
    private static String buildQueryString(Map<String, String> params) {
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (queryString.length() > 0) {
                queryString.append("&");
            }
            queryString.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return queryString.toString();
    }

    public static void main(String[] args) {
        String url = "http://www.jeec1g.com/";

        // 设置请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer YourAccessToken");

        // 设置请求参数
        Map<String, String> params = new HashMap<>();
        params.put("param1", "value1");
        params.put("param2", "value2");

        String response = sendGetRequest(url, headers, params);
        System.out.println("API Response:\n" + response);
    }
}
