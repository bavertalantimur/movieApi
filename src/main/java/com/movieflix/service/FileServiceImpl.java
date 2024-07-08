package com.movieflix.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements  FileService{
    //uploadFile Metodu: Verilen dosyayı belirtilen dizine yükler. Eğer dizin yoksa, oluşturur.
    //getResourceFile Metodu: Belirtilen dizinden dosyayı alır ve bir InputStream olarak döner.
    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename(); // dosyanın orjinal adını döner
        String filePath = path + File.separator + fileName; // dosya yükleneceği yol

        File f = new File(path);  //nesnesi oluşturur ve dizini temsil eder.
        if(!f.exists()){ // Dizinin mevcut olup olmadığını kontrol eder ve yoksa, dizini oluşturur.
            f.mkdirs();
        }

        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING); //Dosyayı belirtilen yola kopyalar. Eğer dosya zaten varsa, üzerine yazar.

        //file.getInputStream(): MultipartFile nesnesinden dosya içeriğini okumak için kullanılan InputStream.
        //Paths.get(filePath): Kopyalanacak dosyanın hedef yolunu ve adını belirten bir Path nesnesi. filePath, dosyanın kaydedileceği dizin ve dosya adını içerir.
        //StandardCopyOption.REPLACE_EXISTING: Eğer hedef dosya zaten varsa, onun üzerine yazılmasını sağlayan bir seçenek. Bu seçenek kullanıcıya belirli bir davranış biçimi sunar ve var olan dosyayı değiştirme yeteneği verir.

        return fileName;
    }

    @Override
    public InputStream getResourceFile(String path, String fileName) throws FileNotFoundException {
        String filePath = path + File.separator + fileName;
        return new FileInputStream(filePath); //new FileInputStream(filePath) ifadesiyle, belirtilen filePath'teki dosyanın FileInputStream nesnesi oluşturulur. FileInputStream, belirtilen dosyayı okumak için kullanılan bir InputStream türüdür.
       // Belirtilen yoldaki dosyayı okumak için bir FileInputStream nesnesi oluşturur ve döner. Eğer dosya bulunamazsa, FileNotFoundException fırlatır.

    }



}
