package com.example.meethere.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.objects.BookmarkObject
import com.example.meethere.databinding.ItemBookmarkBinding
import com.example.meethere.objects.AddressObject
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
            tv_promise_place_name.text = curBookmark.promise_place_name
            tv_promise_date.text = curBookmark.promise_date
        }

        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val addressObject = AddressObject(
                    curBookmark.promise_place_name,
                    curBookmark.promise_name,
                    curBookmark.promise_road_address_name,
                    curBookmark.promise_address_name,
                    curBookmark.promise_lat.toDouble(),
                    curBookmark.promise_lon.toDouble()
                )
                itemClickListener.onClick(curBookmark.promise_id, addressObject, holder.adapterPosition)
            }
        })

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
                // 데이터베이스에서도 삭제하는 코드 필요
            }
        })
    }

    override fun getItemCount(): Int {
        return bookmarkObjects.size
    }

    interface OnItemClickListener {
        fun onClick(promise_id: Long, addressObject: AddressObject, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener: OnItemClickListener
}