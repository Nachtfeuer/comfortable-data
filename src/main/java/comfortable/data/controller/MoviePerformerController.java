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

import comfortable.data.database.PerformerRepository;
import comfortable.data.model.CustomMediaType;
import comfortable.data.model.Performer;
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
 * Controller for all supported operations on movie performers.
 */
@RestController
public class MoviePerformerController {

    /**
     * Dependency injection of database class responsible for storing and
     * querying data.
     */
    @Autowired
    private transient PerformerRepository repository;

    /**
     * Add (create) given perfomer to the backend (if not present).
     *
     * @param performer movie performer model data.
     * @return created or updated performer.
     */
    @PostMapping(value = "/movies/performers", produces = {CustomMediaType.APPLICATION_JSON_VALUE,
        CustomMediaType.APPLICATION_XML_VALUE, CustomMediaType.APPLICATION_YAML_VALUE})
    public Performer createOrUpdatePerformer(@RequestBody final Performer performer) {
        return this.repository.save(performer);
    }

    /**
     * Provide list of performers.
     *
     * @return provide list of performers.
     */
    @GetMapping(value = "/movies/performers", produces = {CustomMediaType.APPLICATION_JSON_VALUE,
        CustomMediaType.APPLICATION_XML_VALUE, CustomMediaType.APPLICATION_YAML_VALUE})
    public List<Performer> getListOfPerformers() {
        return this.repository.findAll();
    }

    /**
     * Provide list of performers filtered by name containing search string.
     *
     * @param spec the search spec (here: fullName allowing "like" and with ignoring letter case)
     * @return provide list of performers.
     */
    @GetMapping(value = "/movies/performers", produces = {CustomMediaType.APPLICATION_JSON_VALUE,
        CustomMediaType.APPLICATION_XML_VALUE, CustomMediaType.APPLICATION_YAML_VALUE},
        params = {"fullName"})
    public List<Performer> getListOfPerformersBySpec(
            @Spec(path = "fullName", spec = LikeIgnoreCase.class)
            final Specification<Performer> spec) {
        return this.repository.findAll(spec);
    }
}
