package com.aiurt.modules.stock.service;

import com.aiurt.modules.stock.entity.StockLevel2Info;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description:
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
public interface IStockLevel2InfoService extends IService<StockLevel2Info> {
    /**
     * 导入数据
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
