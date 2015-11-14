package com.qiniu.www.android_qiniu_tools

import android.content.Intent
import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.app.ProgressDialog
import android.widget.TextView

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


final class MainActivity : AppCompatActivity() {

    private var btn: Button? = null

    private var editText: EditText? = null

    private var resultView: EditText? = null

    private val progressDialog: ProgressDialog? = null

    private val srv = ToolSrvImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        srv.addObserver(observer)

        //设置监听
        resultView = findViewById(R.id.editText2) as EditText
        editText = findViewById(R.id.editText) as EditText
        btn = findViewById(R.id.button) as Button
        btn!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                //   progressDialog = ProgressDialog.show(MainActivity.this, "", "加载中，请稍后……");
                // srv.ip();//srv.ping(editText.getText().toString());
                // srv.traceroute(editText.getText().toString());
                // LDNetTraceRoute traceRoute=new LDNetTraceRoute();
                // traceRoute.startTraceRoute(editText.getText().toString());
                val intent = Intent(this@MainActivity,
                        tracert::class.java)
                startActivity(intent)
                Log.i("ping", "开始执行ping方法的调用")
            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }


        return super.onOptionsItemSelected(item)


    }

    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> {
                    resultView!!.setText(result.toString())
                    if (progressDialog!!.isShowing) {
                        progressDialog.dismiss()
                    }
                }
                1 -> result.delete(0, result.length())
                else -> {
                }
            }
        }
    }
    private val observer = object : Observer() {
        override fun pingPageChange(str: String, status: Int) {
            result.append(str).append("\r\n")
            this@MainActivity.handler.sendEmptyMessage(0)
            Log.i("ping", "添加显示")
        }

        override fun cleanScreen() {
            this@MainActivity.handler.sendEmptyMessage(1)
        }
    }

    companion object {


        val result = StringBuffer()
    }


}
