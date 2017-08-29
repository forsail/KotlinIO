package com.lee.asher.kotlinio.http

import com.lee.asher.kotlinio.bean.HomeBean
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Created by lihong on 2017/8/21.
 */
interface ApiService {

    companion object {
        val base_url: String
            get() = "http://baobab.kaiyanapp.com/api/"
    }

    //获取首页第一页数据
    @GET("v2/feed?num=2&udid=26868b32e808498db32fd51fb422d00175e179df&vc=83")
    fun getHomeData(): Observable<HomeBean>
}