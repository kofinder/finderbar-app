package com.finderbar.jovian.fragments.tutorial

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.finderbar.jovian.R
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.finderbar.jovian.models.Tutorial
import com.finderbar.jovian.utilities.markdown.setMarkdown


class TutorialFragment : DialogFragment(), MenuItem.OnMenuItemClickListener {


    companion object {
        var TAG = "TutorialFragment"
    }

    private var mToolbar: Toolbar? = null;
    private var mBody: TextView? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_dialog_tutorial, container, false)

        val datum = arguments!!.getSerializable("tutorial") as Tutorial
        val title = arguments!!.getString("heading");
        mToolbar = rootView.findViewById(R.id.toolbar);
        mBody = rootView.findViewById(R.id.mTxtBody);

        mToolbar!!.inflateMenu(R.menu.other_menu);
        (activity as AppCompatActivity).setSupportActionBar(mToolbar)
        mToolbar!!.navigationIcon = resources.getDrawable(R.drawable.ic_clear_black_24dp);
        mToolbar!!.navigationIcon!!.setColorFilter(resources.getColor(R.color.pf_white), PorterDuff.Mode.SRC_ATOP);
        mToolbar!!.setNavigationOnClickListener{ dismiss() }

        mToolbar!!.title = title;
        mToolbar!!.subtitle = datum.titleText
        mBody!!.setMarkdown(datum.htmlBody);

        return rootView;
    }


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window.setLayout(width, height)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }
}