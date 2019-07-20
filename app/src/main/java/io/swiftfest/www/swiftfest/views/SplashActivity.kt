package io.swiftfest.www.swiftfest.views

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import io.swiftfest.www.swiftfest.R
import io.swiftfest.www.swiftfest.data.DataProvider
import kotlinx.android.synthetic.main.splash_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {

    private val dataProvider = DataProvider.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        spin_kit.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.Default) {
            dataProvider.fetchAppData(application)
            launch(Dispatchers.Main) {
                fadeImage()
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0);
        finish()
    }

    private fun fadeImage() {
        spin_kit.visibility = View.INVISIBLE
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
