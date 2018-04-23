package com.demo.ipc;
import com.demo.ipc.AsyncCallback;

 interface AsyncBookService {
     oneway  void loadBooks(AsyncCallback callback);

     oneway  void finish();
}
