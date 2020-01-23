package com.example.mycalendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    /**
     * データベースファイル名の定数フィールド。
     */
    private static final String DATABASE_NAME = "schedule.db";
    /**
     * バージョン情報の定数フィールド。
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * コンストラクタ。
     */
    public DatabaseHelper(Context context) {
        //親クラスのコンストラクタの呼び出し。
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //テーブル作成用SQL文字列の作成。
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE schedule (");
        sb.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,"); //
        sb.append("date TEXT,");
        sb.append("time TEXT,");
        sb.append("title TEXT,");
        sb.append("note TEXT");
        sb.append(");");
//        String str = "CREATE TABLE cocktailmemo (_id INTEGER PRIMARY KEY,name TEXT,note TEXT);"; //上記複数と同じこと
        String sql = sb.toString();

        //SQLの実行。
        db.execSQL(sql);
    }

    // バージョンが変更された場合、アップグレードの内容　更新があれば使用する
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
