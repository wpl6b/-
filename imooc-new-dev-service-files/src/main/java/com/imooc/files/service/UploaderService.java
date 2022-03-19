package com.imooc.files.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface UploaderService {

    public String uploadFiles(MultipartFile file, String FileExtName) throws IOException;
}
