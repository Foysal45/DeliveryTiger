package com.bd.deliverytiger.app.ui.splash

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.ActivitySplashBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.login.LoginActivity
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.alert
import com.bd.deliverytiger.app.utils.appVersion
import com.bd.deliverytiger.app.utils.appVersionCode
import com.bumptech.glide.Glide
import org.koin.android.ext.android.inject
import timber.log.Timber

@SuppressLint("SetTextI18n")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by inject()

    private var showUpdatePopup: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        fetchConfigData()
        animateView()

        /*Glide.with(this)
            .load(R.raw.gif_dt)
            .into(binding.splashGif)*/

        /*val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            goToHome()
        }, 3000L)*/
    }

    private fun initView() {
        binding.versionCode.text = "v${appVersion()}"
    }

    private fun animateView() {
        val animTranslation = ObjectAnimator.ofFloat(binding.car, "translationX", 0f, 450f)
        animTranslation.duration = 2000
        //val animLogoFadeIn = ObjectAnimator.ofFloat(splashLogoLayout, "alpha", 0f, 1f)
        //animLogoFadeIn.duration = 2000
        val animSet = AnimatorSet()
        animSet.play(animTranslation)//.with(animLogoFadeIn)
        animSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {
                if (showUpdatePopup) {
                    showUpdatePopup()
                } else {
                    goToHome()
                }
            }
        })
        animSet.start()
    }

    private fun fetchConfigData() {
        viewModel.getBannerInfo().observe(this, Observer { model ->
            Timber.d("fetchConfigData $model")
            val currentAppVersionCode = model.currentAppVersionCode
            val appVersionCode = appVersionCode()
            if (appVersionCode > 0) {
                if (currentAppVersionCode > appVersionCode) {
                    showUpdatePopup = true
                    Timber.d("showUpdatePopup $showUpdatePopup")
                }
            }
        })
    }

    private fun showUpdatePopup() {
        val msg = "অ্যাপ এর নতুন ভার্সন এসেছে, অনুগ্রহ করে গুগল প্লে স্টোর থেকে আপডেট করে নিন।"
        val alert = alert(getString(R.string.instruction), msg, false, "গুগল প্লে স্টোর", "ক্যানসেল") {
            if (it == AlertDialog.BUTTON_POSITIVE) {
                goToGooglePlay()
            } else {
                finish()
            }
        }
        alert.setCancelable(false)
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    private fun goToGooglePlay() {
        val packageId = packageName
        try {
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${packageId}")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            }.also {
                startActivity(it)
            }
        } catch (e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${packageId}")))
        }
    }

    private fun goToHome() {
        if (SessionManager.isLogin) {
            Intent(this@SplashActivity, HomeActivity::class.java).apply {
                if (intent.extras != null) {
                    putExtras(intent.extras!!)
                }
            }.also {
                startActivity(it)
            }
        } else {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        }
        //val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        //startActivity(intent)
        finish()
    }
}
