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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 * Testing of class {@link MoveId}.
 */
public class MovieIdTest {
    /** Test title. */
    private static final String TITLE_1 = "some title";
    /** Test original title. */
    private static final String ORIGINAL_TITLE_1 = "some original title";
    /** Test title 2. */
    private static final String TITLE_2 = "some title 2";
    /** Test original title 2. */
    private static final String ORIGINAL_TITLE_2 = "some original title 2";
    /** Test year. */
    private static final int YEAR_OF_PUBLICATION_1 = 1969;
    /** Test year 2. */
    private static final int YEAR_OF_PUBLICATION_2 = 1970;


    /**
     * Testing default constructor.
     */
    @Test
    public void testDefault() {
        final var entity = new MovieId();
        assertThat(entity.getTitle(), equalTo(null));
        assertThat(entity.getOriginalTitle(), equalTo(null));
        assertThat(entity.getYearOfPublication(), equalTo(0));
    }

    /**
     * Testing setter.
     */
    @Test
    public void testChangeData() {
        final var entity = createData();
        assertThat(entity.getTitle(), equalTo(TITLE_1));
        assertThat(entity.getOriginalTitle(), equalTo(ORIGINAL_TITLE_1));
        assertThat(entity.getYearOfPublication(), equalTo(YEAR_OF_PUBLICATION_1));
    }

    /**
     * Testing toString.
     */
    @Test
    public void testToString() {
        final var entity = createData();
        final var expected = "MovieId(title=some title"
                + ", originalTitle=some original title, "
                + "yearOfPublication=1969)";
        assertThat(entity.toString(), equalTo(expected));
    }
    
    /**
     * Testing {@link MovieId#equals(java.lang.Object)}.
     */
    @Test
    public void testEquals() {
        final var movieId1 = createData(TITLE_1, ORIGINAL_TITLE_1, YEAR_OF_PUBLICATION_1);
        final var movieId2 = createData(TITLE_1, ORIGINAL_TITLE_1, YEAR_OF_PUBLICATION_1);
        final var movieId3 = createData(TITLE_2, ORIGINAL_TITLE_2, YEAR_OF_PUBLICATION_2);
        assertThat(movieId1, equalTo(movieId2));
        assertThat(movieId1, not(equalTo(movieId3)));
        assertThat(movieId1, not(equalTo(1234)));
        assertThat(movieId1, not(equalTo(null)));
    }
    /**
     * Testing {@link MovieId#hashCode()}.
     */
    @Test
    public void testHasCode() {
        final var movieId1 = createData(TITLE_1, ORIGINAL_TITLE_1, YEAR_OF_PUBLICATION_1);
        final var movieId2 = createData(TITLE_1, ORIGINAL_TITLE_1, YEAR_OF_PUBLICATION_1);
        final var movieId3 = createData(TITLE_2, ORIGINAL_TITLE_2, YEAR_OF_PUBLICATION_2);
        assertThat(movieId1.hashCode(), equalTo(movieId2.hashCode()));
        assertThat(movieId1.hashCode(), not(equalTo(movieId3.hashCode())));
    }

    /**
     * Create test data.
     *
     * @return test data instance of type {@link MovieId}.
     */
    private MovieId createData() {
        return createData(TITLE_1, ORIGINAL_TITLE_1, YEAR_OF_PUBLICATION_1);
    }

    /**
     * Create test data.
     *
     * @param title the movie title in your language
     * @param originalTitle the original title
     * @param yearOfPublication the yeahr the movie has been published
     * @return test data instance of type {@link MovieId}.
     */
    private MovieId createData(final String title, final String originalTitle,
            final int yearOfPublication) {
        final var entity = new MovieId();
        entity.setTitle(title);
        entity.setOriginalTitle(originalTitle);
        entity.setYearOfPublication(yearOfPublication);
        return entity;
    }
}
