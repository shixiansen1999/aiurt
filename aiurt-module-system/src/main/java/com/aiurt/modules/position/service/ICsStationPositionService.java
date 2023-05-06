package com.aiurt.modules.position.service;

import com.aiurt.modules.position.entity.CsStationPosition;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description: cs_station_position
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface ICsStationPositionService extends IService<CsStationPosition> {
    /**
     * 查询列表
     * @param page
     * @param csStationPosition
     * @return
     */
    List<CsStationPosition> readAll(Page<CsStationPosition> page,CsStationPosition csStationPosition);
    /**
     * 添加
     *
     * @param csStationPosition
     * @return
     */
    Result<?> add(CsStationPosition csStationPosition);
    /**
     * 编辑
     *
     * @param csStationPosition
     * @return
     */
    Result<?> update(CsStationPosition csStationPosition);

    /**
     * 导入
     * @param file
     * @param params
     * @return
     */
    Result<?> importExcelMaterial(MultipartFile file, ImportParams params) throws Exception;

    /**
     * 异步加载
     * @param name
     * @param pid
     * @return
     */
    List<CsStationPosition> queryTreeListAsync(String name, String pid);
}
