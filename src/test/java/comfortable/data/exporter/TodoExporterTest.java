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
package comfortable.data.exporter;

import com.fasterxml.jackson.databind.json.JsonMapper;
import comfortable.data.configuration.ApplicationConfiguration;
import comfortable.data.database.TodoRepository;
import comfortable.data.model.Todo;
import comfortable.data.tools.DateAndTime;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testing of class {@link TodoExporter}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@SuppressWarnings({"checkstyle:classfanoutcomplexity"})
public class TodoExporterTest {    
    /**
     * Mocking of the application configuration.
     */
    @MockBean
    private ApplicationConfiguration configuration;

    /**
     * Mocking of the repository.
     */
    @MockBean
    private TodoRepository repository;

    /**
     * The instance of the exporter for the todos.
     */
    @Autowired
    private TodoExporter exporter;

    @Test
    public void testingExport() throws IOException {
        final Path path = Files.createTempDirectory("todo-test-");
        final var jsonPath = Paths.get(path.toString(), "todos.json");
        final var now = DateAndTime.now();
        final var todos = List.of(
                Todo.builder().title("first todo").created(now).changed(now).build(),
                Todo.builder().title("second todo").created(now).changed(now).build());
        
        Mockito.when(this.configuration.getTodosPath()).thenReturn(jsonPath);
        Mockito.when(this.repository.findAll()).thenReturn(todos);
        this.exporter.exportData();
        
        final var content = new String(Files.readAllBytes(jsonPath));

        final var mapper = new JsonMapper();
        final var exportedTodos = mapper.readValue(content,
                    mapper.getTypeFactory().constructCollectionType(List.class, Todo.class));

        assertThat(exportedTodos, equalTo(todos));
        
        Files.delete(jsonPath);
        Files.delete(path);
    }
}
