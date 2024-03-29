/*
 * The MIT License
 *
 * Copyright 2019 Thomas Lehmann.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package comfortable.data.controller;

import comfortable.data.database.MovieRepository;
import comfortable.data.model.CustomMediaType;
import comfortable.data.model.Movie;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Providing REST commands for persisting movies and running queries on it.
 */
@RestController
public class MovieController {
    /**
     * Dependency injection of database class responsible for storing and querying data.
     */
    @Autowired
    private transient MovieRepository repository;

    /**
     * Create or update movie in database.
     *
     * @param movie movie to create or update in database.
     * @return persistent movie
     */
    @PostMapping(value = "/movies", produces = {CustomMediaType.APPLICATION_JSON_VALUE,
        CustomMediaType.APPLICATION_XML_VALUE, CustomMediaType.APPLICATION_YAML_VALUE})
    public Movie createOrUpdateBook(@RequestBody final Movie movie) {
        return this.repository.save(movie);
    }

    /**
     * Provide list of movies.
     *
     * @return provide list of movies.
     */
    @GetMapping(value = "/movies", produces = {CustomMediaType.APPLICATION_JSON_VALUE,
        CustomMediaType.APPLICATION_XML_VALUE, CustomMediaType.APPLICATION_YAML_VALUE})
    public List<Movie> getListOfMovies() {
        return this.repository.findAll();
    }
}
