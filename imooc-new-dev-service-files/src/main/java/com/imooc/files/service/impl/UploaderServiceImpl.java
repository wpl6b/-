package com.imooc.files.service.impl;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.imooc.files.service.UploaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UploaderServiceImpl implements UploaderService {

    @Autowired
    public FastFileStorageClient fastFileStorageClient;

    @Override
    public String uploadFiles(MultipartFile file, String FileExtName) throws IOException {
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), FileExtName, null);

        return storePath.getFullPath();
    }
}
