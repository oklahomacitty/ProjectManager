package com.example.projectmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectmanager.R
import com.example.projectmanager.databinding.ItemBoardBinding
import com.example.projectmanager.models.Board

open class BoardItemsAdapter(private val context: Context, private var boardList: ArrayList<Board>) :
    RecyclerView.Adapter<BoardItemsAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    class ViewHolder(private val itemBinding: ItemBoardBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(board: Board, context: Context) {
            Glide
                .with(context)
                .load(board.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(itemBinding.ivBoardImage)

            itemBinding.tvName.text = board.name
            itemBinding.tvCreatedBy.text = buildString {
                append("Created by: ")
                append(board.createdBy)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBoardBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val board = boardList[position]
        holder.bindItem(board, context)
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, board)
            }
        }
    }

    override fun getItemCount(): Int {
       return boardList.size
    }

    interface OnClickListener {
        fun onClick(position: Int, board: Board)
    }

    fun setOnCLickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

}