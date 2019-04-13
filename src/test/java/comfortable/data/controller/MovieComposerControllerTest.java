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

import comfortable.data.model.Composer;
import comfortable.data.model.CustomMediaType;
import comfortable.data.model.Director;
import comfortable.data.tools.RequestMaker;
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

/**
 * Testing of {@link MovieComposerController} class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MovieComposerControllerTest {

    /**
     * test composer.
     */
    private static final String COMPOSER_1 = "Henry Mancini";

    /**
     * another test composer.
     */
    private static final String COMPOSER_2 = "Vangelis";

    /**
     * yet another test composer.
     */
    private static final String COMPOSER_3 = "James Horner";

    @Autowired
    private MockMvc mvc;

    /**
     * Using /movies/composers REST to create a new composer and to receive the id as JSON response.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreateComposerWithJsonResponse() throws Exception {
        runTest(COMPOSER_1, CustomMediaType.APPLICATION_JSON);
    }

    /**
     * Using /movies/composers REST to create a new composer and to receive the id as XML response.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreateComposerWithXmlResponse() throws Exception {
        runTest(COMPOSER_2, CustomMediaType.APPLICATION_XML);
    }

    /**
     * Using /movies/composers REST to create a new composer and to receive the id as YAML response.
     *
     * @throws Exception (should never happen)
     */
    @Test
    public void testCreateComposerWithYamlResponse() throws Exception {
        runTest(COMPOSER_3, CustomMediaType.APPLICATION_YAML);
    }

    /**
     * Performing create or update request and a request to retrieve the list. Finally the list
     * should contain the created (or updated) record once only.
     *
     * @param fullName full name of composer
     * @param expectedMediaType expected response type (JSON, XML or YAML).
     * @throws Exception if coonversion or a request has failed.
     */
    private void runTest(final String fullName, final MediaType expectedMediaType) throws Exception {
        final var requestMaker = new RequestMaker(this.mvc);
        final var  newComposer = new Composer(fullName);
        final var responseComposer = requestMaker
                .createOrUpdate("/movies/composers", newComposer, CustomMediaType.APPLICATION_JSON, expectedMediaType);

        assertThat(responseComposer.getFullName(), equalTo(fullName));

        final var composers = requestMaker.getListOfComposers(expectedMediaType);
        assertThat(composers.stream()
                .filter(composer -> composer.getFullName().equals(fullName))
                .count(), equalTo(1L));

    }
}
