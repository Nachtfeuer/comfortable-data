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

import comfortable.data.database.BookPublisherRepository;
import comfortable.data.exceptions.InternalServerError;
import comfortable.data.model.CustomMediaType;
import comfortable.data.model.Publisher;
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
 * Controller for all supported operations on book publishers.
 *
 * <p>
 * The REST calls (except for the HTML) are all supporting JSON, XML, YAML and MsgPack for
 * sending/recieving data to/from the REST service.
 * </p>
 */
@RestController
@SuppressWarnings({
    "PMD.AvoidDuplicateLiterals",
    "checkstyle:classfanoutcomplexity"})
public class BookPublisherController {

    /**
     * dependency injection of database class responsible for storing and querying data.
     */
    @Autowired
    private transient BookPublisherRepository repository;

    /**
     * Add (create) given publisher to the backend (if not present).
     *
     * @param publisher book publisher model data.
     * @return created or updated publisher.
     */
    @PostMapping(value = "/books/publishers", produces = {
        CustomMediaType.APPLICATION_JSON_VALUE, CustomMediaType.APPLICATION_XML_VALUE,
        CustomMediaType.APPLICATION_YAML_VALUE, CustomMediaType.APPLICATION_MSGPACK_VALUE})
    @Timed(value = "books.publishers.create.or.update",
            extraTags = {"books.publishers", "create.or.update"})
    public Publisher createOrUpdatePublisher(@RequestBody final Publisher publisher) {
        final Publisher responsePublisher;
        final var optionalAuthor = repository.findById(publisher.getFullName());
        if (optionalAuthor.isPresent()) {
            responsePublisher = optionalAuthor.get();
        } else {
            responsePublisher = repository.save(publisher);
        }
        return responsePublisher;
    }

    /**
     * Provide list of all publishers.
     *
     * @return list of publishers.
     */
    @GetMapping(value = "/books/publishers", produces = {
        CustomMediaType.APPLICATION_JSON_VALUE, CustomMediaType.APPLICATION_XML_VALUE,
        CustomMediaType.APPLICATION_YAML_VALUE, CustomMediaType.APPLICATION_MSGPACK_VALUE})
    @Timed(value = "books.publishers.get.list", extraTags = {"books.publishers", "list"})
    public List<Publisher> getListOfPublishers() {
        return repository.findAll();
    }

    /**
     * Provide list of publishers filtered by name containing search string.
     *
     * @param spec the search spec (here: fullName allowing "like" and with ignoring letter case)
     * @return provide list of publishers.
     */
    @GetMapping(value = "/books/publishers", produces = {
        CustomMediaType.APPLICATION_JSON_VALUE, CustomMediaType.APPLICATION_XML_VALUE,
        CustomMediaType.APPLICATION_YAML_VALUE, CustomMediaType.APPLICATION_MSGPACK_VALUE},
            params = {"fullName"})
    @Timed(value = "books.publishers.get.by.spec", extraTags = {"books.publishers", "spec"})
    public List<Publisher> getListOfPublishersBySpec(
            @Spec(path = "fullName", spec = LikeIgnoreCase.class)
            final Specification<Publisher> spec) {
        return repository.findAll(spec);
    }

    /**
     * Provide list of books publishers as HTML.
     *
     * @return provide list of book publishers rendered into HTML.
     */
    @GetMapping(value = "/books/publishers", produces = {CustomMediaType.TEXT_HTML_VALUE})
    @Timed(value = "books.publishers.get.html", extraTags = {"books.publishers", "html"})
    public String renderHtml() {
        final var content = FileTools.readResource("/html/publishers.dynamic.html");
        if (content != null) {
            return content;
        }

        throw new InternalServerError("Failed to provide HTML for book publishers");
    }
}
