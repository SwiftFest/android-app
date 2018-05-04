package io.swiftfest.www.swiftfest.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import io.swiftfest.www.swiftfest.R
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AlphaAnimation
import io.swiftfest.www.swiftfest.data.DataProvider
import kotlinx.android.synthetic.main.splash_activity.logo_text
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class SplashActivity : AppCompatActivity() {

    private var loading: Boolean = true
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        launch(CommonPool) {
            DataProvider.instance.loadData(application)
            launch(UI) {
                loading = false
                fadeImage()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        handler.postDelayed(this::fadeImage, MIN_SPLASH_DURATION)
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0);
        finish()
    }

    private fun fadeImage() {
        if (loading) {
            return
        }
        handler.removeCallbacks(this::fadeImage)
        val a = AlphaAnimation(1.00f, 0.00f)

        a.interpolator = AccelerateDecelerateInterpolator()
        a.duration = FADE_DURATION

        a.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                logo_text.setVisibility(View.GONE)
                startMainActivity()
            }
        })

        logo_text.startAnimation(a)
    }

    companion object {
        val FADE_DURATION: Long = 750
        val MIN_SPLASH_DURATION: Long = 1500
    }
}
