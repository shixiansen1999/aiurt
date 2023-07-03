package com.aiurt.modules.train.trainarchive.service;

import com.aiurt.modules.train.trainarchive.dto.TrainArchiveDTO;
import com.aiurt.modules.train.trainarchive.entity.TrainArchive;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: train_archive
 * @Author: aiurt
 * @Date:   2023-06-25
 * @Version: V1.0
 */
public interface ITrainArchiveService extends IService<TrainArchive> {
    /**
     * 培训档案-添加
     * @param trainArchiveDTO 添加的数据
     * @return 结果集
     */
    Result<String> add(TrainArchiveDTO trainArchiveDTO);

    /**
     * 培训档案-编辑
     * @param trainArchiveDTO 编辑的数据
     * @return 结果集
     */
    Result<String> edit(TrainArchiveDTO trainArchiveDTO);

    /**
     * 培训档案-分页列表查询
     * @param page 分页参数
     * @param trainArchiveDTO 查询参数
     * @return list
     */
    IPage<TrainArchiveDTO> pageList(Page<TrainArchiveDTO> page, TrainArchiveDTO trainArchiveDTO);

    /**
     * 培训档案-删除
     * @param id 删除的id
     * @return 结果集
     */
    Result<String> delete(String id);

    /**
     * 培训档案-通过id查询
     * @param id 培训档案id
     * @return 培训档案信息
     */
    Result<TrainArchiveDTO> queryById(String id);

    /**
     *
     * 培训档案-导出
     * @param request 请求参数
     * @param response 响应参数
     * @param trainArchiveDTO 请求参数
     * @return 结果集
     * @throws IOException 异常
     */
    Result<String> exportXls(HttpServletRequest request, HttpServletResponse response, TrainArchiveDTO trainArchiveDTO) throws IOException;

    /**
     * 培训档案-导入
     * @param request 请求参数
     * @param response 响应参数
     * @return 结果集
     * @throws IOException 异常
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
