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

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import comfortable.data.database.BookPublisherRepository;

import comfortable.data.model.CustomMediaType;
import comfortable.data.model.Publisher;
import comfortable.data.tools.ContentConverter;
import comfortable.data.tools.RequestMaker;
import java.util.Set;
import static java.util.stream.Collectors.toList;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
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
 * Testing of {@link BookPublisherController} class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class BookPublisherControllerTest {

    /**
     * Book publishers (base) request.
     */
    private static final String REQUEST = "/books/publishers";

    /**
     * Replace part of media type for rest documentation (path).
     */
    private static final String APPLICATION = "application";

    /**
     * test publisher.
     */
    private static final String PUBLISHER_1 = "goldmann";

    /**
     * another test publisher.
     */
    private static final String PUBLISHER_2 = "dtv";

    /**
     * yet another test publisher.
     */
    private static final String PUBLISHER_3 = "heyne";

    /**
     * Test client for web layer.
     */
    @Autowired
    private MockMvc mvc;

    /**
     * Database access to the publishers (entities).
     */
    @Autowired
    private BookPublisherRepository repository;

    /**
     * Remove all concrete test publishers before test.
     */
    @Before
    public void setUp() {
        if (this.repository.findById(PUBLISHER_1).isPresent()) {
            this.repository.deleteById(PUBLISHER_1);
        }
        if (this.repository.findById(PUBLISHER_2).isPresent()) {
            this.repository.deleteById(PUBLISHER_2);
        }
        if (this.repository.findById(PUBLISHER_3).isPresent()) {
            this.repository.deleteById(PUBLISHER_3);
        }
    }

    /**
     * Using /books/publisher REST to create a new publisher with JSON only.
     *
     * @throws Exception (should never happen).
     */
    @Test
    public void testCreatePublisherWithJsonOnly() throws Exception {
        runTest(Set.of(PUBLISHER_1),
                CustomMediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_JSON);
    }

    /**
     * Using /books/publisher REST to create a new publisher with XML only.
     *
     * @throws Exception (should never happen).
     */
    @Test
    public void testCreatePublisherWithXmlOnly() throws Exception {
        runTest(Set.of(PUBLISHER_2),
                CustomMediaType.APPLICATION_XML, CustomMediaType.APPLICATION_XML);
    }

    /**
     * Using /books/publishers REST to create a new publisher with YAML only.
     *
     * @throws Exception (should never happen).
     */
    @Test
    public void testCreatePublisherWithYamlOnly() throws Exception {
        runTest(Set.of(PUBLISHER_3),
                CustomMediaType.APPLICATION_YAML, CustomMediaType.APPLICATION_YAML);
    }

    /**
     * Querying with ignore case for one publisher.
     *
     * @throws Exception should never happen.
     */
    @Test
    public void testQueryOnePublisherByFilterWithIgnoreCase() throws Exception {
        createPublisher(PUBLISHER_1,
                CustomMediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_JSON);
        createPublisher(PUBLISHER_2,
                CustomMediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_JSON);
        createPublisher(PUBLISHER_3,
                CustomMediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_JSON);

        final var documentName = "get" + REQUEST + "/byFullName";
        final var mapper = new ObjectMapper();
        final var content = this.mvc.perform(
                get(REQUEST + "?fullName=n").accept(CustomMediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(documentName))
                .andReturn().getResponse().getContentAsString();
        final List<Publisher> listOfPublishers = mapper.readValue(content,
                new TypeReference<List<Publisher>>() {
        });

        // there might be more publishers from other tests but
        // we are interested to have at least the two from this test:
        final long count = listOfPublishers.stream()
                .filter(publisher
                        -> publisher.getFullName().equals(PUBLISHER_1)
                || publisher.getFullName().equals(PUBLISHER_2)
                || publisher.getFullName().equals(PUBLISHER_3)).count();

        assertThat(count, equalTo(2L));
    }

    /**
     * Testing HTML response.
     *
     * @throws Exception should never happen.
     */
    @Test
    public void testHtmlReponse() throws Exception {
        createPublisher(PUBLISHER_1,
                CustomMediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_JSON);

        final var content = this.mvc.perform(get(REQUEST)
                .accept(CustomMediaType.TEXT_HTML))
                .andReturn().getResponse().getContentAsString().trim();

        assertThat(content, startsWith("<!doctype html>"));
        assertThat(content, containsString("<title>Books / Publishers</title>"));
        assertThat(content, endsWith("</html>"));
    }

    /**
     * Testing date conversion.
     *
     * @throws Exception should never happen.
     */
    @Test
    public void testCreatedDate() throws Exception {
        final var responsePublisher = createPublisher(PUBLISHER_1,
                CustomMediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_JSON);
        assertThat(responsePublisher.getCreated(), not(equalTo(null)));
    }

    /**
     * Performing create or update request and a request to retrieve the list. Finally the list
     * should contain the created (or updated) record once only.
     *
     * @param fullNames unique set of full names of publishers.
     * @param contentType request content type (JSON, XML or YAML).
     * @param expectedResponseType response content type (JSON, XML or YAML).
     * @throws Exception if coonversion or a request has failed.
     */
    private void runTest(final Set<String> fullNames,
            final MediaType contentType,
            final MediaType expectedResponseType) throws Exception {
        for (var fullName : fullNames) {
            final var responsePublisher = createPublisher(fullName,
                    contentType, expectedResponseType);
            assertThat(responsePublisher.getFullName(), equalTo(fullName));
        }

        final var requestMaker = new RequestMaker(this.mvc);
        final var listOfPublishers = requestMaker.getListOfPublishers(expectedResponseType);

        final var filteredListOfPublishers = listOfPublishers.stream()
                .filter(publisher -> fullNames.contains(publisher.getFullName()))
                .collect(toList());

        assertThat(filteredListOfPublishers.size(), equalTo(fullNames.size()));
        filteredListOfPublishers.forEach(publisher -> {
            assertThat(publisher.getCreated(), not(equalTo(null)));
        });
    }

    /**
     * Create or update author in database.
     *
     * @param fullName name of the publisher to be created or updated.
     * @param acceptContentType request content type (JSON, XML or YAML).
     * @param responseContentType response content type (JSON, XML or YAML).
     * @return response publisher.
     * @throws Exception when request or conversion has failed.
     */
    private Publisher createPublisher(final String fullName,
            final MediaType acceptContentType,
            final MediaType responseContentType) throws Exception {
        final Publisher newPublisher = Publisher.builder().fullName(fullName).build();
        final var converter = new ContentConverter<>(Publisher.class,
                responseContentType, acceptContentType);

        final var documentName = "post" + REQUEST + "/"
                + acceptContentType.toString().replace(APPLICATION, "")
                + responseContentType.toString().replace(APPLICATION, "");

        final String content = this.mvc.perform(post(REQUEST)
                .accept(responseContentType)
                .contentType(acceptContentType)
                .content(converter.toString(newPublisher))).andExpect(status().isOk())
                .andDo(document(documentName))
                .andReturn().getResponse().getContentAsString();

        return converter.fromString(content);
    }
}
