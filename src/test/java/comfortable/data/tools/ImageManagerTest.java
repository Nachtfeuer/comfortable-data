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

import java.net.URISyntaxException;
import java.nio.file.Path;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 * Testing class {@link ImageManager}.
 */
public class ImageManagerTest {

    /**
     * Testing of {@link ImageManager#getAllImages(java.lang.String) }.
     *
     * @throws java.net.URISyntaxException if URI syntax is wrong (should never happen here).
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void testGetAllImages() throws URISyntaxException {
        final var rootPath = Path.of(getClass().getResource("/images").toURI());
        final var manager = new ImageManager();
        final var images = manager.getAllImages(rootPath);
        assertThat(images.size(), equalTo(5));
    }

    /**
     * Testing of {@link ImageManager#getMetaData(java.nio.file.Path) }.
     *
     * @throws URISyntaxException if URI syntax is wrong (should never happen here).
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void testGetMetaData() throws URISyntaxException {
        final var imagePath = Path.of(getClass().getResource("/images/image3.JPG").toURI());
        final var manager = new ImageManager();
        final var metaData = manager.getMetaData(imagePath);

        assertThat(metaData.get("Windows XP Title"), equalTo("Spaziergang in Schönbusch"));
        assertThat(metaData.get("Windows XP Subject"), equalTo("Schönbusch"));
        assertThat(metaData.get("Windows XP Comment"),
                equalTo("Ein schöner Spaziergang im Park um den See"));
        assertThat(metaData.get("Artist"), equalTo("Thomas Lehmann"));
        assertThat(metaData.get("Exif Image Width"), equalTo("4608 pixels"));
        assertThat(metaData.get("Exif Image Height"), equalTo("3456 pixels"));
        assertThat(metaData.get("File Size"), equalTo("8185206 bytes"));
        assertThat(metaData.get("Rating"), equalTo("5"));
        assertThat(metaData.get("Make"), equalTo("NIKON"));
        assertThat(metaData.get("Model"), equalTo("COOLPIX S6800"));
        assertThat(metaData.get("Date/Time"), equalTo("2018:10:20 15:54:41"));
        assertThat(metaData.get("Detected File Type Name"), equalTo("JPEG"));
        assertThat(metaData.get("Detected MIME Type"), equalTo("image/jpeg"));
    }
}
