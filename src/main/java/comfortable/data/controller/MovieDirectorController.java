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

import comfortable.data.database.DirectorRepository;
import comfortable.data.model.CustomMediaType;
import comfortable.data.model.Director;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for all supported operations on movie directors.
 */
@RestController
public class MovieDirectorController {

    /**
     * dependency injection of database class responsible for storing and
     * querying data.
     */
    @Autowired
    private transient DirectorRepository repository;

    /**
     * Add (create) given director to the backend (if not present).
     *
     * @param director movie director model data.
     * @return created or updated director.
     */
    @PostMapping(value = "/movies/directors", produces = {CustomMediaType.APPLICATION_JSON_VALUE,
        CustomMediaType.APPLICATION_XML_VALUE, CustomMediaType.APPLICATION_YAML_VALUE})
    public Director createOrUpdateDirector(@RequestBody final Director director) {
        return this.repository.save(director);
    }

    /**
     * Provide list of directors.
     *
     * @return list of given directors.
     */
    @GetMapping(value = "/movies/directors", produces = {CustomMediaType.APPLICATION_JSON_VALUE,
        CustomMediaType.APPLICATION_XML_VALUE, CustomMediaType.APPLICATION_YAML_VALUE})
    public List<Director> getListOfDirectors() {
        return this.repository.findAll();
    }
}
