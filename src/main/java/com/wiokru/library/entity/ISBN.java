package com.wiokru.library.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "isbn")
public class ISBN implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "isbn10", unique = true)
    private String isbn10;

    @Column(name = "isbn13", unique = true)
    private String isbn13;
}
