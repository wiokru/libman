package com.wiokru.library.utils;

import com.wiokru.library.entity.Book;
import com.wiokru.library.repositories.BookRepository;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BookUtils {

    public static List<Book> filterBooks(String[] searchedWords, BookRepository bookRepository){
        List<Book> bookList;

        bookList = bookRepository.findAll()
                .stream()
                .filter(book ->
                        StringUtils.containsAny(book.getTitle().toLowerCase(), searchedWords)
                                && StringUtils.containsAny(book.listAuthors().toLowerCase(), searchedWords))
                .sorted(Comparator.comparing(Book::getTitle))
                .collect(Collectors.toList());

        if (bookList.isEmpty()){
            bookList = bookRepository.findAll()
                    .stream()
                    .filter(book ->
                            StringUtils.containsAny(book.getTitle().toLowerCase(), searchedWords)
                                    || StringUtils.containsAny(book.listAuthors().toLowerCase(), searchedWords)
                                    || StringUtils.containsAny(book.getPublisher().toLowerCase(), searchedWords))
                    .sorted(Comparator.comparing(Book::getTitle))
                    .collect(Collectors.toList());
        }

        return bookList;
    }
}
