package com.one.framework.app.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.one.framework.MainActivity
import com.one.framework.R
import com.one.framework.app.base.BaseActivity
import com.one.framework.log.Logger
import kotlinx.android.synthetic.main.one_login_layout.*
import java.util.regex.Pattern

class LoginActivity : BaseActivity(), View.OnClickListener {
    private lateinit var loginProxy: LoginProxy
    private val watcher: EditWatcher = EditWatcher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.one_login_layout)
        with(oneLoginTitle) {
            hideRightImage(true)
            setLeftImage(R.drawable.one_top_bar_back_normal)
            setRightResId(R.string.one_login_skip)
            setRightClickListener {
                skip()
            }
        }
        oneObtainCode.setOnClickListener(this)
        oneLoginBtn.setOnClickListener(this)
        loginProxy = LoginProxy(this)
        oneLoginPhone.addTextChangedListener(watcher)
        oneLoginCode.addTextChangedListener(watcher)
        oneLoginBtn.setRippleColor(Color.parseColor("#f3f3f3"), Color.parseColor("#f3f3f3"))
        oneLoginBtn.isEnabled = false
    }

    private fun skip() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun isMobileNO(phone: String): Boolean {
        val telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(16[0-9])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$"
        var pattern = Pattern.compile(telRegex)
        return if (TextUtils.isEmpty(phone)) {
            false
        } else {
            pattern.matcher(phone).matches()
        }
    }

    override fun onClick(v: View?) {
        var id = v?.id
        when (id) {
            R.id.oneObtainCode -> {
                doSendSms()
            }
            R.id.oneLoginBtn -> {
                doLogin()
            }
        }
    }


    private fun doSendSms() {
        loginProxy.doSms(oneLoginPhone.text.toString(), object : ILogin.ILoginVerifyCode {
            override fun onSuccess() {
                loginProxy.countDown(object : ILogin.ILoginCountDownTimer {
                    override fun onTick(millisUntilFinished: Long) {
                        oneObtainCode.text = String.format(getString(R.string.one_login_recode_count_down), millisUntilFinished / 1000)
                        oneObtainCode.setTextColor(Color.parseColor("#999ba1"))
                    }

                    override fun onFinish() {
                        oneObtainCode.text = getString(R.string.one_login_verify_code)
                        oneObtainCode.setTextColor(Color.parseColor("#1665ff"))
                    }
                })
            }

            override fun onFail() {
            }
        })

    }

    private fun doLogin() {
        var phone = oneLoginPhone.text.toString()
        var code = oneLoginCode.text.toString()
        if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(code)) {
            return
        }
        loginProxy.doLogin(phone, code, object : ILogin.ILoginListener {
            override fun onLoginSuccess() {
                skip()
            }

            override fun onLoginFail(message: String?) {
            }
        })
    }

    fun checkPhone(phone: String) {
        oneLoginCheck.visibility = if (!isMobileNO(phone)) View.VISIBLE else View.GONE
    }

    inner class EditWatcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            Logger.e("ldx", "after ${s.toString()} length ${s.toString().length}")
            if (s.toString().length >= 11) {
                checkPhone(s.toString())
            } else {
                oneLoginCheck.visibility = View.GONE
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            checkLoginStatus()

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    private fun checkLoginStatus() {
        oneLoginBtn.isEnabled = !TextUtils.isEmpty(oneLoginPhone.text.toString()) && !TextUtils.isEmpty(oneLoginCode.text.toString()) && isMobileNO(oneLoginPhone.text.toString())
    }
}
