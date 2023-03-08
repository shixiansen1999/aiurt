package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.sparepart.entity.SparePartOutOrder;
import com.aiurt.modules.sparepart.entity.SparePartScrap;
import com.aiurt.modules.sparepart.mapper.SparePartOutOrderMapper;
import com.aiurt.modules.sparepart.mapper.SparePartScrapMapper;
import com.aiurt.modules.sparepart.service.ISparePartReturnOrderService;
import com.aiurt.modules.sparepart.service.ISparePartScrapService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: spare_part_scrap
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
@Service
public class SparePartScrapServiceImpl extends ServiceImpl<SparePartScrapMapper, SparePartScrap> implements ISparePartScrapService {
    @Autowired
    private SparePartScrapMapper sparePartScrapMapper;
    @Autowired
    private SparePartOutOrderMapper sparePartOutOrderMapper;
    @Autowired
    private ISparePartReturnOrderService sparePartReturnOrderService;
    @Autowired
    private ISysParamAPI iSysParamAPI;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private ISTodoBaseAPI isTodoBaseAPI;
    /**
     * 查询列表
     * @param page
     * @param sparePartScrap
     * @return
     */
    @Override
    public List<SparePartScrap> selectList(Page page, SparePartScrap sparePartScrap){
        return sparePartScrapMapper.readAll(page,sparePartScrap);
    }
    /**
     * 查询列表不分页
     * @param sparePartScrap
     * @return
     */
    @Override
    public List<SparePartScrap> selectListById( SparePartScrap sparePartScrap){
        return sparePartScrapMapper.readAll(sparePartScrap);
    }
    /**
     * 修改
     *
     * @param sparePartScrap
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(SparePartScrap sparePartScrap) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SparePartScrap scrap = getById(sparePartScrap.getId());
        if (sparePartScrap.getStatus().equals(CommonConstant.SPARE_PART_SCRAP_STATUS_3)) {
            sparePartScrap.setConfirmId(user.getUsername());
            sparePartScrap.setConfirmTime(new Date());

            //更新已出库库存数量,做减法
            List<SparePartOutOrder> orderList = sparePartOutOrderMapper.selectList(new LambdaQueryWrapper<SparePartOutOrder>().eq(SparePartOutOrder::getDelFlag, CommonConstant.DEL_FLAG_0).eq(SparePartOutOrder::getMaterialCode, sparePartScrap.getMaterialCode()).eq(SparePartOutOrder::getWarehouseCode, sparePartScrap.getWarehouseCode()));
            if (!orderList.isEmpty()) {
                for (int i = 0; i < orderList.size(); i++) {
                    SparePartOutOrder order = orderList.get(i);
                    if (Integer.parseInt(order.getUnused()) >= scrap.getNum()) {
                        Integer number = Integer.parseInt(order.getUnused()) - scrap.getNum();
                        order.setUnused(number + "");
                        sparePartReturnOrderService.updateOrder(order);
                    } else {
                        return Result.error("剩余数量不足！");
                    }
                }
            }

            try {
                LoginUser userByName = sysBaseApi.getUserByName(scrap.getCreateBy());
                //发送通知
                MessageDTO messageDTO = new MessageDTO(user.getUsername(),userByName.getUsername(), "备件报废申请-确认" + DateUtil.today(), null);

                //构建消息模板
                HashMap<String, Object> map = new HashMap<>();
                map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, scrap.getId());
                map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.SPAREPART_SCRAP.getType());
                map.put("materialCode",scrap.getMaterialCode());
                String materialName= sysBaseApi.getMaterialNameByCode(scrap.getMaterialCode());
                map.put("name",materialName);
                map.put("num",scrap.getNum());
                map.put("realName",userByName.getRealname());
                map.put("scrapTime", DateUtil.format(scrap.getScrapTime(),"yyyy-MM-dd HH:mm:ss"));

                messageDTO.setData(map);
                //业务类型，消息类型，消息模板编码，摘要，发布内容
                messageDTO.setTemplateCode(CommonConstant.SPAREPARTSCRAP_SERVICE_NOTICE);
                SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE);
                messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
                messageDTO.setMsgAbstract("备件报废申请-确认");
                messageDTO.setPublishingContent("备件报废申请通过");
                messageDTO.setCategory(CommonConstant.MSG_CATEGORY_10);
                sysBaseApi.sendTemplateMessage(messageDTO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sparePartScrapMapper.updateById(sparePartScrap);
        return Result.OK("操作成功！");
    }
}
