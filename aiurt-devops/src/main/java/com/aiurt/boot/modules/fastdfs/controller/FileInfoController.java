package com.aiurt.boot.modules.fastdfs.controller;

import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.modules.fastdfs.entity.FileInfo;
import com.swsc.copsms.modules.fastdfs.model.FileInfoVo;
import com.swsc.copsms.modules.fastdfs.model.UploadFile;
import com.swsc.copsms.modules.fastdfs.service.IFileInfoService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 附件表
 * @Author: swsc
 * @Date:   2020-10-23
 * @Version: V1.0
 */
@Slf4j
@Api(tags="附件表")
@RestController
@RequestMapping("/fastdfs/fileInfo")
public class FileInfoController {
	@Autowired
	private IFileInfoService fileInfoService;

	/**
	  * 分页列表查询
	 * @param fileInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "附件表-分页列表查询")
	@ApiOperation(value="附件表-分页列表查询", notes="附件表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FileInfo>> queryPageList(FileInfo fileInfo,
												 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												 HttpServletRequest req) {
		Result<IPage<FileInfo>> result = new Result<IPage<FileInfo>>();
		/*QueryWrapper<FileInfo> queryWrapper = QueryGenerator.initQueryWrapper(fileInfo, req.getParameterMap());
		Page<FileInfo> page = new Page<FileInfo>(pageNo, pageSize);
		IPage<FileInfo> pageList = fileInfoService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);*/
		return result;
	}

	 /**
	  * 文件上传
	  * 根据fileType选择上传方式
	  *
	  * @param file
	  * @return
	  * @throws Exception
	  */
	 @AutoLog(value = "文件上传")
	 @ApiOperation(value="附件表-文件上传", notes="附件表-文件上传")
	 @PostMapping("/upload")
	 public Result<?> upload(@RequestParam(value = "file", required = false) MultipartFile file) {
		 try {
			 FileInfo fileInfo = fileInfoService.uploadFile(file);
			 FileInfoVo vo = new FileInfoVo();
			 BeanUtil.copyProperties(fileInfo, vo);
			 Result<FileInfoVo> result = new Result<>();
			 result.setResult(vo);
			 return result;
		 } catch (Exception e) {
			 return Result.error("上传失败！");
		 }
	 }
	 @AutoLog(value = "文件上传")
	 @ApiOperation(value="附件表-文件上传", notes="附件表-文件上传")
	 @PostMapping("/uploadfile")
	 public Result<?> upload(@RequestBody UploadFile file) {
		 FileInfo fileInfo = fileInfoService.uploadFile(file);
		 FileInfoVo vo = new FileInfoVo();
		 BeanUtil.copyProperties(fileInfo, vo);
		 return Result.ok(vo);
	 }

	 /**
	  * 文件删除
	  *
	  * @param md5
	  */
	 @AutoLog(value = "文件删除")
	 @ApiOperation(value="附件表-文件删除", notes="附件表-文件删除")
	 @PostMapping("/del")
	 public Result<?> delete(@RequestParam(value = "md5", required = true) String md5) {
		 try {
			 fileInfoService.deleteFile(md5);
			 return Result.ok("操作成功");
		 } catch (Exception ex) {
			 return Result.error("操作失败");
		 }
	 }
}
