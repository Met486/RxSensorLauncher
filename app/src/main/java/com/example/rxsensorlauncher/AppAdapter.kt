package com.example.rxsensorlauncher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class AppAdapter(
    private val inflater: LayoutInflater,
    private val list: List<AppInfo>,
    private val onClick: (view: View, info: AppInfo) -> Unit
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder =
        AppViewHolder(inflater.inflate(R.layout.li_application, parent, false))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val info = list[position]
        holder.itemView.setOnClickListener { onClick(it, info) }
        holder.icon.setImageDrawable(info.icon)
        holder.label.text = info.label
        holder.packageName.text = info.componentName.packageName
    }

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val label: TextView = itemView.findViewById(R.id.label)
        val packageName: TextView = itemView.findViewById(R.id.packageName)
    }
}