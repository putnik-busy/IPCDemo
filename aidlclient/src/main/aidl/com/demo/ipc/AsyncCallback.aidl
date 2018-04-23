package com.demo.ipc;
import com.demo.ipc.Book;

interface AsyncCallback {
    void handleResult(inout List<Book> book);
}
