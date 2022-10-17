package com.aiurt.modules.sparepart.controller;

import com.aiurt.modules.sparepart.service.ISparePartStockService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zwl
 * @Date:   2022-10-17
 * @Version: V1.0
 */
@Api(tags="备件管理-备件统计")
@RestController
@RequestMapping("/sparepart/sparepartstatistics")
@Slf4j
public class SparePartStatisticsController {

    @Autowired
    private ISparePartStockService sparePartStockService;
}
