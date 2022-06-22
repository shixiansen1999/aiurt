package com.aiurt.modules.common;

import com.aiurt.modules.common.entity.SelectTable;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Api(tags = "共用模块")
@RestController
@RequestMapping("/common")
public class CommonCtroller {

    @Autowired
    private ICsMajorService csMajorService;

    public Result<List<Device>> query() {
        return Result.OK();
    }

    /**
     * 查询当前人员所管辖的专业
     * @return
     */
    @GetMapping("/major/queryMajorByAuth")
    @ApiOperation("查询当前人员所管辖的专业")
    public Result<List<SelectTable>> queryMajorByAuth() {
        LambdaQueryWrapper<CsMajor> queryWrapper = new LambdaQueryWrapper<>();

        //todo 查询当前人员所管辖的专业。
        List<CsMajor> csMajorList = csMajorService.getBaseMapper().selectList(queryWrapper);

        List<SelectTable> list = csMajorList.stream().map(csMajor -> {
            SelectTable table = new SelectTable();
            table.setLabel(csMajor.getMajorName());
            table.setValue(csMajor.getMajorCode());
            return table;
        }).collect(Collectors.toList());

        return Result.OK(list);
    }



}
