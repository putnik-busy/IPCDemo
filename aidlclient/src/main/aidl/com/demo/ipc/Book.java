package com.demo.ipc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Sergey Rodionov
 */
public class Book implements Parcelable {

    private String mAuthor;
    private float mRates;

    protected Book(Parcel in) {
        mAuthor = in.readString();
        mRates = in.readFloat();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public float getRates() {
        return mRates;
    }

    public void setRates(float rates) {
        mRates = rates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Book book = (Book) o;

        if (Float.compare(book.mRates, mRates) != 0) {
            return false;
        }
        return mAuthor != null ? mAuthor.equals(book.mAuthor) : book.mAuthor == null;
    }

    @Override
    public int hashCode() {
        int result = mAuthor != null ? mAuthor.hashCode() : 0;
        result = 31 * result + (mRates != +0.0f ? Float.floatToIntBits(mRates) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Book{" +
                "mAuthor='" + mAuthor + '\'' +
                ", mRates=" + mRates +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAuthor);
        dest.writeFloat(mRates);
    }
}
