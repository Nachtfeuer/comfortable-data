/*
 * The MIT License
 *
 * Copyright 2020 Thomas Lehmann.
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

import comfortable.data.tools.DateAndTime;
import comfortable.data.tools.SuppressFBWarnings;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

/**
 * Todo data class.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressFBWarnings("EI_EXPOSE_REP")
public class Todo implements Serializable {
    /**
     * version of class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Unique Id for a todo.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Date and time when the todo has been created.
     */
    @Column(updatable = false)
    private DateAndTime created;

    /**
     * Date and time when the todo has been changed.
     */
    private DateAndTime changed;
    
    /**
     * Short text (title) what to do.
     */
    private String title;

    /**
     * Longer text with additional explanations (optional).
     */
    private String description;
    
    /**
     * When true when todo is done.
     */
    private boolean completed;
    
    /**
     * Priority of the todo.
     */
    private Priority priority;

    /**
     * List of tags (basically strings).
     */
    @ElementCollection
    @Singular
    private List<Tag> tags;

    /**
     * List of projects (basically strings).
     */
    @ElementCollection
    @Singular
    private List<Project> projects;

    /**
     * Called before persisting the todo.
     */
    @PrePersist
    @SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
    @SuppressWarnings("PMD.UnusedPrivateMethod") // it is used!
    private void onPrePersist() {
        this.created = DateAndTime.now();
        this.changed = this.created;
    }
}
