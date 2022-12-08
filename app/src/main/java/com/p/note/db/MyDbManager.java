package com.p.note.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.p.note.adapter.NoteItem;

import java.util.ArrayList;
import java.util.List;

public class MyDbManager {

    private Context context;
    private MyDbHelper myDbHelper;
    private SQLiteDatabase db;

    public MyDbManager(Context context) {
        this.context = context;
        myDbHelper = new MyDbHelper(context);

    }

    public void openDb(){
        db = myDbHelper.getWritableDatabase();
    }

    public void insertToDb(String title, String disc,String uri){

        ContentValues cv = new ContentValues();
        cv.put(MyConstants.TITLE, title);
        cv.put(MyConstants.DISC,disc);
        cv.put(MyConstants.URI, uri);
        db.insert(MyConstants.TABLE_NAME, null, cv);

    }

    public List<NoteItem> getFromDb(String searchText){

        List<NoteItem> tempList = new ArrayList<>();
        String selection = MyConstants.TITLE + " like ?";
        //есть функции которие помагает пулачать елементи
        @SuppressLint("Recycle") Cursor cursor = db.query(MyConstants.TABLE_NAME,null,selection,new String[]{"%" + searchText + "%"},null,null,null);
        while (cursor.moveToNext()){

            NoteItem item = new NoteItem();
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MyConstants.TITLE));
            @SuppressLint("Range") String desc = cursor.getString(cursor.getColumnIndex(MyConstants.DISC));
            @SuppressLint("Range") String uri = cursor.getString(cursor.getColumnIndex(MyConstants.URI));
            @SuppressLint("Range") int _id = cursor.getInt(cursor.getColumnIndex(MyConstants._ID));
            item.setTitle(title);
            item.setDesc(desc);
            item.setUri(uri);
            item.setId(_id);
            tempList.add(item);

        }
        cursor.close();
        return tempList;
    }
    public List<NoteItem> getFromDb(){

        List<NoteItem> tempList = new ArrayList<>();
        //есть функции которие помагает пулачать елементи
        @SuppressLint("Recycle") Cursor cursor = db.query(MyConstants.TABLE_NAME,null,null,null,null,null,null);
        while (cursor.moveToNext()){

            NoteItem item = new NoteItem();
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MyConstants.TITLE));
            @SuppressLint("Range") String desc = cursor.getString(cursor.getColumnIndex(MyConstants.DISC));
            @SuppressLint("Range") String uri = cursor.getString(cursor.getColumnIndex(MyConstants.URI));
            @SuppressLint("Range") int _id = cursor.getInt(cursor.getColumnIndex(MyConstants._ID));
            item.setTitle(title);
            item.setDesc(desc);
            item.setUri(uri);
            item.setId(_id);
            tempList.add(item);

        }
        cursor.close();
        return tempList;
    }

    public void getFromDb(String searchText, OnDataReceived onDataReceived){
        final List<NoteItem> tempList = new ArrayList<>();
        String selection = MyConstants.TITLE + " like ?";
        final Cursor cursor = db.query(MyConstants.TABLE_NAME,null,selection,
                new String[]{"%" + searchText},null,null,null);
        while (cursor.moveToNext()){
            NoteItem item = new NoteItem();
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MyConstants.TITLE));
            @SuppressLint("Range") String desc = cursor.getString(cursor.getColumnIndex(MyConstants.DISC));
            @SuppressLint("Range") String uri = cursor.getString(cursor.getColumnIndex(MyConstants.URI));
            @SuppressLint("Range") int _id = cursor.getInt(cursor.getColumnIndex(MyConstants._ID));
            item.setTitle(title);
            item.setDesc(desc);
            item.setUri(uri);
            item.setId(_id);

            tempList.add(item);

        }
        cursor.close();
        onDataReceived.onReceived(tempList);

    }

//    public void getFromDb(String searchText){
//        final List<NoteItem> tempList = new ArrayList<>();
//        String selection = MyConstants.TITLE + " like ?";
//        final Cursor cursor = db.query(MyConstants.TABLE_NAME,null,selection,
//                new String[]{"%" + searchText},null,null,null);
//        while (cursor.moveToNext()){
//            NoteItem item = new NoteItem();
//            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MyConstants.TITLE));
//            @SuppressLint("Range") String desc = cursor.getString(cursor.getColumnIndex(MyConstants.DISC));
//            @SuppressLint("Range") String uri = cursor.getString(cursor.getColumnIndex(MyConstants.URI));
//            @SuppressLint("Range") int _id = cursor.getInt(cursor.getColumnIndex(MyConstants._ID));
//            item.setTitle(title);
//            item.setDesc(desc);
//            item.setUri(uri);
//            item.setId(_id);
//
//            tempList.add(item);
//
//        }
//        cursor.close();
//
//    }

    public void updateItem(String title, String disc, String uri,int id){
        String selection = MyConstants._ID + "=" + id;
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.TITLE, title);
        cv.put(MyConstants.DISC,disc);
        cv.put(MyConstants.URI, uri);
        db.update(MyConstants.TABLE_NAME,cv,selection, null);
    }

    public void delete(int id){
        String selection = MyConstants._ID + "=" + id;
        db.delete(MyConstants.TABLE_NAME,selection, null);
    }


    public void closeDb(){
        myDbHelper.close();
    }
}
