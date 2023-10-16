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
import com.finderbar.jovian.R
import com.finderbar.jovian.viewmodels.user.LoginVM
import android.widget.ExpandableListView
import android.widget.TextView
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.finderbar.jovian.adaptor.PrivacyPolicyAdapter
import com.finderbar.jovian.prefs
import es.dmoral.toasty.Toasty


/**
 * Created by thein on 1/14/19.
 */
class PrivacyPolicyActivity: AppCompatActivity() {

    private var mToolbar: Toolbar? = null
    private var loginVM: LoginVM? = null
    private var listAdapter: PrivacyPolicyAdapter? = null
    private var expandableListView: ExpandableListView? = null
    private var listDataHeader: List<String>? = null
    private lateinit var dialog: ACProgressFlower
    private lateinit var notiMenuItem: MenuItem
    private lateinit var badgeView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        loginVM = ViewModelProviders.of(this).get(LoginVM::class.java)

        mToolbar = findViewById(R.id.toolbar)
        mToolbar!!.title = "Privacy Policy"
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .fadeColor(Color.LTGRAY).build()

        expandableListView = findViewById(R.id.lvExp)
        if (expandableListView != null) {
            val listData = data
            listDataHeader = ArrayList(listData.keys)
            listAdapter = PrivacyPolicyAdapter(this, listDataHeader as ArrayList<String>, listData)
            expandableListView!!.setAdapter(listAdapter)

            expandableListView!!.setOnGroupExpandListener { groupPosition -> Toast.makeText(applicationContext, (listDataHeader as ArrayList<String>)[groupPosition] + " List Expanded.", Toast.LENGTH_SHORT).show() }

            expandableListView!!.setOnGroupCollapseListener { groupPosition -> Toast.makeText(applicationContext, (listDataHeader as ArrayList<String>)[groupPosition] + " List Collapsed.", Toast.LENGTH_SHORT).show() }

            expandableListView!!.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
                Toast.makeText(applicationContext, "Clicked: " + (listDataHeader as ArrayList<String>)[groupPosition] + " -> " + listData[(listDataHeader as ArrayList<String>)[groupPosition]]!!.get(childPosition), Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    val data: HashMap<String, List<String>>
        get() {
            val listData = HashMap<String, List<String>>()

            val answering = ArrayList<String>()
            answering.add("FinderBar ေပၚရွိ သင္ခန္းစာမ်ားအား မည္သူမဆို အခမဲ့ လြတ္လပ္စြာ ဖတ္ရႈ႕ႏိုင္သည္။")
            answering.add("Tutorial မ်ားမွ သင္ခန္းစာ project မ်ားအား လက္ေတြ႕လုပ္ေဆာင္၍ ၎ မိမိကုိယ္ပိုင္ Project \n" +
                    "\t\t\t\tမ်ားကို FinderBar၊ မူရင္း သင္ခန္းစာေရးသားသူ ႏွင့္ www.finderbar.com \n" +
                    "\t\t\t\tတို႔ အား အျပည့္အစံု Credit ေပး၍ public သို႔ အခမဲ့ ျဖင့္ လြတ္လပ္စြာ ျဖန္႔ေဝႏုိင္သည္။")
            answering.add("FinderBar ေပၚရွိ သင္ခန္းစာမ်ား၏ မူပိုင္ခြင့္သည္ FinderBar \n" +
                    "\t\t\t\tတြင္ရွိေသာေၾကာင့္ သင္ခန္းစာမ်ားအား မိမိသေဘာဆႏၵအေလ်ာက္ မရိုးမေျဖာင့္ေသာ သေဘာျဖင့္ ျပင္ဆင္ျခင္း၊ \n" +
                    "\t\t\t\tသံတူေၾကာင္းကဲြျပဳလုပ္ျခင္း၊ အျခားမည္သည့္နည္းလမ္းျဖင့္ တုပ၍\n" +
                    "\t\t\t\t မိမိကိုယ္ပိုင္အျဖစ္ျဖင့္ အခမဲ့ေသာ္လည္းေကာင္း၊ အခေၾကးေငြျဖင့္ေသာ္လည္း ျဖန္႔ေဝျခင္း မျပဳလုပ္ရ။ \n" +
                    "\t\t\t\tထိုသို႔ျပဳလုပ္ပါက တည္ဆဲဥပေဒအရ အေရးယူျခင္း ခံရမည္ျဖစ္သည္။")

            val tutorial = ArrayList<String>()
            tutorial.add("FinderBar တြင္ Programming ႏွင့္ သက္ဆိုင္ေသာ ေမးခြန္းမ်ား၊ Networking ႏွင့္ သက္ဆိုင္ေသာ ေမးခြန္းမ်ား၊\n" +
                    "\t\t\t\t Computer သို႔မဟုတ္ Mobile Phone Services မ်ားႏွင့္ သက္ဆိုင္ေသာ ေမးခြန္း မ်ား ႏွင့္ အျခားမည္သည့္ နည္းပညာႏွင့္ သက္ဆိုင္ေသာ ေမးခြန္းမ်ားအား\n" +
                    "\t\t\t\t Facebook account ျဖင့္ေသာ္လည္းေကာင္း၊ Google+ account ျဖင့္ေသာ္လည္းေကာင္း၊\n" +
                    "\t\t\t\t Gmail account ျဖင့္ေသာ္လည္းေကာင္း Register ျပဳလုပ္၍ ေမးခြန္းအေရအတြက္ အကန္႔အသတ္မရွိ လြတ္လပ္စြာ ေမးျမန္းႏုိင္သည္။")
            tutorial.add("ေမးခြန္းမ်ားေမးျမန္းရာတြင္ နည္းပညာ ပိုင္းဆိုင္ရာမဟုတ္ေသာ ေမးခြန္းမ်ား၊ ညစ္ညမ္းစာမ်ား၊ ေၾကာ္ျငာမ်ား၊ တမင္ စိတ္ညစ္ႏြမ္းေစရန္ ရည္ရြယ္ေသာေမးခြန္းမ်ား၊\n" +
                    "\t\t\t\t လူ႔ပတ္ဝန္းက်င္ထိခုိက္ေစေသာ စာမ်ား၊ ႏိုင္ငံေရးမ်ား၊ ႏိုင္ငံေတာ္ အၾကည္ညိဳပ်က္ေစေသာ စာမ်ား၊ ဘာသာေရး ႏွင့္ လူ႕အသင္းအဖဲြ႕ ပဋိပကၡျဖစ္ေစရန္ၾကံရြယ္ေသာ\n" +
                    "\t\t\t\t  စာမ်ားအား ေမးျမန္းျခင္း၊ ေရးသားျခင္းမျပဳရ။ အထက္ပါ စည္းကမ္းခ်က္ (၂) အား ေဖာက္ဖ်က္ပါက တည္ဆဲဥပေဒအရ ျပင္းျပင္းထန္ထန္ အေရးယူျခင္းခံရမည္ျဖစ္သည္။")
            tutorial.add("ေအာက္ပါ ေမးခြန္းအမ်ဳိးအစားမ်ားအား ေျဖၾကားျခင္း၊ Comment ေပးျခင္းမ်ား ျပဳလုပ္မည္မဟုတ္ဘဲ ေျဖၾကားခြင့္ႏွင့္ Comment\n" +
                    "\t\t\t\t  ေပးခြင့္ ပိတ္ထားမည္ျဖစ္သည္။ ေအာက္ပါေမးခြန္းအမ်ဳိးအစား ေမးျမန္းေသာ Auth သည္ Warning ေပးခံရမည္ျဖစ္ၿပီး သတ္မွတ္ထားေသာ Warning\n" +
                    "\t\t\t\t   အၾကိမ္အေရတြက္ ေရာက္ရွိပါက FinderBar Member အျဖစ္မွ ရပ္စဲမည္ျဖစ္သည္။")

            val asking = ArrayList<String>()
            asking.add("ေမးခြန္းေျဖဆိုလိုသူမ်ားသည္ FinderBar တြင္ account ျပဳလုပ္၍ မည္သည့္ေမးခြန္းမဆို အကန္႔အသတ္မရွိ မိမိ ကၽြမ္းက်င္ သုိ႔မဟုတ္ နားလည္သလို ေျဖဆိုႏိုင္ပါသည္။")
            asking.add("မိမိအေျဖအား ေမးျမန္းသူမ်ား၊ ၾကည့္ရႈ႕သူမ်ားမွ ႀကိဳက္ႏွစ္သက္ပါက ၎တို႔မွ ေပးေသာ Vote အား ရရွိမည္ျဖစ္သည္။")
            asking.add("vote အမ်ားဆံုး ရရွိ သူမ်ားသည္ FinderBar မွ အခါအားေလ်ာ္စြာ ျပဳလုပ္မည့္ နည္းပညာ ေဆြးေႏြးပဲြမ်ား၊ ႏွီးေႏွာဖလွယ္ပဲြမ်ားအား VIP အျဖစ္ တက္ေရာက္ခြင့္ရမည္ျဖစ္ၿပီး FinderBar ႏွင့္ လက္တဲြ အလုပ္လုပ္ရန္ အခြင့္အလမ္းမ်ား ရရွိမည္ျဖစ္သည္။")
            asking.add("ေမးခြန္းမ်ား၊ အေျဖမ်ား မရွင္းမလင္း ျဖစ္ပါက ေမးခြန္းေမးသူ သို႔မဟုတ္ အေျဖေပးသူ သို႔ ၎၏ ေမးခြန္း သို႔မဟုတ္ အေျဖ ေအာက္တြင္ ကြန္မန္႔ေပး၍ အကန္႔အသတ္မရွိ ေမးျမန္းႏုိင္သည္။")
            asking.add("မိမိအေျဖ၏ တာဝန္မွာ မိမိကိုယ္တိုင္သာျဖစ္၍ ေပၚေပါက္လာေသာ ျပႆနာ တစ္စံုတစ္ရာသည္ ေျဖဆိုသူတြင္သာ တာဝန္က်ေရာက္ၿပီး FinderBar ႏွင့္ လံုးဝသက္ဆိုင္ျခင္းမရွိသကဲ့သုိ႔ FinderBar မွလည္း မည္သို႔မွ တာဝန္ယူ ေျဖရွင္းေပးမည္မဟုတ္ပါ။")
            asking.add("အေျဖ ေျဖဆိုျခင္းႏွင့္ ကြန္မန္႔ေပးရာတြင္ ေမးခြန္းႏွင့္ မသက္ဆိုင္ေသာ အေျဖမ်ား၊ Website Link မ်ား၊ ညစ္ညမ္းစာမ်ား၊ ေၾကာ္ျငာမ်ား၊ တမင္ စိတ္ညစ္ႏြမ္းေစရန္ ရည္ရြယ္ေသာအေျဖမ်ား၊\n" +
                    " \t\t\t\tလူ႔ပတ္ဝန္းက်င္ထိခုိက္ေစေသာ စာမ်ား၊ ႏိုင္ငံေရးမ်ား၊ ႏိုင္ငံေတာ္ အၾကည္ညိဳပ်က္ေစေသာ စာမ်ား၊ ဘာသာေရး ႏွင့္ လူ႕အသင္းအဖဲြ႕ ပဋိပကၡျဖစ္ေစရန္ၾကံရြယ္ေသာ စာမ်ားအား ေျဖဆိုေရးသားခြင့္ လံုးဝ(လံုးဝ) ခြင့္မျပဳပါ။")
            asking.add("အထက္ပါ စည္းကမ္းခ်က္ (၂) အား ေဖာက္ဖ်က္ပါက တည္ဆဲဥပေဒအရ ျပင္းျပင္းထန္ထန္ အေရးယူျခင္းခံရမည္ျဖစ္သည္။")


            listData["သင္ခန္းစာမ်ားႏွင့္ သက္ဆိုင္ေသာ စည္းကမ္းခ်က္မ်ား "] = answering
            listData["ေမးခြန္းေမးျမန္းျခင္းဆိုင္ရာ စည္းကမ္းခ်က္မ်ား"] = tutorial
            listData["ေမးခြန္းေျဖၾကားျခင္း ႏွင့္ ကြန္မန္႔ေပးျခင္းဆိုင္ရာ စည္းကမ္းမ်ား"] = asking

            return listData
        }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.other_menu, menu)

        notiMenuItem = menu.findItem(R.id.notify)
        val actionView = MenuItemCompat.getActionView(notiMenuItem);
        badgeView = actionView.findViewById(R.id.badgeCount)
        notiMenuItem.actionView.setOnClickListener {
            startActivity(Intent(this@PrivacyPolicyActivity, NotificationActivity::class.java))
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
                    startActivity(Intent(this@PrivacyPolicyActivity, SignInActivity::class.java))
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