package com.qiniu.www.android_qiniu_tools


import android.util.Log
import android.widget.EditText

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.util.ArrayList
import java.util.Random
import com.*


/**
 * Created by Yuting on 2015/9/18.
 */
class ToolSrvImpl : ToolSrv {

    override fun addObserver(observer: Observer) {
        if (!observerList.contains(observer)) {
            observerList.add(observer)
        }
    }

    fun cleanOldData() {
        for (observer in observerList) {
            observer.cleanScreen()
        }
    }

    fun changePingPage(str: String, status: Int) {
        for (observer in observerList) {
            observer.pingPageChange(str, status)
            Log.i("ping", str)
        }
    }

    override fun ping(ipAddress: String) {
        Log.i("ping", "进入ping方法")
        Thread(object : Runnable {
            override fun run() {
                cleanOldData()// 调用观察者，清除屏幕上的数据
                val result = StringBuffer()
                var str: String
                try {
                    val cmdPing = "/system/bin/ping -c 5 -w 15   " + ipAddress
                    Log.i("ping", "执行命令前" + ipAddress)
                    val p = Runtime.getRuntime().exec(cmdPing)
                    Log.i("ping", "执行命令后")
                    val bufferReader = BufferedReader(InputStreamReader(p.inputStream))
                    var str = bufferReader.readLine();
                    while (str != null) {
                        Log.i("ping", str)
                        changePingPage(str, 0)
                        str = bufferReader.readLine();
                    }
                    val status = p.waitFor()// 只有status=0时，正常
                    if (status == 0) {
                        str = "success"
                        Log.i("ping", str)
                        Log.i("ping", "执行成功")
                        email(MainActivity.result.toString(), ipAddress)
                    } else {
                        Log.i("ping", "执行失败")
                        Log.i("ping", "全局变量获取数据")
                        email(MainActivity.result.toString(), ipAddress)

                    }
                    bufferReader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }).start()

    }

    fun chanUrlPage(status: Int) {
        for (observer in observerList) {
            observer.urlPageChange(status)
        }
    }

    override fun callUrl() {
        chanUrlPage(0)
    }

    fun changeTraceroutePage(str: String, status: Int) {
        for (observer in observerList) {
            observer.traceroutePageChange(str, status)
        }
    }

    override fun ip() {
        Thread(object : Runnable {
            override fun run() {
                val result = StringBuffer()
                val str: String
                var infoUrl: URL? = null
                var inStream: InputStream? = null
                try {
                    //http://iframe.ip138.com/ic.asp
                    //infoUrl = new URL("http://city.ip138.com/city0.asp");
                    val now = System.currentTimeMillis()
                    val ra = Random()

                    val rb = ra.nextInt(900) + 100
                    Log.i("ping", "ip" + now)
                    infoUrl = URL("http://7563614540466$rb.testns.cdnunion.net/?callback=jQuery18102853321498259902_1442981784438&_=$now")
                    val connection = infoUrl.openConnection()
                    val httpConnection = connection as HttpURLConnection
                    val responseCode = httpConnection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        inStream = httpConnection.inputStream
                        val reader = BufferedReader(InputStreamReader(inStream, "utf-8"))
                        val strber = StringBuilder()
                        var line: String? = reader.readLine()
                        while (line != null) {
                            strber.append(line!! + "\n")
                            line = reader.readLine()
                        }
                        inStream!!.close()
                        //从反馈的结果中提取出IP地址
                        Log.i("ping", "ip" + strber)
                        // int start = strber.indexOf("ip：");
                        // int end = 13;
                        //   line = strber.substring(start + 1, end);
                        changePingPage(strber.toString(), 0)

                    }
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }).start()
    }

    fun email(s: String, title: String) {
        Thread(object : Runnable {
            override fun run() {
                Log.i("mail", "进入发送邮件")
                val result = StringBuffer()
                val str: String
                val infoUrl: URL? = null
                val inStream: InputStream? = null

                val mailInfo = MailSenderInfo()
                mailInfo.mailServerHost = "smtp.exmail.qq.com"
                mailInfo.mailServerPort = "25"
                mailInfo.isValidate = true
                //填写发送的邮箱的地址
                mailInfo.userName = ""
                //您的邮箱密码
                mailInfo.password = ""
                mailInfo.fromAddress = ""
                mailInfo.toAddress = ""
                mailInfo.subject = "七牛网络测试-" + title
                mailInfo.content = s
                Log.i("mail", "设置邮件配置成功")
                //这个类主要来发送邮件
                val sms = SimpleMailSender()
                sms.sendTextMail(mailInfo)//发送文体格式
                // sms.sendHtmlMail(mailInfo);//发送html格式
            }

        }).start()
    }

    override fun traceroute(ipAddress: String) {
        Thread(object : Runnable {
            override fun run() {
                val result = StringBuffer()
                var str: String?
                try {
                    Log.i("traceroute", "执行traceroute")
                    val cmdTraceroute = "/system/xbin/traceroute " + "liuhanlin-work.qiniudn.com"
                    Log.i("traceroute", "执行命令前" + ipAddress)
                    val p = Runtime.getRuntime().exec(cmdTraceroute)
                    Log.i("traceroute", "执行命令后" + ipAddress)
                    val bufferReader = BufferedReader(InputStreamReader(p.inputStream))
                    Log.i("traceroute", "执行读取" + ipAddress)
                    str = bufferReader.readLine()
                    while (str != null) {
                        changeTraceroutePage(str!!, 0)
                        str = bufferReader.readLine()
                    }
                    val status = p.waitFor()// 只有status=0时，正常
                    Log.i("traceroute", "执行traceroute失败status" + status)
                    bufferReader.close()
                } catch (e: IOException) {
                    Log.i("traceroute", "执行traceroute失败1")
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    Log.i("traceroute", "执行traceroute失败2")
                }

            }
        }).start()
    }

    companion object {
        private val observerList = ArrayList<Observer>()
    }

}

