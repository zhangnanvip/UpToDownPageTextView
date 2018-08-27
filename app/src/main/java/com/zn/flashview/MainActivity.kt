package com.zn.flashview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
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

        btn_update.setOnClickListener { fv_test.updateData(data) }

        fl_test.adapter = object : FlashAdapter() {
            override fun loadView(position: Int, parent: ViewGroup): View {
                val tv = TextView(this@MainActivity)
                tv.text = data[position]
                tv.gravity = Gravity.CENTER
                return tv
            }

            override fun getCount(): Int = data.size
        }
        fl_test.onItemClickListener = object : FlashLayout.OnItemClickListener {
            override fun onItemClick(position: Int) {
                Toast.makeText(this@MainActivity, fv_test.data[position], Toast.LENGTH_SHORT).show()
            }
        }
    }
}
