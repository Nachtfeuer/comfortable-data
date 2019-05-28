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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import static java.util.stream.Collectors.toList;

/**
 * Providing random test data.
 */
public final class RandomDataProvider {

    /**
     * Group "author" in data file.
     */
    public static final String AUTHOR = "author";

    /**
     * Group "publisher" in data file.
     */
    public static final String PUBLISHER = "publisher";

    /**
     * Group "rating" in data file.
     */
    public static final String RATING = "rating";

    /**
     * Group "pages" in data file.
     */
    public static final String PAGES = "pages";

    /**
     * Group "year" in data file.
     */
    public static final String YEAR = "year";

    /**
     * group values like authors, publishers, directory, and so on.
     */
    private final transient Map<String, List<String>> dataMap = new HashMap<>();

    /**
     * Randomizer instance.
     */
    private final transient Random random = new Random();

    /**
     * Adding for a given key (group) a new value.
     *
     * @param key key (group) name.
     * @param value value to add.
     */
    public void add(final String key, final String value) {
        final var values = this.dataMap.get(key);
        if (values == null) {
            this.dataMap.put(key, List.of(value).stream().collect(toList()));
        } else {
            values.add(value);
        }
    }

    /**
     * Provide a set of random values for a given group (key).
     *
     * @param key name of the group
     * @param count number of random values out of this group
     * @return set of random values.
     */
    public Set<String> get(final String key, final int count) {
        final var existingValues = this.dataMap.get(key);
        final var randomValues = new HashSet<String>();
        if (existingValues != null && count <= existingValues.size()) {
            while (randomValues.size() < count) {
                // provides index in range 0..(size-1)
                final var index = this.random.nextInt(existingValues.size());
                randomValues.add(existingValues.get(index));
            }
        }
        return randomValues;
    }
    /**
     * Provide a on value for a given group (key).
     *
     * @param key name of the group
     * @return set of random values.
     */
    public String get(final String key) {
        final var existingValues = this.dataMap.get(key);
        var value = "";
        if (existingValues != null && !existingValues.isEmpty()) {
            // provides index in range 0..(size-1)
            final var index = this.random.nextInt(existingValues.size());
            value = existingValues.get(index);
        }
        return value;
    }

    /**
     * Create a data provider for a concrete data file.
     *
     * @param path path and name of data file.
     * @return data provider instance.
     * @throws IOException when the JSON reader has failed.
     */
    public static RandomDataProvider of(final String path) throws IOException {
        final var stream = RandomDataProvider.class.getResourceAsStream(path);
        final var content = new String(stream.readAllBytes(), "utf-8");
        stream.close();

        final var provider = new RandomDataProvider();
        final var mapper = new ObjectMapper();
        final var document = mapper.readTree(content);

        document.fieldNames().forEachRemaining(name -> {
            document.get(name).forEach(node -> {
                provider.add(name, node.asText());
            });
        });

        return provider;
    }
}
