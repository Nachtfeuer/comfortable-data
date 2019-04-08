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

import comfortable.data.model.Book;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author Thomas Lehmann
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TemplareEngineTest {

    /**
     * Template engine for rendering.
     */
    @Autowired
    private TemplateEngine templateEngine;

    /**
     * Testing the rendering of resources.
     *
     * @throws IOException should never happen
     */
    @Test
    public void testRenderBooks() throws IOException {
        final List<Book> books = new ArrayList<>();
        books.add(Book.builder().title("title1").build());
        books.add(Book.builder().title("title2").build());

        final var renderer = templateEngine.getRenderer("/books.html");
        final var content = renderer.add("books", books).render().trim();
        assertThat(content, startsWith("<!doctype html>"));
        assertThat(content, containsString("title1"));
        assertThat(content, containsString("title2"));
        assertThat(content, endsWith("</html>"));
    }
}
