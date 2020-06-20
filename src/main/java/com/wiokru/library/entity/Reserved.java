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
@Table(name = "reserved")
public class Reserved implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="book_id")
    private Book book;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="dated")
    private Timestamp dated;

    @Column(name="due_date")
    private LocalDate dueDate;

    public Reserved(Book book, User user) {
        this.book = book;
        this.user = user;
        this.dated = new Timestamp(new Date().getTime());
        this.dueDate = LocalDate.now().plusDays(Const.RESERVED_VALID_DAYS);
    }

    public String displayDueDate() {
        return this.dueDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
