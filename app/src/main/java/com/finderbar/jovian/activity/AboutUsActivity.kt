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
import com.finderbar.jovian.utilities.markdown.setMarkdown
import es.dmoral.toasty.Toasty

/**
 * Created by thein on 1/14/19.
 */
class AboutUsActivity: AppCompatActivity() {
    private var mToolbar: Toolbar? = null
    private var loginVM: LoginVM? = null
    private lateinit var dialog: ACProgressFlower
    private lateinit var notifyMenuItem: MenuItem
    private lateinit var badgeView: TextView
    private var txtAboutUs: TextView ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        loginVM = ViewModelProviders.of(this).get(LoginVM::class.java)
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        txtAboutUs = findViewById(R.id.txtAboutUs)

        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .fadeColor(Color.LTGRAY).build()

        val p = "My name is Thein Lwin Htun and my native town is Kyauk Taw city, Rakhine state. Now, I’ve been working as a developer nearly 9 years. During 9 year experiences, I’m responsible, android, window and web application independently and cooperating with the teams.<br><br>";
        val p1 = "I’ve started my first carrier as a typing writer and web designer for 2 years. And also I’ve worked for 4 to 5 years with Java. Finally the Elixir, NodeJS and BlockChain technology goes with me.<br><br>";
        val p2 = "I’ve got some difficulties and barriers in self-study while working on the other hand. At that time, I had to take the help of the Google, StackOverflow and Medium. Later on, I’ve got an idea to create a place where we can study and discuss in  Myanmar.<br><br>";
        val p3 = "So, I start created **“FinderBar”** application at my free time and public holiday like Saturday and Sunday with my work on the other hand. In 2014, I’ve released my own application “finderbar” and suspended for a while because of my financial problems.<br><br>";
        val p4 = "But I’ve been still working on that application with my strong spirit. In these years, I work as a freelancer and take responsible for mobile, window and web but I select the language to accept the Job.<br><br>";
        val p5 = "My objective is to be able to provide the people not only from IT field but also others. I’ve passed difficulties and still going on till this time.<br><br>";
                "I would like to invite the other people to cooperate with me without the discrimination of race and religion who love and crazy on IT. Now, I need a developer to help me for IOS.<br><br>";
        val p6 = " If there is someone or some communities to help my **“FinderBar”** application, I warmly welcome to create better saturation. Let’s do our best. We can do it. Thank you for your time.<br><br>";
        val aboutUs = p.plus(p1).plus(p2).plus(p3).plus(p4).plus(p5).plus(p6);
        txtAboutUs!!.setMarkdown(aboutUs)

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
            startActivity(Intent(this@AboutUsActivity, NotificationActivity::class.java))
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
                    startActivity(Intent(this@AboutUsActivity, SignInActivity::class.java))
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