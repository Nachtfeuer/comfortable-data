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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import comfortable.data.model.Image;
import java.io.IOException;
import org.apache.tomcat.util.codec.binary.Base64;

/**
 * Deserilization a JSON string containing content type and base64 encoded image data back to an
 * instance of type {@link Image}.
 */
public class ImageDeserializer extends JsonDeserializer<Image> {

    /**
     * key inside base64 string containing content type and data.
     */
    private static final String BASE64 = "base64,";

    @Override
    public Image deserialize(final JsonParser parser, final DeserializationContext context)
            throws IOException, JsonProcessingException {
        final var node = parser.getCodec().readTree(parser);
        final var data = ((JsonNode) node.get("data")).asText();

        final var pos = data.indexOf(BASE64);
        if (pos < 0) {
            throw new IOException("Invalid base64 image data");
        }

        final var imageDataPos = pos + BASE64.length();
        return Image.builder()
                .imageData(Base64.decodeBase64(data.substring(imageDataPos, data.length())))
                .contentType(data.substring(0, pos - 1)) // one less because of the semicolon
                .build();
    }
}
