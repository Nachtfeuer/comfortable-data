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

import comfortable.data.model.Author;
import comfortable.data.tools.RequestMaker;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testing of {@link BookAuthorController} class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookAuthorControllerTest {

    /**
     * test author
     */
    private static final String BOOK_TITLE_1 = "Agatha Christie";
    /**
     * another test author
     */
    private static final String BOOK_TITLE_2 = "Rex Stout";

    @Autowired
    private MockMvc mvc;

    /**
     * Using /authors/create REST to create a new author and to receive the id as JSON response.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreateAuthorWithJsonResponse() throws Exception {
        final var requestMaker = new RequestMaker(this.mvc);
        final Author newAuthor = new Author(BOOK_TITLE_2);
        final Author responseAuthor = requestMaker
                .createOrUpdate("/book/authors", newAuthor, MediaType.APPLICATION_JSON);

        assertThat(responseAuthor.getFullName(), equalTo(BOOK_TITLE_2));

        final var authors = requestMaker.getListOfAuthors(MediaType.APPLICATION_JSON);
        assertThat(authors.stream()
                .filter(author -> author.getFullName().equals(BOOK_TITLE_2))
                .count(), equalTo(1L));
    }

    /**
     * Using /authors/create REST to create a new author and to receive the id as XML response.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreateAuthorWithXmlResponse() throws Exception {
        final var requestMaker = new RequestMaker(this.mvc);
        final Author newAuthor = new Author(BOOK_TITLE_1);
        final Author responseAuthor = requestMaker
                .createOrUpdate("/book/authors", newAuthor, MediaType.APPLICATION_XML);

        assertThat(responseAuthor.getFullName(), equalTo(BOOK_TITLE_1));

        final var authors = requestMaker.getListOfAuthors(MediaType.APPLICATION_XML);
        assertThat(authors.stream()
                .filter(author -> author.getFullName().equals(BOOK_TITLE_1))
                .count(), equalTo(1L));
    }
}
