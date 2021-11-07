package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meethere.R
import com.example.meethere.adapter.FriendAdapter
import com.example.meethere.databinding.ActivityFriendBinding
import com.example.meethere.objects.FriendObject

class FriendActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFriendBinding
    private val listItems = arrayListOf<FriendObject>()
    private val friendAdapter = FriendAdapter(listItems)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvFriendList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rvFriendList.adapter = friendAdapter

        var example = FriendObject("예시 이름", "예시 이메일", "예시 전화번호")
        listItems.add(example)
        friendAdapter.notifyDataSetChanged()

        binding.buttonAddFriend.setOnClickListener() {
            val intent = Intent(this,AddFriendActivity::class.java)
            startActivity(intent)
        }
    }
}