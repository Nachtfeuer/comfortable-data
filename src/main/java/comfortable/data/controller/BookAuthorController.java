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

import comfortable.data.database.BookAuthorRepository;
import comfortable.data.exceptions.InternalServerError;
import comfortable.data.model.Author;
import comfortable.data.model.CustomMediaType;
import comfortable.data.tools.FileTools;
import io.micrometer.core.annotation.Timed;
import java.util.List;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for all supported operations on book authors.
 *
 * <p>
 * The REST calls (except for the HTML) are all supporting JSON, XML, YAML and MsgPack for
 * sending/recieving data to/from the REST service.
 * </p>
 */
@RestController
@SuppressWarnings({
    "PMD.AvoidDuplicateLiterals", "PMD.AvoidFinalLocalVariable",
    "checkstyle:classfanoutcomplexity"})
public class BookAuthorController {

    /**
     * dependency injection of database class responsible for storing and querying data.
     */
    @Autowired
    private transient BookAuthorRepository repository;

    /**
     * Add (create) given author to the backend (if not present).
     *
     * @param author book author model data.
     * @return created or updated author.
     */
    @PostMapping(value = "/books/authors", produces = {
        CustomMediaType.APPLICATION_JSON_VALUE, CustomMediaType.APPLICATION_XML_VALUE,
        CustomMediaType.APPLICATION_YAML_VALUE, CustomMediaType.APPLICATION_MSGPACK_VALUE})
    @Timed(value = "books.authors.create.or.update",
            extraTags = {"books.authors", "create.or.update"})
    public Author createOrUpdateAuthor(@RequestBody final Author author) {
        final Author responseAuthor;
        final var optionalAuthor = repository.findById(author.getFullName());
        if (optionalAuthor.isPresent()) {
            responseAuthor = optionalAuthor.get();
        } else {
            responseAuthor = repository.save(author);
        }
        return responseAuthor;
    }

    /**
     * Provide list of all authors.
     *
     * @return list of all authors.
     */
    @GetMapping(value = "/books/authors", produces = {
        CustomMediaType.APPLICATION_JSON_VALUE, CustomMediaType.APPLICATION_XML_VALUE,
        CustomMediaType.APPLICATION_YAML_VALUE, CustomMediaType.APPLICATION_MSGPACK_VALUE})
    @Timed(value = "books.authors.get.list", extraTags = {"books.authors", "list"})
    public List<Author> getListOfAuthors() {
        return repository.findAll();
    }

    /**
     * Provide list of authors filtered by name containing search string.
     *
     * @param spec the search spec (here: fullName allowing "like" and with ignoring letter case)
     * @return list of authors.
     */
    @GetMapping(value = "/books/authors", produces = {
        CustomMediaType.APPLICATION_JSON_VALUE, CustomMediaType.APPLICATION_XML_VALUE,
        CustomMediaType.APPLICATION_YAML_VALUE, CustomMediaType.APPLICATION_MSGPACK_VALUE},
            params = {"fullName"})
    @Timed(value = "books.authors.get.by.spec", extraTags = {"books.authors", "spec"})
    public List<Author> getListOfAuthorsBySpec(
            @Spec(path = "fullName", spec = LikeIgnoreCase.class)
            final Specification<Author> spec) {
        return repository.findAll(spec);
    }

    /**
     * Provide list of books authors as HTML.
     *
     * @return provide list of book authors rendered into HTML.
     */
    @GetMapping(value = "/books/authors", produces = {CustomMediaType.TEXT_HTML_VALUE})
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @Timed(value = "books.authors.get.html", extraTags = {"books.authors", "html"})
    public String renderHtml() {
        final var content = FileTools.readResource("/html/authors.dynamic.html");
        if (content != null) {
            return content;
        }

        throw new InternalServerError("Failed to provide HTML for book authors");
    }
}
