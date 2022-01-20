package com.example.quote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.absoluteValue


//https://firebase.google.com/docs/remote-config?hl=ko  firebase 원격 구성
class MainActivity : AppCompatActivity() {

    private val viewPager: ViewPager2 by lazy {
        findViewById(R.id.viewPager)
    }

    private val progressBar: ProgressBar by lazy{
        findViewById(R.id.progressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initData()
    }

    private fun initViews(){
        viewPager.setPageTransformer { page, position ->
            when{
                position.absoluteValue >= 1.0F -> {
                    page.alpha = 0F
                }
                position.absoluteValue == 0F -> {
                    page.alpha = 1.0F
                }
                else -> {
                    page.alpha = 1F - 2 * position.absoluteValue
                }
            }
        }
    }

    private fun initData() {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync( //비동기로 셋팅
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0 //서버에서 블록하지 않는 이상은 앱을 draw할 때마다 곧바로 fetch가 됨
            }
        )

        //fetch 자체가 비동기로 이루어지기 때문에 리스너를 따로 해줘야 한다.
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            progressBar.visibility = View.GONE
            if (it.isSuccessful) {  //작업이 성공한다면
                val quotes = parseQuotesJson(remoteConfig.getString("quotes"))
                val isNameRevealed = remoteConfig.getBoolean("is_name_revealed")


                displayQuotesPager(quotes, isNameRevealed)
            }
        }
    }

    private fun parseQuotesJson(json: String): List<Quote> {
        val jsonArray = JSONArray(json)
        var jsonList = emptyList<JSONObject>() //JSONArray는 JSONObject로 구성되어있다고 생각하면 된다. 하나씩 가져와서 jsonList에 추가해준다.
        for(index in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(index)
            jsonObject?.let{
                jsonList = jsonList + it
            }
        }
        return jsonList.map {
            Quote(quote = it.getString("quote"), name = it.getString("name"))
        }
    }

    private fun displayQuotesPager(quotes: List<Quote>, isNameRevealed: Boolean){
        val adapter = QuotesPagerAdapter(
            quotes = quotes, isNameRevealed = isNameRevealed
        )
        viewPager.adapter = adapter
        viewPager.setCurrentItem(adapter.itemCount/2, false)
    }
}