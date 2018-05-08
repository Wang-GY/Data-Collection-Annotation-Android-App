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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 上传临时文件和获取临时文件
 * 用于测试
 * 文件存储路径： /data/temp
 */
@Controller
@RequestMapping("api/file")
public class FileController {
    private static Logger logger = LoggerFactory.getLogger(FileController.class);
    private final FileService fileService;
    private final String BASE_PATH = Paths.get(".").toAbsolutePath().toString();
    private final String DATA_PATH = "";
    private final String TMP_PATH = "/data/temp" + DATA_PATH;
    private final Environment environment; // 监听应用的ip:port

    private final ResourceLoader resourceLoader;

    @Autowired
    public FileController(FileService fileService, ResourceLoader resourceLoader, Environment environment) {
        this.fileService = fileService;
        this.resourceLoader = resourceLoader;
        this.environment = environment;
    }

    /**
     * 临时的文件上传，返回获得文件的完整url
     * 写死了服务器的ip 需要处理
     * 
     * @param multipartFiles
     * @return
     * @throws BaseException
     */
    @PostMapping(value = "")
    public ResponseEntity<String> uploadFile(@RequestParam("file") List<MultipartFile> multipartFiles) throws BaseException {
        logger.info("request uploadFile");
        Path path = Paths.get(BASE_PATH, TMP_PATH);
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
                url_list.put(file.getOriginalFilename(), "http://" + "206.189.35.98" + ":" + port + DATA_PATH + "/api/file/" + newFilename);
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
     * get file by file name
     * @param  filename
     * @return file
     * @throws BaseException
     */
    @GetMapping(value = "/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable("filename") String filename) throws BaseException{
        try {
            // 获取文件路径
            String filePath = Paths.get(BASE_PATH,TMP_PATH).resolve(filename).toString();

            UrlResource resource = new FileUrlResource(filePath);
            String contentType = Files.probeContentType(Paths.get(filePath));
            String[] base_sub = contentType.split("/");
            String type = base_sub[0];
            String subtype = base_sub[1];
            return ResponseEntity.ok().contentType(new MediaType(type, subtype)).body(resource);
        } catch (MalformedURLException e) {
            throw new BaseException("File not found", HttpStatus.NOT_FOUND);

        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseException("IO exception", HttpStatus.NOT_FOUND);
        }
    }

}
