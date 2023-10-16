package com.finderbar.jovian.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.finderbar.jovian.R

class JobDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_detail);
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }
}