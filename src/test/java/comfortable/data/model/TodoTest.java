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
package comfortable.data.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import comfortable.data.tools.DateAndTime;
import comfortable.data.tools.RandomDataProvider;
import comfortable.data.tools.TemplateEngine;
import java.io.IOException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testing of class {@link Todo}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@SuppressWarnings("checkstyle:classfanoutcomplexity")
public class TodoTest {

    /**
     * Test id.
     */
    private static final long TODO_ID = 1234L;

    /**
     * Test title.
     */
    private static final String TODO_TITLE = "some title";

    /**
     * Test description.
     */
    private static final String TODO_DESCRIPTION = "some description";

    /**
     * Test Priority.
     */
    private static final String TODO_PRIORITY = " ";

    /**
     * Test working time.
     */
    private static final long TODO_WORKING_TIME = 123;

    /**
     * Provider for test data.
     */
    private static RandomDataProvider provider;

    /**
     * Template engine.
     */
    @Autowired
    private TemplateEngine engine;

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
     * Testing deserialization from JSON.
     *
     * @throws JsonProcessingException when deserialization from json has failed
     */
    @Test
    public void testDeserializeFromJson() throws JsonProcessingException {
        final var now = DateAndTime.now();
        final var todoA = createTestTodo(now);
        final var mapper = new ObjectMapper();
        final var json = mapper.writeValueAsString(todoA);
        final var todoB = mapper.readValue(json, Todo.class);

        assertThat(todoA, equalTo(todoB));
    }

    /**
     * Testing serialization to JSON.
     *
     * @throws JsonProcessingException when serialization has failed
     * @throws IOException when reading JSON has failed.
     */
    @Test
    public void testSerializeToJson() throws JsonProcessingException, IOException {
        // Date and time at current zone (serializing to json converts to UTC)
        final var currentDateTime = DateAndTime.now();

        final var todo = createTestTodo(currentDateTime);
        final String expectedJson = renderTodo(todo, currentDateTime);
        final var mapper = new ObjectMapper();
        final var json = mapper.writeValueAsString(todo);

        assertThat(json, equalTo(expectedJson));
    }

    /**
     * Create test todo.
     *
     * @param currentDateTime current date and time.
     * @return test todo object.
     */
    private static Todo createTestTodo(final DateAndTime currentDateTime) {
        return Todo.builder()
                .id(TODO_ID)
                .created(currentDateTime)
                .changed(currentDateTime)
                .title(TODO_TITLE)
                .description(TODO_DESCRIPTION)
                .completed(false)
                .priority(Priority.fromString(TODO_PRIORITY))
                .complexity(Complexity.MEDIUM)
                .tag(Tag.builder().name("tag1").build())
                .tag(Tag.builder().name("tag2").build())
                .project(Project.builder().name("project1").build())
                .project(Project.builder().name("project2").build())
                .build();
    }

    /**
     * Render todo as JSON string with a template.
     *
     * @param todo test todo data
     * @param currentDateTime current date and time
     * @return JSON string representation of the todo.
     */
    private String renderTodo(final Todo todo,
                              final DateAndTime currentDateTime) throws IOException {
        final var renderer = engine.getRenderer("/templates/todo.json");
        renderer.add("id", todo.getId());
        renderer.add("created", currentDateTime.toString());
        renderer.add("changed", currentDateTime.toString());
        renderer.add("title", todo.getTitle());
        renderer.add("description", todo.getDescription());
        renderer.add("completed", todo.isCompleted());
        renderer.add("priority", todo.getPriority());
        renderer.add("complexity", todo.getComplexity());
        renderer.add("tags", todo.getTags());
        renderer.add("projects", todo.getProjects());
        renderer.add("workingTime", todo.getWorkingTime());
        renderer.add("estimatedWorkingTime", todo.getEstimatedWorkingTime());

        return renderer.render().replaceAll("\\s(?=(\"[^\"]*\"|[^\"])*$)", "");        
    }
}
