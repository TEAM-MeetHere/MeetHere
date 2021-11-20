package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.adapter.BookmarkAdapter
import com.example.meethere.databinding.ActivityShowBookmarkBinding
import com.example.meethere.objects.AddressObject
import com.example.meethere.objects.BookmarkObject

class ShowBookmarkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowBookmarkBinding

    lateinit var bookmarkObjects: MutableList<BookmarkObject>
    lateinit var bookmarkAdapter: BookmarkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityShowBookmarkBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        bookmarkObjects = intent.getSerializableExtra("bookmarkData") as MutableList<BookmarkObject>
        bookmarkAdapter = BookmarkAdapter(bookmarkObjects)

        binding.rvBookmarkList.layoutManager =
            LinearLayoutManager(this@ShowBookmarkActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvBookmarkList.adapter = bookmarkAdapter

        if (intent.hasExtra("date")) {
            val bookmarkIter = bookmarkObjects.listIterator()
            val dateString = intent.getStringExtra("date")
            binding.tvShowBookmark.text = dateString + " 일정"
                while (bookmarkIter.hasNext()) {
                val nextString = bookmarkIter.next().promise_date
                if(nextString != dateString) {
                    Log.d("테스트", "$nextString == $dateString")
                    bookmarkIter.remove()
                }
            }
        }
        else binding.tvShowBookmark.text = "전체 일정"

        bookmarkAdapter.notifyDataSetChanged()

        bookmarkAdapter.setItemClickListener(object : BookmarkAdapter.OnItemClickListener {
            override fun onClick(
                promise_id: Long,
                addressObject: AddressObject,
                promise_name: String,
                promise_date: String,
                position: Int,
            ) {
                val intent =
                    Intent(this@ShowBookmarkActivity, ShowBookmarkResultActivity::class.java)
                intent.putExtra("bookmarkId", promise_id.toString())
                intent.putExtra("addressObject", addressObject)
                intent.putExtra("promise_name", promise_name)
                intent.putExtra("promise_date", promise_date)
                startActivity(intent)
            }
        })
    }
}