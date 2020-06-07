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
import comfortable.data.model.CustomMediaType;
import comfortable.data.model.Todo;
import java.util.List;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
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
    @SuppressWarnings("PMD.AvoidFinalLocalVariable")
    public Todo createOrUpdate(@RequestBody final Todo todo) {
        final Todo finalTodo;
        
        if (todo.getId() == null) {
            finalTodo = repository.save(todo);
        } else {
            final var optionalTodo = repository.findById(todo.getId());
            if (optionalTodo.isPresent()) {
                finalTodo = optionalTodo.get();
            } else {
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
    @GetMapping(value = "todos", produces = {
        CustomMediaType.APPLICATION_JSON_VALUE, CustomMediaType.APPLICATION_XML_VALUE,
        CustomMediaType.APPLICATION_YAML_VALUE, CustomMediaType.APPLICATION_MSGPACK_VALUE},
            params = {"id"})
    public List<Todo> getListOfTodosBySpec(
            @Spec(path = "id", spec=Equal.class)
            final Specification<Todo> spec) {
        return repository.findAll(spec);
    }
}
