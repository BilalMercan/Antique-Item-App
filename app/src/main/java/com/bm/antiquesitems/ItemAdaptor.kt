package com.bm.antiquesitems

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bm.antiquesitems.databinding.RecyclerrowBinding

class ItemAdaptor (val antiqueList : ArrayList<Antique>): RecyclerView.Adapter<ItemAdaptor.ArtHolder>() {

    class ArtHolder(val binding : RecyclerrowBinding) : RecyclerView.ViewHolder(binding.root){


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtHolder {
        val binding = RecyclerrowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ArtHolder(binding)

    }

    override fun onBindViewHolder(holder: ArtHolder, position: Int) {
        holder.binding.rcviewtxt.text = antiqueList.get(position).name
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,ItemsDetail::class.java)
            intent.putExtra("info","old")
            intent.putExtra("id",antiqueList[position].id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
    return antiqueList.size
    }


}