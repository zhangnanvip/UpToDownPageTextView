package com.zn.flashview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val data: List<String> = arrayListOf("111", "222", "333", "444", "555", "666")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fv_test.initData(data)
        fv_test.onItemClickListener = object : FlashView.OnItemClickListener {
            override fun onItemClick(position: Int) {
                Toast.makeText(this@MainActivity, fv_test.data[position], Toast.LENGTH_SHORT).show()
            }
        }

        fl_test.viewLoader = object : FlashLayout.ViewLoader {
            override fun loadView(position: Int): View {
                val tv = TextView(this@MainActivity)
                tv.text = data[position]
                tv.gravity = Gravity.CENTER
                return tv
            }
        }
        fl_test.childNumber = data.size
        fl_test.onItemClickListener = object : FlashLayout.OnItemClickListener {
            override fun onItemClick(position: Int) {
                Toast.makeText(this@MainActivity, fv_test.data[position], Toast.LENGTH_SHORT).show()
            }
        }
    }
}
