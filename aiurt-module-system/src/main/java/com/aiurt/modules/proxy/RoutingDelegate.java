package com.aiurt.modules.proxy;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.util.RedisUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class RoutingDelegate {

    @Autowired
    private RedisUtil redisUtil;


    public String getToken() {
        String url = "http://39.97.225.186:8888/api/pc/v1/login?username=test&password=subway2022";

        String token = redisUtil.getStr("door:user:token");

        if (StrUtil.isNotBlank(token)) {
            return token;
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", "application/json;charset=UTF-8");

        HttpEntity httpEntity = new HttpEntity(new HashMap<>(), header);

        JSONObject resultToken = restTemplate.postForObject(url, httpEntity,JSONObject.class);

        if (Objects.isNull(resultToken)) {
            return "";
        }

        String code = resultToken.getString("code");

        if (!StrUtil.equalsIgnoreCase("200", code)) {
            return "";
        }

        JSONObject data = resultToken.getJSONObject("data");

        token = data.getString("token");

        redisUtil.set("door:user:token", token, 10*60);

        return token;

    }

    public ResponseEntity<String> redirect(HttpServletRequest request, HttpServletResponse response,String routeUrl, String prefix) {
        try {
            // build up the redirect URL
            String redirectUrl = createRedictUrl(request,routeUrl, prefix);
            RequestEntity requestEntity = createRequestEntity(request, redirectUrl);
            return route(requestEntity);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity("REDIRECT ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String createRedictUrl(HttpServletRequest request, String routeUrl, String prefix) {
        String queryString = request.getQueryString();
        String port = ":9527/#";
        if (StrUtil.isNotBlank(queryString) && queryString.indexOf("api")>-1) {
            port = ":8888";
        }

        return routeUrl +port + request.getRequestURI().replace(prefix, "") +
                (queryString != null ? "?" + queryString : "");
    }


    private RequestEntity createRequestEntity(HttpServletRequest request, String url) throws URISyntaxException, IOException {
        String method = request.getMethod();
        HttpMethod httpMethod = HttpMethod.resolve(method);
        MultiValueMap<String, String> headers = parseRequestHeader(request);
        byte[] body = parseRequestBody(request);
        return new RequestEntity<>(body, headers, httpMethod, new URI(url));
    }

    private ResponseEntity<String> route(RequestEntity requestEntity) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        ResponseEntity<String> exchange = restTemplate.exchange(requestEntity, String.class);
        return exchange;
    }


    private byte[] parseRequestBody(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        return StreamUtils.copyToByteArray(inputStream);
    }

    private MultiValueMap<String, String> parseRequestHeader(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        List<String> headerNames = Collections.list(request.getHeaderNames());
        for (String headerName : headerNames) {
            List<String> headerValues = Collections.list(request.getHeaders(headerName));
            for (String headerValue : headerValues) {
                headers.add(headerName, headerValue);
            }
        }

        List<String> cookies =new ArrayList<>();
        String token = getToken();
        /* 登录获取Cookie 这里是直接给Cookie，可使用下方的login方法拿到Cookie给入*/
        cookies.add("Admin-Token="+token+"; Path=/;");       //在 header 中存入cookies
        headers.put(HttpHeaders.COOKIE,cookies);

        return headers;
    }
}
