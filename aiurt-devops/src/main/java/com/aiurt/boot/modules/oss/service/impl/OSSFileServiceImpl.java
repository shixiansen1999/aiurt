package com.aiurt.boot.modules.oss.service.impl;

import com.aiurt.common.util.oss.OssBootUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.modules.oss.entity.OSSFile;
import com.aiurt.boot.modules.oss.mapper.OSSFileMapper;
import com.aiurt.boot.modules.oss.service.IOSSFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service("ossFileService")
public class OSSFileServiceImpl extends ServiceImpl<OSSFileMapper, OSSFile> implements IOSSFileService {

	@Override
	public void upload(MultipartFile multipartFile) throws IOException {
		String fileName = multipartFile.getOriginalFilename();
		OSSFile ossFile = new OSSFile();
		ossFile.setFileName(fileName);
		String url = OssBootUtil.upload(multipartFile,"upload/test");
		ossFile.setUrl(url);
		this.save(ossFile);
	}

	@Override
	public boolean delete(OSSFile ossFile) {
		try {
			this.removeById(ossFile.getId());
			OssBootUtil.deleteUrl(ossFile.getUrl());
		}
		catch (Exception ex) {
			return false;
		}
		return true;
	}

}
