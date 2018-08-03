package com.zn.flashview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fv_test.initData(arrayListOf("111", "222", "333", "444", "555", "666"))
        fv_test.onItemClickListener = object : FlashView.OnItemClickListener {
            override fun onItemClick(position: Int) {
                Toast.makeText(this@MainActivity, fv_test.data[position], Toast.LENGTH_SHORT).show()
            }
        }
    }
}
