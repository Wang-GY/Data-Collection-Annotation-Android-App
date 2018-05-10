package com.weiju.springboot.service.impl;

import com.weiju.springboot.exception.BaseException;
import com.weiju.springboot.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileUrlResource;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


/**
 * from:
 * https://github.com/spring-guides/gs-uploading-files/blob/master/initial/src/main/java/hello/storage/FileSystemStorageService.java
 */
@Service("File Service")
public class FileServiceImpl implements FileService {
    private static Logger logger = LoggerFactory.getLogger(FileService.class);
    private final String BASE_PATH = Paths.get("./data").toAbsolutePath().toString();


    @Autowired
    private final Environment environment; // 监听应用的ip:port

    public FileServiceImpl(Environment environment) {
        this.environment = environment;
    }

    /**
     * 一个基本的文件上传方法
     *
     * @param multipartFiles
     * @param relativePath   需要存放文件的相对路径（相对于/data）
     * @return 新文件的url列表
     */
    @Override
    public Map<String, String> uploadFiles(List<MultipartFile> multipartFiles, String relativePath) {
        LinkedHashMap<String, String> url_list = new LinkedHashMap<>();
        String port = environment.getProperty("local.server.port");
        logger.info("BASE PATH: "+BASE_PATH.toString());
        try {
            Iterator iterator = multipartFiles.iterator();
            while (iterator.hasNext()) {
                MultipartFile file = (MultipartFile) iterator.next();
                String newFilename = store(file, Paths.get(BASE_PATH,relativePath));
                // TODO get real server ip
                url_list.put(file.getOriginalFilename(), "http://" + "206.189.35.98" + ":" + port + "/api/file"  + relativePath + "/" + newFilename);
                logger.info("stored into "+Paths.get(BASE_PATH,relativePath).toString());
            }
            return url_list;
        } catch (BaseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据文件相对路径获取文件
     * @param relativePath 相对于 /data 的路径
     * @return
     */
    @Override
    public Resource getFile(String relativePath) throws MalformedURLException {
        String filePath = Paths.get(BASE_PATH,relativePath).toString();
        UrlResource resource = new FileUrlResource(filePath);
        return resource;
    }

    /**
     * 初始化文件传输路径
     * @param path
     * @throws BaseException
     */
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


    @Override
    public String store(MultipartFile file, Path path) throws BaseException {

        try {
            if (file.isEmpty()) {
                throw new BaseException("Failed to store empty file" + file.getOriginalFilename(), HttpStatus.NOT_FOUND);
            }
            init(path);
            String newFilename = getNewfilename(file.getOriginalFilename());
            Files.copy(file.getInputStream(), path.resolve(newFilename));
            return newFilename;
        } catch (IOException e) {
            throw new BaseException("Failed to store file" + file.getOriginalFilename(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 根据文件路径+文件名，获取文件的MediaType
     *
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
