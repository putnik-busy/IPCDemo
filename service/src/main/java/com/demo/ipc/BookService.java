package com.demo.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Rodionov
 */
public class BookService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BookServiceStub(this);
    }

    private static class BookServiceStub extends IBookService.Stub {

        private final WeakReference<BookService> mBookService;
        private final List<Book> mBooks;

        private BookServiceStub(BookService bookService) {
            mBookService = new WeakReference<>(bookService);
            mBooks = new ArrayList<>();
        }

        @Override
        public List<Book> loadBooks() throws RemoteException {
            SystemClock.sleep(5000);
            return generateBooks();
        }

        @Override
        public void finish() throws RemoteException {
            BookService bookService = mBookService.get();
            if (bookService != null) {
                bookService.stopSelf();
            }
        }

        private List<Book> generateBooks() {
            mBooks.add(createBook("Пушкин А.С.", 5.0f));
            mBooks.add(createBook("Тютчев Ф.И.", 4.9f));
            mBooks.add(createBook("Достоевский Ф.М.", 4.8f));
            return mBooks;
        }

        private Book createBook(String author, float rate) {
            Book book = new Book();
            book.setAuthor(author);
            book.setRates(rate);
            return book;
        }
    }
}
