package com.fitness.module.file;

import com.fitness.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/files")
@Tag(name = "文件上传")
public class FileController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private Path absoluteUploadPath;

    @PostConstruct
    public void init() {
        Path path = Paths.get(uploadDir);
        if (!path.isAbsolute()) {
            path = Paths.get(System.getProperty("user.dir"), uploadDir);
        }
        absoluteUploadPath = path.normalize();
        try {
            Files.createDirectories(absoluteUploadPath);
            log.info("上传目录初始化完成: {}", absoluteUploadPath);
        } catch (IOException e) {
            log.error("初始化上传目录失败: {}", absoluteUploadPath, e);
        }
    }

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + ext;

        try {
            Path targetPath = absoluteUploadPath.resolve(filename);
            file.transferTo(targetPath.toFile());
            log.info("文件上传成功: {} -> {}", originalFilename, filename);
            return Result.success("/api/files/" + filename);
        } catch (IOException e) {
            log.error("文件上传失败: {}", originalFilename, e);
            return Result.error(500, "文件上传失败");
        }
    }

    @GetMapping("/{filename:.+}")
    @Operation(summary = "获取文件")
    public void getFile(@PathVariable String filename, jakarta.servlet.http.HttpServletResponse response) {
        try {
            Path filePath = absoluteUploadPath.resolve(filename).normalize();
            if (!filePath.startsWith(absoluteUploadPath) || !Files.exists(filePath)) {
                response.setStatus(404);
                return;
            }
            String mimeType = URLConnection.guessContentTypeFromName(filename);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            response.setContentType(mimeType);
            response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
            Files.copy(filePath, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("文件读取失败: {}", filename, e);
            response.setStatus(500);
        }
    }
}
