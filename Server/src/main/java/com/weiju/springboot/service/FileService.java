package com.weiju.springboot.service;

import com.weiju.springboot.exception.BaseException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileService {


    void init(Path path) throws BaseException;

    void store(MultipartFile file, Path path, String newFilename) throws BaseException;


    Stream<Path> loadAll(Path path) throws BaseException;


    Path load(Path path, String filename);


    Resource loadAsResource(Path path, String filename) throws BaseException;


    void deleteAll(Path path);
}
