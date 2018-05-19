package com.weiju.springboot.controller;

import com.weiju.springboot.exception.BaseException;
import com.weiju.springboot.model.DataMetaErr;
import com.weiju.springboot.service.FileService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 上传文件和获取文件
 * <p>
 * 文件存储路径： /data
 */
@Controller
@RequestMapping("api/file")
public class FileController {
    private static Logger logger = LoggerFactory.getLogger(FileController.class);
    private final FileService fileService;

    private final String TMP_PATH = "/temp";

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;

    }


    /**
     * 相对于/data
     * 根据相对路径+文件名获取文件
     *
     * @param filePath 文件相对路径
     * @return Resource
     * @throws BaseException
     */
    private ResponseEntity<Resource> getFile(String filePath) throws BaseException {
        try {
            logger.info("try to get file : " + filePath);
            Resource resource = fileService.getFile(filePath);
            return ResponseEntity.ok().contentType(fileService.getFileType(filePath)).body(resource);
        } catch (MalformedURLException e) {
            throw new BaseException("File not found", HttpStatus.NOT_FOUND);

        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseException("IO exception", HttpStatus.NOT_FOUND);
        }
    }


    /**
     * 临时的文件上传，返回获得文件的完整url
     * 写死了服务器的ip 需要在配置文件中说明服务器ip
     * 用于测试
     *
     * @param multipartFiles
     * @return
     * @throws BaseException
     */
    @PostMapping(value = "/")
    public ResponseEntity<String> uploadTempFile(@RequestParam("file") List<MultipartFile> multipartFiles) throws BaseException {
        logger.info("request uploadFile");

        List<String> file_urls = fileService.uploadFiles(multipartFiles, TMP_PATH);
        JSONObject response = new JSONObject();
        response.put("data", file_urls);
        logger.info(response.toString());
        return new ResponseEntity<>(response.toString(), HttpStatus.CREATED);
    }


    /**
     * 根据文件名获取临时文件
     * 用于测试
     *
     * @param filename
     * @return file
     * @throws BaseException
     */
    @GetMapping(value = "/temp/{filename}")
    public ResponseEntity<Resource> getTempFile(@PathVariable("filename") String filename) throws BaseException {

        return getFile(Paths.get(TMP_PATH).resolve(filename).toString());

    }

    /**
     * 根据文件名获取任务的图片
     *
     * @param filename
     * @return
     */
    @GetMapping(value = "/tasks/{taskid}/pictures/{filename}")
    public ResponseEntity<Resource> getTaskFile(@PathVariable("filename") String filename, @PathVariable("taskid") int taskid) throws BaseException {
        String id = String.valueOf(taskid);
        String filepath = Paths.get("tasks/" + id + "/pictures/" + filename).toString();
        return getFile(filepath);
    }

    /**
     * 获取用户上传的xml文件
     *
     * @param taskid
     * @param pictureName
     * @param xmlName
     * @return
     */
    @GetMapping(value = "/tasks/{taskid}/xmls/{pictureName}/{xmlName}")
    public ResponseEntity<Resource> getXMLFIle(@PathVariable("taskid") String taskid,
                                               @PathVariable("pictureName") String pictureName,
                                               @PathVariable("xmlName") String xmlName) throws BaseException {
        String filepath = Paths.get("tasks/" + taskid + "/xmls/" + pictureName + "/" + xmlName).toString();
        return getFile(filepath);
    }


}
