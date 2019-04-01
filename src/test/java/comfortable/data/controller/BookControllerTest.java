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
import comfortable.data.model.Book;
import comfortable.data.model.Publisher;
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
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    /**
     * Using /authors/create REST to create a new author and to receive the id as JSON response.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreateBookWithJsonResponse() throws Exception {
        final var requestMaker = new RequestMaker(this.mvc);

        final var description = "\"Der Unbesiegbare\", ein Raumkreuzer der schweren Klasse,"
                + " sucht nach einem verschollenen Schwesterschiff. Als es geffunden wird,"
                + " stehen die Wissenschaftler vor einem Rätsel. Es gibt keine findliche"
                + " Macht und keine Überlebenden: eine Rettungsexpedition, die allen fast"
                + " zum Verhängnis wird.";

        final var newBook = Book.builder()
                .title("Der Unbesiegbare")
                .originalTitle("Niezwyciezony i inne opowiadania")
                .isbn("3-518-38959-9")
                .author(new Author("Stanislaw Lem"))
                .publisher(new Publisher("suhrkamp"))
                .pages(228)
                .description(description)
                .build();

        final Book responseBook = requestMaker
                .createOrUpdate("/books", newBook, MediaType.APPLICATION_JSON);

        assertThat(responseBook.getTitle(), equalTo("Der Unbesiegbare"));
        assertThat(responseBook.getOriginalTitle(), equalTo("Niezwyciezony i inne opowiadania"));
        assertThat(responseBook.getIsbn(), equalTo("3-518-38959-9"));
        assertThat(responseBook.getPages(), equalTo(228));
        assertThat(responseBook.getAuthors().get(0).getFullName(), equalTo("Stanislaw Lem"));
        assertThat(responseBook.getPublisher().getFullName(), equalTo("suhrkamp"));
        assertThat(responseBook.getDescription(), equalTo(description));

        final var books = requestMaker.getListOfBooks(MediaType.APPLICATION_JSON);
        assertThat(books.stream()
                .filter(book -> book.getTitle().equals("Der Unbesiegbare"))
                .count(), equalTo(1L));
    }
}
