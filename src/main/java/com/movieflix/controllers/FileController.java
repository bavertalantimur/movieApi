package com.movieflix.controllers;

import com.movieflix.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/file/")
public class FileController {


    private final FileService fileService;

    //constructor injection
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Value("${project.poster}") //application.yml dosyasında tanımlanan project.poster özelliği, path değişkenine enjekte edilir. Bu değişken, dosyaların depolandığı dizini gösterir.
    private String path;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFileHandler(@RequestPart MultipartFile file) throws IOException { // file parametresi ile gelen dosya yüklenir
    String uploadedFileName = fileService.uploadFile(path,file);
    return ResponseEntity.ok("File uploaded successfully : "+uploadedFileName);
    }

    @GetMapping("/{fileName}")
    public void serveFileHandler(@PathVariable String fileName , HttpServletResponse response) throws IOException {
        InputStream resourceFile = fileService.getResourceFile(path,fileName);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        StreamUtils.copy(resourceFile,response.getOutputStream()); // satırıyla dosya içeriği HTTP yanıtının OutputStream'ine yazılır.
    }



}
