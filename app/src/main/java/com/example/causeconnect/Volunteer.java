package com.example.causeconnect;

public class Volunteer {
    private String mName;
    private String mNumber;

    private String mUid;

    public Volunteer(String uid, String name , String number){
        mUid = uid;
        mName = name;
        mNumber = number;
    }


    public String getName(){
        return mName;
    }
    public String getUid(){
        return mUid;
    }
    public String getNumber(){
        return mNumber;
    }



}
