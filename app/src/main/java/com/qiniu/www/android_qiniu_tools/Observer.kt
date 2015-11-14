package com.qiniu.www.android_qiniu_tools

/**
 * Created by Yuting on 2015/9/18.
 */
abstract class Observer {
    open fun pingPageChange(str: String, status: Int) {
    }

    open fun cleanScreen() {
    }

    fun urlPageChange(status: Int) {
    }

    fun traceroutePageChange(str: String, status: Int) {
    }

}