package com.imooc.api.controller.files;

import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.bo.NewAdminBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api(value = "文件上传的Controller", tags = {"文件上传的Controller"})
@RequestMapping("fs")
public interface FileUploaderControllerApi {

    @ApiOperation(value = "上传用户头像", notes = "上传用户头像", httpMethod = "POST")
    @PostMapping("/uploadFace")
    public GraceJSONResult uploadFace(@RequestParam String userId, MultipartFile file) throws IOException;

//    @ApiOperation(value = "上传人脸", notes = "上传人脸", httpMethod = "POST")
    @PostMapping("/uploadToGridFS")
    public GraceJSONResult uploadToGridFS(@RequestBody NewAdminBO newAdminBO) throws IOException;

    @ApiOperation(value = "从gridFS读取人脸", notes = "读取人脸", httpMethod = "GET")
    @GetMapping("/readInGridFS")
    public void readInGridFS(@RequestParam String faceId, HttpServletResponse response);

    /**
     * 从gridfs中读取图片内容，并且返回base64
     * @param faceId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping("/readFace64InGridFS")
    public GraceJSONResult readFace64InGridFS(@RequestParam String faceId, HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 上传多个文件
     * @param userId
     * @param files
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadSomeFiles")
    public GraceJSONResult uploadSomeFiles(@RequestParam String userId,
                                           MultipartFile[] files) throws Exception;
}
