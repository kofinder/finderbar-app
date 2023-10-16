package com.finderbar.jovian.utilities

object AppConstants {

    // TEMPLATE
    const val ITEM = 0
    const val TYPE_POST_TEXT = 1
    const val TYPE_POST_PHOTO= 2
    const val TYPE_POST_VIDEO = 3
    const val LOADING = 4
    const val EMPTY = 5

    const val ITEM_QUESTION = 0;
    const val ITEM_ANSWER = 1

    // DISCUSS
    const val ARG_KEY_DISCUSS_ID = "discussId"
    const val ARG_KEY_ANSWER_ID = "answer:id"
    const val ARG_KEY_ANSWER_BODY = "answer:body";
    const val ARG_KEY_QUESTION_ID = "question:id"
    const val ARG_KEY_QUESTION_TITLE = "question:title"
    const val ARG_KEY_QUESTION_BODY = "question:body"
    const val ARG_KEY_QUESTION_TAGS = "question:tags"
    const val ARG_KEY_USER_NAME = "username"
    const val ARG_KEY_USER_AVATAR = "avatar"
    const val ARG_KEY_TIME_AGO = "timeago"

    //POST
    const val EXTRA_POST_POSITION = "post:position"
    const val EXTRA_POST_ITEM = "post"

    // MOVIES
    const val EXTRA_MEDIA_URI = "player:uri"
    const val EXTRA_MEDIA_ORDER = "player:order"
    const val EXTRA_MEDIA_IMAGE = "player:coverImage";
    const val EXTRA_MEDIA_DESCRIPTION = "player:description"
    const val EXTRA_MEDIA_PLAYBACK_INFO = "player:info"
    const val EXTRA_MEDIA_PLAYER_SIZE = "player_size"
    const val EXTRA_MEDIA_VIDEO_SIZE = "player:video_size"
    const val EXTRA_DEFAULT_FULLSCREEN = "player:fullscreen"
    const val STATE_MEDIA_PLAYBACK_INFO = "state:playback"
    const val RESULT_EXTRA_PLAYER_ORDER = "result:order"
    const val RESULT_EXTRA_PLAYBACK_INFO = "result:playback"

    const val BIG_PLAYER_FRAGMENT_TAG = "BigPlayerFragment"
    const val ARG_KEY_VIDEO_ITEM = "fb:player:video_item"
    const val ARG_KEY_VIDEO_ORDER = "fb:player:video:order"
    const val ARG_KEY_INIT_INFO = "fb:player:init_info"
    const val BUNDLE_KEY_VIDEO = "fb:player:bundle:video"
    const val BUNDLE_KEY_ORDER = "fb:player:bundle:order"
    const val BUNDLE_KEY_INFO = "fb:player:bundle:info"

    const val ARG_EXTRA_PLAYBACK_INFO = "fb:more_videos:playback_info"
    const val ARG_EXTRA_BASE_FB_VIDEO = "fb:more_videos:base_video"
    const val ARG_EXTRA_BASE_ORDER = "fb:more_videos:base_order"
    const val STATE_KEY_FB_VIDEO = "fb:timeline:list:state:video"
    const val STATE_KEY_ACTIVE_ORDER = "fb:timeline:list:state:order"
    const val STATE_KEY_PLAYBACK_STATE = "fb:timeline:list:state:playback_info"
    const val STATE_KEY_BIG_PLAYER_BUNDLE = "fb:timeline:list:state:player:bundle"

    const val MOVIE_ID = "movieId"
    const val USER_ID = "userId"
    const val MOVIE_TITLE = "movieTitle"

    const val TYPE_WIFI = 1
    const val TYPE_MOBILE = 2
    const val TYPE_NOT_CONNECTED = 0

    const val NETWORK_STATUS_NOT_CONNECTED = 0
    const val NETWORK_STATUS_WIFI = 1
    const val NETWORK_STATUS_MOBILE = 2

    // NAVIGATE & MENU & TITLE
    const val MENU_SEARCH = "Search..."
    const val MENU_QUESTIONS: String = "Questions"
    const val MENU_TUTORIALS: String = "Tutorials"
    const val MENU_USERS: String = "Users"
    const val MENU_JOBS: String = "Jobs"
    const val MENU_POSTS: String = "Posts"

    const val NAV_HOME: String = "HOME"
    const val NAV_ABOUT_US: String = "About Us"
    const val NAV_CREDIT_LIB: String = "Credit Libraries"
    const val NAV_POLICY: String = "Privacy Policy"
    const val NAV_SETTING: String = "Settings"

    const val SNACK_BAR_RIGHT_BUTTON= "OK!"
    const val AUTH_LOGOUT= 5
    const val GOOGLE_LOG_IN_RC = 1
    const val FACEBOOK_LOG_IN_RC = 2
    const val PHONE_LOG_IN_RC = 3
    const val TWITTER_LOG_IN_RC = 3

    const val PHONE_DIALOG_TAG = "AuthencationFragment"
    const val KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress"
    const val STATE_INITIALIZED = 1
    const val STATE_CODE_SENT = 2
    const val STATE_VERIFY_FAILED = 3
    const val STATE_VERIFY_SUCCESS = 4
    const val STATE_SIGN_IN_FAILED = 5
    const val STATE_SIGN_IN_SUCCESS = 6
    const val STATE_SIGN_IN_WITH_NAME = 7

    val LOCATION_AND_CONTACTS = arrayListOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_CONTACTS);
    const val RC_CAMERA_PERM = 123;
    const val RC_LOCATION_CONTACTS_PERM = 124;
    const val RC_STORE_AGE_PERM = 125;
}