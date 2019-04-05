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
package comfortable.data.tools;

import comfortable.data.model.Book;
import comfortable.data.model.CustomMediaType;
import java.io.IOException;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 * Testing of class {@link ContentConverter}.
 */
public class ContentConverterTest {

    /**
     * Testing conversion from JSON.
     *
     * @throws IOException should never happen.
     */
    @Test
    public void testFromJsonString() throws IOException {
        final var converter = new ContentConverter<>(Book.class, CustomMediaType.APPLICATION_JSON);
        final var book = converter.fromString(readBook("/book.json"));
        validateBook(book);
    }

    /**
     * Testing conversion from XML.
     *
     * @throws IOException should never happen.
     */
    @Test
    public void testFromXmlString() throws IOException {
        final var converter = new ContentConverter<>(Book.class, CustomMediaType.APPLICATION_XML);
        final var book = converter.fromString(readBook("/book.xml"));
        validateBook(book);
    }

    /**
     * Testing conversion from YAML.
     *
     * @throws IOException should never happen.
     */
    @Test
    public void testFromYamlString() throws IOException {
        final var converter = new ContentConverter<>(Book.class, CustomMediaType.APPLICATION_YAML);
        final var book = converter.fromString(readBook("/book.yaml"));
        validateBook(book);
    }

    /**
     * Testing conversion to Json.
     *
     * @throws IOException should never happen.
     */
    @Test
    public void testToJson() throws IOException {
        final var converter = new ContentConverter<>(Book.class, CustomMediaType.APPLICATION_YAML);
        final var referenceBook = converter.fromString(readBook("/book.yaml"));

        final var jsonConverter = new ContentConverter<>(Book.class, 
                CustomMediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_JSON);
        final var book = jsonConverter.fromString(jsonConverter.toString(referenceBook));
        validateBook(book);
    }

    /**
     * Testing conversion to XML.
     *
     * @throws IOException should never happen.
     */
    @Test
    public void testToXml() throws IOException {
        final var converter = new ContentConverter<>(Book.class, CustomMediaType.APPLICATION_YAML);
        final var referenceBook = converter.fromString(readBook("/book.yaml"));

        final var xmlConverter = new ContentConverter<>(Book.class,
                CustomMediaType.APPLICATION_XML, CustomMediaType.APPLICATION_XML);
        final var book = xmlConverter.fromString(xmlConverter.toString(referenceBook));
        validateBook(book);
    }

    /**
     * Testing conversion to YAML.
     *
     * @throws IOException should never happen.
     */
    @Test
    public void testToYaml() throws IOException {
        final var converter = new ContentConverter<>(Book.class, CustomMediaType.APPLICATION_JSON);
        final var referenceBook = converter.fromString(readBook("/book.json"));

        final var yamlConverter = new ContentConverter<>(Book.class,
                CustomMediaType.APPLICATION_YAML, CustomMediaType.APPLICATION_YAML);
        final var book = yamlConverter.fromString(yamlConverter.toString(referenceBook));
        validateBook(book);
    }

    /**
     * Validating the book data.
     * 
     * @param book the book to validate.
     */
    private void validateBook(final Book book) {
        assertThat(book.getTitle(), equalTo("Der Unbesiegbare"));
        assertThat(book.getOriginalTitle(), equalTo("Niezwyciezony i inne opowiadania"));
        assertThat(book.getSeries(), equalTo("no series"));
        assertThat(book.getAuthors().get(0).getFullName(), equalTo("Stanislaw Lem"));
        assertThat(book.getPublisher().getFullName(), equalTo("suhrkamp"));
        assertThat(book.getYearOfPublication(), equalTo(1995));
        assertThat(book.getPages(), equalTo(226));
        assertThat(book.getIsbn(), equalTo("3-518-38959-9"));
        assertThat(book.getDescription(), containsString("ein Raumkreuzer der schweren Klasse"));
        assertThat(book.getTags().get(0).getName(), equalTo("science-fiction"));
    }

    /**
     * Reading a book file as string.
     *
     * @param pathAndFilename path and filename of the book.
     * @return the book content (file) as string.
     * @throws IOException when reading the book has failed.
     */
    private String readBook(final String pathAndFilename) throws IOException {
        final var stream = this.getClass().getResourceAsStream(pathAndFilename);
        final var content = new String(stream.readAllBytes());
        stream.close();
        return content;
    }
}
