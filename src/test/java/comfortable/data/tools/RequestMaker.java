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
package comfortable.data.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import comfortable.data.model.Author;
import comfortable.data.model.Book;
import comfortable.data.model.CustomMediaType;
import comfortable.data.model.Director;
import comfortable.data.model.Movie;
import comfortable.data.model.Performer;
import comfortable.data.model.Publisher;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Helpers fpr running requests in the tests.
 */
public class RequestMaker {

    /**
     * mocked web layer
     */
    private final MockMvc mvc;

    /**
     * Initializes the request make with the mvc.
     *
     * @param mvc the web layer.
     */
    public RequestMaker(final MockMvc mvc) {
        this.mvc = mvc;
    }

    /**
     * Get the current list of authors requesting either in JSON or in XML.
     *
     * @param expectedResponseType XML or JSON
     * @return list of authors (empty list of nothing has been found).
     * @throws Exception (should never happen)
     */
    @SuppressWarnings("unchecked")
    public List<Author> getListOfAuthors(final MediaType expectedResponseType) throws Exception {
        final String content = this.mvc.perform(get("/book/authors")
                .accept(expectedResponseType))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Author> authors;
        if (expectedResponseType == CustomMediaType.APPLICATION_JSON) {
            final var mapper = new ObjectMapper();
            authors = mapper.readValue(content, new TypeReference<List<Author>>() {
            });
        } else if (expectedResponseType == CustomMediaType.APPLICATION_YAML) {
            final var mapper = new ObjectMapper(new YAMLFactory());
            authors = mapper.readValue(content, new TypeReference<List<Author>>() {
            });
        } else {
            final var xmlMapper = new XmlMapper();
            authors = xmlMapper.readValue(content, new TypeReference<List<Author>>() {
            });
        }
        return authors;
    }

    /**
     * Get the current list of publishers requesting either in JSON or in XML.
     *
     * @param expectedReponseType XML or JSON
     * @return list of publishers (empty list of nothing has been found).
     * @throws Exception (should never happen)
     */
    public List<Publisher> getListOfPublishers(final MediaType expectedReponseType) throws Exception {
        final String content = this.mvc.perform(get("/book/publishers")
                .accept(expectedReponseType))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Publisher> publishers;
        if (expectedReponseType == MediaType.APPLICATION_JSON) {
            final var mapper = new ObjectMapper();
            publishers = mapper.readValue(content, new TypeReference<List<Publisher>>() {
            });
        } else if (expectedReponseType == CustomMediaType.APPLICATION_YAML) {
            final var mapper = new ObjectMapper(new YAMLFactory());
            publishers = mapper.readValue(content, new TypeReference<List<Publisher>>() {
            });
        } else {
            final var xmlMapper = new XmlMapper();
            publishers = xmlMapper.readValue(content, new TypeReference<List<Publisher>>() {
            });
        }
        return publishers;
    }

    /**
     * Get the current list of performers requesting either in JSON or in XML.
     *
     * @param expectedReponseType XML or JSON
     * @return list of publishers (empty list of nothing has been found).
     * @throws Exception (should never happen)
     */
    public List<Performer> getListOfPerformers(final MediaType expectedReponseType) throws Exception {
        final String content = this.mvc.perform(get("/movies/performers")
                .accept(expectedReponseType))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Performer> performers;
        if (expectedReponseType == MediaType.APPLICATION_JSON) {
            final var mapper = new ObjectMapper();
            performers = mapper.readValue(content, new TypeReference<List<Performer>>() {
            });
        } else if (expectedReponseType == CustomMediaType.APPLICATION_YAML) {
            final var mapper = new ObjectMapper(new YAMLFactory());
            performers = mapper.readValue(content, new TypeReference<List<Performer>>() {
            });
        } else {
            final var xmlMapper = new XmlMapper();
            performers = xmlMapper.readValue(content, new TypeReference<List<Performer>>() {
            });
        }
        return performers;
    }

    /**
     * Get the current list of books requesting either in JSON or in XML.
     *
     * @param expectedReponseType XML or JSON
     * @return list of book (empty list of nothing has been found).
     * @throws Exception (should never happen)
     */
    public List<Book> getListOfBooks(final MediaType expectedReponseType) throws Exception {
        final String content = this.mvc.perform(get("/books")
                .accept(expectedReponseType))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Book> books;
        if (expectedReponseType == MediaType.APPLICATION_JSON) {
            final var mapper = new ObjectMapper();
            books = mapper.readValue(content, new TypeReference<List<Book>>() {
            });
        } else if (expectedReponseType == CustomMediaType.APPLICATION_YAML) {
            final var mapper = new ObjectMapper(new YAMLFactory());
            books = mapper.readValue(content, new TypeReference<List<Book>>() {
            });
        } else {
            final var xmlMapper = new XmlMapper();
            books = xmlMapper.readValue(content, new TypeReference<List<Book>>() {
            });
        }
        return books;
    }

    /**
     * Get the current list of movies requesting either in JSON or in XML.
     *
     * @param expectedReponseType XML or JSON
     * @return list of movies (empty list of nothing has been found).
     * @throws Exception (should never happen)
     */
    public List<Movie> getListOfMovies(final MediaType expectedReponseType) throws Exception {
        final String content = this.mvc.perform(get("/movies")
                .accept(expectedReponseType))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Movie> movies;
        if (expectedReponseType == MediaType.APPLICATION_JSON) {
            final var mapper = new ObjectMapper();
            movies = mapper.readValue(content, new TypeReference<List<Movie>>() {
            });
        } else if (expectedReponseType == CustomMediaType.APPLICATION_YAML) {
            final var mapper = new ObjectMapper(new YAMLFactory());
            movies = mapper.readValue(content, new TypeReference<List<Movie>>() {
            });
        } else {
            final var xmlMapper = new XmlMapper();
            movies = xmlMapper.readValue(content, new TypeReference<List<Movie>>() {
            });
        }
        return movies;
    }

    /**
     * Get the current list of directors requesting either in JSON or in XML.
     *
     * @param expectedReponseType XML or JSON
     * @return list of directors (empty list of nothing has been found).
     * @throws Exception (should never happen)
     */
    public List<Director> getListOfDirectors(final MediaType expectedReponseType) throws Exception {
        final String content = this.mvc.perform(get("/movies/directors")
                .accept(expectedReponseType))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Director> directors;
        if (expectedReponseType == MediaType.APPLICATION_JSON) {
            final var mapper = new ObjectMapper();
            directors = mapper.readValue(content, new TypeReference<List<Director>>() {
            });
        } else if (expectedReponseType == CustomMediaType.APPLICATION_YAML) {
            final var mapper = new ObjectMapper(new YAMLFactory());
            directors = mapper.readValue(content, new TypeReference<List<Director>>() {
            });
        } else {
            final var xmlMapper = new XmlMapper();
            directors = xmlMapper.readValue(content, new TypeReference<List<Director>>() {
            });
        }
        return directors;
    }

    /**
     * Create or update an entity in the database.
     *
     * @param <E> the type like book or author
     * @param path the REST path
     * @param entity the entity to create or update in the database.
     * @param contentType the format on how to send data to the Server.
     * @param expectedResponseType XML or JSON
     * @return persistent entity.
     * @throws Exception when serialization/deserialization or the request has failed.
     */
    public <E> E createOrUpdate(final String path, final E entity,
            final MediaType contentType,
            final MediaType expectedResponseType) throws Exception {
        @SuppressWarnings("unchecked")
        final var converter = new ContentConverter<>((Class<E>) entity.getClass(),
                expectedResponseType, contentType);

        final String content = this.mvc.perform(post(path)
                .accept(expectedResponseType)
                .contentType(contentType)
                .content(converter.toString(entity))).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return converter.fromString(content);
    }
}
