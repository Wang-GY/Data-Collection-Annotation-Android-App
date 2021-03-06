package com.weiju.springboot.service;

import com.weiju.springboot.exception.BaseException;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface FileService {


    List<String> uploadFiles(List<MultipartFile> multipartFiles, String relativePath);

    Resource getFile(String relativePath) throws MalformedURLException;

    void init(Path path) throws BaseException;


    void storeString(String data, String filename, String relativePath) throws FileNotFoundException, BaseException;

    String getRelativePathByUrl(String url);

    String getParentPath(String path);

    String getFilenameByRelativePath(String relativePath);

    String getFilenameByUrl(String url);

    String relativePathToUrl(String relativePath);

    /**
     * File
     * path 存放文件的目录 /xxx
     * newFilename 存放文件的新名字
     * output: /xxx/newFilename
     */
    String store(MultipartFile file, Path path) throws BaseException;

    MediaType getFileType(String filePath) throws IOException;

    //Stream<Path> loadAll(Path path) throws BaseException;


    Path load(Path path, String filename);


    //Resource loadAsResource(Path path, String filename) throws BaseException;


    //void deleteAll(Path path);

    String getNewFilename(String oldfilename) throws BaseException;

    String getFileTypeByFileName(String fileName) throws BaseException;
}
