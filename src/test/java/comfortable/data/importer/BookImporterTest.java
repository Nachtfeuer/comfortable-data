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
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.never;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testing {@link BookImporter#importBook(java.nio.file.Path).}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BookImporterTest {

    @MockBean
    private Database database;
    @Autowired
    private BookImporter bookImporter;

    /**
     * Checking import for a concrete book without storing it into a database.
     *
     * @throws IOException should never happen
     * @throws URISyntaxException should never happen
     */
    @Test
    public void testImportBook() throws IOException, URISyntaxException {
        final var path = Paths.get(getClass().getResource("/book.yaml").toURI());
        final var content = new String(Files.readAllBytes(path), "utf-8");

        final var converter = new ContentConverter<>(Book.class, CustomMediaType.APPLICATION_YAML);
        final var book = converter.fromString(content);

        bookImporter.importBook(path);
        Mockito.verify(database).persist(book);
    }

    /**
     * Testing the case that null pointer is passed.
     */
    @Test
    public void testImportBookFails() {
        bookImporter.importBook(null);
        Mockito.verify(database, never()).persist(any(Book.class));
    }
}
