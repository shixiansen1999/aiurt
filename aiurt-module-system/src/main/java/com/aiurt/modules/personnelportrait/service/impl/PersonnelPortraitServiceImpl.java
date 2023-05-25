package com.aiurt.modules.personnelportrait.service.impl;

import com.aiurt.modules.personnelportrait.dto.*;
import com.aiurt.modules.personnelportrait.service.PersonnelPortraitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @description
 */
@Slf4j
@Service
public class PersonnelPortraitServiceImpl implements PersonnelPortraitService {


    @Override
    public PersonnelPortraitResDTO portrait(String orgCode) {
        return new PersonnelPortraitResDTO();
    }

    @Override
    public UserDetailResDTO userDetail(String userId) {
        return new UserDetailResDTO();
    }

    @Override
    public RadarResDTO radarMap(String userId) {
        RadarResDTO data = new RadarResDTO();
        data.setHandle(BigDecimal.valueOf(60));
        data.setPerformance(BigDecimal.valueOf(60));
        data.setAptitude(BigDecimal.valueOf(60));
        data.setSeniority(BigDecimal.valueOf(60));
        data.setEfficiency(BigDecimal.valueOf(60));
        return data;
    }

    @Override
    public DashboardResDTO dashboard(String userId) {
        DashboardResDTO data = new DashboardResDTO();
        data.setScore(BigDecimal.valueOf(90));
        data.setOrgRank(1);
        data.setOrgTotal(16);
        data.setMajorRank(1);
        data.setMajorTotal(32);
        data.setPerformances(new ArrayList<>());
        return data;
    }

    @Override
    public List<ExperienceResDTO> experience(String userId) {
        return new ArrayList<>();
    }

    @Override
    public List<WaveResDTO> waveRose(String userId) {
        return new ArrayList<>();
    }

    @Override
    public HistoryResDTO history(String userId) {
        return new HistoryResDTO();
    }
}
