package com.example.mycalendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class CalendarAdapter  extends BaseAdapter{
    private List<Date> dateArray = new ArrayList();
    private Context mContext;
    private DateManager mDateManager;
    private LayoutInflater mLayoutInflater;

    private String _tenki ="016010";
    String _tenkiBasyo= "札幌";

    //カスタムセルを拡張したらここでWigetを定義
    private static class ViewHolder {
        public TextView dateText;
        //追加
        public TextView memoText;
        public TextView textComent;
        private TextView _tvWeatherTelop;

        //仮　20200109
        private ImageView _ivTenki;
    }

    public CalendarAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDateManager = new DateManager();
        dateArray = mDateManager.getDays();
    }
    public CalendarAdapter(Context context,String tenki,String tenkiBasyo){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDateManager = new DateManager();
        dateArray = mDateManager.getDays();
        _tenki = tenki;
        _tenkiBasyo = tenkiBasyo;
    }

    @Override
    public int getCount() {
        return dateArray.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.cell_layout, null);
            holder = new ViewHolder();
            holder.dateText = convertView.findViewById(R.id.dateText);

            //追加
            holder.memoText = convertView.findViewById(R.id.memoText);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        //セルのサイズを指定
        float dp = mContext.getResources().getDisplayMetrics().density;
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(parent.getWidth()/7 - (int)dp, (parent.getHeight() - (int)dp * mDateManager.getWeeks() ) / mDateManager.getWeeks());
        convertView.setLayoutParams(params);

        //日付のみ表示させる
        SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.US);
        holder.dateText.setText(dateFormat.format(dateArray.get(position)));

        //追加　→　メモを表示させる
        //ここはもろもろ修正必要
        SimpleDateFormat dateFormatMemo = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
        String memo = dateFormatMemo.format(dateArray.get(position));//ここでSQLiteからメモを取得　dateArray.get(position)を使う？
//        holder.memoText.setText(memo);
        Log.i("MainSctivity",memo + "メモを表示させる");

        //sqlのデータを取得
        DatabaseHelper helper = new DatabaseHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();
        try{
            String sql = "SELECT * FROM schedule WHERE date = '" + memo +"'" ;  //_id =  + _id
            Cursor cursor = db.rawQuery(sql,null);
            String time = "";
            String title = "";
            int count = 0;
            while(cursor.moveToNext()){
                int idxtime = cursor.getColumnIndex("time");
                int idxtitle = cursor.getColumnIndex("title");
                time = cursor.getString(idxtime) + "～ ";
                title = cursor.getString(idxtitle);
                count ++;
            }

            if(count <= 1){
                holder.memoText.setText(time+title);
            } else if(count > 1){
                holder.memoText.setText(count + "件の予定");
            }


        }
        finally {
            db.close();
        }
        //ここまでsql

        //追加　天気
        holder._tvWeatherTelop = convertView.findViewById(R.id.tvWeatherTelop);
        holder._ivTenki = convertView.findViewById(R.id.ivTenki);

        //オプションメニュー
//        String cityID = MyApplication.getAppContext().getid();
//        String cityID = "016010";

        WeatherInfoReceiver receiver;





//        String urlStr = "http://weather.livedoor.com/forecast/webservice/json/v1?city=016010";

        //当月以外のセルをグレーアウト
        if (mDateManager.isCurrentMonth(dateArray.get(position))){
            convertView.setBackgroundColor(Color.WHITE);
        }else {
            convertView.setBackgroundColor(Color.LTGRAY);
        }

        //当日の背景を黄色に（今回追記）
        if (mDateManager.isToday(dateArray.get(position))) {
            convertView.setBackgroundColor(Color.parseColor("#ffefd5"));

            receiver = new WeatherInfoReceiver(holder._tvWeatherTelop,holder._ivTenki,0);
            receiver.execute(_tenki);
        } else
        //翌日
        if ( mDateManager.isTomorrow(dateArray.get(position))) {
            receiver = new WeatherInfoReceiver(holder._tvWeatherTelop,holder._ivTenki,1);
            receiver.execute(_tenki);
        } else
        //前日
        if ( mDateManager.isYesterday(dateArray.get(position))) {
            holder._tvWeatherTelop.setText(_tenkiBasyo + "の天気情報");
            }  else
         {
            //他月の同positionにも上記の画像が出てしまうので他月は非表示に設定
            holder._tvWeatherTelop.setText("");
//            holder._ivTenki.setImageResource(.actionModeBackground); //GONE
//            holder._tvWeatherTelop.setVisibility(View.INVISIBLE);
            holder._ivTenki.setVisibility(View.INVISIBLE); //GONE
        }
//        //翌翌日 なかった。。
//        if ( mDateManager.isTwoDaysLater(dateArray.get(position))) {
//            receiver = new WeatherInfoReceiver(holder._tvWeatherTelop,holder._ivTenki,2);
//            receiver.execute(cityID);
//        }

        //日曜日を赤、土曜日を青に
        int colorId;
        switch (mDateManager.getDayOfWeek(dateArray.get(position))){
            case 1:
                colorId = Color.RED;
                break;
            case 7:
                colorId = Color.BLUE;
                break;

            default:
                colorId = Color.BLACK;
                break;
        }
        holder.dateText.setTextColor(colorId);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return dateArray.get(position);
    }

    //表示月を取得
    public String getTitle(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM", Locale.US);
        return format.format(mDateManager.mCalendar.getTime());
    }

    //翌月表示
    public void nextMonth(){
        mDateManager.nextMonth();
        dateArray = mDateManager.getDays();
        this.notifyDataSetChanged();
    }

    //前月表示
    public void prevMonth(){
        mDateManager.prevMonth();
        dateArray = mDateManager.getDays();
        this.notifyDataSetChanged();
    }
}