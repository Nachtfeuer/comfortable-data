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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import comfortable.data.database.BookAuthorRepository;
import comfortable.data.model.Author;
import comfortable.data.model.CustomMediaType;
import comfortable.data.tools.ContentConverter;
import comfortable.data.tools.RequestMaker;
import java.util.List;
import java.util.Set;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;

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
@SuppressWarnings("checkstyle:classfanoutcomplexity")
public class BookAuthorControllerTest {
    
    /**
     * The book authors (base) request.
     */
    private static final String REQUEST = "/books/authors";

    /**
     * Part of media type to remove for rest doc.
     */
    private static final String APPLICATION = "application";

    /**
     * test author.
     */
    private static final String BOOK_TITLE_1 = "Agatha Christie";

    /**
     * another test author.
     */
    private static final String BOOK_TITLE_2 = "Rex Stout";

    /**
     * yet another test author.
     */
    private static final String BOOK_TITLE_3 = "Stanislaw Lem";

    /**
     * Retries for same operation.
     */
    private static final int RETRIES = 10;

    /**
     * Test client for web layer.
     */
    @Autowired
    private MockMvc mvc;

    /**
     * Database access to book authors.
     */
    @Autowired
    private BookAuthorRepository repository;

    /**
     * Cleanup authors for consistent tests.
     */
    @Before
    public void setUp() {
        this.repository.deleteAll();
    }

    /**
     * Using /books/authors REST to create a new author sending/receiving JSON.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreateAuthorWithJsonOnly() throws Exception {
        runTest(Set.of(BOOK_TITLE_1),
                CustomMediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_JSON);
    }

    /**
     * Using /books/authors REST to create a new author sending/receiving XML.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreateAuthorWithXmlOnly() throws Exception {
        runTest(Set.of(BOOK_TITLE_2),
                CustomMediaType.APPLICATION_XML, CustomMediaType.APPLICATION_XML);
    }

    /**
     * Using /books/authors REST to create a new author sending/receiving YAML.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreateAuthorWithYamlOnly() throws Exception {
        runTest(Set.of(BOOK_TITLE_3),
                CustomMediaType.APPLICATION_YAML, CustomMediaType.APPLICATION_YAML);
    }

    /**
     * Querying with ignore case for one author.
     *
     * @throws Exception should never happen.
     */
    @Test
    public void testQueryOneAuthorByFilterWithIgnoreCase() throws Exception {
        createAuthor(BOOK_TITLE_1,
                CustomMediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_JSON);
        createAuthor(BOOK_TITLE_2,
                CustomMediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_JSON);
        createAuthor(BOOK_TITLE_3,
                CustomMediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_JSON);

        final var documentName = "get" + REQUEST + "/byFullName";
        final var mapper = new ObjectMapper();
        final var content = this.mvc.perform(
                get(REQUEST + "?fullName=is").accept(CustomMediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(documentName))
                .andReturn().getResponse().getContentAsString();

        final List<Author> authors = mapper.readValue(content,
                new TypeReference<List<Author>>() {
        });

        // sorting by ascending names
        authors.sort((entryA, entryB) -> entryA.getFullName().compareTo(entryB.getFullName()));

        assertThat(authors.size(), equalTo(2));
        assertThat(authors.get(0).getFullName(), equalTo(BOOK_TITLE_1));
        assertThat(authors.get(1).getFullName(), equalTo(BOOK_TITLE_3));
    }

    /**
     * Testing HTML response.
     *
     * @throws Exception should never happen.
     */
    @Test
    public void testHtmlReponse() throws Exception {
        createAuthor(BOOK_TITLE_1,
                CustomMediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_JSON);

        final var content = this.mvc.perform(get(REQUEST)
                .accept(CustomMediaType.TEXT_HTML))
                .andReturn().getResponse().getContentAsString().trim();

        assertThat(content, startsWith("<!doctype html>"));
        assertThat(content, containsString("<title>Books / Authors</title>"));
        assertThat(content, endsWith("</html>"));
    }

    /**
     * Testing date conversion.
     *
     * @throws Exception should never happen.
     */
    @Test
    public void testCreatedDate() throws Exception {
        final var responseAuthor = createAuthor(BOOK_TITLE_1,
                CustomMediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_JSON);
        assertThat(responseAuthor.getCreated(), not(equalTo(null)));
    }

    /**
     * Testing date conversion II.
     *
     * @throws Exception should never happen.
     */
    @Test
    public void testSameMultipleTimes() throws Exception {
        for (var ix = 0; ix < RETRIES; ++ix) {
            final var responseAuthor = createAuthor(BOOK_TITLE_1,
                    CustomMediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_JSON);
            assertThat(responseAuthor.getCreated(), not(equalTo(null)));
        }
    }

    /**
     * Performing create or update request and a request to retrieve the list. Finally the list
     * should contain the created (or updated) record once only.
     *
     * @param fullNames unique container with full names of authors.
     * @param contentType request content type (JSON, XML or YAML).
     * @param expectedResponseType response content type (JSON, XML or YAML).
     * @throws Exception if coonversion or a request has failed.
     */
    private void runTest(final Set<String> fullNames,
            final MediaType contentType,
            final MediaType expectedResponseType) throws Exception {
        for (var fullName : fullNames) {
            final var responseAuthor = createAuthor(fullName, contentType, expectedResponseType);
            assertThat(responseAuthor.getFullName(), equalTo(fullName));
        }

        final var requestMaker = new RequestMaker<Author>(Author.class, this.mvc);
        final var listOfAuthors = requestMaker.getListOfData(
                REQUEST, expectedResponseType);
        assertThat(listOfAuthors.size(), equalTo(fullNames.size()));

        listOfAuthors.forEach(author -> {
            assertThat(fullNames, hasItem(author.getFullName()));
            assertThat(author.getCreated(), not(equalTo(null)));
        });
    }

    /**
     * Create or update author in database.
     *
     * @param fullName name of the author to be created or updated.
     * @param acceptContentType content type to send (JSON, XML or YAML).
     * @param responseContentType expected response content type (JSON, XML or YAML).
     * @return response author.
     * @throws Exception when request or conversion has failed.
     */
    private Author createAuthor(final String fullName,
            final MediaType acceptContentType,
            final MediaType responseContentType) throws Exception {
        final Author newAuthor = Author.builder().fullName(fullName).build();
        final var converter = new ContentConverter<>(Author.class,
                responseContentType, acceptContentType);

        final var documentName = "post" + REQUEST + "/"
                + acceptContentType.toString().replace(APPLICATION, "")
                + responseContentType.toString().replace(APPLICATION, "");

        final String content = this.mvc.perform(post(REQUEST)
                .accept(responseContentType)
                .contentType(acceptContentType)
                .content(converter.toString(newAuthor))).andExpect(status().isOk())
                .andDo(document(documentName))
                .andReturn().getResponse().getContentAsString();

        return converter.fromString(content);
    }
}
