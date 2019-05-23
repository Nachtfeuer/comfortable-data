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
package comfortable.data.controller;

import comfortable.data.model.Author;
import comfortable.data.model.Book;
import comfortable.data.model.CustomMediaType;
import comfortable.data.model.Publisher;
import comfortable.data.model.Tag;
import comfortable.data.tools.ContentConverter;
import comfortable.data.tools.RequestMaker;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Testing of {@link BookAuthorController} class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class BookControllerTest {
    /**
     * Test year of publication.
     */
    private static final int YEAR_OF_PUBLICATION = 1995;
    
    /**
     * Test number of pages.
     */
    private static final int PAGES = 228;

    /**
     * Request (base) for books.
     */
    private static final String REQUEST = "/books";
    
    /**
     * Media part for removal for rest documentation (path).
     */
    private static final String APPLICATION = "application";

    /**
     * Test client for web layer.
     */
    @Autowired
    private MockMvc mvc;

    /**
     * Using /books REST to create a new author and to receive the id as JSON response.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreateBookWithJsonOnly() throws Exception {
        runTest(createBook(),
                CustomMediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_JSON);
    }

    /**
     * Using /books REST to create a new author with XML.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreateBookWithXmlOnly() throws Exception {
        runTest(createBook(),
                CustomMediaType.APPLICATION_XML, CustomMediaType.APPLICATION_XML);
    }

    /**
     * Using /books REST to create a new author with YAML.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreateBookWithYamlOnly() throws Exception {
        runTest(createBook(),
                CustomMediaType.APPLICATION_YAML, CustomMediaType.APPLICATION_YAML);
    }

    /**
     * Testing HTML response.
     *
     * @throws Exception should never happen.
     */
    @Test
    public void testHtmlReponse() throws Exception {
        runTest(createBook(), CustomMediaType.APPLICATION_YAML, CustomMediaType.APPLICATION_JSON);
        final var content = this.mvc.perform(get(REQUEST).accept(CustomMediaType.TEXT_HTML))
                .andReturn().getResponse().getContentAsString().trim();

        assertThat(content, startsWith("<!doctype html>"));
        assertThat(content, containsString("<title>Books</title>"));
        assertThat(content, endsWith("</html>"));
    }

    /**
     * Performing create or update request and a request to retrieve the list. Finally the list
     * should contain the created (or updated) record once only.
     *
     * @param theBook the book to create or update in database
     * @param acceptContentType the type how to send book data to the server.
     * @param responseContentType the expected response type (XML, JSON or YAML).
     * @throws Exception when conversion has failed or one request
     */
    private void runTest(final Book theBook,
            final MediaType acceptContentType,
            final MediaType responseContentType) throws Exception {
        final var converter = new ContentConverter<>(Book.class,
                responseContentType, acceptContentType);

        final var documentName = "post" + REQUEST + "/"
                + acceptContentType.toString().replace(APPLICATION, "")
                + responseContentType.toString().replace(APPLICATION, "");

        final String content = this.mvc.perform(post(REQUEST)
                .accept(responseContentType)
                .contentType(acceptContentType)
                .content(converter.toString(theBook))).andExpect(status().isOk())
                .andDo(document(documentName))
                .andReturn().getResponse().getContentAsString();

        final var responseBook = converter.fromString(content);
        
        // adjusting dates
        theBook.getAuthors().get(0).setCreated(responseBook.getAuthors().get(0).getCreated());
        theBook.getPublisher().setCreated(responseBook.getPublisher().getCreated());

        // why null? assertThat(responseBook.getAuthors().get(0).getCreated(), not(null));
        // why null? assertThat(responseBook.getPublisher().getCreated(), not(null));
        assertThat(responseBook, equalTo(theBook));

        final var requestMaker = new RequestMaker(this.mvc);
        final var books = requestMaker.getListOfBooks(responseContentType);

        assertThat(books.stream()
                .filter(book -> book.getTitle().equals(theBook.getTitle()))
                .count(), equalTo(1L));
    }

    /**
     * Create a test book instance.
     *
     * @return test book instance.
     */
    private Book createBook() {
        final var description = "\"Der Unbesiegbare\", ein Raumkreuzer der schweren Klasse,"
                + " sucht nach einem verschollenen Schwesterschiff. Als es geffunden wird,"
                + " stehen die Wissenschaftler vor einem Rätsel. Es gibt keine findliche"
                + " Macht und keine Überlebenden: eine Rettungsexpedition, die allen fast"
                + " zum Verhängnis wird.";

        return Book.builder()
                .title("Der Unbesiegbare")
                .originalTitle("Niezwyciezony i inne opowiadania")
                .isbn("3-518-38959-9")
                .author(Author.builder().fullName("Stanislaw Lem").build())
                .publisher(Publisher.builder().fullName("suhrkamp").build())
                .pages(PAGES)
                .yearOfPublication(YEAR_OF_PUBLICATION)
                .description(description)
                .tag(new Tag("science-fiction"))
                .tag(new Tag("evolution"))
                .rating("sehr gut")
                .build();
    }
}
