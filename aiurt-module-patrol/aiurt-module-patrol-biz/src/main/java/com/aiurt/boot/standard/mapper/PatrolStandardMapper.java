package com.aiurt.boot.standard.mapper;

import com.aiurt.boot.standard.dto.InspectionStandardDto;
import com.aiurt.boot.standard.dto.PatrolStandardDto;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.DictModel;

import java.util.List;

/**
 * @Description: patrol_standard
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@EnableDataPerm
public interface PatrolStandardMapper extends BaseMapper<PatrolStandard> {
    /**
     * 分页查询
     * @param page
     * @param patrolStandard
     * @return
     */
    List<PatrolStandardDto> pageList (@Param("page") Page page, @Param("patrolStandard") PatrolStandard patrolStandard);

    /**
     * 分页查询
     * @param page
     * @param patrolStandard
     * @param stations
     * @param majorCodes
     * @return
     */
    List<PatrolStandardDto> pageLists (@Param("page") Page page,
                                       @Param("patrolStandard") PatrolStandardDto patrolStandard,
                                       @Param("stations") List<String>stations,
                                       @Param("majorCodes") List<String>majorCodes);

    /**
     * 获取分类
     * @param professionCode
     * @param subsystemCode
     * @return
     */
    List<InspectionStandardDto> list(@Param("professionCode")String professionCode, @Param("subsystemCode") String subsystemCode);

    /**
     * 根据多个id查询
     * @param ids
     * @return
     */
    List<PatrolStandardDto> selectbyIds(@Param("ids")List <String> ids);

    /**
     * 查询可不可删
     * @param code
     * @return
     */
    Integer number(@Param("code")String code);
    /**
     * 查询配置巡检项的表
     * @param id
     * @return
     */
    Integer number1(@Param("id") String id);

    /**
     * 查询标准表
     * @param patrolStandard
     * @return
     */
    List<PatrolStandard> getList(@Param("patrolStandard")PatrolStandard patrolStandard);

    /**
     * 获取巡检模块的字典信息
     * @param modules
     * @return
     */
    List<DictModel> querySysDict(@Param("modules")Integer modules);

    /**
     * 根据字典名称，获取字典code
     * @param dictName
     * @return
     */
    String getDictCode(@Param("dictName")String dictName);
}
