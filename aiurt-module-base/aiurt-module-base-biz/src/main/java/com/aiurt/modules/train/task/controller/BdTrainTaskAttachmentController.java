package com.aiurt.modules.train.task.controller;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.train.task.entity.BdTrainTaskAttachment;
import com.aiurt.modules.train.task.service.IBdTrainTaskAttachmentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

import static sun.plugin2.os.windows.OVERLAPPED.size;

/**
 * @Description: 培训任务附件
 * @Author: jeecg-boot
 * @Date:   2022-04-24
 * @Version: V1.0
 */
@Api(tags="培训任务附件")
@RestController
@RequestMapping("/task/bdTrainTaskAttachment")
@Slf4j
public class BdTrainTaskAttachmentController extends BaseController<BdTrainTaskAttachment, IBdTrainTaskAttachmentService> {
	@Autowired
	private IBdTrainTaskAttachmentService bdTrainTaskAttachmentService;

	/**
	 * 分页列表查询
	 *
	 * @param bdTrainTaskAttachment
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "培训任务附件-分页列表查询")
	@ApiOperation(value="培训任务附件-分页列表查询", notes="培训任务附件-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BdTrainTaskAttachment bdTrainTaskAttachment,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BdTrainTaskAttachment> queryWrapper = QueryGenerator.initQueryWrapper(bdTrainTaskAttachment, req.getParameterMap());
		Page<BdTrainTaskAttachment> page = new Page<BdTrainTaskAttachment>(pageNo, pageSize);

		IPage<BdTrainTaskAttachment> pageList = bdTrainTaskAttachmentService.page(page, queryWrapper);
		return Result.OK(pageList);
	}



	/**
	 * 通过任务id查询课件
	 * @param taskId
	 * @author hlq
	 */
	@AutoLog(value = "培训任务附件-通过任务id查询")
	@ApiOperation(value="培训任务附件-通过任务id查询", notes="培训任务附件-通过任务id查询")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", response = BdTrainTaskAttachment.class),
	})
	@GetMapping(value = "/queryTaskList")
	public Result<?> queryTaskList(@RequestParam(name="taskId",required=true) String taskId) {
		List<BdTrainTaskAttachment> uploadTaskList = bdTrainTaskAttachmentService.getUploadTaskList(taskId);
		return Result.OK(uploadTaskList);
	}

	/**
	 *   添加
	 *
	 * @param bdTrainTaskAttachment
	 * @return
	 */
	@AutoLog(value = "培训任务附件-添加")
	@ApiOperation(value="培训任务附件-添加", notes="培训任务附件-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BdTrainTaskAttachment bdTrainTaskAttachment) {
		String filename = bdTrainTaskAttachment.getFileName();
		String filePath = bdTrainTaskAttachment.getFilePath();
		if (StrUtil.isNotEmpty(filePath)){
			List<String> list = Arrays.asList(filePath.split(","));
			for (int i = 0; i < list.size() ; i++) {
				bdTrainTaskAttachment.setFilePath(list.get(i));
				bdTrainTaskAttachment.setFileName(Arrays.asList(filename.split(",")).get(i));
				bdTrainTaskAttachment.setId(null);
				bdTrainTaskAttachmentService.save(bdTrainTaskAttachment);
			}
		}
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param bdTrainTaskAttachment
	 * @return
	 */
	@AutoLog(value = "培训任务附件-编辑")
	@ApiOperation(value="培训任务附件-编辑", notes="培训任务附件-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BdTrainTaskAttachment bdTrainTaskAttachment) {
		bdTrainTaskAttachmentService.updateById(bdTrainTaskAttachment);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "培训任务附件-通过id删除")
	@ApiOperation(value="培训任务附件-通过id删除", notes="培训任务附件-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bdTrainTaskAttachmentService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "培训任务附件-批量删除")
	@ApiOperation(value="培训任务附件-批量删除", notes="培训任务附件-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bdTrainTaskAttachmentService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "培训任务附件-通过id查询")
	@ApiOperation(value="培训任务附件-通过id查询", notes="培训任务附件-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BdTrainTaskAttachment bdTrainTaskAttachment = bdTrainTaskAttachmentService.getById(id);
		if(bdTrainTaskAttachment==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bdTrainTaskAttachment);
	}
    /**
    * 导出excel
    *
    * @param request
    * @param bdTrainTaskAttachment
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BdTrainTaskAttachment bdTrainTaskAttachment) {
        return super.exportXls(request, bdTrainTaskAttachment, BdTrainTaskAttachment.class, "培训任务附件");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BdTrainTaskAttachment.class);
    }

}
