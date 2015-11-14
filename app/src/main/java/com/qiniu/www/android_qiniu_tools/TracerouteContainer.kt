package com.qiniu.www.android_qiniu_tools

/**
 * Created by Yuting on 2015/9/29.
 */

import java.io.Serializable

class TracerouteContainer(var hostname: String?, var ip: String?, var ms: Float, var isSuccessful: Boolean) : Serializable {

    override fun toString(): String {
        return "Traceroute : \nHostname : $hostname\nip : $ip\nMilliseconds : $ms"
    }

    companion object {
        private val serialVersionUID = 1034744411998219581L
    }
}
