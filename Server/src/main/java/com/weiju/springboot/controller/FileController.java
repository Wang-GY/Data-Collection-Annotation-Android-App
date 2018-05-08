package com.weiju.springboot.controller;

import com.weiju.springboot.exception.BaseException;
import com.weiju.springboot.service.FileService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * 上传文件和获取文件
 *
 * 文件存储路径： /data/temp
 */
@Controller
@RequestMapping("api/file")
public class FileController {
    private static Logger logger = LoggerFactory.getLogger(FileController.class);
    private final FileService fileService;
    private final String BASE_PATH = Paths.get(".").toAbsolutePath().toString();
    private final String DATA_PATH = "/data";
    private final String TMP_PATH =   DATA_PATH + "/temp";
    private final Environment environment; // 监听应用的ip:port

    private final ResourceLoader resourceLoader;

    @Autowired
    public FileController(FileService fileService, ResourceLoader resourceLoader, Environment environment) {
        this.fileService = fileService;
        this.resourceLoader = resourceLoader;
        this.environment = environment;
    }

    /**
     * 基本的文件上传方法，需要提供目标路径
     * @param multipartFiles
     * @param path
     * @return ResponseEntity<String>
     * @throws BaseException
     */
    private ResponseEntity<String> uploadFile( List<MultipartFile> multipartFiles , Path path , String relativePath) throws BaseException {
        LinkedHashMap<String, String> url_list = new LinkedHashMap<>();
        logger.info("get store path" + path.toString());

        String port = environment.getProperty("local.server.port");
        InetAddress ip = null;
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new BaseException("InteAdress error", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
        try {
            Iterator iterator = multipartFiles.iterator();
            while (iterator.hasNext()) {
                MultipartFile file = (MultipartFile) iterator.next();
                String newFilename = fileService.getNewfilename(file.getOriginalFilename());
                URL url = fileService.store(file, path, newFilename);
                // TODO get real server ip
                url_list.put(file.getOriginalFilename(), "http://" + "206.189.35.98" + ":" + port+"/api/file" + DATA_PATH +relativePath+ "/"+ newFilename);
                logger.info("stored");
                logger.info(url.toString());
            }

        } catch (BaseException e) {
            e.printStackTrace();
        }

        JSONObject response = new JSONObject();
        response.put("data", url_list);
        return new ResponseEntity<>(response.toString(), HttpStatus.CREATED);
    }

    /**
     * 根据完整文件路径和文件名获取文件
     * @param filePath
     * @return Resource
     * @throws BaseException
     */
    private ResponseEntity<Resource> getFile(String filePath) throws BaseException {
        try {
            logger.info("try to get file : "+filePath);
            UrlResource resource = new FileUrlResource(filePath);
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
     *用于测试
     * @param multipartFiles
     * @return
     * @throws BaseException
     */
    @PostMapping(value = "")
    public ResponseEntity<String> uploadTempFile(@RequestParam("file") List<MultipartFile> multipartFiles) throws BaseException {
        logger.info("request uploadFile");
        Path path = Paths.get(BASE_PATH, TMP_PATH);
        return  uploadFile(multipartFiles,path,"/temp");
    }




    /**
     * 根据文件名获取临时文件
     * 用于测试
     * @param  filename
     * @return file
     * @throws BaseException
     */
    @GetMapping(value = "/data/temp/{filename}")
    public ResponseEntity<Resource> getTempFile(@PathVariable("filename") String filename) throws BaseException{

        return getFile(Paths.get(BASE_PATH,TMP_PATH).resolve(filename).toString());

    }



    /**
     * 根据文件名获取任务的图片
     * @param filename
     * @return
     */
    @GetMapping(value = "/data/tasks/{taskid}/pictures/{filename}")
    public ResponseEntity<Resource> getTaskFile(@PathVariable("filename") String filename,@PathVariable("taskid") int taskid) throws BaseException {
        String id = String.valueOf(taskid);
        String filepath = Paths.get(BASE_PATH,DATA_PATH+"/tasks/" + id + "/pictures/" + filename).toString();
        return getFile(filepath);
    }

    /**
     * 上传图片到任务
     * @param taskid
     * @return
     */
    @PostMapping(value = "/tasks/pictures/{taskid}")
    public ResponseEntity<String> uploadTaskFile(@PathVariable("taskid") int taskid, @RequestParam("file")List<MultipartFile> multipartFiles) throws BaseException {
        logger.info("request uploadFile");
        String id = String.valueOf(taskid);
        String relativePath = "/tasks/" + id + "/pictures";
        Path path = Paths.get(BASE_PATH, DATA_PATH + relativePath);
        return uploadFile(multipartFiles,path,relativePath);
    }
}
