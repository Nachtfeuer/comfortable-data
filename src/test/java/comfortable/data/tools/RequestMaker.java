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
import comfortable.data.model.Author;
import comfortable.data.model.Book;
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
    /** mocked web layer */
    private final MockMvc mvc;

    public RequestMaker(final MockMvc mvc) {
        this.mvc = mvc;
    }

    /**
     * Get the current list of authors requesting either in JSON or in XML.
     *
     * @param expectedReponseType XML or JSON
     * @return list of authors (empty list of nothing has been found).
     * @throws Exception (should never happen)
     */
    public List<Author> getListOfAuthors(final MediaType expectedReponseType) throws Exception {
        final String content = this.mvc.perform(get("/book/authors")
                .accept(expectedReponseType))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Author> authors;
        if (expectedReponseType == MediaType.APPLICATION_JSON) {
            final var mapper = new ObjectMapper();
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

        List<Publisher> authors;
        if (expectedReponseType == MediaType.APPLICATION_JSON) {
            final var mapper = new ObjectMapper();
            authors = mapper.readValue(content, new TypeReference<List<Publisher>>() {
            });
        } else {
            final var xmlMapper = new XmlMapper();
            authors = xmlMapper.readValue(content, new TypeReference<List<Publisher>>() {
            });
        }
        return authors;
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
        } else {
            final var xmlMapper = new XmlMapper();
            books = xmlMapper.readValue(content, new TypeReference<List<Book>>() {
            });
        }
        return books;
    }

    /**
     * Create or update an entity in the database.
     *
     * @param <E> the type like book or author
     * @param path the REST path
     * @param entity the entity to create or update in the database.
     * @param expectedResponseType XML or JSON
     * @return persistent entity.
     * @throws Exception when serialization/deserialization or the request has failed.
     */
    public <E> E createOrUpdate(final String path, final E entity, final MediaType expectedResponseType) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final String content = this.mvc.perform(post(path)
                .accept(expectedResponseType)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(entity))).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        @SuppressWarnings("unchecked")
        final Class<E> responseClass = (Class<E>) entity.getClass();

        E responseEntity;

        if (expectedResponseType == MediaType.APPLICATION_JSON) {
            responseEntity = mapper.readValue(content, responseClass);
        } else {
            final var xmlMapper = new XmlMapper();
            responseEntity = xmlMapper.readValue(content, responseClass);
        }
        return responseEntity;
    }
}
