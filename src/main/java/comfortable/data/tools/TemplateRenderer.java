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

import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Render that allows to define context data and then executing the render process.
 */
public class TemplateRenderer {

    /**
     * context data to be used in the template by names.
     */
    private final Map<String, Object> context;

    /**
     * the loaded template.
     */
    private final PebbleTemplate template;

    /**
     * Initializing render with loaded template.
     *
     * @param initTemplate loaded template.
     */
    TemplateRenderer(final PebbleTemplate initTemplate) {
        this.context = new HashMap<>();
        this.template = initTemplate;
    }

    /**
     * Extend context with a name and an object.
     *
     * @param name any name
     * @param value data assigned to the name
     * @return builder (renderer) itself to allow method chaining.
     */
    public TemplateRenderer add(final String name, final Object value) {
        this.context.put(name, value);
        return this;
    }

    /**
     * Rendering the final String.
     *
     * @return rendered string
     * @throws IOException when the write operation has failed (StringWriter).
     */
    public String render() throws IOException {
        final var writer = new StringWriter();
        this.template.evaluate(writer, this.context);
        return writer.toString();
    }
}
