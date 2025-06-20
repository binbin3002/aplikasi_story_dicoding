package com.example.storyapp.ui.story.ui.detailstory
import com.example.storyapp.R
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.storyapp.data.pref.StoryAppPreferences
import com.example.storyapp.data.pref.dataStore
import com.example.storyapp.data.remote.response.story.ListStoryItem
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.ui.customview.CustomButton
import kotlin.getValue

class DetailStoryActivity : AppCompatActivity() {
    private var _binding: ActivityDetailStoryBinding? = null
    private val binding get() = _binding!!

    private val detailStoryViewModel: DetailStoryViewModel by viewModels {
        DetailStoryViewModelFactory(this)
    }

    private lateinit var pref: StoryAppPreferences

    private lateinit var photoImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var errorContainer: ConstraintLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var retryButton: CustomButton
    private lateinit var backButton: ImageButton
    private lateinit var storyId: String

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        photoImageView = binding.ivDetailPhoto
        nameTextView = binding.tvDetailName
        descriptionTextView = binding.tvDetailDescription
        errorContainer = binding.errorContainer
        progressBar = binding.progressBar
        errorTextView = binding.tvErrorMessage
        retryButton = binding.btnRetry

        pref = StoryAppPreferences.getInstance(dataStore)

        backButton = binding.btnBack

        retryButton.text = getString(R.string.retry)

        storyId = intent.getStringExtra(EXTRA_STORY_ID) ?: ""

        backButton.setOnClickListener {
            onBackPressed()
            this.overridePendingTransition(R.anim.exit, R.anim.enter)
        }

        detailStoryViewModel.fetchStoryDetail(storyId)
        setupView()
    }

    private fun setupView() {
        supportActionBar?.hide()

        observeStoryDetail()
        observeError()
        observeLoading()
    }

    private fun observeStoryDetail() {
        detailStoryViewModel.story.observe(this) { story ->
            if (story != null) {
                updateUI(story)
            }
        }
    }

    private fun updateUI(story: ListStoryItem) {
        nameTextView.text = story.name
        descriptionTextView.text = story.description

        Glide.with(this)
            .load(story.photoUrl)
            .into(photoImageView)

        errorContainer.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun observeError() {
        detailStoryViewModel.error.observe(this) { errorMessage ->
            if (errorMessage != null) {
                errorTextView.text = getString(R.string.fetch_story_failed)
                errorContainer.visibility = View.VISIBLE
                progressBar.visibility = View.GONE

                retryButton.setOnClickListener {
                    observeStoryDetail()
                    errorContainer.visibility = View.GONE
                }
            }
        }
    }

    private fun observeLoading() {
        detailStoryViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
    }
}