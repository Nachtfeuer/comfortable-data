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

import com.mitchellbosecke.pebble.PebbleEngine;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Define component being a injectable template engine.
 */
@Component
public final class TemplateEngine {
    /** Logger for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngine.class);

    /**
     * Template engine from https://pebbletemplates.io/.
     */
    private PebbleEngine engine;
    
    /**
     * Initialize engine when service has started.
     */
    @PostConstruct
    public void initEngine() {
        LOGGER.info("Creating pepple engine instance ...");
        this.engine = new PebbleEngine.Builder().build();
    }

    /**
     * Provide the renderer for a concrete template file.
     *
     * @param pathAndFileName path and filename of the resource.
     * @return renderer
     */
    public TemplateRenderer getRenderer(final String pathAndFileName) {
        final var resource = getClass().getResource(pathAndFileName).getFile();
        return new TemplateRenderer(this.engine.getTemplate(resource));
    }
}
