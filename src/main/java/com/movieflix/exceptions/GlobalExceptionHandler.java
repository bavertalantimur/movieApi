package com.movieflix.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//Bu anotasyon, RESTful servisler için global exception handling sağlayan bir danışman sınıfı olduğunu belirtir.
@RestControllerAdvice
public class GlobalExceptionHandler {
    //Bu anotasyon, belirtilen istisna türlerini ele alacak yöntemleri belirtmek için kullanılır.
    // Yani, her @ExceptionHandler yöntemi, belirtilen istisna türü
    // (MovieNotFoundException, FileExistsException, EmptyFileException) için hangi HTTP durum kodu ve hata mesajı döneceğini belirler.
    @ExceptionHandler(MovieNotFoundException.class)
    public ProblemDetail handleMovieNotFoundException(MovieNotFoundException ex) {
        //Bu sınıf, Spring Framework tarafından sunulan ve HTTP cevaplarında problem detaylarını temsil etmek için kullanılan bir sınıftır.
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND , ex.getMessage());
    }

    @ExceptionHandler(FileExistsException.class)
    public ProblemDetail handleFileExistsException(FileExistsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EmptyFileException.class)
    public ProblemDetail handleEmptyFileExistsException(FileExistsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }


}
