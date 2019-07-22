package com.wahdanz.testbridschallenge.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

open class RecyclerAdapter<ITEM>(
    private var itemList: List<ITEM>,
    private val layoutResIds: Array<Int>,
    private val bindHolder: View.(ITEM) -> Unit,
    private var itemClick: ITEM.() -> Unit = {}

) :
    RecyclerView.Adapter<RecyclerAdapter.Holder>() {

    override fun getItemCount() = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = parent inflate layoutResIds[viewType]
        val viewHolder = Holder(view)
        val itemView = viewHolder.itemView
        itemView.setOnClickListener {
            val adapterPosition = viewHolder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onItemClick(itemView, adapterPosition)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemView.bindHolder(itemList[position])
    }

    fun addAll(item: List<ITEM>) {
        itemList = itemList.plus(item)
        notifyDataSetChanged()
    }

    fun add(item: ITEM) {
        itemList = itemList.plus(item)
        notifyItemInserted(itemList.size)
    }

    fun remove(position: Int) {
        itemList = itemList.minus(itemList[position])
        notifyItemRemoved(position)
    }

    protected open fun onItemClick(itemView: View, position: Int) {
        itemList[position].itemClick()
    }

    fun reset() {
        itemList = listOf()
        notifyDataSetChanged()
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

fun <ITEM> RecyclerView.setUp(
    items: List<ITEM>,
    layoutResId: Array<Int>,
    bindHolder: View.(ITEM) -> Unit,
    itemClick: ITEM.() -> Unit = {},
    loadMore: (totalCount: Int) -> Unit = {},
    manager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)
): RecyclerAdapter<ITEM> {
    val recyclerAdapter by lazy {
        RecyclerAdapter(
            itemList = items.toMutableList(),
            layoutResIds = layoutResId,
            bindHolder = bindHolder,
            itemClick = itemClick
        )
    }
    adapter = recyclerAdapter
    layoutManager = manager
    if (manager is LinearLayoutManager)
        loadMore(manager, loadMore)
    return recyclerAdapter
}

fun RecyclerView.loadMore(
    manager: LinearLayoutManager,
    loadMore: (totalCount: Int) -> Unit
) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val totalItemCount = recyclerView.layoutManager?.itemCount
            val current = manager.findLastVisibleItemPosition() + 1
            if (totalItemCount == current)
                loadMore(totalItemCount)
        }
    })
}

infix fun ViewGroup.inflate(layoutResId: Int): View =
    LayoutInflater.from(context).inflate(layoutResId, this, false)
