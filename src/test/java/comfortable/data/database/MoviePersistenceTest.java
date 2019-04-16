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
package comfortable.data.database;

import comfortable.data.model.Composer;
import comfortable.data.model.Director;
import comfortable.data.model.Movie;
import comfortable.data.model.Performer;
import comfortable.data.model.Role;
import comfortable.data.model.Tag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testing Movie persistence.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MoviePersistenceTest {

    /**
     * Injected Database for storing data.
     */
    @Autowired
    private MovieRepository repository;
    
    /**
     * Testing the storing of a movie.
     */
    @Test
    public void testPersistingMovie() {
        this.repository.save(this.createMovie());
    }

    /**
     * Creating a movie test instance.
     *
     * @return test movie instance.
     */
    private Movie createMovie() {
        return Movie.builder()
                .title("another movie title")
                .originalTitle("the original movie title")
                .aspectRatio("16:9")
                .yearOfPublication(1969)
                .role(Role.builder()
                        .performer(new Performer("First Performer"))
                        .roleName("Mr. X").build())
                .role(Role.builder()
                        .performer(new Performer("Second Performer"))
                        .roleName("Mr. Y").build())
                .composer(new Composer("First Composer"))
                .composer(new Composer("SecondComposer"))
                .director(new Director("First Director"))
                .director(new Director("Second Director"))
                .tag(new Tag("science-fiction"))
                .tag(new Tag("utopie"))
                .build();
    }

}
