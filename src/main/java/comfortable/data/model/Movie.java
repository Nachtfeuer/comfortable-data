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

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

/**
 * Movie representing a Dvd or Blu-ray.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(MovieId.class)
public class Movie implements Serializable {
    /** version of class. */
    private static final long serialVersionUID = 1L;

    /** the title in your language. */
    @Id
    private String title;

    /** the original title. */
    @Id
    private String originalTitle;

    /** list of performers and their roles. */
    @ElementCollection
    @Singular
    private List<Role> roles;

    /** the year when this movie has been published. */
    @Id
    private int yearOfPublication;
    
    /** aspect ratio of movie. */
    private String aspectRatio;

    
    /** list of movie directors. */
    @ManyToMany(cascade = CascadeType.MERGE)
    @Singular
    private List<Director> directors;
    
    /** list of movie composers. */
    @ManyToMany(cascade = CascadeType.MERGE)
    @Singular
    private List<Composer> composers;
    
    /** the runtime of the movie in minutes. */
    private int runtime;
    
    /** all tags of the book. */
    @ElementCollection
    @Singular
    private List<Tag> tags;
}
