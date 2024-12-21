package com.dicoding.storyapp.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import androidx.lifecycle.asFlow
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.db.Story
import com.dicoding.storyapp.data.db.StoryDao
import com.dicoding.storyapp.data.db.StoryRoomDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

internal class StackRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private var widgetItems = ArrayList<Story>()
    private lateinit var dao: StoryDao

    override fun onCreate() {
        dao = StoryRoomDatabase.getDatabase(context.applicationContext).storyDao()
    }

    private fun fetchDataDB() {
        runBlocking {
            widgetItems = dao.getAllStories().asFlow().first().toMutableList() as ArrayList<Story>
        }
    }

    override fun onDataSetChanged() {
        fetchDataDB()
    }

    override fun onDestroy() {}

    override fun getCount(): Int = widgetItems.size

    @SuppressLint("RemoteViewLayout")
    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_item)
        try {
            val bitmap: Bitmap = Glide.with(context.applicationContext)
                .asBitmap()
                .load(widgetItems[position].photoUrl)
                .submit()
                .get()
            rv.setImageViewBitmap(R.id.iv_item_photo, bitmap)
            rv.setTextViewText(R.id.tv_item_name, widgetItems[position].name)
            rv.setTextViewText(R.id.tv_item_description, widgetItems[position].description)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val extras = bundleOf(StoriesWidget.EXTRA_ITEM to position)
        val fillInIntent = Intent().apply { putExtras(extras) }
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}