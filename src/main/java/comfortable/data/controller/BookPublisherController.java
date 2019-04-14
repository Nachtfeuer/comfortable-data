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
import comfortable.data.model.CustomMediaType;
import comfortable.data.model.Publisher;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for all supported operations on book publishers.
 */
@RestController
public class BookPublisherController {

    /**
     * dependency injection of database class responsible for storing and
     * querying data.
     */
    @Autowired
    private transient BookPublisherRepository repository;

    /**
     * Add (create) given publisher to the backend (if not present).
     *
     * @param publisher book publisher model data.
     * @return created or updated publisher.
     */
    @PostMapping(value = "/book/publishers", produces = {CustomMediaType.APPLICATION_JSON_VALUE,
        CustomMediaType.APPLICATION_XML_VALUE, CustomMediaType.APPLICATION_YAML_VALUE})
    public Publisher createOrUpdatePublisher(@RequestBody final Publisher publisher) {
        return repository.save(publisher);
    }

    /**
     * Provide list of publishers.
     *
     * @return provide list of publishers.
     */
    @GetMapping(value = "/book/publishers", produces = {CustomMediaType.APPLICATION_JSON_VALUE,
        CustomMediaType.APPLICATION_XML_VALUE, CustomMediaType.APPLICATION_YAML_VALUE})
    public List<Publisher> getListOfPublishers() {
        return repository.findAll();
    }
}
