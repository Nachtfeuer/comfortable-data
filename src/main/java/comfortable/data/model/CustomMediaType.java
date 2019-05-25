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

import org.springframework.http.MediaType;

/**
 * Own media type class to handle also media types that are not defined as constants like yaml.
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public final class CustomMediaType {

    /**
     * Value for Media Type for YAML.
     */
    public static final String APPLICATION_YAML_VALUE = "application/x-yaml";

    /**
     * Value for Media Type for XML.
     */
    public static final String APPLICATION_XML_VALUE = MediaType.APPLICATION_XML_VALUE;

    /**
     * Value of Media Type for JSON.
     */
    public static final String APPLICATION_JSON_VALUE = MediaType.APPLICATION_JSON_VALUE;

    /**
     * Value for Media Type for MsgPack.
     */
    public static final String APPLICATION_MSGPACK_VALUE = "application/x-msgpack";

    /**
     * Media type for JSON.
     */
    public static final MediaType APPLICATION_JSON = MediaType.APPLICATION_JSON;

    /**
     * Media type for XML.
     */
    public static final MediaType APPLICATION_XML = MediaType.APPLICATION_XML;

    /**
     * Media type for YAML.
     */
    public static final MediaType APPLICATION_YAML = MediaType.valueOf(APPLICATION_YAML_VALUE);

    /**
     * Media type for HTML.
     */
    public static final MediaType TEXT_HTML = MediaType.TEXT_HTML;

    /**
     * Value for Media type HTML.
     */
    public static final String TEXT_HTML_VALUE = MediaType.TEXT_HTML_VALUE;

    /**
     * Media type for MsgPack.
     */
    public static final MediaType APPLICATION_MSGPACK
            = MediaType.valueOf(APPLICATION_MSGPACK_VALUE);

    /**
     * tool should not be instantiated.
     */
    private CustomMediaType() {
    }
}
