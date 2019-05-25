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
package comfortable.data.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Manages application configuration.
 */
@Configuration
public class ApplicationConfiguration {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

    /**
     * The folder name for configuration and data of this application in the user home.
     */
    private static final String APPLICATION_FOLDER = "comfortable-data-service";

    /**
     * The sub folder name for books.
     */
    private static final String BOOKS_FOLDER = "books";

    /**
     * The sub folder name for movies.
     */
    private static final String MOVIES_FOLDER = "movies";

    /**
     * Executed after service has been started to ensure existence of the application folder for
     * configuration and data.
     */
    @PostConstruct
    public void onPostConstruct() {
        try {
            Files.createDirectories(getBooksPath());
            Files.createDirectories(getMoviesPath());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Provide path to the books.
     *
     * @return path to the books.
     */
    public Path getBooksPath() {
        return Paths.get(getUserHome(), APPLICATION_FOLDER, BOOKS_FOLDER);
    }

    /**
     * Provide path to the movies.
     *
     * @return path to the movies.
     */
    public Path getMoviesPath() {
        return Paths.get(this.getUserHome(), APPLICATION_FOLDER, MOVIES_FOLDER);
    }

    /**
     * Provide users home path.
     *
     * @return userrs home path.
     */
    private String getUserHome() {
        return System.getProperty("user.home");
    }
}
