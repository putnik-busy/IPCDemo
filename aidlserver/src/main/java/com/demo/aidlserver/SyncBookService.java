package com.demo.aidlserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.demo.ipc.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Rodionov
 */
public class SyncBookService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BookServiceStub();
    }

    private class BookServiceStub extends com.demo.ipc.SyncBookService.Stub {

        @Override
        public List<Book> loadBooks() throws RemoteException {
            return generateBooks();
        }

        @Override
        public void finish() throws RemoteException {
            stopSelf();
        }

        private List<Book> generateBooks() {
            List<Book> books = new ArrayList<>();
            books.add(fillDataBook("Пушкин А.С.", 5.0f));
            books.add(fillDataBook("Тютчев Ф.И.", 4.9f));
            books.add(fillDataBook("Достоевский Ф.М.", 4.8f));
            return books;
        }

        private Book fillDataBook(String author, float rate) {
            Book book = new Book();
            book.setAuthor(author);
            book.setRates(rate);
            return book;
        }
    }
}
