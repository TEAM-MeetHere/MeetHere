package com.example.meethere.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.databinding.ItemFriendListBinding
import com.example.meethere.objects.FriendObject
import kotlinx.android.synthetic.main.item_friend_list.view.*

class FriendListAdapter(
    private val friendObjects: MutableList<FriendObject>,     // public
) : RecyclerView.Adapter<FriendListAdapter.FriendViewHolder>() {
    class FriendViewHolder(val binding: ItemFriendListBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val curFriend = friendObjects[position]
        val friendObject = FriendObject(curFriend.friend_id,
            curFriend.friend_name,
            curFriend.friend_email,
            curFriend.friend_phone)

        holder.itemView.apply {
            tv_friend_name.text = curFriend.friend_name
/*            tv_friend_email.text = curFriend.friend_email
            tv_friend_phone.text = curFriend.friend_phone*/
        }

        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                // 친구 정보 불러오기 (인원 더 추가하기에서 사용)
                itemClickListener.onClick(friendObject, holder.adapterPosition)
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

    interface OnItemClickListener {
        fun onClick(friendObject: FriendObject, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
}