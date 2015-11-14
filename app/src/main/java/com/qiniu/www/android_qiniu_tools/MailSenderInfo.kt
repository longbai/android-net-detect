package com.qiniu.www.android_qiniu_tools

/**
 * Created by Yuting on 2015/9/23.
 */
import java.util.Properties

class MailSenderInfo {


    // 发送邮件的服务器的IP和端口
    var mailServerHost: String? = null
    var mailServerPort = "25"
    // 邮件发送者的地址
    var fromAddress: String? = null
    // 邮件接收者的地址
    var toAddress: String? = null
    // 登陆邮件发送服务器的用户名和密码
    var userName: String? = null
    var password: String? = null
    // 是否需要身份验证
    var isValidate = false
    // 邮件主题
    var subject: String? = null
    // 邮件的文本内容
    var content: String? = null
    // 邮件附件的文件名
    var attachFileNames: Array<String>? = null
    /**
     * 获得邮件会话属性
     */
    val properties: Properties
        get() {
            val p = Properties()
            p.put("mail.smtp.host", this.mailServerHost)
            p.put("mail.smtp.port", this.mailServerPort)
            p.put("mail.smtp.auth", if (isValidate) "true" else "false")
            return p
        }
}

