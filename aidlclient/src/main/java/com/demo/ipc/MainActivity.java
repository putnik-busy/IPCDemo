package com.demo.ipc;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mBookTextView;
    private BookServiceConnectionManager mConnectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        mConnectionManager = new BookServiceConnectionManager();
    }

    private void startLoadingBooks() {
        if (!mConnectionManager.isBound()) {
            attemptToBindService();
        }
        mBookTextView.setText(mConnectionManager.getLoadingLog());
    }

    private void stopLoadingBooks() {
        mConnectionManager.finishConnection();
    }

    private void attemptToBindService() {
        Intent serviceIntent = new Intent()
                .setComponent(new ComponentName("com.demo.aidlserver",
                        "com.demo.aidlserver.BookService"));
        bindService(serviceIntent, mConnectionManager.getBookServiceConnection(), BIND_AUTO_CREATE);
    }

    private void initViews() {
        mBookTextView = findViewById(R.id.book_text_view);
        Button startLoadBookButton = findViewById(R.id.start_load_book_button);
        Button stopLoadBookButton = findViewById(R.id.stop_load_book_button);
        startLoadBookButton.setOnClickListener(view -> startLoadingBooks());
        stopLoadBookButton.setOnClickListener(view -> stopLoadingBooks());
    }
}
