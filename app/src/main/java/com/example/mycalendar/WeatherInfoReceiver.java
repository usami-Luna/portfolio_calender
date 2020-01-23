package com.example.mycalendar;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.squareup.picasso.Picasso;





class WeatherInfoReceiver extends AsyncTask<String,String,String> {
    private TextView _tvWeatherTelop;

    //天気の詳細を表示する画面部品フィールド
    private TextView _tvWeatherDesc;
    private ImageView _ivTenki;
    private int _index;


    //コンストラクタ
    //お天気情報を表示する画面部品をあらかじめ取得し、フィールドに格納
//    public WeatherInfoReceiver(TextView tvWeatherTelop,TextView tvWeatherDesc) {
//        //引数をフィールドに入れる
//        _tvWeatherTelop = tvWeatherTelop;
//        _tvWeatherDesc = tvWeatherDesc;
//    }

    public WeatherInfoReceiver(TextView tvWeatherTelop,ImageView ivTenki,int index) {
        _tvWeatherTelop = tvWeatherTelop;
        _ivTenki = ivTenki;
        _index = index;

    }


    @Override
    protected String doInBackground(String... strings) {
        //可変長引数の1個目(インデックス0)を取得。これが都市ID
//        String id = params[0];//0番にcityIdを入れる　複数あればString num = params[1]とする
        //都市IDを使って接続URL文字列を作成。
//        _id = MainActivity.swi;;

        String urlStr = "http://weather.livedoor.com/forecast/webservice/json/v1?city=" + strings[0];
        //天気情報サービスから取得したJSON文字列　天気予報が格納されている
        String result = "";

        Log.i("MainSctivity",strings[0] + "Weather");


        //HTTP接続を行うHttpURLConnectionオブジェクトを宣言。finallyで確実に開放するためにtryの外で宣言
        HttpURLConnection con = null;
        //HTTP接続のレスポンスデータとして取得するInputStreamオブジェクトを宣言。同じくtry外で宣言しておく
        InputStream is = null;

        try{
            //URLオブジェクトを作成
            URL url = new URL(urlStr);
            //URLオブジェクトからHttpURLConnectionオブジェクトを取得
            con = (HttpURLConnection)url.openConnection();
            //HTTP接続メソッドを設定
            con.setRequestMethod("GET");//ネットにつなげる方法　POST（urlにのせずbodyにのせる、pw含める時など）かGET（送った情報をurlに乗せる）
            //接続
            con.connect();      //成功すると、urlにアクセスしている
            //HttpURLConnectionオブジェクトからレスポンスデータを取得
            is = con.getInputStream();
            //レスポンスデータであるInputStreamオブジェクトを文字列に変換
            result = is2String(is);

        }
        catch (MalformedURLException e) {
        }
        catch (IOException e) {
        }
        finally {
            //conを開放する
            if(con != null){
                con.disconnect();
            }
            if (is != null){
                try{
                    is.close();
                } catch (IOException e) { }
            }

        }
        return result;//onPostExecuteメソッドに行く
    }
    //onPostExecuteメソッドは画面(UIスレッド)にやってもらいたい処理を記述する
    //ここに書く内容は主に画面に対して操作することが多いと思ってください
    @Override
    protected void onPostExecute(String result) {
        //天気情報用文字列変数を用意
        String telop = "";
        String desc = "";
        String imgUrl = "";

        // ここに天気情報JSON文字列を解析する処理を記述する
        try{
            //JSON文字列からJSONObjectオブジェクトを生成。これをルートJSONオブジェクトとする
            JSONObject rootJSON = new JSONObject(result);
            //ルートJSON直下の「description」JSONオブジェクトを取得
            JSONObject descriptionJSON = rootJSON.getJSONObject("description");
            //「description」プロパティ直下の「text」文字列(天気概況文)を取得
            desc = descriptionJSON.getString("text");
            //ルートJSON直下の「forecasts」JSON配列を取得
            JSONArray forecasts = rootJSON.getJSONArray("forecasts");//forecastsが配列で作られているので取得も配列で。0が今日1が明日・・・
            //「forecasts」JSON配列の1つ目(インデックス0)のJSONオブジェクトを取得
            JSONObject forecastNow = forecasts.getJSONObject(_index);
            //「forecasts」１つめのJSONオブジェクトから、「telop」文字列(天気)を取得
            telop = forecastNow.getString("telop");
            imgUrl = forecastNow.getJSONObject("image").getString("url");
            Log.i("MainSctivity",imgUrl);

//                JSONArray temp = forecastNow.getJSONArray("temperature");
//                JSONObject max = temp.getJSONObject(0);
//                String celsius = max.getString("celsius");
            //上記を1行で書く方法
            String celsius = forecastNow.getJSONObject("temperature").getJSONObject("max").getString("celsius");
            Log.i("MainSctivity","最高気温"+celsius+"℃");

        } catch (JSONException e) {
        }

        //解析から得られた文字列をセットする
        _tvWeatherTelop.setText(telop);
//        _tvWeatherDesc.setText(desc);

        //jsonで画像取得できない時用
//        _ivTenki.setImageResource(R.drawable.hare);

        Picasso.with(MyApplication.getAppContext())
                .load(imgUrl)
                .resize(100, 61)
                .into(_ivTenki);

        Log.i("MainSctivity",imgUrl);

//        try {
//            InputStream bais = new ByteArrayInputStream(imgUrl.getBytes("utf-8"));
//            Drawable image;
//            image = Drawable.createFromStream(bais,"a");
//            _ivTenki.setImageDrawable(image);
//
//            Log.i("MainSctivity", String.valueOf(bais));
//            Log.i("MainSctivity", String.valueOf(image));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }


        //別の方法(でも面倒そう・・・)、配列もあり？
//            HashMap<String ,Integer> map = new HashMap<>();
//            map.put("晴れ",R.drawable.hare);
//
//            ivTenki.setImageResource(map.get(telop));

    }




    private String is2String(InputStream is) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));

        StringBuffer sb = new StringBuffer();
        char[] b = new char[1024];
        int line;

        //reader.read(b)...１文字ずつ読み込み、それをb(char配列)に代入する
        //戻り値は読み込めた文字数

        //HTTPレスポンスにより得られたデータis(InputStream型)を文字列に直したい。
        //そのために、バイト単位で文字を読み取りそれらを文字列に変換する必要がある

        while(0 <= (line = reader.read(b))){
            sb.append(b,0,line);
        }
        return  sb.toString();

        //Scannerでも代用できるかも？
//        Scanner sc = new Scanner(is,"UTF-8");
//        StringBuffer sb = new StringBuffer();
//        char[] b = new char[1024];
//        int line;
//
//
//        return  sb.toString();


    }
}
