/**
 * 『出席状況確認アプリ』
 */

package com.example.asyncsampleapplication

import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.core.os.HandlerCompat
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.concurrent.Executors
import android.content.Intent
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    // private定数
    companion object {
        private const val DEBUG_TAG = "[DEBUG]"
        private const val ATTENDANCES_URL = "http://10.0.2.2:8000/api/attendances?date="
        private const val REQUESTCODE = 1
    }

    // ListViewに表示させるデータ
    private var _list: MutableList<MutableMap<String, String>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.asyncsampleapplication.R.layout.activity_main)

        val intent = Intent(application, CalendarActivity::class.java)
        intent.putExtra("DATE", "");
        startActivityForResult(intent, REQUESTCODE);
        Log.d(DEBUG_TAG,intent.getIntExtra("DATE", 0).toString())
    }
    
    /**
     * リストビューに表示させるユーザデータを生成するメソッド。
     *
     * @return 生成されたユーザデータ。
     */
//    private fun createList(): MutableList<MutableMap<String, String>> {
    private fun createList(): MutableList<MutableMap<String, String>> {
        val lvCityList = findViewById<ListView>(com.example.asyncsampleapplication.R.id.lvCityList)
        val adapter = SimpleAdapter(
            this@MainActivity, _list, android.R.layout.simple_list_item_2,
            arrayOf("name", "attend"), intArrayOf(android.R.id.text1, android.R.id.text2)
        )

        lvCityList.adapter = adapter
        lvCityList.onItemClickListener = ListItemClickListener()

        return _list
    }

    /**
     * ユーザ情報の取得処理を行うメソッド。
     *
     * @param url ユーザデータを取得するURL。
     */
    @UiThread
    private fun receiveUserInfo(urlFull: String, progress: ProgressDialog) {
        progress.show()
        val handler = HandlerCompat.createAsync(mainLooper)
        val backgroundReceiver = UserInfoBackgroundReceiver(handler, urlFull, progress)
        val executeService = Executors.newSingleThreadExecutor()
        executeService.submit(backgroundReceiver)
    }

    /**
     * 非同期でユーザ情報APIにアクセスするためのクラス。
     *
     * @param handler ハンドラオブジェクト。
     * @param url ユーザ情報を取得するURL。
     */
    private inner class UserInfoBackgroundReceiver(handler: Handler, url: String, progress: ProgressDialog): Runnable {
        // ハンドラ
        private val _handler = handler
        // ユーザデータ取得URL
        private val _url = url

        private val _progress = progress

        @WorkerThread
        override fun run() {
            // 問い合わせ結果
            var result = ""
            // URLオブジェクトを生成。
            val url = URL(_url)
            Log.d(DEBUG_TAG, url.toString())
            // URLオブジェクトからHttpURLConnectionオブジェクトを取得。
            val con = url.openConnection() as? HttpURLConnection
            // conがnullじゃないならば…
            con?.let {
                try {
                    // 接続に使ってもよい時間を設定。
                    it.connectTimeout = 1000
                    // データ取得に使ってもよい時間。
                    it.readTimeout = 1000
                    // HTTP接続メソッドをGETに設定。
                    it.requestMethod = "GET"
                    // 接続。
                    it.connect()
                    // HttpURLConnectionオブジェクトからレスポンスデータを取得。
                    val stream = it.inputStream
                    // レスポンスデータであるInputStreamオブジェクトを文字列に変換。
                    result = is2String(stream)
                    Log.d(DEBUG_TAG, result)
                    // InputStreamオブジェクトを解放。
                    stream.close()
                }
                catch(ex: SocketTimeoutException) {
                    Log.w(DEBUG_TAG, "通信タイムアウト", ex)
                    _progress.dismiss()
                }
                // HttpURLConnectionオブジェクトを解放。
                it.disconnect()
            }
            val postExecutor = UserInfoPostExecutor(result, _progress)
            _handler.post(postExecutor)
        }

        /**
         * InputStreamオブジェクトを文字列に変換するメソッド。 変換文字コードはUTF-8。
         *
         * @param stream 変換対象のInputStreamオブジェクト。
         * @return 変換された文字列。
         */
        private fun is2String(stream: InputStream): String {
            val sb = StringBuilder()
            val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
            var line = reader.readLine()
            while(line != null) {
                sb.append(line)
                line = reader.readLine()
            }
            reader.close()
            return sb.toString()
        }
    }

    /**
     * 非同期でユーザ情報を取得した後にUIスレッドでその情報を表示するためのクラス。
     *
     * @param result APIから取得したユーザ情報JSON文字列。
     */
    private inner class UserInfoPostExecutor(result: String, progress: ProgressDialog): Runnable {
        private val _result = result

        private val _progress = progress

        @UiThread
        override fun run() {
            // ルートJSONオブジェクトを生成。
            val rootJSON = JSONObject(_result)  // JSONパース処理
            val rootUserAttendance = JSONArray(rootJSON.getString("attendance"))        // 出席者のリスト (配列)
            val rootUserNotAttendance = JSONArray(rootJSON.getString("not_attendance")) // 欠席者のリスト (配列)

            _list.clear()
            // 出席者のデータを1件ずつ取り出し
            for (i in 0 until rootUserAttendance.length()) {
                val userAttendance: JSONObject = rootUserAttendance.getJSONObject(i)
                val userID = userAttendance.getString("user_id")
                val userName = userAttendance.getString("name")

                var user = mutableMapOf("name" to userName, "id" to userID, "attend" to "出席")
                _list.add(user)
                Log.d(DEBUG_TAG, userName)
            }
            
            // 欠席者のデータを1件ずつ取り出し
            for (i in 0 until rootUserNotAttendance.length()) {
                val userNotAttendance: JSONObject = rootUserNotAttendance.getJSONObject(i)
                val userID = userNotAttendance.getString("user_id")
                val userName = userNotAttendance.getString("name")

                var user = mutableMapOf("name" to userName, "id" to userID, "attend" to "欠席")
                _list.add(user)
                Log.d(DEBUG_TAG, userName)
            }

            createList()
            _progress.dismiss()
        }
    }

    /**
     * リストがタップされた時の処理が記述されたリスナクラス。
     */
    private inner class ListItemClickListener: AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            val item = _list.get(position)
            val id = item.get("id")
            id?.let {
                val urlFull = ""
                //receiveWeatherInfo(urlFull)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?){
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            REQUESTCODE ->
                if (RESULT_OK == resultCode) {
                    val date: String = intent?.getStringExtra("DATE")!!

                    val progressDialog = ProgressDialog(this);
                    progressDialog.setTitle("読み込み中...");
                    progressDialog.setMessage("データを取得しています。");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    receiveUserInfo(ATTENDANCES_URL + date, progressDialog)

                    this.title = "出席状態確認アプリ ( " + date + " )"

                    Log.d(DEBUG_TAG, date)
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater = menuInflater
        //メニューのリソース選択
        inflater.inflate(com.example.asyncsampleapplication.R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            com.example.asyncsampleapplication.R.id.item -> {
                val intent = Intent(application, CalendarActivity::class.java)
                intent.putExtra("DATE", "");
                startActivityForResult(intent, REQUESTCODE);
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}