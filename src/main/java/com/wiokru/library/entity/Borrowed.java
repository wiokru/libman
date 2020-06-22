package com.wiokru.library.entity;

import com.wiokru.library.utils.Const;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "borrowed")
public class Borrowed implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "dated")
    private Timestamp dated;

    @Column(name = "due_due")
    private LocalDate dueDate;

    public Borrowed(Book book, User user) {
        this.book = book;
        this.user = user;
        this.dated = new Timestamp(new Date().getTime());
        this.dueDate = LocalDate.now().plusDays(Const.BORROWED_VALID_DAYS);
    }

    public String displayDueDate() {
        return this.dueDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

}