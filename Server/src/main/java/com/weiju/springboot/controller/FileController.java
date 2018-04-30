package com.weiju.springboot.controller;

import com.weiju.springboot.exception.BaseException;
import com.weiju.springboot.service.FileService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/*
* 支持上传文件临时存储
* 返回文件URL
* */
@Controller
@RequestMapping("api/file")
public class FileController {
    private static Logger logger = LoggerFactory.getLogger(FileController.class);
    private final FileService fileService;
    private final String BASE_PATH = Paths.get(".").toAbsolutePath().toString();
    private final String DATA_PATH = "/data/temp";
    private final String TMP_PATH = "/src/main/resources/static" + DATA_PATH;
    private final Environment environment; // 监听应用的ip:port

    private final ResourceLoader resourceLoader;

    @Autowired
    public FileController(FileService fileService, ResourceLoader resourceLoader, Environment environment) {
        this.fileService = fileService;
        this.resourceLoader = resourceLoader;
        this.environment = environment;
    }

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
                url_list.put(file.getOriginalFilename(), "http://" + ip.getHostAddress() + ":" + port + DATA_PATH + "/" + newFilename);
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


}
