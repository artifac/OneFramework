package com.one.framework.app.login

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.one.framework.R
import com.one.framework.app.base.BaseActivity
import com.one.framework.log.Logger
import com.one.framework.utils.ToastUtils
import com.one.framework.utils.UIUtils
import kotlinx.android.synthetic.main.one_login_layout.*

class LoginActivity : BaseActivity(), View.OnClickListener {
    private lateinit var loginProxy: LoginProxy
    private val watcher: EditWatcher = EditWatcher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.one_login_layout)
        with(oneLoginTitle) {
            setLeftClickListener { skip() }
            hideRightImage(true)
            setLeftImage(R.drawable.one_top_bar_back_selector)
//            setRightResId(R.string.one_login_skip)
            setRightClickListener {
                skip()
            }
        }
        oneObtainCode.setOnClickListener(this)
        oneLoginBtn.setOnClickListener(this)
        loginProxy = LoginProxy(this)
        oneLoginPhone.addTextChangedListener(watcher)
        oneLoginCode.addTextChangedListener(watcher)
        oneLoginInvite.addTextChangedListener(watcher)
        oneLoginBtn.setRippleColor(Color.parseColor("#f3f3f3"), Color.parseColor("#f3f3f3"))
        oneLoginBtn.isEnabled = false

        oneLoginPhone.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) checkPhone(oneLoginPhone.text.toString())  }
        oneLoginProto.setText(Html.fromHtml(getString(R.string.one_login_proto)))
        oneLoginProto.setOnClickListener(this)
        parseIntent()
    }

    private fun skip() {
//        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun parseIntent() {
    }

    override fun onClick(v: View?) {
        if (UIUtils.isFastDoubleClick()) {
            return
        }
        var id = v?.id
        when (id) {
            R.id.oneObtainCode -> {
                if (checkPhone(oneLoginPhone.text.toString())) {
                    doSendSms()
                }
            }
            R.id.oneLoginBtn -> {
                doLogin()
            }
            R.id.oneLoginProto -> {

            }
        }
    }


    private fun doSendSms() {
        loginProxy.doSms(oneLoginPhone.text.toString(), object : ILogin.ILoginVerifyCode {
            override fun onSuccess(isNew: Boolean) {
                oneInviteCodeLayout.visibility = if (isNew) View.VISIBLE else View.GONE
                loginProxy.countDown(object : ILogin.ILoginCountDownTimer {
                    override fun onTick(millisUntilFinished: Long) {
                        oneObtainCode.setTextColor(Color.parseColor("#999ba1"))
                        oneObtainCode.text = UIUtils.highlight(String.format(getString(R.string.one_login_recode_count_down), millisUntilFinished / 1000), Color.parseColor("#1665ff"))
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
        var inviteCode = oneLoginInvite.text.toString()
        oneLoginLoading.visibility = View.VISIBLE
        oneLoginBtn.tripButtonText = ""
        loginProxy.doLogin(phone, code, inviteCode, object : ILogin.ILoginListener {
            override fun onLoginSuccess() {
                setResult(Activity.RESULT_OK)
                skip()
            }

            override fun onLoginFail(message: String?) {
                setResult(-2)
                ToastUtils.toast(this@LoginActivity, message)
                oneLoginLoading.visibility = View.GONE
                oneLoginBtn.setTripButtonText(R.string.one_login)
            }
        })
    }

    fun checkPhone(phone: String) : Boolean {
        oneLoginCheck.visibility = if (UIUtils.isMobileNO(phone)) View.GONE else View.VISIBLE
        oneObtainCode.isEnabled = UIUtils.isMobileNO(phone)
        oneObtainCode.setTextColor(if (UIUtils.isMobileNO(phone)) Color.parseColor("#1665ff") else Color.parseColor("#babfca"))
        return UIUtils.isMobileNO(phone)
    }

    inner class EditWatcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            Logger.e("ldx", "after ${s.toString()} length ${s.toString().length}")
            if (s.toString().length >= 11) {
                checkPhone(s.toString())
            } else {
                oneLoginCheck.visibility = View.GONE
            }
            checkLoginStatus()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            checkLoginStatus()

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    private fun checkLoginStatus() {
        var flag = (oneInviteCodeLayout.visibility == View.VISIBLE && !TextUtils.isEmpty(oneLoginInvite.text.toString())) || oneInviteCodeLayout.visibility == View.GONE
        oneLoginBtn.isEnabled = !TextUtils.isEmpty(oneLoginPhone.text.toString())
                && !TextUtils.isEmpty(oneLoginCode.text.toString())
                && UIUtils.isMobileNO(oneLoginPhone.text.toString()) && flag
    }
}
