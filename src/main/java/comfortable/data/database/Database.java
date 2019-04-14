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
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Stores data and doess queries on database.
 */
@Component
public class Database {
    /**
     * Injecting entity manager responsible for database.
     */
    @Autowired
    private transient EntityManager entityManager;

    /**
     * Create or update entity in database.
     *
     * @param <E> type of entity to persist.
     * @param entity the entity to create or update in database.
     * @return persistent entity.
     */
    @Transactional
    public <E> E persist(final E entity) {
        return this.entityManager.merge(entity);
    }

    /**
     * Querying the list of all movies.
     *
     * @return list of found movies.
     */
    @Transactional(readOnly = true)
    public List<Movie> queryMovies() {
        final var query = this.entityManager.createQuery(
                "SELECT m FROM Movie m", Movie.class);
        return query.getResultList();
    }

    /**
     * Querying the list of all performers.
     *
     * @return list of found performers.
     */
    @Transactional(readOnly = true)
    public List<Performer> queryPerformers() {
        final var query = this.entityManager.createQuery(
                "SELECT p FROM Performer p", Performer.class);
        return query.getResultList();
    }

    /**
     * Querying the list of all directors.
     *
     * @return list of found directors.
     */
    @Transactional(readOnly = true)
    public List<Director> queryDirectors() {
        final var query = this.entityManager.createQuery(
                "SELECT d FROM Director d", Director.class);
        return query.getResultList();
    }

    /**
     * Querying the list of all composers.
     *
     * @return list of found composers.
     */
    @Transactional(readOnly = true)
    public List<Composer> queryComposers() {
        final var query = this.entityManager.createQuery(
                "SELECT c FROM Composer c", Composer.class);
        return query.getResultList();
    }
}
