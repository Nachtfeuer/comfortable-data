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

import comfortable.data.model.Author;
import java.util.UUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testing persistence of author.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthorPersistenceTest {
    
    /**
     * Retries in tests doing the same.
     */
    private final static int RETRIES = 10;

    /**
     * Repository to use for saving/persisting authors.
     */
    @Autowired
    private BookAuthorRepository authors;

    /**
     * Testing persisting a new author.
     */
    @Test
    public void testSave() {
        // random "author" name
        final var uuid = UUID.randomUUID();
        // create author instance with random name (don't care about creation date and time)
        final var initialAuthor = Author.builder().fullName(uuid.toString()).build();
        // persist author expecting that creation date and time will be set
        this.authors.save(initialAuthor);

        final var persistentAuthor = this.authors.findById(uuid.toString());
        assertThat(initialAuthor.getFullName(), equalTo(persistentAuthor.get().getFullName()));
        assertThat(persistentAuthor.get().getCreated(), not(equalTo(null)));
    }
}
