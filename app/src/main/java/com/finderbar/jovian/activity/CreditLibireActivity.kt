package com.finderbar.jovian.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.finderbar.jovian.R
import com.finderbar.jovian.prefs
import com.finderbar.jovian.viewmodels.user.LoginVM
import es.dmoral.toasty.Toasty

class CreditLibireActivity: AppCompatActivity() {
    private var mToolbar: Toolbar? = null
    private var loginVM: LoginVM? = null
    private lateinit var dialog: ACProgressFlower
    private lateinit var notifyMenuItem: MenuItem
    private lateinit var badgeView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_libraries)
        loginVM = ViewModelProviders.of(this).get(LoginVM::class.java)

        mToolbar = findViewById(R.id.toolbar)
        mToolbar!!.title = "Credit Libraries"
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .fadeColor(Color.LTGRAY).build()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.other_menu, menu)

        notifyMenuItem = menu.findItem(R.id.notify)
        val actionView = MenuItemCompat.getActionView(notifyMenuItem);
        badgeView = actionView.findViewById(R.id.badgeCount)
        notifyMenuItem.actionView.setOnClickListener {
            startActivity(Intent(this@CreditLibireActivity, NotificationActivity::class.java))
        }

        setupBadge();

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.exit -> finish();
            R.id.logout -> {
                dialog.show()
                loginVM?.logOutUser(prefs.userId, prefs.authToken)
                loginVM?.logoutMessage?.observe(this, Observer {
                    dialog.dismiss()
                    Toasty.success(this, it!!, Toast.LENGTH_SHORT, true).show();
                    startActivity(Intent(this@CreditLibireActivity, SignInActivity::class.java))
                    finish()
                })

                loginVM?.errorMessage?.observe(this, Observer {
                    dialog.dismiss()
                    Toasty.error(this, it!!.message, Toast.LENGTH_SHORT, true).show();
                })
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupBadge() {
        val badgeCount = prefs.menuBadgeCount.toInt()
        if (badgeCount > 0) {
            badgeView.text = prefs.menuBadgeCount
            badgeView.visibility = View.VISIBLE
        } else {
            badgeView.visibility = View.GONE
        }
    }
}