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

import comfortable.data.model.Composer;
import comfortable.data.model.CustomMediaType;
import comfortable.data.model.Director;
import comfortable.data.model.Movie;
import comfortable.data.model.Performer;
import comfortable.data.model.Role;
import comfortable.data.model.Tag;
import comfortable.data.tools.RequestMaker;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testing of {@link MovieController}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerTest {

    /**
     * Test client for web layer.
     */
    @Autowired
    private MockMvc mvc;

    /**
     * Using /movies REST to create a new movie and to receive the persistent one as JSON response.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreateMovieWithJsonResponse() throws Exception {
        runTest(createMovie(), CustomMediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_JSON);
    }

    /**
     * Performing create or update request and a request to retrieve the list. Finally the list
     * should contain the created (or updated) record once only.
     *
     * @param theMovie the movie to create or update in database
     * @param contentType the type how to send movie data to the server.
     * @param expectedMediaType the expected response type (XML, JSON or YAML).
     * @throws Exception when conversion has failed or one request
     */
    private void runTest(final Movie theMovie,
            final MediaType contentType,
            final MediaType expectedMediaType) throws Exception {
        final var requestMaker = new RequestMaker(this.mvc);
        final Movie responseMovie = requestMaker
                .createOrUpdate("/movies", theMovie, contentType, expectedMediaType);

        assertThat(responseMovie, equalTo(theMovie));

        final var movies = requestMaker.getListOfMovies(expectedMediaType);
        assertThat(movies.stream()
                .filter(movie -> {
                    return movie.getTitle().equals(theMovie.getTitle())
                            && movie.getYearOfPublication() == theMovie.getYearOfPublication();
                })
                .count(),
                equalTo(1L));
    }

    /**
     * Creating a movie test instance.
     *
     * @return test movie instance.
     */
    private Movie createMovie() {
        return Movie.builder()
                .title("the movie title")
                .originalTitle("the original movie title")
                .aspectRatio("16:9")
                .yearOfPublication(1963)
                .role(Role.builder()
                        .performer(new Performer("First Performer"))
                        .roleName("Mr. X").build())
                .role(Role.builder()
                        .performer(new Performer("Second Performer"))
                        .roleName("Mr. Y").build())
                .composer(new Composer("First Composer"))
                .composer(new Composer("SecondComposer"))
                .director(new Director("First Director"))
                .director(new Director("Second Director"))
                .tag(new Tag("science-fiction"))
                .tag(new Tag("utopie"))
                .build();
    }
}
