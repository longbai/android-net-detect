package com.qiniu.www.android_qiniu_tools

/**
 * Created by Yuting on 2015/9/23.
 */

import javax.mail.*

class MyAuthenticator : Authenticator {
    internal var userName = ""
    internal var password = ""

    constructor() {
    }

    constructor(username: String, password: String) {
        this.userName = username
        this.password = password
    }

    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(userName, password)
    }
}