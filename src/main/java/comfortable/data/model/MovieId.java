/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comfortable.data.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

/**
 * Composite id of the movie.
 */
@Entity
@Data
public class MovieId implements Serializable {
    /** version of class. */
    private static final long serialVersionUID = 1L;

    /** the title in your language. */
    @Id
    private String title;

    /** the original title. */
    @Id
    private String originalTitle;    

    /** the year when this movie has been published. */
    @Id
    private int yearOfPublication;
}
