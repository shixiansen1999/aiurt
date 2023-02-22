package com.aiurt.boot.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 对接档案系统
 * 1.先获取到档案系统token --getToken
 * 2.获取用户信息 --getArchiveUser
 * 3.根据档案类型id获取档案类型信息 --getTypeInfoById
 * 4.新建整编库文件夹。使用从档案类型信息中获取到refileFolderId（整编库文件夹id）--createFolder
 * 5.上传文件到新建的整编库文件夹中   --upload
 * 6.调用条目数据归档接口 --arch
 *
 * @author: hqy
 * @date: 2023年02月21日 13:59
 */

@Component
@Slf4j
public class ArchiveUtils {


    @Value("${support.IntegrationKey}")
    private String integrationKey;

    @Value("${support.host}")
    private String host;

    @Value("${support.archivesTypeId}")
    private String archivesTypeId;


    public String doPost(String url, JSONObject json) throws UnsupportedEncodingException {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(url);

        postMethod.addRequestHeader("accept", "*/*");
        postMethod.addRequestHeader("connection", "Keep-Alive");
        //设置json格式传送
        postMethod.addRequestHeader("Content-Type", "application/json;charset=UTF-8");
        //必须设置下面这个Header
        postMethod.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
        //添加请求参数
        RequestEntity se = null;
        if (ObjectUtils.isNotEmpty(json)) {
            se = new StringRequestEntity(json.toString(), "application/json", "UTF-8");
        }
        postMethod.setRequestEntity(se);

        String res = "";
        try {
            int code = httpClient.executeMethod(postMethod);
            if (code == 200) {
                res = postMethod.getResponseBodyAsString();
                System.out.println(res);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            postMethod.releaseConnection();
        }
        return res;
    }

    /**
     * @Description: Content-Type application/x-www-form-urlencoded
     * @author: niuzeyu
     * @date: 2022/9/8 16:25
     */
    public String doPost2(String url, Map param) throws UnsupportedEncodingException {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(url);

        postMethod.addRequestHeader("accept", "*/*");
        postMethod.addRequestHeader("connection", "Keep-Alive");
        //设置json格式传送
        postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        //必须设置下面这个Header
        postMethod.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
        //添加请求参数
        int size = param.size();
        int index = 0;
        NameValuePair[] body = new NameValuePair[size];
        Iterator<Map.Entry<String, Object>> entries = param.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            body[index++] = new NameValuePair(key, value.toString());
        }
        postMethod.setRequestBody(body);

        String res = "";
        try {
            int code = httpClient.executeMethod(postMethod);
            if (code == 200) {
                res = postMethod.getResponseBodyAsString();
                System.out.println(res);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            postMethod.releaseConnection();
        }
        return res;
    }

    public String doGet(String httpUrl) {
        HttpURLConnection conn = null;
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder result = new StringBuilder();
        try {
            //创建远程url连接对象
            URL url = new URL(httpUrl);
            //通过远程url连接对象打开一个连接，强转成HTTPURLConnection类
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //设置连接超时时间和读取超时时间
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(60000);
            conn.setRequestProperty("Accept", "application/json");
            //发送请求
            conn.connect();
            //通过conn取得输入流，并使用Reader读取
            if (200 == conn.getResponseCode()) {
                is = conn.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    result.append(line);
                    System.out.println(line);
                }
            } else {
                System.out.println("ResponseCode is an error code:" + conn.getResponseCode());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            conn.disconnect();
        }
        return result.toString();
    }

    public static String put(String url, String data, Map<String, String> heads) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(url);
            if (heads != null) {
                Set<String> keySet = heads.keySet();
                for (String s : keySet) {
                    httpPut.addHeader(s, heads.get(s));
                }
            }
            StringEntity stringEntity = new StringEntity(data);
            httpPut.setEntity(stringEntity);

            System.out.println("Executing request " + httpPut.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            String responseBody = httpclient.execute(httpPut, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            return responseBody;
        }
    }

    public static String doPut(String url, String jsonStr) {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(url);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000).setConnectionRequestTimeout(35000).setSocketTimeout(60000).build();
        httpPut.setConfig(requestConfig);
        httpPut.setHeader("Accept", "application/json");
        httpPut.setHeader("Content-Type", "application/json");
        httpPut.setHeader("Connection", "keep-alive");

        CloseableHttpResponse httpResponse = null;
        try {
            StringEntity stringEntity = new StringEntity(jsonStr, "utf-8");
            httpPut.setEntity(stringEntity);
            httpResponse = httpClient.execute(httpPut);
            HttpEntity entity = httpResponse.getEntity();
            String result = EntityUtils.toString(entity);
            return result;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * @Description: 获取档案系统token
     * @author: niuzeyu
     * @date: 2022/9/7 18:23
     * @Return: java.lang.String
     */
    public String getToken(String username) throws UnsupportedEncodingException {

        try {
            String httpUrl = host + "/api/services/Org/UserLoginIntegrationByUserLoginName";
            Map<String, String> login = new HashMap<>();
            HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
            String ip = IPUtils.getIpAddr(request);
            login.put("LoginName", username);
            login.put("IPAddress", ip);
            login.put("IntegrationKey", integrationKey);
            JSONObject json = (JSONObject) JSONObject.toJSON(login);
            String res = doPost(httpUrl, json);
            String token = null;
            if (StringUtils.isNotEmpty(res) && res != "") {
                Map<String, String> jsonMap = JSON.parseObject(res, new TypeReference<HashMap<String, String>>() {
                });
                token = jsonMap.get("data");
            }
            return token;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * @Description: 获取档案系统用户信息
     * @author: niuzeyu
     * @date: 2022/9/7 18:23
     * @Return: java.lang.String
     */
    public Map getArchiveUser(String account, String token){
        String httpUrl = host + "/api/services/OrgUser/GetUserInfoByAccount?account=" + account + "&token=" + token;
        Map<String, String> userInfo = null;
        String res = doGet(httpUrl);
        if (StringUtils.isNotEmpty(res) && res != "") {
            Map<String, String> jsonMap = JSON.parseObject(res, new TypeReference<HashMap<String, String>>() {
            });
            userInfo = JSON.parseObject(jsonMap.get("data"), new TypeReference<HashMap<String, String>>() {
            });
        }
        return userInfo;
    }

    /**
     * @Description: 通过archivesTypeId，获取档案类型信息
     * @author: niuzeyu
     * @date: 2022/9/7 18:23
     * @Return: java.lang.String
     */
    public Map getTypeInfoById(String token) {
        String httpUrl = host + "/edrmscore/api/archType/getById?id=" + archivesTypeId + "&token=" + token;
        String res = doGet(httpUrl);
        if (StringUtils.isNotEmpty(res) && res != "") {
            Map<String, String> map = JSON.parseObject(res, new TypeReference<HashMap<String, String>>() {
            });
            Map<String, String> info = JSON.parseObject(map.get("obj"), new TypeReference<HashMap<String, String>>() {
            });
            return info;
        }
        return null;
    }

    //创建文件夹
    public String createFolder(String token, String refileFolderId, String name) throws UnsupportedEncodingException {
        String httpUrl = host + "/api/services/Folder/CreateFolder";
        Map<String, Object> folderInfo = new HashMap<>();
        folderInfo.put("Token", token);
        folderInfo.put("Name", name);
        folderInfo.put("FolderCode", "");
        folderInfo.put("Remark", "");
        folderInfo.put("ParentFolderId", refileFolderId);
        JSONObject json = (JSONObject) JSONObject.toJSON(folderInfo);
        String res = doPost(httpUrl, json);
        String folderId = null;
        if (StringUtils.isNotEmpty(res) && res != "") {
            Map<String, String> jsonMap = JSON.parseObject(res, new TypeReference<HashMap<String, String>>() {
            });
            Map<String, String> jsonMap2 = JSON.parseObject(jsonMap.get("data"), new TypeReference<HashMap<String, String>>() {
            });
            folderId = jsonMap2.get("FolderId");
        }
        return folderId;
    }

    public Map arch(Map<String, Object> values, String token) {
        Map<String, Object> param = new HashMap<>();
        Date date = new Date();
        UUID uuid = UUID.randomUUID();
        SimpleDateFormat sdf = null;
        param.put("Id", uuid.toString());//唯一标识
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        param.put("archivedate", sdf.format(date));//归档时间
        param.put("archiver", values.get("archiver")); //归档人id
        param.put("archivername", values.get("username")); //归档姓名
        param.put("archtypeid", archivesTypeId); //归档姓名
        param.put("carrier", "电子");//载体形式
        param.put("duration", values.get("duration"));//保管期限
        param.put("objtype", "其他");//实体类型
        param.put("entrystate", "0");//档案类型 0整编库（未归档）1档案库（已归档）
        param.put("fileList", values.get("fileList"));//文件集合
        param.put("ifDossiered", "0");//是否卷内文件
        param.put("ifInbound", "0");//是否上架
        param.put("folderId", "");//新建档案库文件夹id
        param.put("lastAutoAddNo", "其他");//编号
        param.put("littleStatus", "0");//流程状态
        param.put("name", values.get("name"));//档案名称
        param.put("secert", values.get("secert"));//密级
        param.put("number", values.get("number"));//档案编号
        param.put("refileFolderId", values.get("refileFolderId"));//新建整编库文件夹id
        param.put("secertduration", values.get("secertduration"));//保密期限
        param.put("sectid", values.get("sectid"));//所属全宗id
        param.put("times", date.getTime());//13位时间戳
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        param.put("writtendate", sdf.format(date.getTime()));//成文日期
        String json = JSONObject.toJSONString(param);
        String url = host + "/edrmscore/api/arch?token=" + token;
        String result = doPut(url, json);
        Map<String, String> jsonMap = JSON.parseObject(result, new TypeReference<HashMap<String, String>>() {
        });
        return jsonMap;
    }

    public JSONObject upload(String token, String refileFolderIdNew, String fileName, Long size, String fileType, InputStream in) throws UnsupportedEncodingException {
        JSONObject result = new JSONObject();
        //第一步发送上传请求
        String checkUrl = host + "/WebCore?module=RegionDocOperationApi&fun=CheckAndCreateDocInfo";
        HashMap<String, Object> checkParam = new HashMap<>();
        checkParam.put("folderId", refileFolderIdNew);
        checkParam.put("token", token);
        checkParam.put("fileName", fileName);
        checkParam.put("size", size);
        checkParam.put("type", fileType);
        checkParam.put("attachType", "0");
        checkParam.put("strategy", "majorUpgrade");
        checkParam.put("fileModel", "UPLOAD");
        String res = doPost2(checkUrl, checkParam);

        //第二步上传文件
        JSONObject resjson = JSONObject.parseObject(res);
        if (resjson.getInteger("result") == 0) {
            JSONObject regData = resjson.getJSONObject("data");
            String FileId = regData.getString("FileId");
            String regionHash = regData.getString("RegionHash");
            int regionId = regData.getInteger("RegionId");
            String dataUrl = host + "/document/upload?token=" + token;
            String uploadId = UUID.randomUUID().toString();
            int num = (int) Math.ceil(size / 5242880.0);
            log.info(num + "");
            try {
                byte[] buffer = null;
                if (num == 1) {
                    buffer = new byte[Integer.parseInt(String.valueOf(size))];
                } else {
                    buffer = new byte[5242880];
                }
                int len;
                int i = 0;
                while ((len = in.read(buffer)) != -1) {
                    Map<String, String> datamap = new HashMap<>();
                    datamap.put("uploadId", uploadId);
                    datamap.put("regionHash", regionHash);
                    datamap.put("regionId", String.valueOf(regionId));
                    datamap.put("fileName", fileName);
                    datamap.put("size", String.valueOf(size));
                    datamap.put("chunkSize", String.valueOf(5242880));
                    datamap.put("chunks", String.valueOf(num));
                    datamap.put("chunk", String.valueOf(i));
                    datamap.put("blockSize", String.valueOf(len));
                    log.info(len + "传输长度");
                    String resdata = doPostFile(dataUrl, datamap, buffer);
                    log.info("返回数据{}", resdata);
                    i++;
                    if (i + 1 == num) {
                        int endSize = Integer.parseInt(String.valueOf(size - 5242880 * i));
                        buffer = new byte[endSize];
                    }
                }
                result.put("status", 0);
                result.put("fileId", FileId);
                return result;
            } catch (Exception e) {
                log.info("异常", e);
                result.put("status", 1);
                result.put("fileId", "");
                return result;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        log.error("io流关闭异常", e);
                    }
                }
            }

        } else {
            result.put("status", resjson.getInteger("result"));
            result.put("fileId", "");
            return result;
        }

    }

    public static String doPostFile(String url, Map<String, String> paramMap, byte[] buffer) {
        // 创建Http实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建HttpPost实例
        HttpPost httpPost = new HttpPost(url);
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(java.nio.charset.Charset.forName("UTF-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            //表单中参数
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                builder.addPart(entry.getKey(), new StringBody(entry.getValue(), ContentType.create("text/plain", Consts.UTF_8)));
            }
            builder.addPart("file", new ByteArrayBody(buffer, paramMap.get("fileName")));
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);


            HttpResponse response = httpClient.execute(httpPost);// 执行提交

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 返回
                String res = EntityUtils.toString(response.getEntity(), java.nio.charset.Charset.forName("UTF-8"));
                log.info("传文件了");
                return res;
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("调用HttpPost失败！" + e.toString());
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    log.error("关闭HttpPost连接失败！");
                }
            }
        }
        return null;
    }
}
