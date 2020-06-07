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
package comfortable.data.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import comfortable.data.tools.RandomDataProvider;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Testing of class {@link Author}.
 */
public class AuthorTest {

    /**
     * Max time difference.
     */
    private static final long MAX_TIME_DIFF = 500L;

    /**
     * Provider for test data.
     */
    private static RandomDataProvider provider;

    /**
     * Load test data.
     * @throws java.io.IOException should never happen here.
     */
    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        provider = RandomDataProvider.of("/test.data.json");
    }

    /**
     * Testing default constructor.
     */
    @Test
    public void testDefault() {
        final Author author = new Author();
        assertThat(author.getFullName(), equalTo(null));
    }

    /**
     * Testing changing of values via setter.
     */
    @Test
    public void testSetter() {
        final var fullName = AuthorTest.provider.get(RandomDataProvider.BOOK_AUTHOR);
        final Author author = new Author();
        author.setFullName(fullName);
        assertThat(author.getFullName(), equalTo(fullName));
    }

    /**
     * Testing creation via builder.
     */
    @Test
    public void testBuilder() {
        final var fullName = AuthorTest.provider.get(RandomDataProvider.BOOK_AUTHOR);
        final var author = Author.builder().fullName(fullName).build();
        assertThat(author.getFullName(), equalTo(fullName));
    }

    /**
     * Testing conversion to JSON and the way back.
     *
     * @throws java.lang.Exception should never happen
     */
    @Test
    public void testToAndFromJson() throws Exception {
        final var timestamp = Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC));
        final var author = Author.builder().fullName(
                AuthorTest.provider.get(RandomDataProvider.BOOK_AUTHOR)).created(timestamp).build();

        final var mapper = new ObjectMapper();
        final var json = mapper.writeValueAsString(author);
        final var finalAuthor = mapper.readValue(json, Author.class);
        final var diff = finalAuthor.getCreated().getTime() - author.getCreated().getTime();

        assertThat(finalAuthor.getFullName(), equalTo(author.getFullName()));
        // that's because the precision is seconds only.
        assertThat(diff, lessThanOrEqualTo(MAX_TIME_DIFF));
    }
}
