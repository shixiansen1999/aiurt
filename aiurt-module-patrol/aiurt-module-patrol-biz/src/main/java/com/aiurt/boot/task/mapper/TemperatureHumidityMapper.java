package com.aiurt.boot.task.mapper;

import com.aiurt.boot.dto.UserTeamPatrolDTO;
import com.aiurt.boot.report.model.FailureOrgReport;
import com.aiurt.boot.report.model.FailureReport;
import com.aiurt.boot.report.model.PatrolReport;
import com.aiurt.boot.report.model.PatrolReportModel;
import com.aiurt.boot.report.model.dto.LineOrStationDTO;
import com.aiurt.boot.report.model.dto.MonthDTO;
import com.aiurt.boot.screen.model.ScreenModule;
import com.aiurt.boot.screen.model.ScreenStatisticsGraph;
import com.aiurt.boot.screen.model.ScreenStatisticsTask;
import com.aiurt.boot.screen.model.ScreenTran;
import com.aiurt.boot.standard.dto.StationDTO;
import com.aiurt.boot.statistics.dto.IndexScheduleDTO;
import com.aiurt.boot.statistics.dto.IndexTaskDTO;
import com.aiurt.boot.statistics.model.IndexTaskInfo;
import com.aiurt.boot.statistics.model.PatrolCondition;
import com.aiurt.boot.statistics.model.PatrolIndexTask;
import com.aiurt.boot.statistics.model.ScheduleTask;
import com.aiurt.boot.task.dto.GeneralReturn;
import com.aiurt.boot.task.dto.PatrolTaskDTO;
import com.aiurt.boot.task.dto.PatrolTaskUserContentDTO;
import com.aiurt.boot.task.dto.SubsystemDTO;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskOrganization;
import com.aiurt.boot.task.entity.PatrolTaskStandard;
import com.aiurt.boot.task.entity.TemperatureHumidity;
import com.aiurt.boot.task.param.PatrolTaskParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Description: patrol_task
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface TemperatureHumidityMapper extends BaseMapper<TemperatureHumidity> {

}
