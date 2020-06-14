/*
 * The MIT License
 *
 * Copyright 2020 Thomas Lehmann.
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

import comfortable.data.database.TodoRepository;
import comfortable.data.exceptions.InternalServerError;
import comfortable.data.model.CustomMediaType;
import comfortable.data.model.Todo;
import comfortable.data.tools.DateAndTime;
import comfortable.data.tools.FileTools;
import io.micrometer.core.annotation.Timed;
import java.util.List;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for all supported operations on todo data.
 *
 * <p>
 * The REST calls (except for the HTML) are all supporting JSON, XML, YAML and MsgPack for
 * sending/receiving data to/from the REST service.
 * </p>
 */
@RestController
@SuppressWarnings({"checkstyle:classfanoutcomplexity", "PMD.AvoidDuplicateLiterals"})
public class TodoController {

    /**
     * dependency injection of database class responsible for storing and querying data.
     */
    @Autowired
    private transient TodoRepository repository;

    /**
     * Create or update a todo.
     *
     * @param todo the todo data to be created or updated
     * @return the created or updated to (with the real id).
     */
    @PostMapping(value = "/todos", produces = {
        CustomMediaType.APPLICATION_JSON_VALUE, CustomMediaType.APPLICATION_XML_VALUE,
        CustomMediaType.APPLICATION_YAML_VALUE, CustomMediaType.APPLICATION_MSGPACK_VALUE})
    public Todo createOrUpdate(@RequestBody final Todo todo) {
        final Todo finalTodo;

        if (todo.getId() == null) {
            // create mode
            finalTodo = repository.save(todo);
        } else {
            final var optionalTodo = repository.findById(todo.getId());
            if (optionalTodo.isPresent()) {
                // update mode
                todo.setChanged(DateAndTime.now());
                finalTodo = repository.save(todo);
            } else {
                // create mode
                finalTodo = repository.save(todo);
            }
        }

        return finalTodo;
    }

    /**
     * Provide list of all todos.
     *
     * @return list of all todos.
     */
    @GetMapping(value = "/todos", produces = {
        CustomMediaType.APPLICATION_JSON_VALUE, CustomMediaType.APPLICATION_XML_VALUE,
        CustomMediaType.APPLICATION_YAML_VALUE, CustomMediaType.APPLICATION_MSGPACK_VALUE})
    public List<Todo> getListOfTodos() {
        return repository.findAll();
    }

    /**
     * Provide list of todos filtered by id (empty list or one entry if found).
     *
     * @param spec the search spec (here: id must match)
     * @return list of todos.
     */
    @GetMapping(value = "/todos", produces = {
        CustomMediaType.APPLICATION_JSON_VALUE, CustomMediaType.APPLICATION_XML_VALUE,
        CustomMediaType.APPLICATION_YAML_VALUE, CustomMediaType.APPLICATION_MSGPACK_VALUE},
            params = {"id"})
    public List<Todo> getListOfTodosBySpec(
            @Spec(path = "id", spec = Equal.class)
            final Specification<Todo> spec) {
        return repository.findAll(spec);
    }

    /**
     * Deleting a todo by a given id.
     *
     * @param id id of an existing todo.
     * @return reponse entity with id and http status code.
     */
    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Long> deleteTodoById(@PathVariable final Long id) {
        repository.deleteById(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    /**
     * Provide visualization and management of todos via HTML frontend.
     *
     * @return provide list of book authors rendered into HTML.
     */
    @GetMapping(value = "/todos", produces = {CustomMediaType.TEXT_HTML_VALUE})
    @Timed(value = "todos.get.html", extraTags = {"todos", "html"})
    public String renderHtml() {
        final var content = FileTools.readResource("/html/todos.dynamic.html");
        if (content != null) {
            return content;
        }

        throw new InternalServerError("Failed to provide HTML for book authors");
    }
}
