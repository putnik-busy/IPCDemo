package com.demo.ipc;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView mBookTextView;
    private BookServiceConnection mBookServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        mBookServiceConnection = new BookServiceConnection();
        if (!mBookServiceConnection.isBound()) {
            attemptToBindService();
        }
    }

    private void startLoadingBooks() {
        if (!mBookServiceConnection.isBound()) {
            attemptToBindService();
        }
        mBookTextView.setText(mBookServiceConnection.getLoadingLog());
    }

    private void stopLoadingBooks() {
        mBookServiceConnection.finish();
    }

    private void asyncStartLoadingBooks() {
        startActivity(CallbackMainActivity.newIntent(this));
    }

    private void messengerStartLoadingBooks() {
        startActivity(MessengerActivity.newIntent(this));
    }

    private void attemptToBindService() {
        Intent serviceIntent = new Intent()
                .setComponent(new ComponentName("com.demo.aidlserver",
                        "com.demo.aidlserver.SyncBookService"));
        bindService(serviceIntent, mBookServiceConnection, BIND_AUTO_CREATE);
    }

    private void initViews() {
        mBookTextView = findViewById(R.id.book_text_view);
        Button startLoadBookButton = findViewById(R.id.start_load_book_button);
        Button stopLoadBookButton = findViewById(R.id.stop_load_book_button);
        Button asyncLoadBookButton = findViewById(R.id.async_load_book);
        Button messengerLoadBookButton = findViewById(R.id.messenger_load_book);
        asyncLoadBookButton.setOnClickListener(view -> asyncStartLoadingBooks());
        messengerLoadBookButton.setOnClickListener(view -> messengerStartLoadingBooks());
        startLoadBookButton.setOnClickListener(view -> startLoadingBooks());
        stopLoadBookButton.setOnClickListener(view -> stopLoadingBooks());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBookServiceConnection.isBound()) {
            unbindService(mBookServiceConnection);
            mBookServiceConnection.setBound(false);
        }
    }

    private class BookServiceConnection implements ServiceConnection {

        private final StringBuilder mBuilder;
        private SyncBookService mBookService;
        private boolean mBound;

        BookServiceConnection() {
            mBuilder = new StringBuilder();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBound = true;
            mBuilder.append("Service binded!\n");
            mBookService = SyncBookService.Stub.asInterface(service);
            loadBooks();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBuilder.append("Service disconnected.\n");
            mBookService = null;
            mBound = false;
        }

        public void setBound(boolean bound) {
            mBound = bound;
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

        private boolean isBound() {
            return mBound;
        }
    }

}
