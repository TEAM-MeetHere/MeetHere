package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.meethere.R
import com.example.meethere.databinding.ActivityMainNewBinding
import com.example.meethere.fragment.*
import kotlinx.android.synthetic.main.activity_main_new.*

class MainNewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainNewBinding

    private var flag: Int = 1
    private val f1: Fragment = MainHomeFragment()
    private val f2: Fragment = MainFriendFragment()
    private val f3: Fragment = MainPromiseFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().add(binding.frameLayoutMain.id, f1, "TAG1")
        supportFragmentManager.beginTransaction().add(binding.frameLayoutMain.id, f2, "TAG2")
        supportFragmentManager.beginTransaction().add(binding.frameLayoutMain.id, f3, "TAG3")

        binding.bnvMain.setOnItemSelectedListener {
            Log.d("메인", it.title.toString())
            changeFragment(it.title.toString())
            true
        }

        binding.ivMain.setOnClickListener {
            when (flag) {
                1 -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }
                2 -> {
                    val intent = Intent(this, AddFriendActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        changeFragment("홈")
    }

    private fun changeFragment(title: String) {
        when (title) {
            "홈" -> {
                flag = 1
                binding.tvMain.setText("메인")
                binding.ivMain.visibility = View.VISIBLE
                binding.ivMain.setImageResource(R.drawable.image_button_setting)
                replaceFragment(f1)
            }
            "친구" -> {
                flag = 2
                binding.tvMain.setText("친구")
                binding.ivMain.visibility = View.VISIBLE
                binding.ivMain.setImageResource(R.drawable.image_button_plus)
                replaceFragment(f2)
            }
            "약속" -> {
                flag = 3
/*                val fragment3: MainPromiseFragment =
                    supportFragmentManager.findFragmentByTag("TAG3") as MainPromiseFragment
                fragment3.refresh()*/
                binding.tvMain.setText("약속")
                binding.ivMain.visibility = View.GONE
                replaceFragment(f3)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            if (fragment.isAdded) {
                show(fragment)
            } else {
                add(binding.frameLayoutMain.id, fragment)
            }

            supportFragmentManager.fragments.forEach {
                if (it != fragment && it.isAdded) {
                    hide(it)
                }
            }
        }.commit()
    }

    var lastTimeBackPressed : Long = 0

    override fun onBackPressed() {
        if (System.currentTimeMillis() - lastTimeBackPressed >= 1500) {
            lastTimeBackPressed =
                System.currentTimeMillis()
            Toast.makeText (this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show()
        } else {
            finishAffinity()
        }
    }
}