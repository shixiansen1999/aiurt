package com.aiurt.boot.controller;

import com.aiurt.boot.service.IKService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2023/2/1716:17
 */
@Api(tags = "动态词库")
@RestController
@RequestMapping("/ik/")
@Slf4j
public class IkController {
    @Autowired
    private IKService ikService;

    @RequestMapping(value = "/extExtDict", method = RequestMethod.HEAD)
    public String headExtDict(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        String modified = request.getHeader("If-Modified-Since");
        String eTag = request.getHeader("If-None-Match");
        log.info("es:head请求，接收modified：{} ，eTag：{}", modified, eTag);
        String newModified = ikService.getCurrentNewModified();
        String newTag = String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(newModified).getTime());
        response.setHeader("Last-Modified", newModified);
        response.setHeader("ETag", newTag);
        return "success";
    }


    @RequestMapping(value = "/extExtDict", method = RequestMethod.GET)
    public String getExtDict(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        String newModified = ikService.getCurrentNewModified();
        String newTag = String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(newModified).getTime());
        response.setHeader("Last-Modified", newModified);
        response.setHeader("ETag", newTag);
        response.setCharacterEncoding("UTF-8");
        return ikService.getDict();
    }

}
