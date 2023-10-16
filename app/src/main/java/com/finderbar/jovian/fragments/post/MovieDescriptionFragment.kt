package com.finderbar.jovian.fragments.post
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.finderbar.jovian.R
import com.finderbar.jovian.utilities.AppConstants

class MovieDescriptionFragment : Fragment() {

    private var txtTitle: TextView? = null;
    private var txtBody: TextView? = null;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val extras = activity?.intent?.extras
        val title = extras?.getString(AppConstants.MOVIE_TITLE);
        val description = extras?.getString(AppConstants.EXTRA_MEDIA_DESCRIPTION)

        val views = inflater.inflate(R.layout.fragment_movie_description, container, false)
        txtTitle = views.findViewById(R.id.movie_title)
        txtBody = views.findViewById(R.id.movie_description)

        txtTitle?.text = title
        txtBody?.text = description

        return views;
    }
}