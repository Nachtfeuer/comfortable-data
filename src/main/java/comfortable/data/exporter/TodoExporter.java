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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Export of all todo into a JSON file.
 */
@Component
@SuppressWarnings({"checkstyle:classfanoutcomplexity"})
public class TodoExporter {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoExporter.class);

    /**
     * Application configuration.
     */
    @Autowired
    private transient ApplicationConfiguration configuration;

    /**
     * Database where to get the todos from.
     */
    @Autowired
    private transient TodoRepository repository;

    /**
     * when true the book import runs when service has started otherwise not.
     */
    @Value("${todos.export.enabled:false}")
    private transient boolean exportEnabled;

    /**
     * Date and time of last export.
     */
    private transient DateAndTime lastExport;

    /**
     * Export each 5 seconds.
     */
    @Scheduled(fixedRateString = "${todos.export.fixedRate}",
            initialDelayString = "${todos.export.initialDelay}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void exportData() {
        if (this.exportEnabled) {
            final var todos = this.repository.findAll();

            // let's see when there was the latest change
            final var latestChange = todos.stream()
                    .map(Todo::getChanged)
                    .mapToLong(DateAndTime::getValue)
                    .max();

            final boolean shouldRunExport = latestChange.isPresent()
                    && (this.lastExport == null
                    || latestChange.getAsLong() > this.lastExport.getValue());

            if (shouldRunExport && export2Json(this.configuration.getTodosPath(), todos)) {
                this.lastExport = DateAndTime.now();
            }
        }
    }

    /**
     * Export off all todos to JSON.
     *
     * @param path the path and name of file to write to.
     * @param todos the list of all todos
     * @return true when successful.
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private static boolean export2Json(final Path path, final List<Todo> todos) {
        boolean success = true;
        try {
            LOGGER.info("Exporting current todos!");
            final var mapper = new JsonMapper();
            final var options = new OpenOption[]{
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};

            Files.write(path,
                    mapper.writeValueAsString(todos).getBytes(StandardCharsets.UTF_8),
                    options);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            success = false;
        }
        return success;
    }
}
