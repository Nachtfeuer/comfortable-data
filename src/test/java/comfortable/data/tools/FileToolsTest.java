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
package comfortable.data.tools;

import org.apache.tomcat.util.codec.binary.Base64;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 * Testing of class {@link FileTools}.
 */
public class FileToolsTest {
    /**
     * Testing of{@link FileTools#readResource(java.lang.String)}.
     */
    @Test
    public void testReadResourceFileExists() {
        final var content = FileTools.readResource("/book.xml");
        assertThat(content, startsWith("<book>"));
    }

    /**
     * Testing of{@link FileTools#readResource(java.lang.String)}.
     */
    @Test
    public void testReadResourceFileDoesNotExist() {
        assertThat(FileTools.readResource("/unknown.xml"), equalTo(null));
    }

    /**
     * Testing encode file content as base64 and back.
     */
    @Test
    public void testEncodeFileAsBase64() {
        final var path = getClass().getResource("/simple.txt").getFile();
        final var base64 = FileTools.encodeAsBase64(path);
        assertThat(new String(Base64.decodeBase64(base64)), equalTo("hello world!\n"));
    }
}
