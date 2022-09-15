package com.tasdjilati.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.tasdjilati.databinding.ActivitySplashBinding
import com.tasdjilati.ui.main.MainActivity
import com.tasdjilati.ui.settings.SplashImageViewModel
import java.util.*

class SplashActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySplashBinding
    private lateinit var splashImageViewModel: SplashImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        splashImageViewModel = ViewModelProvider(this)[SplashImageViewModel::class.java]

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        Handler().postDelayed({ openMainActivity() }, 2000)

        try{
            splashImageViewModel.getImage.observe(this , {
                if(it.isNotEmpty()){
                    Glide.with(this)
                        .load(it[0].image)
                        .into(binding.imageView)

                    binding.tvTitle.text = it[0].name

                }
            })
        }catch(e : Exception){

        }
    }

    private fun openMainActivity() {
        startActivity(Intent(this , MainActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()
        val sp = getSharedPreferences("lang_selected" , Context.MODE_PRIVATE)
        val language = sp?.getString("lang" , "fr")

        val resources = resources
        val metrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(language!!)
        if (language == "ar")
            configuration.setLayoutDirection(Locale("ar"))
        else
            configuration.setLayoutDirection(Locale("fr"))

        resources.updateConfiguration(configuration , metrics)

    }
}