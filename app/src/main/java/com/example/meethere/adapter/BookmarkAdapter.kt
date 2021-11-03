package com.example.meethere.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.BookmarkObject
import com.example.meethere.databinding.ItemBookmarkBinding
import kotlinx.android.synthetic.main.item_bookmark.view.*

class BookmarkAdapter(
    private val bookmarkObjects: MutableList<BookmarkObject>
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {
    class BookmarkViewHolder(val binding: ItemBookmarkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val buttonEdit = binding.buttonEdit
        val buttonDelete = binding.buttonDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding =
            ItemBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(binding)
    }

    fun addPromise(bookmarkObject: BookmarkObject) {
        bookmarkObjects.add(bookmarkObject)
        notifyItemInserted(bookmarkObjects.size - 1)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val curBookmark = bookmarkObjects[position]
        holder.itemView.apply {
            tv_promise_name.text = curBookmark.promise_name
            tv_promise_date.text = curBookmark.promise_date
            tv_promise_member.text = curBookmark.promise_member
        }

        holder.buttonEdit.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                // 수정
            }
        })
        holder.buttonDelete.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (bookmarkObjects.isNotEmpty())
                    bookmarkObjects.remove(bookmarkObjects[holder.adapterPosition])
                notifyDataSetChanged()
            }
        })
    }

    override fun getItemCount(): Int {
        return bookmarkObjects.size
    }
}