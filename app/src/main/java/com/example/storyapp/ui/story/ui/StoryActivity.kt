package com.example.storyapp.ui.story.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.storyapp.R
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.storyapp.data.pref.SessionViewModel
import com.example.storyapp.data.pref.SessionViewModelFactory
import com.example.storyapp.data.pref.StoryAppPreferences
import com.example.storyapp.data.pref.dataStore
import com.example.storyapp.databinding.ActivityStoryBinding
import com.example.storyapp.ui.MainActivity
import com.example.storyapp.ui.maps.MapsActivity
import com.example.storyapp.ui.story.ui.addstory.AddStoryActivity
import com.example.storyapp.ui.story.ui.liststory.ListStoryFragment
import com.example.storyapp.utils.EspressoIdlingResource.idlingResource
import com.example.storyapp.utils.Utils.showToast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class StoryActivity : AppCompatActivity() {
    private var _binding: ActivityStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: Toolbar
    private lateinit var fabAddStory: FloatingActionButton

    private lateinit var pref: StoryAppPreferences

    private val sessionViewModel: SessionViewModel by viewModels {
        SessionViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = StoryAppPreferences.getInstance(dataStore)

        toolbar = binding.toolbar
        fabAddStory = binding.fabAddStory
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val navHostFragment =
                        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                    val currentFragment =
                        navHostFragment.childFragmentManager.fragments.firstOrNull() as? ListStoryFragment
                    currentFragment?.refresh()
                }
            }

        fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            resultLauncher.launch(intent)
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }

            R.id.action_settings -> {
                val navController = findNavController(R.id.nav_host_fragment)
                if (navController.currentDestination?.id != R.id.settingsFragment) {
                    navController.navigate(R.id.action_listStoryFragment_to_settingsFragment)
                }
                true
            }

            R.id.action_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun logout() {
        val title = getString(R.string.logout)
        val message = getString(R.string.logout_message)
        val builder = AlertDialog.Builder(this@StoryActivity)

        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                performLogout()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun performLogout() {
        idlingResource.increment()
        showToast(this, getString(R.string.logout_process))
        sessionViewModel.logout()

        this.lifecycleScope.launch {
            sessionViewModel.logoutState.collect { logoutState ->
                if (logoutState) {
                    val intent = Intent(this@StoryActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    idlingResource.decrement()
                }
            }
        }
    }
}