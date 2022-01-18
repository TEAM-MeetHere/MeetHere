package com.choitaek.meethere.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.choitaek.meethere.objects.AddressObject
import com.choitaek.meethere.databinding.ItemAddressBinding
import kotlinx.android.synthetic.main.item_address.view.*


class AddressAdapter(
    private val addressObjects: MutableList<AddressObject>     // public
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    class AddressViewHolder(val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val btn2 = binding.btnDeleteAddress
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    /*   private fun toggleStrikeThrough(textViewAddress: TextView, textViewName: TextView) {

       }*/
    fun addAddress(addressObject: AddressObject) {
        addressObjects.add(addressObject)
        notifyItemInserted(addressObjects.size - 1)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val curAddress = addressObjects[position]
        holder.itemView.apply {
            textViewAddress.text = curAddress.road_address_name
            textViewName.text = curAddress.user_name
        }

        holder.btn2.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (addressObjects.isNotEmpty())
                    addressObjects.remove(addressObjects[holder.adapterPosition])
                notifyDataSetChanged()
            }
        })
    }

    override fun getItemCount(): Int {
        return addressObjects.size
    }

    fun getData(): MutableList<AddressObject> {
        return addressObjects
    }
}