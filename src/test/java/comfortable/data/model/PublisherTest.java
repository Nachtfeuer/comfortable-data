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

import comfortable.data.tools.RandomDataProvider;
import java.io.IOException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Testing of class {@link Publisher}.
 */
public class PublisherTest {

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
     * Testing of default constructor.
     */
    @Test
    public void testDefault() {
        final Publisher publisher = new Publisher();
        assertThat(publisher.getFullName(), equalTo(null));
    }

    /**
     * Testing changing of values.
     */
    @Test
    public void testChange() {
        final var fullName = PublisherTest.provider.get(RandomDataProvider.BOOK_PUBLISHER);
        final Publisher publisher = new Publisher();
        publisher.setFullName(fullName);
        assertThat(publisher.getFullName(), equalTo(fullName));
    }
}
