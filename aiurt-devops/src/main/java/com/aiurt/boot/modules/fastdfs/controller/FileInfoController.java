package com.aiurt.boot.modules.fastdfs.controller;

import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.modules.fastdfs.entity.FileInfo;
import com.aiurt.boot.modules.fastdfs.model.FileInfoVo;
import com.aiurt.boot.modules.fastdfs.model.UploadFile;
import com.aiurt.boot.modules.fastdfs.service.IFileInfoService;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.entity.SysUserCard;
import com.aiurt.boot.modules.system.service.ISysUserCardService;
import com.aiurt.boot.modules.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

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
	@Autowired
	private ISysUserCardService sysUserCardService;
	@Autowired
	private ISysUserService sysUserService;
	@Value("${support.path.pic}")
	String path;
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

	@Transactional(rollbackFor = Exception.class)
	@GetMapping(value = "/uploadCards")
	public Result<?>uploadCards(){
		this.uploadCard(this.path);
		return Result.ok();
	}
	private void uploadCard(String path) {

		File file = new File(path);
		if (file.exists()) {
			File[] files = file.listFiles();
			if (null == files || files.length == 0) {
				return;
			} else {
				for (File file2 : files) {
					if (file2.isDirectory()) {
						uploadCard(file2.getAbsolutePath());
					} else {
						String[] s = file2.getAbsolutePath().split("/");
						String fileName = s[s.length - 2] +"的"+ s[s.length - 1];
						String realname = s[s.length - 2];
						System.out.println("文件:" + file2.getAbsolutePath() + "        " + "文件名:" + fileName);
						LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
						queryWrapper.eq(SysUser::getRealname,realname).eq(SysUser::getDelFlag,0);
						SysUser user = sysUserService.getOne(queryWrapper);
						if (ObjectUtil.isEmpty(user)){
							continue;
						}
						UploadFile uploadFile = new UploadFile();
						//将file转成byte[]
						byte[] bytes = null;
						long size = 0;
						try {
							FileInputStream fis = new FileInputStream(file2);
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							byte[] b = new byte[1024];
							int n;
							while ((n = fis.read(b)) != -1) {
								bos.write(b, 0, n);
							}
							fis.close();
							bos.close();
							bytes = bos.toByteArray();
						} catch (Exception e) {
							e.printStackTrace();
						}
						uploadFile.setBytes(bytes);
						uploadFile.setSize(file2.length());
						uploadFile.setName(fileName);
						uploadFile.setContentType("image/jpeg");
						uploadFile.setIsImg(true);
						FileInfo fileInfo = fileInfoService.uploadFile(uploadFile);
						SysUserCard card = new SysUserCard();
						card.setUrl(fileInfo.getUrl());
						card.setUrl(fileInfo.getUrl());
						if (ObjectUtil.isNotEmpty(user)){
							card.setUserId(user.getId());
						}
						String s1 = s[s.length - 1];
						String s2 = realname+"的"+s1;
						card.setName(s2);
						card.setType(s1.split("\\.")[0]);
						System.out.println(card);
						sysUserCardService.save(card);
					}
				}
			}
		} else {
			System.out.println("文件不存在!");
		}
	}


}
