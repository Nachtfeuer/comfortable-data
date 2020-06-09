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
 * Testing enum {@link Priority}.
 */
public class PriorityTest {
    /**
     * Testing conversion from string to enum.
     */
    @Test
    public void testFromString() {
        assertThat(Priority.fromString("A"), equalTo(Priority.A));
        assertThat(Priority.fromString("B"), equalTo(Priority.B));
        assertThat(Priority.fromString("C"), equalTo(Priority.C));
        assertThat(Priority.fromString("D"), equalTo(Priority.D));
        assertThat(Priority.fromString("E"), equalTo(Priority.E));
        assertThat(Priority.fromString("F"), equalTo(Priority.F));
        assertThat(Priority.fromString(" "), equalTo(Priority.UNKNOWN));
    }
}
