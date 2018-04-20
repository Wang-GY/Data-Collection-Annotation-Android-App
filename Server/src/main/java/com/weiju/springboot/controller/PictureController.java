package com.weiju.springboot.controller;

import com.weiju.springboot.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;

@RestController
@RequestMapping("/api/pictures")
public class PictureController {

    private final FileService fileService;

    @Autowired
    public PictureController(FileService fileService) {
        this.fileService = fileService;
    }

//    @GetMapping(value = "/{picture_url}")
//    public ResponseEntity<Resource> getPicture(@PathVariable("picture_url") String picture_url) {
//
//        InputStreamResource resource = new InputStreamResource(new FileInputStream())
//
//    }

    
}
