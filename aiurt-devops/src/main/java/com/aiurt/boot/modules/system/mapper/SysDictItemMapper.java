package com.aiurt.boot.modules.system.mapper;

import com.aiurt.boot.modules.system.entity.SysDictItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@Mapper
@Repository
public interface SysDictItemMapper extends BaseMapper<SysDictItem> {
    @Select("SELECT * FROM SYS_DICT_ITEM WHERE DICT_ID = #{mainId}")
    public List<SysDictItem> selectItemsByMainId(String mainId);

	@Select("select * from sys_dict_item item left join sys_dict dict on dict.id = item.dict_id where dict.del_flag = 0 and dict.dict_code=#{code} ")
	List<SysDictItem> selectByDictCode(@Param("code") String code);
}
