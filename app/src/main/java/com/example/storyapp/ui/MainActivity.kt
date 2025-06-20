package com.example.storyapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.storyapp.data.pref.StoryAppPreferences
import com.example.storyapp.data.pref.dataStore
import com.example.storyapp.R
import com.example.storyapp.data.pref.SettingsViewModelFactory
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.settings.SettingsViewModel
import java.util.Locale


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var pref: StoryAppPreferences
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory.getInstance( pref)
    }

    private lateinit var language:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pref = StoryAppPreferences.getInstance(applicationContext.dataStore)
        language = pref.getLangSettingsSync()
        updateLocale(language)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsViewModel.getLangSettings().observe(this) {language ->
            updateLocale(language)
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun updateLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)
        recreateFragmentIfNecessary()
    }

    @SuppressLint("DetachAndAttachSameFragment")
    private fun recreateFragmentIfNecessary() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (currentFragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.detach(currentFragment)
            transaction.attach(currentFragment)
            transaction.commitAllowingStateLoss()
        }
    }

}