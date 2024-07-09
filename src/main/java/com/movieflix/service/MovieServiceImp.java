package com.movieflix.service;

import com.movieflix.dto.MovieDto;
import com.movieflix.entities.Movie;
import com.movieflix.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImp implements MovieService {


    private final MovieRepository movieRepository;
    private final FileService fileService;
    @Value("${project.poster}") //application.yml dosyasında tanımlanan project.poster özelliği, path değişkenine enjekte edilir. Bu değişken, dosyaların depolandığı dizini gösterir.
    private String path;

    @Value("${base.url}")
    private String baseUrl;


    public MovieServiceImp(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {

       if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
           throw new RuntimeException("File already exists");
       }
       String uploadedFileName = fileService.uploadFile(path,file);

       movieDto.setPoster(uploadedFileName);
        // Movie nesnesi oluşturuluyor (veritabanına kaydedilecek olan nesne)
        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );


        Movie savedMovie = movieRepository.save(movie);

        String posterUrl = baseUrl + "/file/" + uploadedFileName;

        // MovieDto nesnesi oluşturuluyor (istemciye gönderilecek olan nesne)
        MovieDto response = new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );



        return response;
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
       Movie movie= movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie not found"));

       String posterUrl = baseUrl + "/file/" + movie.getPoster();
        MovieDto response = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl

        );
        return response;
    }

    @Override
    public List<MovieDto> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        List<MovieDto> movieDtos =new ArrayList<>();
        for(Movie movie : movies){
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl

            );
            movieDtos.add(movieDto);
        }
        return movieDtos;
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        Movie mv= movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie not found"));

        String fileName =mv.getPoster();
        if (file != null) {
            Files.deleteIfExists(Paths.get(path +File.separator + fileName));
            fileName = fileService.uploadFile(path ,file);
        }
        movieDto.setPoster(fileName);

        Movie movie = new Movie(
                mv.getMovieId(),
                mv.getTitle(),
                mv.getDirector(),
                mv.getStudio(),
                mv.getMovieCast(),
                mv.getReleaseYear(),
                mv.getPoster()

        );
        Movie updateMovie = movieRepository.save(movie);

        String posterUrl = baseUrl + "/file/" + fileName;

        MovieDto response = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl

        );
        return response;


    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        Movie mv= movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie not found"));

        Files.deleteIfExists(Paths.get(path + File.separator+ mv.getPoster()));
        Integer id =  mv.getMovieId();
        movieRepository.deleteById(id);
        return "Movie deleted with id =" + id;
    }
}
