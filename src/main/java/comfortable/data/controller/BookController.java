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

import comfortable.data.database.BookRepository;
import comfortable.data.exceptions.InternalServerError;
import comfortable.data.model.AuthorCount;
import comfortable.data.model.Book;
import comfortable.data.model.CustomMediaType;
import comfortable.data.tools.FileTools;
import java.util.List;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Joins;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for all supported operations on books.
 */
@RestController
public final class BookController {

    /**
     * dependency injection of database class responsible for storing and querying data.
     */
    @Autowired
    private transient BookRepository repository;

    /**
     * Create or update book in database.
     *
     * @param book book to create or update in database.
     * @return persistent book
     */
    @PostMapping(value = "/books", produces = {
        CustomMediaType.APPLICATION_JSON_VALUE, CustomMediaType.APPLICATION_XML_VALUE,
        CustomMediaType.APPLICATION_YAML_VALUE, CustomMediaType.APPLICATION_MSGPACK_VALUE})
    public Book createOrUpdateBook(@RequestBody final Book book) {
        return this.repository.save(book);
    }

    /**
     * Provide list of books using given gilter.
     *
     * @param spec represent the filter that can handle the urls parameters passed by the user.
     * @return provide list of books.
     */
    @GetMapping(value = "/books", produces = {
        CustomMediaType.APPLICATION_JSON_VALUE, CustomMediaType.APPLICATION_XML_VALUE,
        CustomMediaType.APPLICATION_YAML_VALUE, CustomMediaType.APPLICATION_MSGPACK_VALUE})
    public List<Book> getListOfBooksBySpec(
            @Joins({
        @Join(path = "authors", alias = "a"),
        @Join(path = "publisher", alias = "p"),
        @Join(path = "tags", alias = "t")
    })
            @And({
        @Spec(path = "title", params = "title", spec = LikeIgnoreCase.class),
        @Spec(path = "series", params = "series", spec = LikeIgnoreCase.class),
        @Spec(path = "isbn", params = "isbn", spec = LikeIgnoreCase.class),
        @Spec(path = "a.fullName", params = "author", spec = LikeIgnoreCase.class),
        @Spec(path = "p.fullName", params = "publisher", spec = LikeIgnoreCase.class),
        @Spec(path = "t.name", params = "tag", spec = LikeIgnoreCase.class)
    })
            final Specification<Book> spec) {
        return repository.findAll(spec);
    }

    /**
     * List of authors and the number of books they are authors of in given database.
     *
     * @return list of authors and a counter.
     */
    @GetMapping(value = "/books/authors/count", produces = {
        CustomMediaType.APPLICATION_JSON_VALUE, CustomMediaType.APPLICATION_XML_VALUE,
        CustomMediaType.APPLICATION_YAML_VALUE, CustomMediaType.APPLICATION_MSGPACK_VALUE})
    public List<AuthorCount> getListOfAuthorCount() {
        return this.repository.queryAuthorCount();
    }

    /**
     * Provide list of books as HTML.
     *
     * @return provide list of book rendered into HTML.
     */
    @GetMapping(value = "/books", produces = {CustomMediaType.TEXT_HTML_VALUE})
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public String renderHtml() {
        final var content = FileTools.readResource("/html/books.dynamic.html");
        if (content != null) {
            return content;
        }

        throw new InternalServerError("Failed to provide HTML for books");
    }
}
