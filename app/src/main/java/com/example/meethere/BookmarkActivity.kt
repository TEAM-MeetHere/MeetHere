package com.example.meethere

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.BitmapCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.databinding.ActivityBookmarkBinding
import com.example.meethere.databinding.ActivityMainBinding

class BookmarkActivity : AppCompatActivity(){
    private var mBinding : ActivityBookmarkBinding? = null
    private val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)
        mBinding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookmarkList = arrayListOf(
            BookmarkItem(R.drawable.star, "카페베네 인하점"),
            BookmarkItem(R.drawable.star, "카페베네 부곡점"),
            BookmarkItem(R.drawable.star, "카페베네 a점"),
            BookmarkItem(R.drawable.star, "카페베네 b점"),
            BookmarkItem(R.drawable.star, "카페베네 c점"),
            BookmarkItem(R.drawable.star, "카페베네 d점"),
            BookmarkItem(R.drawable.star, "카페베네 e점"),
            BookmarkItem(R.drawable.star, "카페베네 f점"),
            BookmarkItem(R.drawable.star, "카페베네 e점"),
            BookmarkItem(R.drawable.star, "카페베네 f점"),
            BookmarkItem(R.drawable.star, "카페베네 e점"),
            BookmarkItem(R.drawable.star, "카페베네 f점"),
            BookmarkItem(R.drawable.star, "카페베네 e점"),
            BookmarkItem(R.drawable.star, "카페베네 f점"),
            BookmarkItem(R.drawable.star, "카페베네 e점"),
            BookmarkItem(R.drawable.star, "카페베네 f점"),
            BookmarkItem(R.drawable.star, "카페베네 e점"),
            BookmarkItem(R.drawable.star, "카페베네 f점")
        )


        binding.re.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.re.setHasFixedSize(true)
        binding.re.adapter = BookmarkAdapter(bookmarkList)

    }

}
