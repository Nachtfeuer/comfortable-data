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

import comfortable.data.model.AuthorCount;
import comfortable.data.model.Book;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD Access to author data.
 */
@Transactional
public interface BookRepository extends JpaRepository<Book, String>,
        JpaSpecificationExecutor<Book> {

    @Transactional(readOnly = true)
    @Override
    List<Book> findAll();

    @Transactional(readOnly = true)
    @Override
    List<Book> findAll(Specification<Book> spec);

    @Override
    void deleteAll();

    /**
     * Query authors on how many books they wrote.
     *
     * @return list of authors with count of books they wrote.
     */
    @Query("SELECT"
            + " new comfortable.data.model.AuthorCount(a.fullName, count(b))"
            + " FROM Book b JOIN b.authors a GROUP BY a.fullName")
    List<AuthorCount> queryAuthorCount();
}
