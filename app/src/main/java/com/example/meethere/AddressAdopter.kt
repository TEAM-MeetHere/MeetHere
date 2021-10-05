package com.example.meethere

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_address.view.*


class AddressAdopter (
    private val addresses: MutableList<Address>
) : RecyclerView.Adapter<AddressAdopter.AddressViewHolder>() {
    class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_address,
                parent,
                false
            )
        )
    }

    /*   private fun toggleStrikeThrough(textViewAddress: TextView, textViewName: TextView) {

       }*/
    fun addAddress(address: Address) {
        addresses.add(address)
        notifyItemInserted(addresses.size - 1)
    }
    fun deleteAddress() {
        /*addresses.removeAll { Address ->

        }*/
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AddressAdopter.AddressViewHolder, position: Int) {
        val curAddress = addresses[position]
        holder.itemView.apply {
            textViewAddress.text = curAddress.Address
            textViewName.text = curAddress.Name
        }
    }

    override fun getItemCount(): Int {
        return addresses.size
    }
}