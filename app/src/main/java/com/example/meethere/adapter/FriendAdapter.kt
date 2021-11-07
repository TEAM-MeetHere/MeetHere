package com.example.meethere.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.databinding.ItemFriendBinding
import com.example.meethere.objects.FriendObject
import kotlinx.android.synthetic.main.item_friend.view.*


class FriendAdapter(
    private val friendObjects: MutableList<FriendObject>     // public
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
        holder.itemView.apply {
            tv_friend_name.text = curFriend.friend_name
            tv_friend_email.text = curFriend.friend_email
            tv_friend_phone.text = curFriend.friend_phone
        }
        
        holder.binding.buttonDelete.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (friendObjects.isNotEmpty())
                    friendObjects.remove(friendObjects[holder.adapterPosition])
                notifyDataSetChanged()
            }
        })

        holder.binding.buttonSMS1.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                // SMS 전송
            }
        })
        holder.binding.buttonSMS2.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                // SMS 전송
            }
        })
    }

    override fun getItemCount(): Int {
        return friendObjects.size
    }

    fun getData(): MutableList<FriendObject> {
        return friendObjects
    }
}