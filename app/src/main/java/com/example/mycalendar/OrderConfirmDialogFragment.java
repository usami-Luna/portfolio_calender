package com.example.mycalendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class OrderConfirmDialogFragment extends DialogFragment {

    String memo;

    int _id;
    String time;
    String title;

    List<Map<String,Object>> menuList = new ArrayList<>();


    String[] from = {"Schedule","id"};
    int[] to = {android.R.id.text1,android.R.id.text2};

    OrderConfirmDialogFragment(String memo) {
        this.memo = memo;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //追加　→　メモを表示させる
        //ここはもろもろ修正必要
        SimpleDateFormat dateFormatMemo = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
//        String memo = dateFormatMemo.format(dateArray.get(position));//ここでSQLiteからメモを取得　dateArray.get(position)を使う？
//        holder.memoText.setText(memo);
        Log.i("MainSctivity",memo + "メモを表示させる");

        //sqlのデータを取得
        DatabaseHelper helper = new DatabaseHelper(getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        try{
            String sql = "SELECT * FROM schedule WHERE date = '" + memo +"' order by time" ;  //_id =  + _id
            Cursor cursor = db.rawQuery(sql,null);
            time = "";
            title = "";
            while(cursor.moveToNext()){
                int id = cursor.getColumnIndex("_id");
                int idxtime = cursor.getColumnIndex("time");
                int idxtitle = cursor.getColumnIndex("title");
                _id = cursor.getInt(id);
                time = cursor.getString(idxtime) + "～ ";
                title = cursor.getString(idxtitle);

                Map<String,Object> menu = new HashMap<>();
                menu.put("Schedule",time+title);
                menu.put("id",_id);
                menuList.add(menu);
                Log.i("MainSctivity",_id + "cursor.moveToNext()");

            }

            if( title.equals("") ){
                Map<String,Object> menu = new HashMap<>();
                menu.put("Schedule","予定がありません");
                menu.put("id",0);
                menuList.add(menu);
            }
        }
        finally {
            db.close();
        }

        //リストビュー
        SimpleAdapter adapter = new SimpleAdapter
                (getActivity(),menuList,android.R.layout.simple_list_item_1,from,to);

        ListView listView = new ListView(getActivity());
        listView.setAdapter(adapter);



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(memo);
//        builder.setMessage(time+title);
        builder.setView(listView).create();
        listView.setOnItemClickListener(new ListItemClickListener());
//        builder.setItems(menuList, new DialogButtonClickListener());
        builder.setPositiveButton("追加",new DialogButtonClickListener());
        builder.setNegativeButton("戻る",new DialogButtonClickListener());

        AlertDialog dialog = builder.create();


        return dialog;

//        return super.onCreateDialog(savedInstanceState);
    }
    private class DialogButtonClickListener implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialog, int which) {
            //ここからJankenresultメソッドを呼び出したい
            //そのためには、MainActivityのインスタンスを取得する必要がある
            //MainActivity activity = new MainActivity()ではだめ！！
            //上記の場合、アプリの画面を起動しているMainActivityのインスタンスとは異なるインスタンスを生成してしまうため
//            MainActivity mainActivity = (MainActivity) getActivity();
            Intent intent = new Intent(getContext(), ContentActivity.class);
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:


                    intent.putExtra("_id", 0);
                    intent.putExtra("date", memo);
                    startActivity(intent);

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;

            }
        }
    }
    private class ListItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Map<String,Object> item = (Map<String,Object>) parent.getItemAtPosition(position);

            int database_id = (int) item.get("id");
            Log.i("MainSctivity",position + "position");

            Intent intent = new Intent(getContext(), ContentActivity.class);
            intent.putExtra("_id", database_id);
            intent.putExtra("date", memo);

            Log.i("MainSctivity",database_id + "id");

            startActivity(intent);

        }
    }
}
