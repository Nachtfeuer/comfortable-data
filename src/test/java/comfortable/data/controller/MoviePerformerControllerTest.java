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
import comfortable.data.model.CustomMediaType;
import comfortable.data.model.Performer;
import comfortable.data.tools.RequestMaker;
import java.nio.charset.StandardCharsets;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Testing of {@link MoviePerformerController} class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SuppressWarnings("checkstyle:classfanoutcomplexity")
public class MoviePerformerControllerTest {
    /**
     * The movies performer request.
     */
    private static final String REQUEST = "/movies/performers";

    /**
     * test performer.
     */
    private static final String PERFORMER_1 = "John Wayne";

    /**
     * another test performer.
     */
    private static final String PERFORMER_2 = "Maureen O'Hara";

    /**
     * yet another test performer.
     */
    private static final String PERFORMER_3 = "Cary Grant";

    /**
     * Test client for web layer.
     */
    @Autowired
    private MockMvc mvc;

    /**
     * Using /movies/performers REST to create a new performer and to receive the id as JSON
     * response.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreatePerformerWithJsonResponse() throws Exception {
        runTest(PERFORMER_1, CustomMediaType.APPLICATION_JSON);
    }

    /**
     * Using /movies/performers REST to create a new performer and to receive the id as XML
     * response.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreatePerformerWithXmlResponse() throws Exception {
        runTest(PERFORMER_2, CustomMediaType.APPLICATION_XML);
    }

    /**
     * Using /movies/performers REST to create a new performer and to receive the id as YAML
     * response.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreatePerformerWithYamlResponse() throws Exception {
        runTest(PERFORMER_3, CustomMediaType.APPLICATION_YAML);
    }

    /**
     * Querying with ignore case for one performer.
     *
     * @throws Exception should never happen.
     */
    @Test
    public void testQueryOnePerformerByFilterWithIgnoreCase() throws Exception {
        createPerformer(PERFORMER_1, CustomMediaType.APPLICATION_JSON);
        createPerformer(PERFORMER_2, CustomMediaType.APPLICATION_JSON);
        createPerformer(PERFORMER_3, CustomMediaType.APPLICATION_JSON);

        final var mapper = new ObjectMapper();
        final var content = this.mvc.perform(
                get("/movies/performers?fullName=john").accept(CustomMediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        final List<Performer> performers = mapper.readValue(content,
                new TypeReference<List<Performer>>() {
        });
        assertThat(performers.size(), equalTo(1));
        assertThat(performers.get(0).getFullName(), equalTo(PERFORMER_1));
    }
    
    /**
     * Performing create or update request and a request to retrieve the list. Finally the list
     * should contain the created (or updated) record once only.
     *
     * @param fullName full name of performer
     * @param expectedMediaType expected response type (JSON, XML or YAML).
     * @throws Exception if coonversion or a request has failed.
     */
    private void runTest(final String fullName,
            final MediaType expectedMediaType) throws Exception {
        final var responsePerformer = this.createPerformer(fullName, expectedMediaType);
        assertThat(responsePerformer.getFullName(), equalTo(fullName));

        final var requestMaker = new RequestMaker<Performer>(Performer.class, this.mvc);
        final var performers = requestMaker.getListOfData(REQUEST, expectedMediaType);
        assertThat(performers.stream()
                .filter(performer -> performer.getFullName().equals(fullName))
                .count(), equalTo(1L));
    }

    /**
     * Create or update performer in database.
     *
     * @param fullName name of the performer to be created or updated.
     * @param expectedMediaType expected response type (JSON, XML or YAML).
     * @return response performer.
     * @throws Exception when request or conversion has failed.
     */
    private Performer createPerformer(final String fullName,
            final MediaType expectedMediaType) throws Exception {
        final var requestMaker = new RequestMaker<Performer>(Performer.class, this.mvc);
        final Performer newPerformer = new Performer(fullName);
        return requestMaker.postData(REQUEST,
                expectedMediaType, newPerformer);
    }
}
