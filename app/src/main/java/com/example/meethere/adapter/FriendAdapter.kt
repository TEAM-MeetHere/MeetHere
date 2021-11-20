package com.example.meethere.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.activity.SendRequestLocationActivity
import com.example.meethere.databinding.ItemFriendBinding
import com.example.meethere.objects.FriendObject
import com.example.meethere.retrofit.RetrofitManager
import com.example.meethere.utils.Constants.TAG
import com.example.meethere.utils.RESPONSE_STATE
import kotlinx.android.synthetic.main.item_friend.view.*
import org.json.JSONObject


class FriendAdapter(
    private val friendObjects: MutableList<FriendObject>,     // public
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {
    class FriendViewHolder(val binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    fun addAddress(addressObject: FriendObject) {
        friendObjects.add(addressObject)
        notifyItemInserted(friendObjects.size - 1)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val curFriend = friendObjects[position]
        val friendObject = FriendObject(curFriend.friend_id,
            curFriend.friend_name,
            curFriend.friend_email,
            curFriend.friend_phone)

        holder.itemView.apply {
            tv_friend_name.text = curFriend.friend_name
            tv_friend_email.text = curFriend.friend_email
            tv_friend_phone.text = curFriend.friend_phone
        }

        holder.binding.buttonDelete.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                val friendId = friendObjects[holder.adapterPosition].friend_id

                RetrofitManager.instance.deleteFriendService(
                    friendId = friendId,
                    completion = { responseState, responseBody ->
                        when (responseState) {

                            //API 호출 성공시
                            RESPONSE_STATE.OKAY -> {
                                Log.d(TAG, "API 호출 성공 : $responseBody")

                                //JSON parsing
                                //{}->JSONObject, []->JSONArray
                                val jsonObjects = JSONObject(responseBody)
                                val statusCode = jsonObjects.getInt("statusCode")

                                if (statusCode == 200) {
                                    val message = jsonObjects.getString("message")
                                    Log.d(TAG, "message = $message")
                                    Toast.makeText(p0!!.context, message, Toast.LENGTH_LONG).show()

                                    if (friendObjects.isNotEmpty())
                                        friendObjects.remove(friendObjects[holder.adapterPosition])
                                    notifyDataSetChanged()

                                } else {
                                    val errorMessage = jsonObjects.getString("message")
                                    Log.d(TAG, "error message = $errorMessage")
                                    Toast.makeText(p0!!.context, errorMessage, Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            //API 호출 실패시
                            RESPONSE_STATE.FAIL -> {
                                Log.d(TAG, "API 호출 실패 : $responseBody")
                            }
                        }
                    }
                )

            }
        })

        holder.binding.buttonSMS1.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                // SMS 전송 (위치요청)
                itemClickListener.onClick(friendObject, holder.adapterPosition)

            }
        })
        holder.binding.buttonSMS2.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                // SMS 전송 (공유코드)
                itemClickListener2.onClick(friendObject, holder.adapterPosition)
            }
        })

        holder.binding.buttonSMS3.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                // SMS 전송(내 위치 전송)
                itemClickListener3.onClick(friendObject, holder.adapterPosition)
            }
        })

        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                // 친구 정보 불러오기 (인원 더 추가하기에서 사용)
                itemClickListener4.onClick(friendObject, holder.adapterPosition)
            }
        })
    }

    override fun getItemCount(): Int {
        return friendObjects.size
    }

    fun getData(): MutableList<FriendObject> {
        return friendObjects
    }

    private lateinit var itemClickListener: OnItemClickListener
    private lateinit var itemClickListener2: OnItemClickListener
    private lateinit var itemClickListener3: OnItemClickListener
    private lateinit var itemClickListener4: OnItemClickListener

    interface OnItemClickListener {
        fun onClick(friendObject: FriendObject, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    fun setItemClickListener2(onItemClickListener: OnItemClickListener) {
        this.itemClickListener2 = onItemClickListener
    }

    fun setItemClickListener3(onItemClickListener: OnItemClickListener) {
        this.itemClickListener3 = onItemClickListener
    }

    fun setItemClickListener4(onItemClickListener: OnItemClickListener) {
        this.itemClickListener4 = onItemClickListener
    }
}