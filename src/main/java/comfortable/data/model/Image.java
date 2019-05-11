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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import comfortable.data.tools.ImageDeserializer;
import comfortable.data.tools.ImageSerializer;
import comfortable.data.tools.SuppressFBWarnings;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

/**
 * Persistable image storing image data and content type
 * with intention to be embedded into objects like {@link Book}.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonSerialize(using = ImageSerializer.class)
@JsonDeserialize(using = ImageDeserializer.class)
@SuppressFBWarnings("EI_EXPOSE_REP")
@SuppressWarnings("PMD.BeanMembersShouldSerialize")
public class Image implements Serializable {

    /**
     * version of class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Represents binary data for the image.
     */
    @Lob
    private byte[] imageData;

    /**
     * Represents media type like.
     */
    private String contentType;

    /**
     * Provide image as base64 string.
     *
     * @return image as base64 string (usable fo HTML).
     */
    public String toBase64() {
        return this.contentType + ";base64," + Base64.encodeBase64String(this.imageData);
    }
}
