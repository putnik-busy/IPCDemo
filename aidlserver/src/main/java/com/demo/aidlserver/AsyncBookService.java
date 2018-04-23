package com.demo.aidlserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.demo.ipc.AsyncCallback;
import com.demo.ipc.Book;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Rodionov
 */
public class AsyncBookService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BookServiceStub(this);
    }

    private static class BookServiceStub extends com.demo.ipc.AsyncBookService.Stub {

        private final WeakReference<AsyncBookService> mBookService;
        private final List<Book> mBooks;

        private BookServiceStub(AsyncBookService bookService) {
            mBookService = new WeakReference<>(bookService);
            mBooks = new ArrayList<>();
        }

        @Override
        public void loadBooks(AsyncCallback callback) throws RemoteException {
            SystemClock.sleep(5000);
            callback.handleResult(generateBooks());
        }

        @Override
        public void finish() throws RemoteException {
            AsyncBookService bookService = mBookService.get();
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
