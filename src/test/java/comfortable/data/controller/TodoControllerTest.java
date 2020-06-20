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
import comfortable.data.model.Complexity;
import comfortable.data.model.CustomMediaType;
import comfortable.data.model.Priority;
import comfortable.data.model.Project;
import comfortable.data.model.Todo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import comfortable.data.model.Tag;
import comfortable.data.tools.RandomDataProvider;
import comfortable.data.tools.RequestMaker;
import java.io.IOException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.BeforeClass;
import org.springframework.http.MediaType;

/**
 * Testing of {@link TodoController} class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SuppressWarnings("checkstyle:classfanoutcomplexity")
public class TodoControllerTest {
    /**
     * Delay one second.
     */
    private static final long DELAY = 1000L;

    /**
     * The base request for the todos.
     */
    private static final String REQUEST = "/todos";

    /**
     * Using the id parameter for the get reqest.
     */
    private static final String ID_PARAM = "?id=";

    /**
     * Provider for test data.
     */
    private static RandomDataProvider provider;

    /**
     * Database access for todos.
     */
    @Autowired
    private TodoRepository repository;

    /**
     * Test client for web layer.
     */
    @Autowired
    private MockMvc mvc;

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
     * Cleanup todos for consistent tests.
     */
    @Before
    public void setUp() {
        this.repository.deleteAll();
    }

    /**
     * Testing creation of a todo with JSON as communication format.
     * @throws Exception when request has failed.
     */
    @Test
    public void testCreateAndUpdateTodoForJson() throws Exception {
        testCreateAndUpdateTodo(MediaType.APPLICATION_JSON);
    }

    /**
     * Testing creation of a todo with XML as communication format.
     * @throws Exception when request has failed.
     */
    @Test
    public void testCreateTodoForXml() throws Exception {
        testCreateAndUpdateTodo(MediaType.APPLICATION_XML);
    }

    /**
     * Testing creation of a todo with YAML as communication format.
     *
     * @throws Exception when request has failed.
     */
    @Test
    public void testCreateTodoForYaml() throws Exception {
        testCreateAndUpdateTodo(CustomMediaType.APPLICATION_YAML);
    }

    /**
     * Testing deleting of todos.
     * @throws Exception should not happen.
     */
    @Test
    public void testDelete() throws Exception {
        final var todo1 = createTestData();
        final var todo2 = createTestData();

        final var requestMaker = new RequestMaker<Todo>(Todo.class, this.mvc);

        final var savedTodo1 = requestMaker.postData(REQUEST, MediaType.APPLICATION_JSON, todo1);
        final var savedTodo2 = requestMaker.postData(REQUEST, MediaType.APPLICATION_JSON, todo2);
        
        assertThat(requestMaker.getListOfData(REQUEST, MediaType.APPLICATION_JSON).size(),
                   equalTo(2));
        assertThat(requestMaker.deleteData(REQUEST + '/' + savedTodo1.getId()),
                   equalTo(savedTodo1.getId().toString()));
        assertThat(requestMaker.getListOfData(REQUEST, MediaType.APPLICATION_JSON).size(),
                   equalTo(1));
        assertThat(requestMaker.deleteData(REQUEST + '/' + savedTodo2.getId()),
                   equalTo(savedTodo2.getId().toString()));
        assertThat(requestMaker.getListOfData(REQUEST, MediaType.APPLICATION_JSON).size(),
                   equalTo(0));
    }

    /**
     * Get a todo by id using JSON format for communication.
     *
     * @throws Exception whe request has failed.
     */
    @Test
    public void testGetTodoByIdForJson() throws Exception {
        testGetDataByIdForContentType(MediaType.APPLICATION_JSON);
    }

    /**
     * Get a todo by id using XML format for communication.
     *
     * @throws Exception whe request has failed.
     */
    @Test
    public void testGetTodoByIdForXml() throws Exception {
        testGetDataByIdForContentType(MediaType.APPLICATION_XML);
    }

    /**
     * Get a todo by id using YAML format for communication.
     *
     * @throws Exception whe request has failed.
     */
    @Test
    public void testGetTodoByIdForYaml() throws Exception {
        testGetDataByIdForContentType(CustomMediaType.APPLICATION_YAML);
    }

    /**
     * Testing to receive all todos.
     * @throws Exception when request has failed.
     */
    @Test
    public void testGetAllTodosForJson() throws Exception {
        final var todo1 = createTestData();
        final var todo2 = createTestData();

        final var requestMaker = new RequestMaker<Todo>(Todo.class, this.mvc);
        final var savedTodo1 = requestMaker.postData(REQUEST, MediaType.APPLICATION_JSON, todo1);
        final var savedTodo2 = requestMaker.postData(REQUEST, MediaType.APPLICATION_JSON, todo2);
        final var todos = requestMaker.getListOfData(REQUEST, MediaType.APPLICATION_JSON);

        assertThat(todos.size(), equalTo(2));

        assertThat(todos.get(0).getId(), equalTo(savedTodo1.getId()));
        assertThat(todos.get(0).getTitle(), equalTo(todo1.getTitle()));
        assertThat(todos.get(0).getDescription(), equalTo(todo1.getDescription()));
        assertThat(todos.get(0).getTags(), equalTo(todo1.getTags()));
        assertThat(todos.get(0).getProjects(), equalTo(todo1.getProjects()));

        assertThat(todos.get(1).getId(), equalTo(savedTodo2.getId()));
        assertThat(todos.get(1).getTitle(), equalTo(todo2.getTitle()));
        assertThat(todos.get(1).getDescription(), equalTo(todo2.getDescription()));
        assertThat(todos.get(1).getTags(), equalTo(todo2.getTags()));
        assertThat(todos.get(1).getProjects(), equalTo(todo2.getProjects()));
    }

    /**
     * Test update create AND update of a todo.
     *
     * @param contentType for a concrete media type.
     * @throws Exception should not happen.
     */
    private void testCreateAndUpdateTodo(final MediaType contentType) throws Exception {
        final var todo1 = createTestData();
        final var todo2 = createTestData();

        final var requestMaker = new RequestMaker<Todo>(Todo.class, this.mvc);
        final var savedTodo1 = requestMaker.postData(REQUEST, contentType, todo1);

        savedTodo1.setTitle(todo2.getTitle());
        savedTodo1.setDescription(todo2.getDescription());

        // ensuring that the change date is different
        // (must be seconds since milliseconds are ignored)
        Thread.sleep(DELAY);
        final var savedTodo2 = requestMaker.postData(REQUEST, contentType, savedTodo1);
        
        assertThat(savedTodo2.getId(), equalTo(savedTodo1.getId()));
        assertThat(savedTodo2.getCreated(), equalTo(savedTodo1.getCreated()));
        assertThat(savedTodo2.getCreated(), not(equalTo(null)));
        assertThat(savedTodo2.getChanged(), not(equalTo(savedTodo1.getChanged())));
        assertThat(savedTodo2.getTitle(), equalTo(savedTodo1.getTitle()));
        assertThat(savedTodo2.getDescription(), equalTo(savedTodo1.getDescription()));
        assertThat(savedTodo2.getPriority(), equalTo(savedTodo1.getPriority()));        
        assertThat(savedTodo2.getTags(), equalTo(savedTodo1.getTags()));        
    }
    
    /**
     * Testing to retrieve one todo by id customizing communication with
     * given mediat type supporting JSON, YAML, XML and MSGPACK.
     *
     * @param contentType media type to use for communication.
     * @throws Exception when a request has failed.
     */
    private void testGetDataByIdForContentType(final MediaType contentType) throws Exception {
        final var todo1 = createTestData();
        final var todo2 = createTestData();

        final var requestMaker = new RequestMaker<Todo>(Todo.class, this.mvc);
        final var savedTodo1 = requestMaker.postData(REQUEST, contentType, todo1);
        final var savedTodo2 = requestMaker.postData(REQUEST, contentType, todo2);

        final var savedTodos1 = requestMaker.getData(
                REQUEST + ID_PARAM + savedTodo1.getId(), contentType);
        final var savedTodos2 = requestMaker.getData(
                REQUEST + ID_PARAM + savedTodo2.getId(), contentType);
        
        assertThat(savedTodos1.size(), equalTo(1));
        assertThat(savedTodos2.size(), equalTo(1));

        assertThat(savedTodo1, equalTo(savedTodos1.get(0)));
        assertThat(savedTodo2, equalTo(savedTodos2.get(0)));        
    }

    /**
     * Provide (random) Test data.
     *
     * @return Test instance of class {@link Todo}.
     */
    private Todo createTestData() {
        return Todo.builder()
                .title(provider.get(RandomDataProvider.TODO_TITLE))
                .description(provider.get(RandomDataProvider.TODO_DESCRIPTION))
                .priority(Priority.fromString(
                        provider.get(RandomDataProvider.TODO_PRIORITY)))
                .complexity(Complexity.fromString(
                        provider.get(RandomDataProvider.TODO_COMPLEXITY)))
                .tag(Tag.builder().name(
                        provider.get(RandomDataProvider.TODO_TAG)).build())
                .tag(Tag.builder().name(
                        provider.get(RandomDataProvider.TODO_TAG)).build())
                .project(Project.builder().name(
                        provider.get(RandomDataProvider.TODO_PROJECT)).build())
                .project(Project.builder().name(
                        provider.get(RandomDataProvider.TODO_PROJECT)).build())
                .build();
    }
}
