package com.weiju.springboot.controller;

import com.weiju.springboot.exception.BaseException;
import com.weiju.springboot.service.FileService;
import com.weiju.springboot.util.FileProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;


@RestController
@RequestMapping("/api/pictures")
public class PictureController {

    private final FileService fileService;
    private final FileProperties fileProperties;

    @Autowired
    public PictureController(FileService fileService, FileProperties fileProperties) {
        this.fileService = fileService;
        this.fileProperties = fileProperties;
    }

    @GetMapping(value = "/{picture_url}")
    public ResponseEntity<Resource> getPicture(@PathVariable("picture_url") String picture_url) throws BaseException {
        System.out.println(picture_url);
        try {
            UrlResource resource = new FileUrlResource(getNewPicturePath(picture_url));
            String contentType = Files.probeContentType(Paths.get(getNewPicturePath(picture_url)));
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

    @PostMapping(value = "")
    public ResponseEntity<String> uploadPicture(@RequestParam("file") MultipartFile file, @RequestParam("task_id") String task_id) throws BaseException {
        Path filepath = fileProperties.getTaskPicturePath(Integer.valueOf(task_id));
        // TODO
        String new_name = getNewFilename(file, task_id);
        URL url = fileService.store(file, filepath, new_name);

        return new ResponseEntity<String>(new_name, HttpStatus.OK);
    }

    private String getNewFilename(MultipartFile file, String task_id) {
        return task_id + "-" + Instant.now().toString().replace(":", "-") + file.getOriginalFilename();
    }

    private String getNewPicturePath(String new_file_name) {
        String[] sp = new_file_name.split("-");
        return fileProperties.getTaskPicturePath(Integer.valueOf(sp[0])).resolve(new_file_name).toString();
    }


}
