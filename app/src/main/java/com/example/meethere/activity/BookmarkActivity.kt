package com.example.meethere.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.adapter.BookmarkAdapter
import com.example.meethere.BookmarkItem
import com.example.meethere.R
import com.example.meethere.databinding.ActivityBookmarkBinding

class BookmarkActivity : AppCompatActivity() {
    private var mBinding: ActivityBookmarkBinding? = null
    private val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)
        mBinding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookmarkList = arrayListOf(
            BookmarkItem(R.drawable.star, "인하대학교"),
            BookmarkItem(R.drawable.star, "카페베네 부평점"),
            BookmarkItem(R.drawable.star, "CPU 게임랜드"),
        )


        binding.re.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.re.setHasFixedSize(true)
        binding.re.adapter = BookmarkAdapter(bookmarkList)

    }

}