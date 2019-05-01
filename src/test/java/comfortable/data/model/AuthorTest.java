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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 * Testing of class {@link Author}.
 */
public class AuthorTest {
    /** test title. */
    private static final String AUTHOR_1 = "Agatha Christie";

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
        final Author author = new Author();
        author.setFullName(AUTHOR_1);
        assertThat(author.getFullName(), equalTo(AUTHOR_1));
    }

    /**
     * Testing creation via builder.
     */
    @Test
    public void testBuilder() {
        final var author = Author.builder().fullName(AUTHOR_1).build();
        assertThat(author.getFullName(), equalTo(AUTHOR_1));
    }

    /**
     * Testing conversion to JSON.
     * @throws java.lang.Exception should never happen
     */
    @Test
    public void testToJson() throws Exception {
        final var timestamp = Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC));
        final var author = Author.builder().fullName(AUTHOR_1).created(timestamp).build();
        
        final var mapper = new ObjectMapper();
        final var expectedJson = "{\"fullName\":\"Agatha Christie\","
                + "\"created\":xxx}".replace("xxx", ""+timestamp.getTime());
        assertThat(mapper.writeValueAsString(author), equalTo(expectedJson));
    }
}
