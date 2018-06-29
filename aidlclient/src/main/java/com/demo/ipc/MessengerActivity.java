package com.demo.ipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

/**
 * @author Sergey Rodionov
 */
public class MessengerActivity extends AppCompatActivity {

    private static final int MESSAGE_FLAG = 1;
    private static final String MESSAGE_KEY = "message";

    private TextView mBookTextView;
    private ProgressBar mContentProgressBar;
    private BookServiceConnection mBookServiceConnection;
    private Messenger mClientMessenger;

    @NonNull
    public static Intent newIntent(Context context) {
        return new Intent(context, MessengerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callback_activity_main);
        initViews();
        mClientMessenger = new Messenger(new ClientHandler(mBookTextView));
        mBookServiceConnection = new BookServiceConnection();
        bindService();
    }

    @Override
    protected void onDestroy() {
        if (mBookServiceConnection.isBound()) {
            unbindService(mBookServiceConnection);
        }
        super.onDestroy();
    }

    private void bindService() {
        if (!mBookServiceConnection.isBound()) {
            Intent serviceIntent = new Intent()
                    .setComponent(new ComponentName("com.demo.aidlserver",
                            "com.demo.aidlserver.MessengerBookService"))
                    .setAction("com.demo.message");
            bindService(serviceIntent, mBookServiceConnection, BIND_AUTO_CREATE);
        }
    }

    private void initViews() {
        mBookTextView = findViewById(R.id.book_text_view);
        mContentProgressBar = findViewById(R.id.content_progress_bar);
        Button startLoadBookButton = findViewById(R.id.start_load_book_button);
        startLoadBookButton.setOnClickListener(view -> sendMessage());
    }

    private void sendMessage() {
        Message client = Message.obtain(null, MESSAGE_FLAG);
        client.replyTo = mClientMessenger;
        if (mBookServiceConnection.isBound()) {
            try {
                mBookServiceConnection.getBookServiceMessenger().send(client);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ClientHandler extends Handler {

        private final WeakReference<TextView> mTextView;

        private ClientHandler(TextView textView) {
            mTextView = new WeakReference<>(textView);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_FLAG:
                    TextView textView = mTextView.get();
                    if (textView != null) {
                        Bundle data = msg.getData();
                        data.setClassLoader(Book.class.getClassLoader());
                        ArrayList<Book> books = data.getParcelableArrayList(MESSAGE_KEY);
                        StringBuilder builder = new StringBuilder();
                        builder.append("result download: ").append(books.size()).append(" books \n");

                        for (int i = 0; i < books.size(); i++) {
                            Book book = books.get(i);
                            builder.append(formatBookInfo(book.getAuthor(), book.getRates()));
                        }

                        textView.append(builder.toString());
                    }
                    break;
            }
            super.handleMessage(msg);
        }

        private String formatBookInfo(String author, float rates) {
            return String.format(Locale.getDefault(), "%s: %f %s", author, rates, "\n");
        }
    }

    private final class BookServiceConnection implements ServiceConnection {

        private Messenger mBookServiceMessenger;
        private boolean mBound;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBound = true;
            mBookServiceMessenger = new Messenger(service);
            Toast.makeText(MessengerActivity.this, R.string.message_connected, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBookServiceMessenger = null;
            mBound = false;
            Toast.makeText(MessengerActivity.this, R.string.message_connected_lose, Toast.LENGTH_SHORT).show();
        }

        private boolean isBound() {
            return mBound;
        }

        Messenger getBookServiceMessenger() {
            return mBookServiceMessenger;
        }
    }

}
