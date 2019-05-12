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

import comfortable.data.model.CustomMediaType;
import comfortable.data.model.Publisher;
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
 * Testing of {@link BookPublisherController} class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class BookPublisherControllerTest {

    /**
     * test publisher.
     */
    private static final String PUBLISHER_1 = "suhrkamp";

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
     * Using /authors/create REST to create a new publisher and to receive the id as JSON response.
     *
     * @throws Exception (should never happen).
     */
    @Test
    public void testCreatePublisherWithJsonResponse() throws Exception {
        runTest(PUBLISHER_1,
                CustomMediaType.APPLICATION_JSON, CustomMediaType.APPLICATION_JSON);
    }

    /**
     * Using /authors/create REST to create a new publisher and to receive the id as XML response.
     *
     * @throws Exception (should never happen).
     */
    @Test
    public void testCreatePublisherWithXmlOnly() throws Exception {
        runTest(PUBLISHER_2,
                CustomMediaType.APPLICATION_XML, CustomMediaType.APPLICATION_XML
    

    );
    }

    /**
     * Using /authors/create REST to create a new publisher and to receive the id as YAML response.
     *
     * @throws Exception (should never happen).
     */
    @Test
    public void testCreatePublisherWithYamlOnly() throws Exception {
        runTest(PUBLISHER_3,
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

        final var documentName = "get/books/publishers/byFullName";
        final var mapper = new ObjectMapper();
        final var content = this.mvc.perform(
                get("/books/publishers?fullName=h").accept(CustomMediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(documentName))
                .andReturn().getResponse().getContentAsString();
        final List<Publisher> publishers = mapper.readValue(content,
                new TypeReference<List<Publisher>>() {
        });

        // sorting by ascending names
        publishers.sort((entryA, entryB) -> entryA.getFullName().compareTo(entryB.getFullName()));

        assertThat(publishers.size(), equalTo(2));
        assertThat(publishers.get(0).getFullName(), equalTo(PUBLISHER_3));
        assertThat(publishers.get(1).getFullName(), equalTo(PUBLISHER_1));
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

        final var content = this.mvc.perform(get("/books/publishers")
                .accept(CustomMediaType.TEXT_HTML))
                .andReturn().getResponse().getContentAsString().trim();

        assertThat(content, startsWith("<!doctype html>"));
        assertThat(content, containsString("<title>Books / Publishers</title>"));
        assertThat(content, endsWith("</html>"));
    }

    /**
     * Performing create or update request and a request to retrieve the list. Finally the list
     * should contain the created (or updated) record once only.
     *
     * @param fullName full name of publisher
     * @param contentType request content type (JSON, XML or YAML).
     * @param expectedResponseType response content type (JSON, XML or YAML).
     * @throws Exception if coonversion or a request has failed.
     */
    private void runTest(final String fullName,
            final MediaType contentType,
            final MediaType expectedResponseType) throws Exception {
        final var responsePublisher = createPublisher(fullName,
                contentType, expectedResponseType);
        assertThat(responsePublisher.getFullName(), equalTo(fullName));

        final var requestMaker = new RequestMaker(this.mvc);
        final var publishers = requestMaker.getListOfPublishers(expectedResponseType);
        assertThat(publishers.stream()
                .filter(publisher -> publisher.getFullName().equals(fullName))
                .count(), equalTo(1L));
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

        final var documentName = "post/books/publishers/"
                + acceptContentType.toString().replace("application", "")
                + responseContentType.toString().replace("application", "");

        final String content = this.mvc.perform(post("/books/publishers")
                .accept(responseContentType)
                .contentType(acceptContentType)
                .content(converter.toString(newPublisher))).andExpect(status().isOk())
                .andDo(document(documentName))
                .andReturn().getResponse().getContentAsString();

        return converter.fromString(content);
    }
}
