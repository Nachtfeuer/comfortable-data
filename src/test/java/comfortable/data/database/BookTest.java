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
package comfortable.data.database;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testing the quering for books using concrete filters.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SuppressWarnings("checkstyle:classfanoutcomplexity")
public class BookTest {

    /**
     * Maximum number of books for the tests.
     */
    private static final int MAX_BOOKS = 10;

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
     * Remove (cleanup) all books after each individual test.
     */
    @After
    public void tearDown() {
        this.books.deleteAll();
        this.authors.deleteAll();
    }

    /**
     * Testing querying authors count for several books with different author.
     */
    @Test
    public void testQueryAuthorsCountTheyAreUnique() {
        final var newBooks = this.createBooks(/*uniqueAuthor=*/true);
        newBooks.forEach(book -> this.books.save(book));

        final var authorCounts = this.books.queryAuthorCount();
        assertThat(authorCounts.size(), equalTo(newBooks.size()));
        authorCounts.forEach(
                authorCount -> assertThat(authorCount.getCount(), equalTo(1L)));
    }

    /**
     * Testing querying authors count for several books with same author.
     */
    @Test
    public void testQueryAuthorsCountThereIsJustOne() {
        final var newBooks = this.createBooks(/*uniqueAuthor=*/false);
        newBooks.forEach(book -> this.books.save(book));

        final var authorCounts = this.books.queryAuthorCount();
        assertThat(authorCounts.size(), equalTo(1));
        authorCounts.forEach(authorCount -> {
            assertThat(authorCount.getFullName(),
                    equalTo(newBooks.get(0).getAuthors().get(0).getFullName()));
            assertThat(authorCount.getCount(),
                    equalTo(Long.valueOf(newBooks.size())));
        });
    }

    /**
     * Create list of random books.
     *
     * @param uniqueAuthor when true all books have different author otherwise same.
     * @return list of books.
     */
    private List<Book> createBooks(final boolean uniqueAuthor) {
        final var titles = BookTest.provider.get(
                RandomDataProvider.TITLE, MAX_BOOKS);
        final var bookAuthors = BookTest.provider.get(
                RandomDataProvider.AUTHOR, MAX_BOOKS);
        final var publishers = BookTest.provider.get(
                RandomDataProvider.AUTHOR, MAX_BOOKS);
        final var isbn = BookTest.provider.get(
                RandomDataProvider.ISBN, MAX_BOOKS);
        final var years = BookTest.provider.get(
                RandomDataProvider.YEAR, MAX_BOOKS);

        final var newBooks = new ArrayList<Book>();
        for (var index = 0; index < MAX_BOOKS; ++index) {
            final var realAuthorIndex = uniqueAuthor? index : 0;
            newBooks.add(Book.builder()
                    .title(titles.get(index))
                    .author(Author.builder().fullName(bookAuthors.get(realAuthorIndex)).build())
                    .publisher(Publisher.builder().fullName(publishers.get(index)).build())
                    .isbn(isbn.get(index))
                    .yearOfPublication(Integer.valueOf(years.get(index)))
                    .build());
        }

        return newBooks;
    }
}
