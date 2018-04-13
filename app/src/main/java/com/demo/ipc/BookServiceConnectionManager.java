package com.demo.ipc;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Locale;

/**
 * @author Sergey Rodionov
 */
public class BookServiceConnectionManager {

    private final BookServiceConnection mBookServiceConnection;

    public BookServiceConnectionManager() {
        mBookServiceConnection = new BookServiceConnection();
    }

    public String getLoadingLog() {
        return mBookServiceConnection.getLoadingLog();
    }

    public BookServiceConnection getBookServiceConnection() {
        return mBookServiceConnection;
    }

    public void finishConnection() {
        mBookServiceConnection.finish();
    }

    private static class BookServiceConnection implements ServiceConnection {

        private final StringBuilder mBuilder;
        private IBookService mBookService;

        BookServiceConnection() {
            mBuilder = new StringBuilder();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mBuilder.append("Service binded!\n");
            mBookService = IBookService.Stub.asInterface(service);
            loadBooks();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBuilder.append("Service disconnected.\n");
            mBookService = null;
        }

        private void loadBooks() {
            mBuilder.append("Download list book...\n");
            List<Book> books;
            try {
                books = mBookService.loadBooks();

                mBuilder.append("result download: ").append(books.size()).append(" books \n");

                for (int i = 0; i < books.size(); i++) {
                    Book book = books.get(i);
                    mBuilder.append(formatBookInfo(book.getAuthor(), book.getRates()));
                }

                mBookService.finish();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        private String formatBookInfo(String author, float rates) {
            return String.format(Locale.getDefault(), "%s: %f %s", author, rates, "\n");
        }

        @NonNull
        private String getLoadingLog() {
            return mBuilder.toString();
        }

        private void finish() {
            try {
                mBookService.finish();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

}
