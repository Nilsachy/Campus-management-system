package nl.tudelft.oopp.cars.entities;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "announcement")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "posted")
    private Calendar posted;

    @Column(name = "relevant_until")
    private Calendar relevantUntil;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "user")
    private String user;

    /**
     * Constructor - creates instance of an Announcement entity.
     * @param posted - date announcement is posted
     * @param relevantUntil - date announcement is relevant until
     * @param title - title of the announcement
     * @param content - content of the announcement
     * @param user - user who posted the announcement
     */
    public Announcement(Calendar posted, Calendar relevantUntil, String title, String content,
                        String user) {
        this.posted = posted;
        this.relevantUntil = relevantUntil;
        this.title = title;
        this.content = content;
        this.user = user;
    }

}
