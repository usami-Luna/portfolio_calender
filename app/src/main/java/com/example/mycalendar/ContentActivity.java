package com.example.mycalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class ContentActivity extends AppCompatActivity {

    private String currentDate;

    //データベース
    int _id = -1;
    TextView _scheduleDate;
    EditText _tvscheduleTime;
    EditText _tvscheduleTitle;
    EditText _tvscheduleNote;

    Button _btSave;
    Button _btDelete;

    String scheduleTime = "";

    int num;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        Intent intent = getIntent();
        _id = intent.getIntExtra("_id",0);
        currentDate = intent.getStringExtra("date");


        Log.i("MainSctivity",currentDate);



        //TextViewを取得。
        _scheduleDate = findViewById(R.id.etDate);
        _scheduleDate.setText(currentDate);

//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
//        _scheduleDate.setText(dateFormat.format(currentDate.getBytes()));




        //保存ボタンを取得。
        _btSave = findViewById(R.id.btSave);
        _btDelete = findViewById(R.id.btDelete);

//        _btDelete.setEnabled(false);


        //データベースヘルパーオブジェクトを作成。
        DatabaseHelper helper = new DatabaseHelper(ContentActivity.this);
        //データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            //主キーによる検索SQL文字列の用意。
            String sql = "SELECT * FROM schedule WHERE _id = " + _id;
            //SQLの実行。
            Cursor cursor = db.rawQuery(sql, null);

            int positon = cursor.getPosition();

            //新規か更新かの確認用
            num = -1;
            if(cursor.moveToNext() == true){
                //更新
                num = 0;
            }else {
                //新規
                num = 1;
                _btDelete.setEnabled(false);
            }

            Log.i("MainSctivity", String.valueOf(num) + "sql");
            // ここまでkari

            //データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意。
            String time = "";
            String title = "";
            String note = "";
            //SQL実行の戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得。

            cursor.moveToPosition(positon);

            while (cursor.moveToNext()) {
                //カラムのインデックス値を取得。
                int idxtime = cursor.getColumnIndex("time");
                int idxTitle = cursor.getColumnIndex("title");
                int idxNote = cursor.getColumnIndex("note");
                //カラムのインデックス値を元に実際のデータを取得。
                time = cursor.getString(idxtime);
                title = cursor.getString(idxTitle);
                note = cursor.getString(idxNote);
            }
            //感想のEditTextの各画面部品を取得しデータベースの値を反映。
            _tvscheduleTime = findViewById(R.id.edTime);
            _tvscheduleTime.setText(time);
            _tvscheduleTitle = findViewById(R.id.edTitle);
            _tvscheduleTitle.setText(title);
            _tvscheduleNote = findViewById(R.id.edSyousai);
            _tvscheduleNote.setText(note);


        }
        finally {
            //データベース接続オブジェクトの解放。
            db.close();
        }
    }
    /**
     * 保存ボタンがタップされた時の処理メソッド。
     */
    public void onSaveButtonClick(View view) {
        //入力された感想を取得。
//        _tvscheduleTitle = findViewById(R.id.edTitle);
        String title = _tvscheduleTitle.getText().toString();

//        _tvscheduleNote = findViewById(R.id.edSyousai);
        String syousai = _tvscheduleNote.getText().toString();

        //データベースヘルパーオブジェクトを作成。
        DatabaseHelper helper = new DatabaseHelper(ContentActivity.this);
        //データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = helper.getWritableDatabase();

        SQLiteStatement stmt;

        try {
            if(num==1){
                //新規
                //インサート用SQL文字列の用意。
                String sqlInsert = "INSERT INTO schedule (date,time, title,note) VALUES (?,?, ?, ?)";
                //SQL文字列を元にプリペアドステートメントを取得。
                stmt = db.compileStatement(sqlInsert);
                //変数のバインド。
                stmt.bindString(1, currentDate);
                stmt.bindString(2, scheduleTime);
//            stmt.bindLong(1, _cocktailId);
                stmt.bindString(3, title);
                stmt.bindString(4, syousai);
                //インサートSQLの実行。
                stmt.executeInsert();

                Toast.makeText(ContentActivity.this, "保存されました。", Toast.LENGTH_SHORT).show();
            } else if(num==0){
                //更新
                String sqlUpdate = "UPDATE schedule SET time=?, title=?,note=?  WHERE _id = ?";
                stmt = db.compileStatement(sqlUpdate);
                //変数のバインド。
//                stmt.bindString(1, currentDate);
                stmt.bindString(1, scheduleTime);
//            stmt.bindLong(1, _cocktailId);
                stmt.bindString(2, title);
                stmt.bindString(3, syousai);
                stmt.bindLong(4, _id);
                //インサートSQLの実行。
                stmt.executeUpdateDelete();

                Toast.makeText(ContentActivity.this, "更新されました。", Toast.LENGTH_SHORT).show();

            }

        }
        finally {
            //データベース接続オブジェクトの解放。
            db.close();
        }



//        //カクテル名を「未選択」に変更。
//        _tvCocktailName.setText(getString(R.string.tv_name));
//        //感想欄の入力値を消去。
//        etNote.setText("");
        //保存ボタンをタップできないように変更。
        _btSave.setEnabled(false);
        _btDelete.setEnabled(true);
    }
    public void onDeleteButtonClick(View view) {
        //データベースヘルパーオブジェクトを作成。
        DatabaseHelper helper = new DatabaseHelper(ContentActivity.this);
        //データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            //まず、リストで選択されたカクテルのメモデータを削除。その後インサートを行う。
            //削除用SQL文字列を用意。
            //アンドロイドでは?は変数が入る
            String sqlDelete = "DELETE FROM schedule WHERE _id = ?";
            //SQL文字列を元にプリペアドステートメントを取得。
            SQLiteStatement stmt = db.compileStatement(sqlDelete);
            //変数のバイド。
            stmt.bindLong(1, _id);
//            stmt.bindString(1, currentDate);
            //削除SQLの実行。
            stmt.executeUpdateDelete();

        }
        finally {
            //データベース接続オブジェクトの解放。
            db.close();
        }

//        _tvscheduleTime.setText("");
//        _tvscheduleTitle.setText("");
//        _tvscheduleNote.setText("");
        _btSave.setEnabled(true);
        _btDelete.setEnabled(false);

        Toast.makeText(ContentActivity.this, "削除されました。", Toast.LENGTH_SHORT).show();



    }

    public void onReturnButtonClick(View view) {
//        Intent intent2 = new Intent();
//        intent2.putExtra("INPUT_STRING", "a");
//        setResult(Activity.RESULT_OK, intent2);

        Intent intent2 = new Intent(this,MainActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity( intent2 );

        finish();

    }

    public void onDialogClick(View view){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        scheduleTime = String.format("%02d:%02d", hourOfDay,minute);


                        _tvscheduleTime.setText(scheduleTime + "～");
                    }
                },
                hour,minute,true);

        dialog.show();
    }

}
