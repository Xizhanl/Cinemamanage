package ljc.movie_manage.controller;
import jakarta.servlet.http.HttpServletResponse;
import ljc.movie_manage.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

//@RestController
//public class FilesController {
//    @Value("${file.upload-path}")
//    private String uploadPath;
//    @PostMapping("/upload")
//    public Result upload(@RequestParam("file") MultipartFile file) throws IOException {
//
//        //获取文件类型
//        String contentType = file.getContentType();
//        //图片格式
//        List<String> listType = new ArrayList<>();
//        listType.add("image/jpeg");
//        listType.add("image/png");
//        listType.add("image/jpg");
//
//        if (contentType == null || !listType.contains(contentType)) {
//            throw new IllegalArgumentException("只允许上传PNG、JPEG、PNG格式图片");
//        }
//
//        InputStream inputStream = file.getInputStream();
//        //生成文件名uuid
//        String uuid = UUID.randomUUID().toString();
//        //获取上传文件扩展名
//        String fix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
//        //生成文件完整名称
//        String fileName = uuid + fix;
//        //生成文件完整路径
//        String absolutePath = uploadPath+fileName;
//        //文件夹不存在就递归创建文件夹
//        File folder = new File(uploadPath);
//        if (!folder.exists()) {
//            folder.mkdirs();
//        }
//        int len=0;
//        FileOutputStream fileOutputStream = new FileOutputStream(absolutePath);
//        while((len = inputStream.read())!=-1){
//            fileOutputStream.write(len);
//        }
//        fileOutputStream.close();
//        inputStream.close();
//
//        HashMap<Object, Object> map = new HashMap<>();
//        map.put("url","http://localhost:9999/"+"uploads?filename="+fileName);
//        map.put("alt",file.getOriginalFilename());
//        return new Result().suc(map);
//    }
//    @GetMapping("/uploads")
//    public void download(@RequestParam String filename,
//                         HttpServletResponse response) throws IOException {
//        // 1. 获取文件路径
//        Path file = Paths.get(uploadPath, filename);
//        // 2. 设置响应头
//        response.setContentType("application/octet-stream");
//        response.setHeader("Content-Disposition",
//                "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
//        // 3. 传输文件
//        Files.copy(file, response.getOutputStream());
//    }
//
//}
@RestController
public class FilesController {
    @Value("${file.upload-path}")
    private String uploadPath;
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) throws IOException {
        // 验证文件类型
        String contentType = file.getContentType();
        List<String> allowedTypes = Arrays.asList("image/jpeg", "image/png", "image/jpg");

        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new IllegalArgumentException("只允许上传PNG、JPEG、JPG格式图片");
        }

        // 创建上传目录（如果不存在）
        File folder = new File(uploadPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 生成唯一文件名
        String uuid = UUID.randomUUID().toString();
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = uuid + extension;
        String filePath = uploadPath + fileName;

        // 保存文件
        file.transferTo(new File(filePath));

        // 返回结果
        Map<String, String> result = new HashMap<>();
        result.put("url", "http://localhost:9999/uploads?filename=" + fileName);
        result.put("alt", file.getOriginalFilename());
        return new Result().suc(result);
    }

    @GetMapping("/uploads")
    public void download(@RequestParam String filename,
                         HttpServletResponse response) throws IOException {
        // 获取文件路径
        Path file = Paths.get(uploadPath, filename);

        // 设置响应头
        response.setContentType(Files.probeContentType(file));
        response.setHeader("Content-Disposition",
                "inline; filename=" + URLEncoder.encode(filename, "UTF-8"));

        // 传输文件
        Files.copy(file, response.getOutputStream());
        response.flushBuffer();
    }
}
