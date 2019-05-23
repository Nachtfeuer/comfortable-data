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
import comfortable.data.model.Director;
import comfortable.data.tools.RequestMaker;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Testing of {@link MovieDirectorController} class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MovieDirectorControllerTest {

    /**
     * test director.
     */
    private static final String DIRECTOR_1 = "Howard Hawks";

    /**
     * another test director.
     */
    private static final String DIRECTOR_2 = "Ridley Scott";

    /**
     * yet another test director.
     */
    private static final String DIRECTOR_3 = "James Cameron";

    /**
     * Test client for web layer.
     */
    @Autowired
    private MockMvc mvc;

    /**
     * Using /movies/directors REST to create a new director and to receive the id as JSON response.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreateDirectorWithJsonResponse() throws Exception {
        runTest(DIRECTOR_1, CustomMediaType.APPLICATION_JSON);
    }

    /**
     * Using /movies/directors REST to create a new director and to receive the id as XML response.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreateDirectorWithXmlResponse() throws Exception {
        runTest(DIRECTOR_2, CustomMediaType.APPLICATION_XML);
    }

    /**
     * Using /movies/directors REST to create a new director and to receive the id as YAML response.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreateDirectorWithYamlResponse() throws Exception {
        runTest(DIRECTOR_3, CustomMediaType.APPLICATION_YAML);
    }

        /**
     * Querying with ignore case for one director.
     *
     * @throws Exception should never happen.
     */
    @Test
    public void testQueryOneDirectorByFilterWithIgnoreCase() throws Exception {
        createDirector(DIRECTOR_1, CustomMediaType.APPLICATION_JSON);
        createDirector(DIRECTOR_2, CustomMediaType.APPLICATION_JSON);
        createDirector(DIRECTOR_3, CustomMediaType.APPLICATION_JSON);

        final var mapper = new ObjectMapper();
        final var content = this.mvc.perform(
                get("/movies/directors?fullName=howard").accept(CustomMediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        final List<Director> directors = mapper.readValue(content,
                new TypeReference<List<Director>>() {
        });
        assertThat(directors.size(), equalTo(1));
        assertThat(directors.get(0).getFullName(), equalTo(DIRECTOR_1));
    }

    /**
     * Performing create or update request and a request to retrieve the list. Finally the list
     * should contain the created (or updated) record once only.
     *
     * @param fullName full name of director
     * @param expectedMediaType expected response type (JSON, XML or YAML).
     * @throws Exception if coonversion or a request has failed.
     */
    private void runTest(final String fullName,
            final MediaType expectedMediaType) throws Exception {
        final var responseDirector = createDirector(fullName, expectedMediaType);
        assertThat(responseDirector.getFullName(), equalTo(fullName));

        final var requestMaker = new RequestMaker(this.mvc);
        final var directors = requestMaker.getListOfDirectors(expectedMediaType);
        assertThat(directors.stream()
                .filter(director -> director.getFullName().equals(fullName))
                .count(), equalTo(1L));
    }

    /**
     * Create or update movie director in database.
     *
     * @param fullName name of the movie director to be created or updated.
     * @param expectedMediaType expected response type (JSON, XML or YAML).
     * @return response director.
     * @throws Exception when request or conversion has failed.
     */
    private Director createDirector(final String fullName,
            final MediaType expectedMediaType) throws Exception {
        final var requestMaker = new RequestMaker(this.mvc);
        final Director newDirector = new Director(fullName);
        return requestMaker.createOrUpdate("/movies/directors",
                newDirector, CustomMediaType.APPLICATION_JSON, expectedMediaType);
    }
}
