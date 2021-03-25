package com.bd.deliverytiger.app.ui.splash

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.ActivitySplashBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.login.LoginActivity
import com.bd.deliverytiger.app.utils.SessionManager
import com.bumptech.glide.Glide

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*Glide.with(this)
            .load(R.raw.gif_dt)
            .into(binding.splashGif)*/

        /*val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            goToHome()
        }, 3000L)*/

        val animTranslation = ObjectAnimator.ofFloat(binding.car, "translationX", 0f, 450f)
        animTranslation.duration = 2000
        //val animLogoFadeIn = ObjectAnimator.ofFloat(splashLogoLayout, "alpha", 0f, 1f)
        //animLogoFadeIn.duration = 2000
        val animSet = AnimatorSet()
        animSet.play(animTranslation)//.with(animLogoFadeIn)
        animSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {
                goToHome()
            }
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {}
        })
        animSet.start()
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
