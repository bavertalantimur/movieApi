package com.movieflix.service;

import com.movieflix.dto.MovieDto;
import com.movieflix.dto.MoviePageResponse;
import com.movieflix.entities.Movie;
import com.movieflix.exceptions.FileExistsException;
import com.movieflix.exceptions.MovieNotFoundException;
import com.movieflix.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
           throw new FileExistsException("File already exists");
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
       Movie movie= movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found with id " + movieId));

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
        //ovieId ile eşleşen bir filmi bulur ve varsa Optional<Movie> şeklinde döner.
        Movie mv= movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found with id " + movieId));


        String fileName =mv.getPoster();

        if (file != null) {
            // Bu yöntem, belirtilen dosyayı silmeye çalışır. deleteIfExists kullanıldığında, dosya varsa silinir ve işlem başarılı bir şekilde tamamlanırsa true döner.
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
        Movie mv= movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found with id " + movieId));

        Files.deleteIfExists(Paths.get(path + File.separator+ mv.getPoster()));
        Integer id =  mv.getMovieId();
        movieRepository.deleteById(id);
        return "Movie deleted with id =" + id;
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        //PageRequest.of(pageNumber, pageSize) ile belirli bir sayfanın ve sayfa boyutunun bilgisini elde ediyoruz.
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        //bu metod, veritabanındaki tüm filmleri belirtilen sayfalama bilgisine göre getirir
        // burdan getTotalPages ve getTotalElement , isLast gelir
        Page<Movie> moviePages = movieRepository.findAll(pageable);

        //bu metod, Page<Movie> nesnesindeki film listesini alır.
        List<Movie> movies =moviePages.getContent();



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
        return new MoviePageResponse(movieDtos,pageNumber,pageSize,moviePages.getTotalElements(),moviePages.getTotalPages(),moviePages.isLast());
    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize,sort);
        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<Movie> movies =moviePages.getContent();



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
        return new MoviePageResponse(movieDtos,pageNumber,pageSize,moviePages.getTotalElements(),moviePages.getTotalPages(),moviePages.isLast());
    }
}
