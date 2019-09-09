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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import comfortable.data.database.BookAuthorRepository;
import comfortable.data.database.BookRepository;
import comfortable.data.model.Author;
import comfortable.data.model.Book;
import comfortable.data.model.Publisher;
import comfortable.data.tools.RandomDataProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Testing the quering for books using concrete filters.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SuppressWarnings("checkstyle:classfanoutcomplexity")
public class BookControllerQueryTest {

    /**
     * Request (base) for books.
     */
    private static final String REQUEST = "/books";

    /**
     * Maximum number of books for the tests.
     */
    private static final int MAX_BOOKS = 10;

    /**
     * Media part for removal for rest documentation (path).
     */
    private static final String APPLICATION = "application";

    /**
     * Provider for test data.
     */
    private static RandomDataProvider provider;

    /**
     * Test client for web layer.
     */
    @Autowired
    private MockMvc mvc;

    /**
     * Database access for books.
     */
    @Autowired
    private BookRepository books;

    /**
     * Database access for book authors.
     */
    @Autowired
    private BookAuthorRepository authors;

    /**
     * Load test data.
     *
     * @throws java.io.IOException should never happen here.
     */
    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        provider = RandomDataProvider.of("/test.data.json");
    }

    /**
     * Remove all books before each individual test.
     */
    @After
    public void tearDown() {
        this.books.deleteAll();
        this.authors.deleteAll();
    }

    /**
     * Testing filtering by authors.
     *
     * @throws Exception should never be thrown.
     */
    @Test
    public void testFilterAuthors() throws Exception {
        final var newBooks = this.createBooks();
        newBooks.forEach(book -> this.books.save(book));

        final var documentName = "getByAuthor" + REQUEST + "/json/json";

        final String content = this.mvc.perform(get(REQUEST)
                .param("author", newBooks.get(0).getAuthors().get(0).getFullName())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(document(documentName))
                .andReturn().getResponse().getContentAsString();

        final var mapper = new ObjectMapper();
        final List<Book> foundBooks = mapper.readValue(
                content, new TypeReference<List<Book>>() {
        });

        // there should be one book only
        assertThat(foundBooks.size(), equalTo(1));
        assertThat(foundBooks.get(0).getIsbn(), equalTo(newBooks.get(0).getIsbn()));
        assertThat(foundBooks.get(0).getTitle(), equalTo(newBooks.get(0).getTitle()));
        assertThat(foundBooks.get(0).getPublisher().getFullName(),
                equalTo(newBooks.get(0).getPublisher().getFullName()));
    }

    /**
     * Create list of random books.
     *
     * @return list of books.
     */
    private List<Book> createBooks() {
        final var titles = BookControllerQueryTest.provider.get(
                RandomDataProvider.TITLE, MAX_BOOKS);
        final var bookAuthors = BookControllerQueryTest.provider.get(
                RandomDataProvider.AUTHOR, MAX_BOOKS);
        final var publishers = BookControllerQueryTest.provider.get(
                RandomDataProvider.AUTHOR, MAX_BOOKS);
        final var isbn = BookControllerQueryTest.provider.get(
                RandomDataProvider.ISBN, MAX_BOOKS);
        final var years = BookControllerQueryTest.provider.get(
                RandomDataProvider.YEAR, MAX_BOOKS);

        final var newBooks = new ArrayList<Book>();
        for (var index = 0; index < MAX_BOOKS; ++index) {
            newBooks.add(Book.builder()
                    .title(titles.get(index))
                    .author(Author.builder().fullName(bookAuthors.get(index)).build())
                    .publisher(Publisher.builder().fullName(publishers.get(index)).build())
                    .isbn(isbn.get(index))
                    .yearOfPublication(Integer.valueOf(years.get(index)))
                    .build());
        }

        return newBooks;
    }
}
