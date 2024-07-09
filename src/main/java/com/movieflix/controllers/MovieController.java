package com.movieflix.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieflix.dto.MovieDto;
import com.movieflix.entities.Movie;
import com.movieflix.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("/add-movie")
    public ResponseEntity<MovieDto> addMovieHandler(@RequestPart MultipartFile file , @RequestPart String movieDto) throws IOException {
        MovieDto dto = convertToMovieDto(movieDto); //Bu metot, gelen JSON dizesini MovieDto nesnesine dönüştürür.
        return new ResponseEntity<>(movieService.addMovie(dto,file),HttpStatus.CREATED); //Bu metot, servis katmanında yeni bir film ekler ve eklenen filmin DTO nesnesini döner.
        //Service katmanına giderek film ekleme işlemini gerçekleştirir ve eklenen filmin bilgilerini döner.

    }

    private MovieDto convertToMovieDto(String movieDtoObj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper(); // Bu, Jackson kütüphanesinin bir parçasıdır ve JSON verilerini Java nesnelerine ve Java nesnelerini JSON verilerine dönüştürmek için kullanılı
        return objectMapper.readValue(movieDtoObj , MovieDto.class); //Bu metot, gelen JSON dizesini MovieDto nesnesine dönüştürür.
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieHandler(@PathVariable Integer movieId){
        return  ResponseEntity.ok(movieService.getMovie(movieId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieDto>> getAllMovieHandler(){
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMovieHandler(@PathVariable Integer movieId, @RequestPart MultipartFile file , @RequestPart String movieDtoObj) throws IOException{
        MovieDto dto = convertToMovieDto(movieDtoObj);
        return ResponseEntity.ok(movieService.updateMovie(movieId,dto ,file));
    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable Integer movieId) throws IOException {
       return  ResponseEntity.ok(movieService.deleteMovie(movieId));

    }



}
