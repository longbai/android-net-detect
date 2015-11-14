package com.qiniu.www.android_qiniu_tools

/**
 * Created by Yuting on 2015/9/18.
 */
import com.qiniu.www.android_qiniu_tools.Observer

interface ToolSrv {
    fun addObserver(observer: Observer)

    fun ping(ipAddress: String)

    fun callUrl()

    fun traceroute(ipAddress: String)
    fun ip()
}

