package com.aiurt.modules.sysfile.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/download")
public class TestController {

    @Value("${jeecg.path.upload}")
    private String uploadPath;

    public void download() {
        File file = new File(uploadPath + "/1626076141776375809?fileName=ElasticSearch_1676521737845.pdf");
        System.out.println(file);
    }
}
