package com.aiurt.boot.personnelteam.mapper;

import com.aiurt.boot.task.dto.PersonnelTeamDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author zwl
 */
@Component
public interface PersonnelTeamMapper {

    List<PersonnelTeamDTO> getScheduledTask(@Param("userIdList") List<String> userIdList,
                                            @Param("status") Long status,
                                            @Param("startDate") Date startDate,
                                            @Param("endDate") Date endDate,
                                            @Param("userId") String userId);

    PersonnelTeamDTO getTime(@Param("userId") String userId,
                             @Param("startDate") Date startDate,
                             @Param("endDate") Date endDate);

}