package com.finderbar.jovian.viewmodels.user

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloMutationCall
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.finderbar.jovian.*
import com.finderbar.jovian.models.ErrorMessage
import com.finderbar.jovian.models.ModifyMessage
import com.google.firebase.auth.FirebaseUser
import mutation.*
import query.SignOutUsersQuery
import java.util.logging.Logger


class LoginVM : ViewModel() {

    private val logger = Logger.getLogger(LoginVM::class.java.name)

    private var apoSocialUser: ApolloMutationCall<UserSocialSignInMutation.Data>? = null
    private var getSignInUser: ApolloMutationCall<UserSignInMutation.Data>? = null
    private var getRegisterUser: ApolloMutationCall<UserRegisterMutation.Data>? = null
    private var getRestPwdUser: ApolloMutationCall<ResetPasswordMutation.Data>? = null
    private var signOutUser: ApolloCall<SignOutUsersQuery.Data>? = null
    private var modifyUserFCMToken: ApolloMutationCall<UpdateUserFCMTokenMutation.Data>? =null;

    private var auth: MutableLiveData<Auth>? = null
    var modifyMessage: MutableLiveData<ModifyMessage> = MutableLiveData();
    var errorMessage: MutableLiveData<ErrorMessage> = MutableLiveData()
    var logoutMessage: MutableLiveData<String> = MutableLiveData()

    fun getUser(): LiveData<Auth> {
        if (auth == null) {
            auth = MutableLiveData()
            loadUsers()
        }
        return auth as MutableLiveData<Auth>
    }

    private fun loadUsers() {
        auth!!.postValue(Auth(prefs.userId, prefs.authToken, prefs.fullName, prefs.avatar, prefs.provider))
    }

    fun login(user: FirebaseUser) {
        apoSocialUser?.cancel()
        apoSocialUser = apolloClient.mutate(UserSocialSignInMutation.builder().uuid(user.uid).fullName(prefs.fullName).build())
        apoSocialUser?.enqueue({
            val userToken = it.data()?.socialSignUser()?.authToken();
            val error = it.data()?.socialSignUser()?.error();
            if(error != null) {
                errorMessage.postValue(error.message()?.let { it1 -> error.statusCode()?.toLong()?.let { it2 -> ErrorMessage(error.status()!!, it2, it1) } })
            } else {
                prefs.userId = userToken?.userId()!!
                prefs.authToken = userToken?.token()
                prefs.fullName = userToken.username()?.let { user.displayName.toString() }
                prefs.avatar = userToken.avatar()?.let { user.photoUrl.toString() }
                prefs.provider = user.providerId
                loadUsers()
            }
        }, {errorMessage.postValue(ErrorMessage("please try again", 500, it.message!!))})
    }

    fun register(fullName: String, password: String, authCode: String) {
        getRegisterUser?.cancel()
        getRegisterUser = apolloClient.mutate(UserRegisterMutation.builder()
                .authCode(authCode)
                .fullName(fullName)
                .password(password)
                .build())
        getRegisterUser?.enqueue({
            val userToken = it.data()?.registerUser()!!.authToken()!!;
            val error = it.data()?.registerUser()?.error();
            if(error != null) {
                errorMessage.postValue(ErrorMessage(error.status()!!, error.statusCode()!!.toLong(), error.message()!!))
            } else {
                prefs.userId = userToken.userId();
                prefs.authToken = userToken.token();
                prefs.fullName = userToken.username();
                prefs.avatar = userToken.avatar()
                loadUsers()
            }
        },{errorMessage.postValue(ErrorMessage("fail", 500, it.message!!))})
    }

    fun resetPassword(newPassword: String, authCode: String) {
        getRestPwdUser?.cancel()
        getRestPwdUser = apolloClient.mutate(ResetPasswordMutation.builder().newPassword(newPassword).authCode(authCode).build())
        getRestPwdUser?.enqueue({
            val error = it.data()?.resetPassword()?.error();
            if(error != null) {
                errorMessage.postValue(ErrorMessage(error.status()!!, error.statusCode()!!.toLong(), error.message()!!))
            } else {
                val userToken = it.data()?.resetPassword()?.authToken()!!
                prefs.userId = userToken.userId();
                prefs.authToken = userToken.token();
                prefs.fullName = userToken.username();
                prefs.avatar = userToken.avatar()
                loadUsers()
            }
        },{errorMessage.postValue(ErrorMessage("fail", 500, it.message!!))})
    }

    fun logOutUser(userId: String, token: String) {
        signOutUser?.cancel()
        signOutUser = apolloClient.query(SignOutUsersQuery.builder().userId(userId).token(token).build()).responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
        signOutUser?.enqueue({
            val error = it.data()?.signOutUser()!!.error()
            if(error != null) {
                errorMessage.postValue(ErrorMessage("fail", 500, error!!.message()!!))
            } else {
                logoutMessage.postValue(it.data()?.signOutUser()!!.message())
                apolloClient.apolloStore().clearAll();
                apolloClient.clearHttpCache();
                apolloClient.clearNormalizedCache()
                prefs.logout()
            }
        }, {errorMessage.postValue(ErrorMessage("fail", 500, it.message!!))})
    }

    fun setUserFCMToken(fcmToken: String) {
        modifyUserFCMToken?.cancel();
        modifyUserFCMToken = apolloClient.mutate(UpdateUserFCMTokenMutation.builder().fcmToken(fcmToken).build());
        modifyUserFCMToken?.enqueue({
            var error = it.data()?.updateUserFCMToken()!!.error();
            if(error != null) {
                errorMessage.postValue(ErrorMessage(error.status()!!, error.statusCode()!!.toLong(), error.message()!!))
            } else {
                val msg = it.data()?.updateUserFCMToken()?.modifyStatus()!!;
                modifyMessage.postValue(ModifyMessage(msg.status()!!, msg.modifyFlag()!!.toLong(), msg.hashCode().toLong(), msg.message()!!))
            }
        },{errorMessage.postValue(ErrorMessage("fail", 500, it.message!!))})
    }

    override fun onCleared() {
        apoSocialUser?.cancel()
        getRegisterUser?.cancel()
        getSignInUser?.cancel()
        signOutUser?.cancel()
        getRestPwdUser?.cancel()
    }
}