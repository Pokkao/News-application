package com.example.newsapplication.module.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.newsapplication.model.ArticlesItem
import com.example.newsapplication.model.ResponseNews
import kotlinx.android.synthetic.main.fragment_page_news.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.support.v4.app.FragmentActivity
import android.os.Parcelable
import com.example.newsapplication.module.adapter.AdapterRecycle
import com.example.newsapplication.R
import com.example.newsapplication.https.NewsRetofit
import com.example.newsapplication.module.activity.MainActivity
import android.util.Log
import com.example.newsapplication.constants.NewsConstants


class PageNewsFragment : Fragment() {

    private var Pagefm : PageNewsFragment?=null
    private var fragmain: PageNewsFragment?=null
    lateinit var activityMain: MainActivity
    private var myContext: FragmentActivity? = null
    var recyclerViewState: Parcelable? = null
    private var status : String?=null

    private var myFavoritePage :myfavoriteFragment? = null

    var arrayList: ArrayList<ArticlesItem> = arrayListOf()
    var listCurrentSelect: ArrayList<ArticlesItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMain = activity as MainActivity
        Pagefm = PageNewsFragment()
        myContext = FragmentActivity()

        status = NewsConstants.NEWS_STATUS_MAIN

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_page_news, container, false)
    }

//    override fun onAttach(activity: Activity?) {
//        myContext = activity as FragmentActivity?
//        super.onAttach(activity)
//    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTitle()
        loadJson()



    }

    private fun setupTitle() {
        activityMain.setupTitle("News Application")
    }


    private fun loadJson() {

        object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(1000)
                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://newsapi.org/v2/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val request = retrofit.create(NewsRetofit::class.java)
                    val call = request.getJSON()

                    call.enqueue(object : Callback<ResponseNews> {
                        override fun onResponse(call: Call<ResponseNews>, response: Response<ResponseNews>) {
//                          val jsonResponse = response.body()?.getEmployeeArrayList()
                            initView(response.body()?.getNewsArrayList(),activityMain,status)  //articles

                            pg_load_json.visibility = View.INVISIBLE

                        }

                        override fun onFailure(call: Call<ResponseNews>, t: Throwable) {
                            Toast.makeText(activityMain, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show()
                        }
                    })

                } catch (e: Exception) {
                    Log.e("tags", e.message)
                }

            }
        }.start()


    }

    fun initView(
        NewsArrayList: ArrayList<ArticlesItem>?,
        activityMain: MainActivity,
        pagenew: String?
    ) {
        recycler_view.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter =
            AdapterRecycle(NewsArrayList, activityMain, pagenew
                ,{ itemAdd ->
                    myFavorit(itemAdd)
                })

    }

    private fun myFavorit(itemAdd: ArticlesItem) {
         arrayList.add(itemAdd)

        activityMain.keepFavorite(arrayList)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        recyclerViewState = recycler_view.layoutManager!!.onSaveInstanceState()

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

//        recycler_view.layoutManager!!.onRestoreInstanceState(recyclerViewState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            // Restore last state for checked position.
//            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0)
            recycler_view.itemAnimator = null
        }
    }

    companion object {
        fun newInstance(): PageNewsFragment {
            val fm = PageNewsFragment()
            val bundle = Bundle()

            fm.arguments = bundle
            return fm
        }

    }


}
