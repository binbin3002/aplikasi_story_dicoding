package com.example.storyapp.ui.maps

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.response.story.ListStoryItem
import com.example.storyapp.databinding.InfoWindowLayoutBinding.inflate
import com.example.storyapp.ui.story.ui.detailstory.DetailStoryActivity
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.Marker


class CustomInfoWindowAdapter(private var context: Context) : InfoWindowAdapter {

    override fun getInfoContents(marker: Marker): View? {
        val story = marker.tag as? ListStoryItem ?: return null
        val binding = inflate(LayoutInflater.from(context), null, false)

        binding.tvItemName.text = story.name

        Glide.with(binding.ivItemPhoto.context)
            .load(story.photoUrl)
            .preload()

        Glide.with(binding.ivItemPhoto.context)
            .load(story.photoUrl)
            .into(binding.ivItemPhoto)

        binding.root.setOnClickListener{
            val intent = Intent(context, DetailStoryActivity::class.java)
            intent.putExtra(DetailStoryActivity.EXTRA_STORY_ID, story.id)
            context.startActivity(intent)
        }
        return binding.root
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}