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
package comfortable.data.importer;

import comfortable.data.database.Database;
import comfortable.data.model.Book;
import comfortable.data.model.CustomMediaType;
import comfortable.data.tools.ContentConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Automatically imports books from a path when the service has been started.
 */
@Component
public class BookImporter {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BookImporter.class);

    /**
     * when true the book import runs when service has started otherwise not.
     */
    @Value("${books.import.enabled:false}")
    private boolean booksImportEnabled;

    /**
     * dependency injection of database class responsible for storing and querying data.
     */
    @Autowired
    private Database database;

    /**
     * Automatically imports books from a path when the service has been started.
     */
    @PostConstruct
    public void importBooks() {
        if (this.booksImportEnabled) {
            final var path = System.getProperty("user.home");
            try {
                Files.list(Paths.get(path, "books"))
                        .filter(entry -> entry.toString().endsWith(".yaml"))
                        .forEach(entry -> importBook(entry));
            } catch (IOException e) {
                LOGGER.info(e.getMessage());
            }
        } else {
            LOGGER.info("Automatic book import is disabled!");
        }
    }

    /**
     * Import book from a path and filename.
     *
     * @param entry path and filename of book to import.
     */
    public void importBook(final Path entry) {
        try {
            if (entry != null) {
                LOGGER.info("Trying to import {}", entry);
                final var content = new String(Files.readAllBytes(entry), "utf-8");
                final var converter = new ContentConverter<>(Book.class,
                        CustomMediaType.APPLICATION_YAML);
                database.persist(converter.fromString(content));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
