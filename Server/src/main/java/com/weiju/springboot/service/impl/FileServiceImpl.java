package com.weiju.springboot.service.impl;

import com.weiju.springboot.exception.BaseException;
import com.weiju.springboot.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.stream.Stream;

/**
 * from:
 * https://github.com/spring-guides/gs-uploading-files/blob/master/initial/src/main/java/hello/storage/FileSystemStorageService.java
 */
@Service("File Service")
public class FileServiceImpl implements FileService {
    @Override
    public void init(Path path) throws BaseException {
        try {
            Files.createDirectories(path);
        } catch (FileAlreadyExistsException e1) {
            System.out.println("already exist");
        } catch (IOException e) {
            throw new BaseException("Could not initlize storage", HttpStatus.INTERNAL_SERVER_ERROR, e);


        }
    }

    /**
     * File
     * path 存放文件的目录 /xxx
     * newFilename 存放文件的新名字
     * output: /xxx/newFilename
     */
    @Override
    public URL store(MultipartFile file, Path path, String newFilename) throws BaseException {
        try {
            if (file.isEmpty()) {
                throw new BaseException("Failed to store empty file" + file.getOriginalFilename(), HttpStatus.NOT_FOUND);
            }
            init(path);
            Files.copy(file.getInputStream(), path.resolve(newFilename));
            return path.resolve(newFilename).toUri().toURL();
        } catch (IOException e) {
            throw new BaseException("Failed to store file" + file.getOriginalFilename(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 根据文件路径+文件名，获取文件的MediaType
     * @param filePath
     * @return
     * @throws IOException
     */
    @Override
    public MediaType getFileType(String filePath) throws IOException {
        String contentType = Files.probeContentType(Paths.get(filePath));
        String[] base_sub = contentType.split("/");
        String type = base_sub[0];
        String subtype = base_sub[1];
        return new MediaType(type, subtype);
    }

    @Override
    public Stream<Path> loadAll(Path path) throws BaseException {
        try {
            return Files.walk(path, 1).filter(path1 -> !path.equals(path)).map(path1 -> path.relativize(path1));

        } catch (IOException e) {
            throw new BaseException("Fileed to read stored files", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Path load(Path path, String filename) {
        return path.resolve(filename);
    }

    @Override
    public Resource loadAsResource(Path path, String filename) throws BaseException {
        try {
            Path file = load(path, filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new BaseException("Could not read file: " + filename, HttpStatus.INTERNAL_SERVER_ERROR);

            }
        } catch (MalformedURLException e) {
            throw new BaseException("Could not read file: " + filename, HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    public void deleteAll(Path path) {
        FileSystemUtils.deleteRecursively(path.toFile());
    }

    @Override
    public String getNewfilename(String oldfilename) {
        return Instant.now().toString().replace(":", "-") + oldfilename;
    }
}
