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

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
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
    public List<String> uploadFiles(List<MultipartFile> multipartFiles, String relativePath) {
        List<String> url_list = new LinkedList<>();
        String port = environment.getProperty("local.server.port");
        logger.info("BASE PATH: " + BASE_PATH.toString());
        try {
            Iterator iterator = multipartFiles.iterator();
            while (iterator.hasNext()) {
                MultipartFile file = (MultipartFile) iterator.next();
                String newFilename = store(file, Paths.get(BASE_PATH, relativePath));
                // TODO get real server ip
                url_list.add("http://" + "206.189.35.98" + ":" + port + "/api/file" + relativePath + "/" + newFilename);
                logger.info("stored into " + Paths.get(BASE_PATH, relativePath).toString());
            }
            return url_list;
        } catch (BaseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据文件相对路径获取文件
     *
     * @param relativePath 相对于 /data 的路径
     * @return
     */
    @Override
    public Resource getFile(String relativePath) throws MalformedURLException {
        String filePath = Paths.get(BASE_PATH, relativePath).toString();
        UrlResource resource = new FileUrlResource(filePath);
        return resource;
    }

    /**
     * 初始化文件传输路径
     *
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


    /**
     * 将一个字符串写入文件并且储存
     *
     * @param data         待写入的字符串
     * @param filename     写入的文件名
     * @param relativePath 文件存储的相对路径 （/data）
     */
    @Override
    public void storeString(String data, String filename, String relativePath) throws FileNotFoundException, BaseException {
        Path path = Paths.get(BASE_PATH, relativePath);
        init(path);// create path if not exist
        InputStream inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        try {
            Files.copy(inputStream, path.resolve(filename));
        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseException("IOException", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }


    }

    /**
     * 根据url 获取资源在服务器上的相对路径
     *
     * @param url
     * @return 相对路径 (/data)
     */
    @Override
    public String getRelativePathByUrl(String url) {
        boolean is_path = false;
        String relativePath = "";
        String[] path = url.split("/");
        for (String item : path) {
            if (item.equals("file")) { // check the "file" in /api/file. should show up first
                is_path = true;
                continue;// don't want file
            }
            if (is_path) {
                relativePath = relativePath + "/" + item;
            }
        }
        return relativePath;
    }

    /**
     * 传入当前路径，返回上级路径
     *
     * @param path
     * @return
     */
    @Override
    public String getParentPath(String path) {
        String items[] = path.split("/");
        String parentPath = "";
        // TODO why item[0] is ""
        for (int i = 1; i < items.length - 1; i++) { // start from 1 :  why item[0] is ""
            logger.info(items[i]);
            parentPath = parentPath + "/" + items[i];
        }
        return parentPath;
    }

    /**
     * 根据文件的相对路径得到文件名
     *
     * @param relativePath 文件的相对路径
     * @return 文件名
     */
    @Override
    public String getFilenameByRelativePath(String relativePath) {
        String[] path = relativePath.split("/");
        if (path.length - 1 >= 0)
            return path[path.length - 1];
        else // invalid relative path
            return null;
    }

    /**
     * 根据url 返回文件名称
     *
     * @param url
     * @return 文件名
     */
    @Override
    public String getFilenameByUrl(String url) {
        return getFilenameByRelativePath(url);
    }


    public String getBASE_PATH() {
        return BASE_PATH;
    }

    private String getUrlheader() {
        String port = environment.getProperty("local.server.port");
        return "http://" + "206.189.35.98" + ":" + port + "/api/file";
    }

    /**
     * 给定相对路径，返回可以直接访问的url
     *
     * @param relativePath
     * @return
     */
    @Override
    public String relativePathToUrl(String relativePath) {
        return getUrlheader() + relativePath;
    }

    @Override
    public String store(MultipartFile file, Path path) throws BaseException {
        return store(file, path, getNewFilename(file.getOriginalFilename()));
    }

    private String store(MultipartFile file, Path path, String newFilename) throws BaseException {

        try {
            if (file.isEmpty()) {
                throw new BaseException("Failed to store empty file" + file.getOriginalFilename(), HttpStatus.NOT_FOUND);
            }
            init(path);
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

    /*
    @Override
    public Stream<Path> loadAll(Path path) throws BaseException {
        try {
            return Files.walk(path, 1).filter(path1 -> !path.equals(path)).map(path1 -> path.relativize(path1));

        } catch (IOException e) {
            throw new BaseException("Fileed to read stored files", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    */

    @Override
    public Path load(Path path, String filename) {
        return path.resolve(filename);
    }

    /*
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
    */
    @Override
    public String getNewFilename(String oldFilename) {
        return Instant.now().toString().replace(":", "-") + oldFilename;
    }


    /**
     * 根据文件名推断文件类型
     *
     * @param fileName
     * @return
     */
    @Override
    public String getFileTypeByFileName(String fileName) {
        String[] items = fileName.split(".");
        return items[items.length - 1];
    }
}
