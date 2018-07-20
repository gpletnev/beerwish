package com.example.beerwish.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beerwish.App
import com.example.beerwish.data.remote.model.Checkin
import com.example.beerwish.databinding.ListItemCheckinBinding


class CheckinAdapter : ListAdapter<Checkin, CheckinAdapter.ViewHolder>(CheckinDiffCallback()) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val checkin = getItem(position)
        holder.apply {
            bind(createOnClickListener(position), checkin)
            itemView.tag = checkin
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemCheckinBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    private fun createOnClickListener(position: Int): View.OnClickListener {
        return View.OnClickListener {
            getItem(position).let {
                startActivity(App.instance,
                        Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.untappd.com/user/${it.user.user_name}/checkin/${it.checkin_id}")), null)
            }
        }
    }

    class ViewHolder(
            private val binding: ListItemCheckinBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: Checkin) {
            binding.apply {
                clickListener = listener
                checkin = item
                executePendingBindings()
            }
        }
    }
}

class CheckinDiffCallback : DiffUtil.ItemCallback<Checkin>() {
    override fun areItemsTheSame(p0: Checkin, p1: Checkin): Boolean {
        return p0.checkin_id == p1.checkin_id
    }

    override fun areContentsTheSame(p0: Checkin, p1: Checkin): Boolean {
        return p0 == p1
    }

}