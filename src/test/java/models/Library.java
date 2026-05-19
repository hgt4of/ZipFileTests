package models;

import java.util.List;

public class Library {

    private List<Book> books;

    public Library() {}

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}