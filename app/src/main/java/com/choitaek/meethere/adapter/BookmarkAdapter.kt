package com.choitaek.meethere.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.choitaek.meethere.objects.BookmarkObject
import com.choitaek.meethere.databinding.ItemBookmarkBinding
import com.choitaek.meethere.objects.AddressObject
import com.choitaek.meethere.retrofit.RetrofitManager
import com.choitaek.meethere.utils.Constants.TAG
import com.choitaek.meethere.utils.RESPONSE_STATE
import kotlinx.android.synthetic.main.item_bookmark.view.*
import org.json.JSONObject

class BookmarkAdapter(
    private val bookmarkObjects: MutableList<BookmarkObject>,
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {
    class BookmarkViewHolder(val binding: ItemBookmarkBinding) :
        RecyclerView.ViewHolder(binding.root) {
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
                itemClickListener.onClick(
                    curBookmark.promise_id,
                    addressObject,
                    curBookmark.promise_name,
                    curBookmark.promise_date,
                    holder.adapterPosition
                )
            }
        })

        holder.buttonDelete.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                // 데이터베이스에서도 삭제하는 코드 필요
                val bookmarkId = bookmarkObjects[holder.adapterPosition].promise_id

                RetrofitManager.instance.deleteBookmarkService(
                    bookmarkId = bookmarkId,
                    completion = { responseState, responseBody ->
                        when (responseState) {

                            //API 호출 성공시
                            RESPONSE_STATE.OKAY -> {
                                Log.d(TAG, "API 호출 성공 : $responseBody")

                                //JSON parsing
                                //{}->JSONObejct, []->JSONArray
                                val jsonObjects = JSONObject(responseBody)
                                val statusCode = jsonObjects.getInt("statusCode")

                                if (statusCode == 200) {
                                    val message = jsonObjects.getString("message")
                                    Log.d(TAG, "message = $message")
                                    Toast.makeText(p0!!.context, message, Toast.LENGTH_LONG).show()

                                    if (bookmarkObjects.isNotEmpty())
                                        bookmarkObjects.remove(bookmarkObjects[holder.adapterPosition])
                                    notifyDataSetChanged()

                                } else {
                                    val errorMessage = jsonObjects.getString("message")
                                    Log.d(TAG, "error message = $errorMessage")
                                    Toast.makeText(p0!!.context, errorMessage, Toast.LENGTH_LONG).show()
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
    }

    override fun getItemCount(): Int {
        return bookmarkObjects.size
    }

    interface OnItemClickListener {
        fun onClick(
            promise_id: Long,
            addressObject: AddressObject,
            promise_name: String,
            promise_date: String,
            position: Int,
        )
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener: OnItemClickListener
}