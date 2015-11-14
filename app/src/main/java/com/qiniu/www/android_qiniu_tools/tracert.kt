package com.qiniu.www.android_qiniu_tools

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.ArrayList

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText

class tracert : AppCompatActivity() {

    // 输入网址框
    private var et: EditText? = null

    // 开始traceroute的button
    private var searchButton: Button? = null

    // 最大的ttl跳转 可以自己设定
    private val MAX_TTL = 30

    // 初始化默认ttl 为1
    private var ttl = 1
    private var ipToPing: String? = null
    // ping耗时
    private var elapsedTime: Float = 0.toFloat()

    // 存放结果集的tarces
    private val traces = ArrayList<TracerouteContainer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracert)
        et = this.findViewById(R.id.input) as EditText
        searchButton = this.findViewById(R.id.search) as Button
        searchButton!!.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View) {
                // TODO Auto-generated method stub
                Log.i("ping", "开始执行tracert方法的调用")
                ExecuteTracerouteAsyncTask(MAX_TTL, et!!.text.toString()).execute()
            }
        })
    }

    private fun showResultInLog() {
        for (container in traces) {
            Log.i("tracert", container.toString())
        }
    }

    /**
     * 这个任务就是来更新我们的后台log 日志 把所得到的traceroute信息打印出来。

     */
    private inner class ExecuteTracerouteAsyncTask(private val maxTtl: Int, private val url: String) : AsyncTask<Void, Void, String>() {

        /**
         * 后台所做的工作 本质就是调用 ping命令 来完成类似traceroute的功能
         */
        override fun doInBackground(vararg params: Void): String {
            var res = ""
            try {
                res = launchPing(url)
            } catch (e1: IOException) {
                // TODO Auto-generated catch block
                e1.printStackTrace()
            }

            val trace: TracerouteContainer

            if (res.contains(UNREACHABLE_PING) && !res.contains(EXCEED_PING)) {
                trace = TracerouteContainer("", parseIpFromPing(res),
                        elapsedTime, false)
            } else {
                trace = TracerouteContainer("", parseIpFromPing(res),
                        if (ttl == maxTtl)
                            java.lang.Float.parseFloat(parseTimeFromPing(res))
                        else
                            elapsedTime, true)
            }

            val inetAddr: InetAddress
            try {
                inetAddr = InetAddress.getByName(trace.ip)
                val hostname = inetAddr.hostName
                trace.hostname = hostname
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            }

            traces.add(trace)
            return res
        }

        @Throws(IOException::class)
        private fun launchPing(url: String): String {
            val p: Process
            var command = ""

            // 这个实际上就是我们的命令第一封装 注意ttl的值的变化 第一次调用的时候 ttl的值为1
            val format = "/system/bin/ping -c 1 -t %d "
            command = format.format(ttl)

            val startTime = System.nanoTime()
            // 实际调用命令时 后面要跟上url地址
            p = Runtime.getRuntime().exec(command + url)
            val stdInput = BufferedReader(InputStreamReader(
                    p.inputStream))

            var s: String?
            var res = ""
            s = stdInput.readLine()
            while (s != null) {
                res += s!! + "\n"
                // 这个地方这么做的原因是 有的手机 返回的from 有的手机返回的是From所以要
                // 这么去判定 请求结束的事件 算一下 延时
                if (s!!.contains(FROM_PING) || s!!.contains(SMALL_FROM_PING)) {
                    elapsedTime = (System.nanoTime() - startTime) / 1000000.0f
                }
                s = stdInput.readLine()
            }

            // 调用结束的时候 销毁这个资源
            p.destroy()

            if (res == "") {
                throw IllegalArgumentException()
            }
            // 第一次调用ping命令的时候 记得把取得的最终的ip地址 赋给外面的ipToPing
            // 后面要依据这个ipToPing的值来判断是否到达ip数据报的 终点
            if (ttl == 1) {
                ipToPing = parseIpToPingFromPing(res)
            }
            return res
        }

        override fun onPostExecute(result: String) {
            // 如果为空的话就截止吧 过程完毕
            if (TextUtils.isEmpty(result)) {
                return
            }

            // 如果这一跳的ip地址与最终的地址 一致的话 就说明 ping到了终点
            if (traces[traces.size - 1].ip == ipToPing) {
                if (ttl < maxTtl) {
                    ttl = maxTtl
                    traces.removeAt(traces.size - 1)
                    ExecuteTracerouteAsyncTask(maxTtl, url).execute()
                } else {
                    // 如果ttl ==maxTtl的话 当然就结束了 我们就要打印出最终的结果
                    showResultInLog()
                }
            } else {
                // 如果比较的ip 不相等 哪就说明还没有ping到最后一跳。我们就需要继续ping
                // 继续ping的时候 记得ttl的值要加1
                if (ttl < maxTtl) {
                    ttl++
                    ExecuteTracerouteAsyncTask(maxTtl, url).execute()
                }
            }
            super.onPostExecute(result)
        }

    }

    /**
     * 从结果集中解析出ip

     * @param ping
     * *
     * @return
     */
    private fun parseIpFromPing(ping: String): String {
        var ip = ""
        if (ping.contains(FROM_PING)) {
            var index = ping.indexOf(FROM_PING)

            ip = ping.substring(index + 5)
            if (ip.contains(PARENTHESE_OPEN_PING)) {
                val indexOpen = ip.indexOf(PARENTHESE_OPEN_PING)
                val indexClose = ip.indexOf(PARENTHESE_CLOSE_PING)

                ip = ip.substring(indexOpen + 1, indexClose)
            } else {
                ip = ip.substring(0, ip.indexOf("\n"))
                if (ip.contains(":")) {
                    index = ip.indexOf(":")
                } else {
                    index = ip.indexOf(" ")
                }

                ip = ip.substring(0, index)
            }
        } else {
            val indexOpen = ping.indexOf(PARENTHESE_OPEN_PING)
            val indexClose = ping.indexOf(PARENTHESE_CLOSE_PING)

            ip = ping.substring(indexOpen + 1, indexClose)
        }

        return ip
    }

    /**
     * 从结果集中解析出ip

     * @param ping
     * *
     * @return
     */
    private fun parseIpToPingFromPing(ping: String): String {
        var ip = ""
        if (ping.contains(PING)) {
            val indexOpen = ping.indexOf(PARENTHESE_OPEN_PING)
            val indexClose = ping.indexOf(PARENTHESE_CLOSE_PING)

            ip = ping.substring(indexOpen + 1, indexClose)
        }

        return ip
    }

    /**
     * 从结果集中解析出time

     * @param ping
     * *
     * @return
     */
    private fun parseTimeFromPing(ping: String): String {
        var time = ""
        if (ping.contains(TIME_PING)) {
            var index = ping.indexOf(TIME_PING)

            time = ping.substring(index + 5)
            index = time.indexOf(" ")
            time = time.substring(0, index)
        }

        return time
    }

    companion object {

        // 都是一些字符串 用于parse 用的
        private val PING = "PING"
        private val FROM_PING = "From"
        private val SMALL_FROM_PING = "from"
        private val PARENTHESE_OPEN_PING = "("
        private val PARENTHESE_CLOSE_PING = ")"
        private val TIME_PING = "time="
        private val EXCEED_PING = "exceed"
        private val UNREACHABLE_PING = "100%"
    }
}
