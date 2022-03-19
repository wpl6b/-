package com.imooc.files.controller;

import com.imooc.api.controller.files.FileUploaderControllerApi;
import com.imooc.exception.GraceException;
import com.imooc.files.FileResource;
import com.imooc.files.service.UploaderService;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.bo.NewAdminBO;
import com.imooc.utils.FileUtils;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;

@RestController
public class FileUploaderController implements FileUploaderControllerApi {
    final static Logger logger = LoggerFactory.getLogger(FileUploaderController.class);

    @Autowired
    private UploaderService uploaderService;

    @Autowired
    private FileResource fileResource;

    @Autowired
    private GridFSBucket gridFSBucket;

    //上传头像
    @Override
    public GraceJSONResult uploadFace(String userId, MultipartFile file) throws IOException {
        String path;
        if (file != null) {
            String fileName = file.getOriginalFilename();
            if (StringUtils.isNotBlank(fileName)) {
                String[] fileNameArr = fileName.split("\\.");
                String extName = fileNameArr[fileNameArr.length - 1];
                if (!"PNG".equalsIgnoreCase(extName) && !"JPG".equalsIgnoreCase(extName) && !"JPEG".equalsIgnoreCase(extName)) {
                    return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_FORMATTER_FAILD);
                }
                path = uploaderService.uploadFiles(file, extName);
            } else return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);

        } else return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);

        logger.info("文件存储路径： " + path);
        String finalPath = fileResource.getHost() + path;
        logger.info("最终路径： " + finalPath);

        return GraceJSONResult.ok(finalPath);
    }

    //上传人脸
    @Override
    public GraceJSONResult uploadToGridFS(NewAdminBO newAdminBO) throws IOException {
        String img64 = newAdminBO.getImg64();
        byte[] bytes = new BASE64Decoder().decodeBuffer(img64.trim());
        ObjectId fileId = gridFSBucket.uploadFromStream(newAdminBO.getUsername() + ".png", new ByteArrayInputStream(bytes));
        return GraceJSONResult.ok(fileId.toString());
    }

    @Override
    public void readInGridFS(String faceId, HttpServletResponse response) {
        if (StringUtils.isBlank(faceId) || "null".equalsIgnoreCase(faceId))
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        File adminFace = readGridFSByFaceId(faceId);

        //文件流下载，在浏览器展示
        FileUtils.downloadFileByStream(response, adminFace);

    }

    //读取文件并保存在本地或服务器
    private File readGridFSByFaceId(String faceId) {

        GridFSFindIterable gridFSFiles = gridFSBucket.find(Filters.eq("_id", new ObjectId(faceId)));

        GridFSFile gridFSFile = gridFSFiles.first();

        if (gridFSFile == null) GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        String filename = gridFSFile.getFilename();
        System.out.println(filename);

        File myFile = null;
        FileOutputStream myFileOutputStream = null;

        try {
            myFile = new File("D:\\Spring Cloud分布式微服务实战L259\\gridFSFileTemp\\" + filename);
            myFileOutputStream = new FileOutputStream(myFile);
            gridFSBucket.downloadToStream(new ObjectId(faceId), myFileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (myFileOutputStream != null) {
                try {
                    myFileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return myFile;
    }

    @Override
    public GraceJSONResult readFace64InGridFS(String faceId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        File file = readGridFSByFaceId(faceId);
        String base64 = FileUtils.fileToBase64(file);
        return GraceJSONResult.ok(base64);
    }

    @Override
    public GraceJSONResult uploadSomeFiles(String userId, MultipartFile[] files) throws Exception {

        ArrayList<String> imgUrlList = new ArrayList<>();
        for (MultipartFile file :
                files) {
            String path;
            if (file != null) {
                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    String[] fileNameArr = fileName.split("\\.");
                    String extName = fileNameArr[fileNameArr.length - 1];
                    if (!"PNG".equalsIgnoreCase(extName) && !"JPG".equalsIgnoreCase(extName) && !"JPEG".equalsIgnoreCase(extName)) {
                        continue;
                    }
                    path = uploaderService.uploadFiles(file, extName);
                } else continue;

            } else continue;

            logger.info("文件存储路径： " + path);
            String finalPath = fileResource.getHost() + path;
            logger.info("最终路径： " + finalPath);

            imgUrlList.add(finalPath);
        }


        return GraceJSONResult.ok(imgUrlList);

    }
}
