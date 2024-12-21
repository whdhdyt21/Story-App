package com.dicoding.storyapp.view.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import com.dicoding.storyapp.databinding.ActivityWelcomeBinding
import com.dicoding.storyapp.view.login.LoginActivity
import com.dicoding.storyapp.view.signup.SignupActivity

class WelcomeActivity : androidx.appcompat.app.AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        hideStatusBar()
        supportActionBar?.hide()
    }

    private fun hideStatusBar() {
        window.insetsController?.hide(WindowInsets.Type.statusBars())
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener { navigateToLogin() }
        binding.signupButton.setOnClickListener { navigateToSignup() }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun navigateToSignup() {
        startActivity(Intent(this, SignupActivity::class.java))
    }

    private fun playAnimation() {
        createImageViewAnimation().start()
        createUIAnimations().start()
    }

    private fun createImageViewAnimation(): ObjectAnimator {
        return ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
    }

    private fun createUIAnimations(): AnimatorSet {
        val loginAnimation = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(300)
        val signupAnimation = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(300)
        val titleAnimation = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(300)
        val descAnimation = ObjectAnimator.ofFloat(binding.descTextView, View.ALPHA, 1f).setDuration(300)

        return AnimatorSet().apply {
            playSequentially(titleAnimation, descAnimation)
            playTogether(loginAnimation, signupAnimation)
        }
    }
}