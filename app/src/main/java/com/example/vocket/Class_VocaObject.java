package com.example.vocket;

import android.os.Parcel;
import android.os.Parcelable;

public class Class_VocaObject implements Parcelable,Comparable<Class_VocaObject> {
    int savetime;
    double failp;
    String voca;
    String mean1;
    String mean2;
    String pronun;

    Class_VocaObject(int psave,String pvo,String pmen1,String pmen2,String ppron){
        savetime = psave;
        voca = pvo;
        mean1 = pmen1;
        mean2 = pmen2;
        pronun = ppron;
        failp = 0;

    }

    Class_VocaObject(int psave,String pvo,String pmen1,String pmen2,String ppron,Double pfail){
        savetime = psave;
        voca = pvo;
        mean1 = pmen1;
        mean2 = pmen2;
        pronun = ppron;
        failp = pfail;

    }

    Class_VocaObject(Parcel in){  // write 와 변수 순서를 유지해야 전달가능
        this.savetime = in.readInt();
        this.voca = in.readString();
        this.mean1 = in.readString();
        this.mean2 = in.readString();
        this.pronun = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.savetime);
        dest.writeString(this.voca);
        dest.writeString(this.mean1);
        dest.writeString(this.mean2);
        dest.writeString(this.pronun);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Class_VocaObject createFromParcel(Parcel in) {
            return new Class_VocaObject(in);
        }

        @Override
        public Class_VocaObject[] newArray(int size) {
            // TODO Auto-generated method stub
            return new Class_VocaObject[size];
        }

    };

    @Override
    public int compareTo(Class_VocaObject s) {
        if (this.savetime < s.savetime) {
            return -1;
        } else if (this.savetime > s.savetime) {
            return 1;
        }
        return 0;
    }
}
