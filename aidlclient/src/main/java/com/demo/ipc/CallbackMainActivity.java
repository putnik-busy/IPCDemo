package com.demo.ipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class CallbackMainActivity extends AppCompatActivity {

    private TextView mBookTextView;
    private ProgressBar mContentProgressBar;
    private BookServiceConnection mBookServiceConnection;

    @NonNull
    public static Intent newIntent(Context context) {
        return new Intent(context, CallbackMainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callback_activity_main);
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
        mContentProgressBar.setVisibility(View.VISIBLE);
        mBookServiceConnection.loadBooks();
    }

    private void stopLoadingBooks() {
        mBookServiceConnection.finish();
    }

    private void attemptToBindService() {
        Intent serviceIntent = new Intent()
                .setComponent(new ComponentName("com.demo.aidlserver",
                        "com.demo.aidlserver.AsyncBookService"));
        bindService(serviceIntent, mBookServiceConnection, BIND_AUTO_CREATE);
    }

    private void initViews() {
        mBookTextView = findViewById(R.id.book_text_view);
        mContentProgressBar = findViewById(R.id.content_progress_bar);
        Button startLoadBookButton = findViewById(R.id.start_load_book_button);
        Button stopLoadBookButton = findViewById(R.id.stop_load_book_button);
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

        private AsyncBookService mBookService;
        private boolean mBound;

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mBound = true;
            mBookTextView.append("Service binded!\n");
            mBookService = AsyncBookService.Stub.asInterface(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBookTextView.append("Service disconnected.\n");
            mBookService = null;
            mBound = false;
        }

        void setBound(boolean bound) {
            mBound = bound;
        }

        void loadBooks() {
            mBookTextView.append("Download list book...\n");
            try {
                mBookService.loadBooks(new AsyncBookCallback(this));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        private void finish() {
            try {
                mContentProgressBar.setVisibility(View.GONE);
                mBookService.finish();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        private boolean isBound() {
            return mBound;
        }
    }

    private class AsyncBookCallback extends AsyncCallback.Stub {
        private final BookServiceConnection mServiceConnection;

        AsyncBookCallback(BookServiceConnection connection) {
            mServiceConnection = connection;
        }

        @Override
        public void handleResult(List<Book> books) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StringBuilder builder = new StringBuilder();
                    builder.append("result download: ").append(books.size()).append(" books \n");

                    for (int i = 0; i < books.size(); i++) {
                        Book book = books.get(i);
                        builder.append(formatBookInfo(book.getAuthor(), book.getRates()));
                    }

                    mBookTextView.append(builder.toString());
                    mServiceConnection.finish();
                }
            });

        }

        private String formatBookInfo(String author, float rates) {
            return String.format(Locale.getDefault(), "%s: %f %s", author, rates, "\n");
        }
    }

}
