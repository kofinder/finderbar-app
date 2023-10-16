package com.finderbar.jovian.viewmodels.user

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.apollographql.apollo.ApolloMutationCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.finderbar.jovian.apolloClient
import com.finderbar.jovian.enqueue
import com.finderbar.jovian.models.*
import mutation.UpdateUserProfileMutation
import query.UserProfileQuery

class UserProfileVM: ViewModel() {

    var userProfile: MutableLiveData<UserProfile>? = MutableLiveData()
    var errorMessage: MutableLiveData<ErrorMessage> = MutableLiveData();
    var modifyMessage: MutableLiveData<ModifyMessage> = MutableLiveData();
    private var profileQuery: ApolloQueryCall<UserProfileQuery.Data>? = null
    private var modifyUserProfile: ApolloMutationCall<UpdateUserProfileMutation.Data>? = null;


    fun getUserProfile(userId: String) {
        val query = UserProfileQuery.builder().userId(userId).build()
        profileQuery = apolloClient.query(query).responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
        profileQuery?.enqueue({
            val error = it.data()?.userProfile()?.error()
            if(error != null) {
                errorMessage.postValue(ErrorMessage(error.status()!!, error.statusCode()!!.toLong(), error.message()!!))
            } else {
                val result = getProfile(it.data()?.userProfile()?.profile()!!)
                userProfile?.postValue(result)
            }
        }, {errorMessage.postValue(ErrorMessage("fail", 500, it.message!!))})
    }

    fun modifyUserProfile(userId: String, fullName: String, avatar: String, gender: String, relationShip: String, birthday: String, language: String,
                          nationality: String, facebook: String, workPhone: String, handPhone: String, address: String) {
        modifyUserProfile?.cancel();
        modifyUserProfile = apolloClient.mutate(UpdateUserProfileMutation.builder().
                _id(userId)
                .profile(InputUserProfile(userId, fullName, avatar, gender, relationShip, birthday, language, nationality, facebook, workPhone, handPhone, address).get())
                .build())
        modifyUserProfile?.enqueue({
            var error = it.data()?.updateUserProfile()!!.error();
            if(error != null) {
                errorMessage.postValue(ErrorMessage(error.status()!!, error.statusCode()!!.toLong(), error.message()!!))
            } else {
                val msg = it.data()?.updateUserProfile()?.modifyStatus()!!;
                modifyMessage.postValue(ModifyMessage(msg.status()!!, msg.modifyFlag()!!.toLong(), msg.hashCode().toLong(), msg.message()!!))
            }
        },{errorMessage.postValue(ErrorMessage("fail", 500, it.message!!))})
    }

    private fun getProfile(result: UserProfileQuery.Profile): UserProfile {
        return UserProfile(
            result._id(),
            result.userName(),
            result.avatar(),
            result.gender().toString(),
            result.relationship(),
            result.birthday(),
            result.language(),
            result.nationality(),
            result.facebook(),
            result.workPhone(),
            result.handPhone(),
            result.address(),
            result.profileViewCount().toLong(),
            result.answerCount().toLong(),
            result.questionCount().toLong(),
            result.badgeCount()?.let { getBadgeCounts(it) },
            result.repetition().toLong(),
            getExperienceResult(result.experience()),
            getEducationResult(result.education()),
            getSkillResult(result.skill()),
            getHobbieResult(result.hobbie())
        )
    }

    private fun getBadgeCounts(b: UserProfileQuery.BadgeCount?) = BadgeCounts(b?.bronze(), b?.silver(), b?.gold());

    private fun getHobbieResult(arr: MutableList<UserProfileQuery.Hobbie>?): MutableList<Hobbie> {
        val result = java.util.ArrayList<Hobbie>();
        arr?.forEach { result.add(Hobbie(it.hobbId(), it.travel()!!, it.movie()!!, it.music()!!, it.game()!!, it.book()!!))}
        return  result;
    }

    private fun getExperienceResult(arr: MutableList<UserProfileQuery.Experience>?): ArrayList<Experience> {
        val result = ArrayList<Experience>();
        arr?.forEach { result.add(Experience(it.expId(), it.position()!!, it.company()!!, it.fromDate()!!, it.toDate()!!, it.expDescription()!!))}
        return  result;
    }

    private fun getEducationResult(arr: MutableList<UserProfileQuery.Education>?): ArrayList<Education> {
        val result = ArrayList<Education>();
        arr?.forEach { result.add(Education(it.eduId(), it.school()!!, it.city()!!, it.fromDate()!!, it.toDate()!!, it.eduDescription()!!))}
        return  result;
    }

    private fun getSkillResult(arr: MutableList<UserProfileQuery.Skill>?): ArrayList<Skill> {
        val result = ArrayList<Skill>();
        arr?.forEach { result.add(Skill(it.skillId(), it.language()!!, it.framework()!!, it.database()!!))}
        return  result;
    }

    override fun onCleared() {
        super.onCleared()
        modifyUserProfile?.cancel();
    }
}

