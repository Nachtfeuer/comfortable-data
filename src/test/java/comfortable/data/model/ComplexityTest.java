/*
 * The MIT License
 *
 * Copyright 2020 Thomas Lehmann.
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 * Testing of enum {@link Complexity}.
 */
public class ComplexityTest {
    /**
     * Value for testing smalles complexity.
     */
    private static final String TEST_XS = "XS";

    /**
     * Value for testing small complexity.
     */
    private static final String TEST_S = "S";

    /**
     * Value for testing medium complexity.
     */
    private static final String TEST_M = "M";
    
    /**
     * Value for testing large complexity.
     */
    private static final String TEST_L = "L";

    /**
     * Value for testing very large complexity.
     */
    private static final String TEST_XL = "XL";

    /**
     * Testing {@link Complexity#fromString(java.lang.String)}.
     */
    @Test
    public void testFromString() {
        assertThat(Complexity.fromString(TEST_XS), equalTo(Complexity.EXTRA_SMALL));
        assertThat(Complexity.fromString(TEST_S), equalTo(Complexity.SMALL));
        assertThat(Complexity.fromString(TEST_M), equalTo(Complexity.MEDIUM));
        assertThat(Complexity.fromString(TEST_L), equalTo(Complexity.LARGE));
        assertThat(Complexity.fromString(TEST_XL), equalTo(Complexity.EXTRA_LARGE));
    }

    /**
     * Testing {@link Complexity#fromString(java.lang.String)} when value is unknown.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testFromStringFails() {
        Complexity.fromString("not known");
    }
    
    /**
     * Testing {@link Complexity#getValue()}.
     */
    @Test
    public void testGetComplexity() {
        assertThat(Complexity.EXTRA_SMALL.getValue(), equalTo(TEST_XS));
        assertThat(Complexity.SMALL.getValue(), equalTo(TEST_S));
        assertThat(Complexity.MEDIUM.getValue(), equalTo(TEST_M));
        assertThat(Complexity.LARGE.getValue(), equalTo(TEST_L));
        assertThat(Complexity.EXTRA_LARGE.getValue(), equalTo(TEST_XL));
    }
}
