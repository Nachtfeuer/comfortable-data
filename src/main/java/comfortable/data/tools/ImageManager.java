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

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import static java.util.stream.Collectors.toList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Managing images.
 */
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
@SuppressFBWarnings({
        "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE",
        "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"})
public class ImageManager {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageManager.class);

    /**
     * Get all images.
     *
     * @param rootPath root path to start search.
     * @return list of images (path and filenames).
     */
    public List<String> getAllImages(final Path rootPath) {
        final List<String> images = new ArrayList<>();

        try (var stream = Files.find(rootPath, 1, (path, basicFileAttributes) -> {
            final var file = path.toFile();
            return file.getName().toLowerCase(Locale.ROOT).endsWith(".jpg");
        })) {
            final var entries = stream
                    .map(entry -> entry.getFileName().toString()).collect(toList());

            images.addAll(entries);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return images;
    }

    /**
     * Get all meta data about an image.
     *
     * @param imagePath path and filename of an image.
     * @return key/value representing all information about the image.
     */
    public Map<String, String> getMetaData(final Path imagePath) {
        final var simplifiedMetaData = new TreeMap<String, String>();
        try {
            final var metadata = ImageMetadataReader.readMetadata(imagePath.toFile());
            for (final var directory : metadata.getDirectories()) {
                directory.getTags().forEach((tag) -> {
                    simplifiedMetaData.put(tag.getTagName(), tag.getDescription());
                });
            }
        } catch (ImageProcessingException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return simplifiedMetaData;
    }
}
