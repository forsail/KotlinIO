package com.lee.asher.kotlinio

import android.graphics.Rect
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lee.asher.kotlinio.HomeBean.IssueListBean.ItemListBean
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.dip
import java.util.*

class MainActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    var list = ArrayList<ItemListBean>()
    private var adapter = VideoAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestData()
    }

    override fun getLayoutResources(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        refreshLayout.setColorSchemeResources(R.color.colorPrimary)
        refreshLayout.setOnRefreshListener(this@MainActivity)

        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.addItemDecoration(MyItemDecoration())
    }

    fun requestData() {
        val retrofitClient = RetrofitClient.getInstance(this, ApiService.base_url)
        val apiService = retrofitClient.create(ApiService::class.java)

        var observable: Observable<HomeBean>? = apiService?.getHomeData()
        observable?.applyScheduleres()?.subscribe(object : Observer<HomeBean> {
            override fun onSubscribe(d: Disposable?) {
            }

            override fun onNext(t: HomeBean?) {
                println("onNext")
                val oldData = list
                list = t?.issueList!!
                        .flatMap { it.itemList!! }
                        .filter { it.type.equals("video") } as ArrayList<ItemListBean>
//                        .forEach { list.add(it) }
                val size = list.size
                Log.d("asher", "$size")
                if (recyclerView.adapter == null) {
                    recyclerView.adapter = adapter
                } else {
                    val diffUtil = DiffUtil.calculateDiff(DiffCallback(oldData, list))
                    diffUtil.dispatchUpdatesTo(adapter)
                }
            }

            override fun onError(e: Throwable?) {
                refreshLayout.isRefreshing = false
                println("onError")
                println(e?.printStackTrace())
            }

            override fun onComplete() {
                refreshLayout.isRefreshing = false
                println("onComplete")
            }
        })
    }

    override fun onRefresh() {
        requestData()
    }

    internal inner class VideoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_video_item, parent, false)
            val viewHolder = ViewHolder(view)
            return viewHolder
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = list[position]
            val viewHolder = holder as ViewHolder
            Glide.with(this@MainActivity).load(item.data?.cover?.detail).fitCenter().into(viewHolder.videoImg)
            viewHolder.title?.text = item.data?.title
        }

        override fun getItemCount(): Int {
            return list.size
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var videoImg: ImageView? = null
            var title: TextView? = null

            init {
                videoImg = view.findViewById(R.id.video_img) as ImageView
                title = view.findViewById(R.id.title) as TextView
            }
        }
    }

    inner class DiffCallback(val oldData: List<ItemListBean>?, val newData: List<ItemListBean>?) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return true
        }

        override fun getOldListSize(): Int {
            return oldData?.size ?: 0
        }

        override fun getNewListSize(): Int {
            return newData?.size ?: 0
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldData?.get(oldItemPosition)
            val newItem = newData?.get(newItemPosition)
            return (oldItem?.data?.cover?.detail == newItem?.data?.cover?.detail)
                    .and(oldItem?.data?.title == newItem?.data?.title)
        }
    }

    inner class MyItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
            if (parent?.layoutManager?.getPosition(view) != (list.size - 1)) {
                outRect?.set(0, 0, 0, this@MainActivity.dip(10))
            }
        }
    }
}
