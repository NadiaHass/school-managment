package com.tasdjilati.ui.settings

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.navigation.Navigation
import com.tasdjilati.R
import com.tasdjilati.databinding.FragmentChangeLanguageBinding
import com.tasdjilati.databinding.FragmentSettingBinding
import com.tasdjilati.ui.main.MainActivity
import java.util.*


class ChangeLanguageFragment : Fragment() {
    private var _binding: FragmentChangeLanguageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChangeLanguageBinding.inflate(inflater, container, false)

        binding.rgLanguage.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rb_fr -> {
                    val language = "en"
                    val sp = activity?.getSharedPreferences("lang_selected" , Context.MODE_PRIVATE)
                    val editor = sp?.edit()
                    editor?.putString("lang" , language)
                    editor?.apply()
                    setLocale(language)
                    startActivity(Intent(activity , MainActivity::class.java))
                    activity?.finish()
                }
                R.id.rb_ar-> {
                    val language = "ar"
                    val sp = activity?.getSharedPreferences("lang_selected" , Context.MODE_PRIVATE)
                    val editor = sp?.edit()
                    editor?.putString("lang" , language)
                    editor?.apply()
                    setLocale(language)
                    startActivity(Intent(activity , MainActivity::class.java))
                    activity?.finish()
                }
            }
        }

        return binding.root
    }

    private fun setLocale(language: String) {
        val resources = resources
        val metrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(language)
        if (language == "ar")
            configuration.setLayoutDirection(Locale("ar"))
        else
            configuration.setLayoutDirection(Locale("fr"))

        resources.updateConfiguration(configuration , metrics)
        onConfigurationChanged(configuration)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

}