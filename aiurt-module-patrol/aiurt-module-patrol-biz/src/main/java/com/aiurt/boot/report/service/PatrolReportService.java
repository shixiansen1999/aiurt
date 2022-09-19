package com.aiurt.boot.report.service;

import com.aiurt.boot.report.model.PatrolReport;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/9/19
 * @desc
 */
@Service
public class PatrolReportService {
    @Autowired
    private PatrolTaskMapper patrolTaskMapper;
    public Page<PatrolReport> getTaskDate(Page<PatrolReport> pageList, PatrolReport report) {
        List<PatrolReport> reportList = patrolTaskMapper.getReportTastList(report);
       return  pageList.setRecords(reportList);
    }
}
