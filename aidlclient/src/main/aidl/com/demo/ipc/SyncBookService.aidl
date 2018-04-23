package com.demo.ipc;
import com.demo.ipc.Book;

interface SyncBookService {
      List<Book> loadBooks();

      void finish();
}
