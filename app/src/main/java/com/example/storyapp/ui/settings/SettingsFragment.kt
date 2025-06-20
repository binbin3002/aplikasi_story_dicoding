package com.example.storyapp.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.storyapp.data.pref.StoryAppPreferences
import com.example.storyapp.databinding.FragmentSettingsBinding
import com.example.storyapp.R
import com.example.storyapp.data.pref.SettingsViewModelFactory
import com.example.storyapp.data.pref.dataStore
import com.example.storyapp.ui.story.ui.StoryActivity
import java.util.Locale

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var pref: StoryAppPreferences
    private lateinit var languageSpinner: Spinner

    private var isFirstSelection = true
    private var isDialogShown = false

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory.getInstance(pref)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        languageSpinner = binding.spinnerLanguage
        (activity as StoryActivity).supportActionBar?.title = getString(R.string.settings)

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        val dataStore = requireContext().applicationContext.dataStore
        pref = StoryAppPreferences.getInstance(dataStore)

        setupLanguageSpinner()
    }

    private fun setupLanguageSpinner() {
        settingsViewModel.getLangSettings().observe(viewLifecycleOwner) { language ->
            val selectedIndex = when (language) {
                "en" -> 0
                "in" -> 1
                else -> 0
            }

            languageSpinner.setSelection(selectedIndex)
        }

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (isFirstSelection) {
                    isFirstSelection = false
                    return
                }

                val selectedLanguageCode = when (position) {
                    0 -> "en"
                    1 -> "in"
                    else -> "en"
                }

                settingsViewModel.getLangSettings().observe(viewLifecycleOwner) { currentLanguage ->
                    if (selectedLanguageCode != currentLanguage) {
                        if (!isDialogShown) {
                            settingsViewModel.saveLangSettings(selectedLanguageCode)
                            updateLocale(selectedLanguageCode)
                            isDialogShown = true
                            showRestartDialog()
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showRestartDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.restart_required)
            .setMessage(R.string.restart_message)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                isDialogShown = false
                restartApp()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
                isDialogShown = false
            }
            .setCancelable(false)
            .show()
    }


    private fun updateLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)

        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun restartApp() {
        val intent = requireActivity().intent
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        requireActivity().startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}