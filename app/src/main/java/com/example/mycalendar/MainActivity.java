package com.example.mycalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private TextView titleText;
    private Button prevButton, nextButton;
    private CalendarAdapter mCalendarAdapter;
    private GridView calendarGridView;

    //オプションメニュー
    private String id = "016010";
    String itemString= "札幌";


    //戻り値
    private static final int REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleText = findViewById(R.id.titleText);
        prevButton = findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarAdapter.prevMonth();
                titleText.setText(mCalendarAdapter.getTitle());
            }
        });
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarAdapter.nextMonth();
                titleText.setText(mCalendarAdapter.getTitle());

            }
        });
        calendarGridView = findViewById(R.id.calendarGridView);
        mCalendarAdapter = new CalendarAdapter(this);
        calendarGridView.setAdapter(mCalendarAdapter);
        //追加
        calendarGridView.setOnItemClickListener(this);

        titleText.setText(mCalendarAdapter.getTitle());

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
//        _scheduleDate.setText(dateFormat.format(mCalendarAdapter.getItem(position)));

        String memo = dateFormat.format(mCalendarAdapter.getItem(position));//ここでSQLiteからメモを取得　dateArray.get(position)を使う？


        OrderConfirmDialogFragment dialogFragment = new OrderConfirmDialogFragment(memo);
        dialogFragment.show(getSupportFragmentManager(), "OrderConfirmDialogFragment");


//        Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
//        intent.putExtra("date", memo);
//
//        startActivityForResult(intent, REQUEST_CODE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();
        itemString = item.toString();
        switch (itemId) {
            case R.id.hoSapporo:
                id = "016010";
                break;
            case R.id.hoAsahikawa:
                id = "012010";
                break;
            case R.id.hoKushiro:
                id = "014020";
                break;
            case R.id.hoMuroran:
                id = "015010";
                break;
            case R.id.hoHakodate:
                id = "017010";
                break;
            case R.id.menuAkita:
                id = "050010";
                break;
            case R.id.menuSendai:
                id = "040010";
                break;
            case R.id.menuNiigata:
                id = "150010";
                break;
            case R.id.menuTokyo:
                id = "130010";
                break;
            case R.id.menuNagano:
                id = "200010";
                break;
            case R.id.menuNagoya:
                id = "230010";
                break;
            case R.id.menuOosaka:
                id = "270000";
                break;
            case R.id.menuHiroshima:
                id = "340010";
                break;
            case R.id.menuKouti:
                id = "390010";
                break;
            case R.id.menuFukuoka:
                id = "400010";
                break;
            case R.id.menuKagoshima:
                id = "460010";
                break;
            case R.id.menuNaha:
                id = "471010";
                break;

        }

        mCalendarAdapter = new CalendarAdapter(this,id,itemString);
        calendarGridView.setAdapter(mCalendarAdapter);
        titleText.setText(mCalendarAdapter.getTitle());

        Log.i("aaaMainSctivity",mCalendarAdapter.getTitle() + "getTitle");

//        return true;
        return super.onOptionsItemSelected(item);
    }

    //ContentActivityから戻ってきた時の対応
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        String kekka = data.getStringExtra("INPUT_STRING");

        if( REQUEST_CODE == -1 ) {

            // 返却結果ステータスとの比較
            if (resultCode == Activity.RESULT_OK) {

                // 返却されてきたintentから値を取り出す
//                String str = intent.getStringExtra( "key" );
                mCalendarAdapter = new CalendarAdapter(this, id, itemString);
                calendarGridView.setAdapter(mCalendarAdapter);
                titleText.setText(mCalendarAdapter.getTitle());
            }
        }



    }
}