package com.demo.ipc;
import com.demo.ipc.Book;

interface IBookService {
      List<Book> loadBooks();

      void finish();
}
