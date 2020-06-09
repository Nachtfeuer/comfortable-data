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
import comfortable.data.tools.TemplateEngine;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
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
public class TodoTest {
    /**
     * Test id.
     */
    private static final int TODO_ID = 1234;

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
     * Template engine.
     */
    @Autowired
    TemplateEngine engine;

    /**
     * Testing deserialization from JSON.
     *
     * @throws JsonProcessingException when deserialization from json has failed
     */
    @Test
    public void testDeserializeFromJson() throws JsonProcessingException {
        final var separator = "\",";
        final var json = "{\"id\": 123, \"title\": \"" + TODO_TITLE + separator
                + "\"completed\": true,"
                + "\"description\": \"" + TODO_DESCRIPTION + separator
                + "\"priority\": \"" + TODO_PRIORITY + "\"}";
        final var mapper = new ObjectMapper();
        final var todo = mapper.readValue(json, Todo.class);

        assertThat(todo.getTitle(), equalTo(TODO_TITLE));
        assertThat(todo.getDescription(), equalTo(TODO_DESCRIPTION));
        assertThat(todo.isCompleted(), equalTo(true));
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
        final var currentDateTime = LocalDateTime.now();
        final var currentTimestamp = Timestamp.valueOf(currentDateTime);

        // Get current UTC date and time in zone UTC
        final var currentDateTimeWithTimeZone = LocalDateTime.now(
                ZoneOffset.UTC).atZone(ZoneId.of("UTC"));
        final var pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        final var strCurrentDateTime = currentDateTimeWithTimeZone.format(pattern);

        final var todo = Todo.builder()
                .id(TODO_ID)
                .created(currentTimestamp)
                .changed(currentTimestamp)
                .title(TODO_TITLE)
                .description(TODO_DESCRIPTION)
                .completed(false)
                .priority(Priority.fromString(TODO_PRIORITY))
                .tag(Tag.builder().name("tag1").build())
                .tag(Tag.builder().name("tag2").build())
                .project(Project.builder().name("project1").build())
                .project(Project.builder().name("project2").build())
                .build();

        final var mapper = new ObjectMapper();
        final var json = mapper.writeValueAsString(todo);

        final var renderer = engine.getRenderer("/templates/todo.json");
        renderer.add("id", todo.getId());
        renderer.add("created", strCurrentDateTime);
        renderer.add("changed", strCurrentDateTime);
        renderer.add("title", todo.getTitle());
        renderer.add("description", todo.getDescription());
        renderer.add("completed", todo.isCompleted());
        renderer.add("priority", todo.getPriority());
        renderer.add("tags", todo.getTags());
        renderer.add("projects", todo.getProjects());

        final var expectedJson = renderer.render().replaceAll("\\s(?=(\"[^\"]*\"|[^\"])*$)", "");
        System.out.println(expectedJson);
        assertThat(json, equalTo(expectedJson));
    }
}
