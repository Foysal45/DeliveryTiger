package com.bd.deliverytiger.app.ui.splash

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.login.LoginActivity
import com.bd.deliverytiger.app.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    private lateinit var splashBackIV: ImageView
    private lateinit var splashLogoLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashBackIV = findViewById(R.id.splash_back_image)
        splashLogoLayout = findViewById(R.id.splash_logo_layout)

        val animTranslation = ObjectAnimator.ofFloat(splashBackIV, "translationX", 0f, 80f)
        animTranslation.duration = 2000

        val animLogoFadeIn = ObjectAnimator.ofFloat(splashLogoLayout, "alpha", 0f, 1f)
        animLogoFadeIn.duration = 2000

        val animSet = AnimatorSet()
        animSet.play(animTranslation).with(animLogoFadeIn)
        animSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                //Helper.showToast("Completed")
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

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationStart(p0: Animator?) {

            }

        })
        animSet.start()
    }
}
