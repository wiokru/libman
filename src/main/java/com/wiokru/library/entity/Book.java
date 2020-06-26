package com.wiokru.library.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "book")
public class Book implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="title")
    private String title;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "book_author", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    private Set<Author> authors;

    @Column(name="publisher")
    private String publisher;

    @Column(name="published_date")
    private String publishedDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "book_bookcategory", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "bookcategory_id"))
    private Set<BookCategory> categories;

    @Column(name = "description", columnDefinition="text", length=10485760)
    @Type(type = "text")
    private String description;

    @Column(name="page_count")
    private Integer pageCount;

    @OneToOne(mappedBy = "book", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private Reserved reserved;

    public Book(String title, String publisher, String publishedDate, String description, Integer pageCount) {
        this.title = title;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.pageCount = pageCount;
    }

    public String listAuthors() {
        StringBuilder authorsList = new StringBuilder();

        for (Author author : authors) {
            authorsList.append(author.getName()).append(", ");
        }
        return  authorsList.toString();
    }

    public String listCategories() {
        StringBuilder catList = new StringBuilder();

        for (BookCategory category : categories) {
            catList.append(category.getName()).append(", ");
        }
        return  catList.toString();
    }
}
