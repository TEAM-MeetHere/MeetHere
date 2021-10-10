package com.example.meethere

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.meethere.databinding.ItemAddressBinding
import kotlinx.android.synthetic.main.item_address.view.*


class AddressAdopter(
    private val addresses: MutableList<Address>
) : RecyclerView.Adapter<AddressAdopter.AddressViewHolder>() {
    class AddressViewHolder(val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val btn2 = binding.btnDeleteAddress

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        /*  return AddressViewHolder(
              LayoutInflater.from(parent.context).inflate(
                  R.layout.item_address,
                  parent,
                  false
              )
          )*/
        return AddressViewHolder(binding)
    }

    /*   private fun toggleStrikeThrough(textViewAddress: TextView, textViewName: TextView) {

       }*/
    fun addAddress(address: Address) {
        addresses.add(address)
        notifyItemInserted(addresses.size - 1)
    }

    override fun onBindViewHolder(holder: AddressAdopter.AddressViewHolder, position: Int) {
        val curAddress = addresses[position]
        holder.itemView.apply {
            textViewAddress.text = curAddress.Address
            textViewName.text = curAddress.Name
        }

        holder.btn2.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (addresses.isNotEmpty())
                    addresses.remove(addresses[holder.adapterPosition])
                notifyDataSetChanged()
            }
        })
    }

    override fun getItemCount(): Int {
        return addresses.size
    }
}