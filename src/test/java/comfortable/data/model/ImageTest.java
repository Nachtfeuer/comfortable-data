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
import java.io.IOException;
import org.apache.tomcat.util.codec.binary.Base64;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import org.springframework.http.MediaType;

/**
 * Testing of class {@link Image}.
 */
public class ImageTest {

    /**
     * Testing {@link Image#toBase64()}.
     *
     * @throws IOException should never happen here (loadImage).
     */
    @Test
    public void testToBase64() throws IOException {
        final var image = loadImage();
        final var base64 = "image/jpeg;base64," + Base64.encodeBase64String(image.getImageData());
        assertThat(image.toBase64(), equalTo(base64));
    }

    /**
     * Testing serialization of {@link Image} instance to JSON.
     *
     * @throws JsonProcessingException should never happen here (loadImage).
     * @throws IOException should never happen here (ObjectMapper).
     */
    @Test
    public void testToJson() throws JsonProcessingException, IOException {
        final var image = loadImage();
        final var base64 = "image/jpeg;base64," + Base64.encodeBase64String(image.getImageData());
        final var mapper = new ObjectMapper();
        final var json = mapper.writeValueAsString(image);
        assertThat(json, equalTo("{\"data\":\"" + base64 + "\"}"));
    }

    /**
     * Testing deserializing from JSON.
     *
     * @throws IOException should never happen here (loadImage).
     */
    @Test
    public void testFromJson() throws IOException {
        final var oldImage = loadImage();
        final var mapper = new ObjectMapper();
        final var newImage = mapper.readValue(
                mapper.writeValueAsString(oldImage), Image.class);
        assertThat(newImage, equalTo(oldImage));
    }

    /**
     * Loads test image from resources.
     *
     * @return instance of {@link Image}.
     * @throws IOException should never happen here.
     */
    private Image loadImage() throws IOException {
        final var stream = getClass().getResourceAsStream("/book.jpg");
        return Image.builder()
                .imageData(stream.readAllBytes())
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .build();
    }
}
