package com.finderbar.jovian.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import com.finderbar.jovian.OnItemNotifyClick
import com.finderbar.jovian.R
import com.finderbar.jovian.adaptor.NotificationAdaptor
import com.finderbar.jovian.models.Notification
import com.finderbar.jovian.viewmodels.user.LoginVM
import com.finderbar.jovian.viewmodels.notification.NotificationVM


class NotificationActivity: AppCompatActivity(), OnItemNotifyClick {

    private var toolbar: Toolbar? = null;
    private var loginVM: LoginVM? = null;
    private var notificationVM: NotificationVM? = null;
    private var adaptor: NotificationAdaptor? = null;
    private var recyclerView: RecyclerView? = null;
    private var swipeRefreshLayout: SwipeRefreshLayout? = null;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification)
        loginVM = ViewModelProviders.of(this).get(LoginVM::class.java);
        notificationVM = ViewModelProviders.of(this).get(NotificationVM::class.java);

        toolbar = findViewById(R.id.toolbar)
        toolbar!!.title = "Notifications"
        setSupportActionBar(toolbar);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);


        adaptor = NotificationAdaptor(this)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.addItemDecoration(DividerItemDecoration(recyclerView!!.context, DividerItemDecoration.VERTICAL))
        recyclerView!!.adapter = adaptor
        recyclerView!!.setHasFixedSize(false)

        swipeRefreshLayout!!.setOnRefreshListener {
            notificationVM!!.refresh()
        }

        notificationVM!!.notificationList.observe(this, Observer { pagedList ->
            adaptor!!.submitList(pagedList)
            swipeRefreshLayout!!.setRefreshing(false);
        })

        notificationVM!!.loadingInitial.observe(this, Observer { loading ->
            val isRefreshing = loading == true
            if (swipeRefreshLayout!!.isRefreshing != isRefreshing) {
                swipeRefreshLayout!!.isRefreshing = isRefreshing
            }
        })

        notificationVM!!.loadingBefore.observe(this, Observer {
            adaptor!!.updateLoadingBefore(it == true)
        })

        notificationVM!!.loadingAfter.observe(this, Observer {
            adaptor!!.updateLoadingAfter(it == true)
        })
    }

    override fun onItemClick(notification: Notification) {
        val intent = Intent(this@NotificationActivity, DiscussActivity::class.java)
        intent.putExtra("discussId", notification.notifyId)
        startActivity(intent)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_notifications, menu)
        return true
    }
}

