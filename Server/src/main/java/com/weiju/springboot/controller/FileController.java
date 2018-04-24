package com.weiju.springboot.controller;

import com.weiju.springboot.exception.BaseException;
import com.weiju.springboot.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;


@Controller
@RequestMapping("api/file")
public class FileController {
    private static Logger logger = LoggerFactory.getLogger(FileController.class);
    private final FileService fileService;
    private final String BASE_PATH = Paths.get(".").toAbsolutePath().toString();
    private final String TMP_PATH = "/Data/temp";

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "")
    public ResponseEntity<String> uploadFile(@RequestParam("file") List<MultipartFile> multipartFiles) throws BaseException {
        logger.info("request uploadFile");
        Path path = Paths.get(BASE_PATH, TMP_PATH);
        logger.info("get store path" + path.toString());
        try {
            Iterator iterator = multipartFiles.iterator();
            while (iterator.hasNext()) {
                MultipartFile file = (MultipartFile) iterator.next();
                String newFilename = fileService.getNewfilename(file.getOriginalFilename());
                fileService.store(file, path, newFilename);
                logger.info("stored");
            }

        } catch (BaseException e) {
            e.printStackTrace();
        }
        return null;
    }


}
