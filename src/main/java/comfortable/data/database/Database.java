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
import comfortable.data.model.Book;
import comfortable.data.model.Movie;
import comfortable.data.model.Publisher;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import org.springframework.stereotype.Component;

/**
 * Stores data and doess queries on database.
 */
@Component
public class Database {

    /**
     * todo.
     */
    private final transient EntityManager entityManager;

    /**
     * Initialize persistence layer.
     */
    public Database() {
        final var entityManagerFactory = Persistence.createEntityManagerFactory("database.odb");
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    /**
     * Create or update entity in database.
     *
     * @param <E> type of entity to persist.
     * @param entity the entity to create or update in database.
     * @return persistent entity.
     */
    public <E> E persist(final E entity) {
        this.entityManager.getTransaction().begin();
        final var result = this.entityManager.merge(entity);
        this.entityManager.getTransaction().commit();
        return result;
    }

    /**
     * Querying the list of all authors.
     *
     * @return list of found authors.
     */
    public List<Author> queryAuthors() {
        final var query = this.entityManager.createQuery(
                "SELECT a FROM Author a", Author.class);
        return query.getResultList();
    }

    /**
     * Querying the list of all books.
     *
     * @return list of found books.
     */
    public List<Book> queryBooks() {
        final var query = this.entityManager.createQuery(
                "SELECT b FROM Book b", Book.class);
        return query.getResultList();
    }

    /**
     * Querying the list of all publishers.
     *
     * @return list of found publishers.
     */
    public List<Publisher> queryPublishers() {
        final var query = this.entityManager.createQuery(
                "SELECT p FROM Publisher p", Publisher.class);
        return query.getResultList();
    }

    /**
     * Querying the list of all movies.
     *
     * @return list of found movies.
     */
    public List<Movie> queryMovies() {
        final var query = this.entityManager.createQuery(
                "SELECT m FROM Movie m", Movie.class);
        return query.getResultList();
    }
}
