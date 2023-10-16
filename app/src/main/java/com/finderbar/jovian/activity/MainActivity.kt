package com.finderbar.jovian.activity

import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.databinding.BindingAdapter
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.support.design.chip.Chip
import android.support.design.chip.ChipGroup
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.finderbar.jovian.*
import com.finderbar.jovian.adaptor.ViewPagerAdapter
import com.finderbar.jovian.fragments.discuss.QuestionFragment
import com.finderbar.jovian.fragments.job.JobFragment
import com.finderbar.jovian.fragments.post.PostFragment
import com.finderbar.jovian.fragments.tutorial.CategoryFragment
import com.finderbar.jovian.fragments.user.UsersFragment
import com.finderbar.jovian.viewmodels.user.LoginVM
import com.finderbar.jovian.services.NetworkChangeReceiver
import com.finderbar.jovian.utilities.AppConstants.MENU_SEARCH
import com.finderbar.jovian.utilities.AppConstants.NAV_ABOUT_US
import com.finderbar.jovian.utilities.AppConstants.NAV_CREDIT_LIB
import com.finderbar.jovian.utilities.AppConstants.NAV_HOME
import com.finderbar.jovian.utilities.AppConstants.MENU_QUESTIONS
import com.finderbar.jovian.utilities.AppConstants.AUTH_LOGOUT
import com.finderbar.jovian.utilities.AppConstants.LOCATION_AND_CONTACTS
import com.finderbar.jovian.utilities.AppConstants.MENU_POSTS
import com.finderbar.jovian.utilities.AppConstants.MENU_TUTORIALS
import com.finderbar.jovian.utilities.AppConstants.MENU_USERS
import com.finderbar.jovian.utilities.AppConstants.RC_CAMERA_PERM
import com.finderbar.jovian.utilities.AppConstants.RC_LOCATION_CONTACTS_PERM
import com.finderbar.jovian.utilities.AppConstants.RC_STORE_AGE_PERM
import com.finderbar.jovian.utilities.android.loadAvatar
import com.finderbar.jovian.utilities.android.loadLarge
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.context.IconicsContextWrapper
import es.dmoral.toasty.Toasty
import io.reactivex.disposables.CompositeDisposable
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity :  AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener, IFragmentListener, NetworkChangeReceiver.NetworkListener, EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks {

    companion object {
        @JvmStatic
        @BindingAdapter("thumbImage")
        fun loadImage(view: ImageView, thumbImage: String) {
            view.loadAvatar(Uri.parse(thumbImage))
        }

        @JvmStatic
        @BindingAdapter("bigImage")
        fun loadLargeImage(view: ImageView, bigImage: String) {
            view.loadLarge(Uri.parse(bigImage))
        }

        @JvmStatic
        @BindingAdapter("timeAgo")
        fun loadTimeAgo(view: TextView, timeAgo: String) {
            view.text = agoTimeUtil(timeAgo)
        }

        @JvmStatic
        @BindingAdapter("chips")
        fun loadTags(chipGroup: ChipGroup, chips: List<String>) {
            chipGroup.removeAllViews()
            repeat(chips.size) {
                var chip = Chip(chipGroup!!.context)
                chip.setChipBackgroundColorResource(R.color.pf_white)
                chip.setChipStrokeColorResource(R.color.pf_green)
                chip.setTextAppearanceResource(R.style.ChipTextStyle)
                chip.chipStrokeWidth = 1f
                chip.chipStartPadding = 20.0f;
                chip.chipEndPadding = 20.0f
                chip.textEndPadding = 0.0f
                chip.textStartPadding = 0.0f
                chip.chipMinHeight = 30.0f
                chip.setPadding(5, 10, 5, 10)
                chip.text = chips[it]
                chip.isCheckable = false
                chipGroup.addView(chip);
            }
        }
    }
    private var drawer: DrawerLayout? = null
    private var toggle: ActionBarDrawerToggle? = null
    private var navigationView: NavigationView? = null
    private var loginVM: LoginVM? = null
    private var mToolbar: Toolbar? = null
    private var navHeader: View? = null
    private var txtName: TextView? = null
    private var txtLink: TextView? = null
    private var imgView: ImageView? = null

    private lateinit var notifyMenuItem: MenuItem
    private lateinit var badgeView: TextView
    private var searchView: SearchView? = null
    private lateinit var searchMenuItem: MenuItem

    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private var tabSelected: Int = 0

    private var disposables = CompositeDisposable()
    private var iSearch = ArrayList<ISearch>()
    private lateinit var dialog: ACProgressFlower
    private var currentTag = NAV_HOME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerReceiver(NetworkChangeReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        loginVM = ViewModelProviders.of(this).get(LoginVM::class.java)
        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        mToolbar = findViewById(R.id.toolbar)
        mToolbar!!.title = MENU_QUESTIONS
        setSupportActionBar(mToolbar)

        var upArrow = resources.getDrawable(R.drawable.ic_menu_black_24dp)
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        drawer = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawer, mToolbar, R.string.openDrawer, R.string.closeDrawer)
        drawer!!.setDrawerListener(toggle)
        toggle!!.syncState()
        navigationView = findViewById(R.id.nav_view)
        navigationView!!.setNavigationItemSelectedListener(this)

        // Navigation view header
        navHeader = navigationView!!.getHeaderView(0)
        imgView = navHeader!!.findViewById(R.id.profile_image)
        txtName = navHeader!!.findViewById(R.id.name)
        txtLink = navHeader!!.findViewById(R.id.txtLink)
        txtName?.text = prefs.fullName
        imgView?.loadAvatar(Uri.parse(prefs.avatar))

        navHeader!!.setOnClickListener{
            drawer!!.closeDrawer(GravityCompat.START)
            val intent = Intent(this@MainActivity, UserProfileActivity::class.java)
            intent.putExtra("userId", prefs.userId)
            intent.putExtra("userName", prefs.fullName)
            intent.putExtra("avatar", prefs.avatar)
            startActivity(intent)
        }
        navigationView!!.setCheckedItem(R.id.nav_home)

        tabLayout = findViewById(R.id.tabs)
        viewPager = findViewById(R.id.view_pager)
        setupViewPager(viewPager!!)
        tabLayout?.setupWithViewPager(viewPager)
        if (savedInstanceState != null) {
            tabSelected = savedInstanceState.getInt("tabSelected", 2)
        }
        viewPager?.offscreenPageLimit = 0
        viewPager?.currentItem = tabSelected

        tabLayout!!.getTabAt(0)!!.icon = IconicsDrawable(this).icon(FontAwesome.Icon.faw_code).sizeDp(24)
        tabLayout!!.getTabAt(1)!!.icon = IconicsDrawable(this).icon(FontAwesome.Icon.faw_graduation_cap).sizeDp(24)
        tabLayout!!.getTabAt(2)!!.icon = IconicsDrawable(this).icon(FontAwesome.Icon.faw_home).sizeDp(24)
        tabLayout!!.getTabAt(3)!!.icon = IconicsDrawable(this).icon(FontAwesome.Icon.faw_briefcase).sizeDp(24)
        tabLayout!!.getTabAt(4)!!.icon = IconicsDrawable(this).icon(FontAwesome.Icon.faw_users).sizeDp(24)
        tabLayout!!.getTabAt(0)!!.icon!!.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN)
        tabLayout!!.getTabAt(1)!!.icon!!.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN)
        tabLayout!!.getTabAt(2)!!.icon!!.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN)
        tabLayout!!.getTabAt(3)!!.icon!!.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN)
        tabLayout!!.getTabAt(4)!!.icon!!.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN)

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            loginVM!!.setUserFCMToken(task.result?.token!!)
        })

        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .fadeColor(Color.LTGRAY).build()

        tabLayout?.addOnTabSelectedListener(object: TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                mToolbar?.title = when(tab?.position) {
                    0 -> MENU_QUESTIONS
                    1 -> MENU_TUTORIALS
                    2 -> MENU_POSTS
                    3 -> MENU_USERS
//                    4 -> MENU_USERS
                    else -> MENU_QUESTIONS
                }
            }
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        toggle!!.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchManager =  getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchMenuItem = menu.findItem(R.id.action_search)
        searchView = searchMenuItem.actionView as SearchView
        searchView?.queryHint = MENU_SEARCH
        searchView?.maxWidth = Integer.MAX_VALUE

        searchView?.setOnQueryTextListener(this)
        searchView?.setOnSearchClickListener{ supportActionBar!!.setDisplayHomeAsUpEnabled(false) }
        searchView?.setOnCloseListener {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            false
        }

        notifyMenuItem = menu.findItem(R.id.notify)
        val actionView = MenuItemCompat.getActionView(notifyMenuItem)
        badgeView = actionView.findViewById(R.id.badgeCount)
        notifyMenuItem.actionView.setOnClickListener {
            startActivity(Intent(this@MainActivity, NotificationActivity::class.java))
        }

        setupBadge()
        clearSearch()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.notify -> {}
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                currentTag = NAV_HOME
                drawer!!.closeDrawer(GravityCompat.START)
            }
            R.id.nav_credit -> {
                currentTag = NAV_CREDIT_LIB
                drawer!!.closeDrawer(GravityCompat.START)
                startActivity(Intent(this@MainActivity, CreditLibireActivity::class.java))
            }
            R.id.nav_about_us -> {
                currentTag = NAV_ABOUT_US
                drawer!!.closeDrawer(GravityCompat.START)
                startActivity(Intent(this@MainActivity, AboutUsActivity::class.java))
            }
//            R.id.nav_privacy_policy -> {
//                currentTag = NAV_POLICY
//                drawer!!.closeDrawer(GravityCompat.START)
//                startActivity(Intent(this@MainActivity, PrivacyPolicyActivity::class.java))
//            }
//            R.id.nav_settings -> {
//                currentTag = NAV_SETTING
//                drawer!!.closeDrawer(GravityCompat.START)
//                startActivity(Intent(this@MainActivity, SettingActivity::class.java))
//            }
            R.id.nav_logout -> {
                logout()
            }
        }

        return true
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
        AccessToken.setCurrentAccessToken(null)
        dialog.show()
        loginVM?.logOutUser(prefs.userId, prefs.authToken)
        loginVM?.logoutMessage?.observe(this, Observer {
            dialog.dismiss()
            Toasty.success(this, it!!, Toast.LENGTH_SHORT, true).show()
            val inActivity = Intent(this, SignInActivity::class.java)
            startActivityForResult(inActivity, AUTH_LOGOUT)
            finish()
        })
        loginVM?.errorMessage?.observe(this, Observer {
            dialog.dismiss()
            Toasty.error(this, it!!.message, Toast.LENGTH_SHORT, true).show()
        })
    }

    override fun updateNetworkStatus(result: Int) {
        when (result) {
            0 ->  showSnack(this, resources.getDrawable(R.drawable.ic_network_check_white_24dp), "Connection, No Internet Connection")
            1 -> showSnack(this, resources.getDrawable(R.drawable.ic_wifi_white_24dp),"Connecting to a WiFi ")
            2 -> showSnack(this, resources.getDrawable(R.drawable.ic_signal_cellular_3_bar_white_24dp),"Connection to aMobile")
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        var adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(QuestionFragment(), "")
        adapter.addFrag(CategoryFragment(), "")
        adapter.addFrag(PostFragment(), "")
        adapter.addFrag(JobFragment(), "")
        adapter.addFrag(UsersFragment(), "")
        viewPager.adapter = adapter
    }

    private fun clearSearch() {
        searchView?.setQuery("", false)
        searchMenuItem.collapseActionView()
        searchView?.onActionViewCollapsed()
        searchView?.clearFocus()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }


    override fun onQueryTextSubmit(query: String): Boolean {
        iSearch.forEach{it.onTextQuery(query)}
        return true
    }

    override fun onQueryTextChange(query: String): Boolean {
        if (TextUtils.isEmpty(query)){
            iSearch.forEach{it.onTextQuery(query)}
        }
        return false
    }

    override fun addiSearch(iSearch: ISearch) {
        this.iSearch.add(iSearch)
    }

    override fun removeISearch(iSearch: ISearch) {
        this.iSearch.remove(iSearch)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onRationaleDenied(requestCode: Int) {
        print("onRationaleDenied ================>:$requestCode")
    }

    override fun onRationaleAccepted(requestCode: Int) {
        print("onRationaleAccepted ================>$requestCode")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        print("onPermissionsGranted ============>:" + requestCode + ":" + perms.size)
    }

    @AfterPermissionGranted(RC_CAMERA_PERM)
    fun cameraTask() {
        if (hasCameraPermission()) {
            Toasty.warning(this, "TODO: Camera things", Toast.LENGTH_LONG).show()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera), RC_CAMERA_PERM, android.Manifest.permission.CAMERA)
        }
    }

    @AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
    fun locationAndContactsTask() {
        if (hasLocationAndContactsPermissions()) {
            Toasty.warning(this, "TODO: Location and Contacts things", Toast.LENGTH_LONG).show()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera), RC_LOCATION_CONTACTS_PERM, LOCATION_AND_CONTACTS.toString())
        }
    }

    @AfterPermissionGranted(RC_STORE_AGE_PERM)
    fun storagePermission() {
        if (hasStoragePermission()) {
            Toasty.warning(this, "TODO: Storage things", Toast.LENGTH_LONG).show()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera), RC_STORE_AGE_PERM, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun hasCameraPermission() = EasyPermissions.hasPermissions(this, android.Manifest.permission.CAMERA)

    private fun hasLocationAndContactsPermissions()  =  EasyPermissions.hasPermissions(this, LOCATION_AND_CONTACTS.toString())

    private fun hasStoragePermission()  = EasyPermissions.hasPermissions(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onResume() {
        super.onResume()
        NetworkChangeReceiver.connectivityReceiverListener = this
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(IconicsContextWrapper.wrap(base))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedTab", viewPager?.currentItem!!)
    }

}