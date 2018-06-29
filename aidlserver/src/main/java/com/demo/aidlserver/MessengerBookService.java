package com.demo.aidlserver;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.demo.ipc.Book;

import java.util.ArrayList;

/**
 * @author Sergey Rodionov
 */
public class MessengerBookService extends Service {

    private static final int MESSAGE_FLAG = 1;
    private static final String MESSAGE_KEY = "message";

    private Messenger mServiceMessenger = new Messenger(new ServiceHandler());

    private static class ServiceHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Message clientMessage = Message.obtain(msg);
            switch (clientMessage.what) {
                case MESSAGE_FLAG:
                    clientMessage.what = MESSAGE_FLAG;
                    try {
                        Thread.sleep(5000);
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(MESSAGE_KEY, generateBooks());
                        clientMessage.setData(bundle);
                        clientMessage.replyTo.send(clientMessage);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

        private static ArrayList<Book> generateBooks() {
            ArrayList<Book> books = new ArrayList<>();
            books.add(fillDataBook( "Пушкин А.С.", 5.0f));
            books.add(fillDataBook("Тютчев Ф.И.", 4.9f));
            books.add(fillDataBook("Достоевский Ф.М.", 4.8f));
            return books;
        }

        private static Book fillDataBook(String author, float rate) {
            Book book = new Book();
            book.setAuthor(author);
            book.setRates(rate);
            return book;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mServiceMessenger.getBinder();
    }
}
