package com.finderbar.jovian.viewmodels.discuss

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.v4.content.ContextCompat
import android.view.View
import com.apollographql.apollo.ApolloMutationCall
import com.finderbar.jovian.R
import com.finderbar.jovian.apolloClient
import com.finderbar.jovian.enqueue
import com.finderbar.jovian.models.Command
import com.finderbar.jovian.models.ErrorMessage
import com.finderbar.jovian.models.InputVote
import kotlinx.android.synthetic.main.item_discuss_question.view.*
import mutation.SaveAnswerVoteMutation
import mutation.SaveQuestionFavoriteMutation
import mutation.SaveQuestionViewMutation
import mutation.SaveQuestionVoteMutation
import type.VoteStatus

class DiscussVoteVM: ViewModel() {
    var errorMessage: MutableLiveData<ErrorMessage> = MutableLiveData()
    var result: MutableLiveData<Command> = MutableLiveData()

    private var saveVoteQuestion: ApolloMutationCall<SaveQuestionVoteMutation.Data>? = null
    private var saveVoteAnswer: ApolloMutationCall<SaveAnswerVoteMutation.Data>? = null
    private var saveFavoriteQuestion: ApolloMutationCall<SaveQuestionFavoriteMutation.Data>? = null
    private var saveQuestionView: ApolloMutationCall<SaveQuestionViewMutation.Data>? = null

    fun questionVote(holder: View, _id: String, vote: InputVote) {
        saveVoteQuestion?.cancel()
        saveVoteQuestion = apolloClient.mutate(SaveQuestionVoteMutation.builder()._id(_id).vote(vote.get()).build())
        saveVoteQuestion?.enqueue({
            val error = it.data()?.createQuestionVote()?.error();
            if(error != null) {
                errorMessage.postValue(error.status()?.let { it1 -> error.statusCode()?.toLong()?.let { it2 -> ErrorMessage(it1, it2, error.message()!!) } })
            } else {
                val datum = it.data()?.createQuestionVote()?.command()
                result.postValue(Command(datum?.id()!!, datum?.status()!!, datum.modifyFlag()!!, datum.statusCode()!!, datum.message()!!))
                var totalCount: Int;
                if(vote.status == VoteStatus.UP) {
                    var helper = holder.up_vote_helper
                    if (helper.text == "0") {
                        holder.up_vote.icon.color(ContextCompat.getColor(holder.context, R.color.colorPrimary))
                        holder.down_vote.icon.color(ContextCompat.getColor(holder.context, R.color.pf_white))
                        totalCount = Integer.parseInt(holder.txt_vote_count.text as String) + 1
                        helper.text = "1"
                    } else {
                        holder.up_vote.icon.color(ContextCompat.getColor(holder.context, R.color.pf_white))
                        holder.down_vote.icon.color(ContextCompat.getColor(holder.context, R.color.pf_white))
                        totalCount = Integer.parseInt(holder.txt_vote_count.text as String) - 1
                        helper.text = "0"
                    }
                } else {
                    var helper = holder.down_vote_helper
                    if (helper.text == "0") {
                        holder.up_vote.icon.color(ContextCompat.getColor(holder.context, R.color.pf_white))
                        holder.down_vote.icon.color(ContextCompat.getColor(holder.context, R.color.colorPrimary))
                        totalCount = Integer.parseInt(holder.txt_vote_count.text as String) - 1
                        helper.text = "1"
                    } else {
                        holder.up_vote.icon.color(ContextCompat.getColor(holder.context, R.color.pf_white))
                        holder.down_vote.icon.color(ContextCompat.getColor(holder.context, R.color.pf_white))
                        totalCount = Integer.parseInt(holder.txt_vote_count.text as String) + 1
                        helper.text = "0"
                    }
                }
                holder.txt_vote_count.text = totalCount.toString()
            }
        }, {errorMessage.postValue(ErrorMessage("fail", 500, it.message!!))})
    }

    fun questionFavorite(holder: View, _id: String, userId: String) {
        saveFavoriteQuestion?.cancel()
        saveFavoriteQuestion = apolloClient.mutate(SaveQuestionFavoriteMutation.builder()._id(_id).userId(userId).build())
        saveFavoriteQuestion?.enqueue({
            val datum = it.data()?.createQuestionFavorite()?.command()
            result.postValue(Command(datum?.id()!!, datum?.status()!!, datum.modifyFlag()!!, datum.statusCode()!!, datum.message()!!))
            var helper = holder.favorite_helper
            if (helper.text == "0") {
                holder.favorite.icon.color(ContextCompat.getColor(holder.context, R.color.pf_gold))
                helper.text = "1"
            } else {
                holder.favorite.icon.color(ContextCompat.getColor(holder.context, R.color.pf_white))
                helper.text = "0"
            }
        }, {errorMessage.postValue(ErrorMessage("fail", 500, it.message!!))})
    }

    fun questionView(holder: View, _id: String, userId: String) {
        saveQuestionView?.cancel()
        saveQuestionView = apolloClient.mutate(SaveQuestionViewMutation.builder()._id(_id).userId(userId).build())
        saveQuestionView?.enqueue({
            val datum = it.data()?.createQuestionView()?.command()
            result.postValue(Command(datum?.id()!!, datum?.status()!!, datum.modifyFlag()!!, datum.statusCode()!!, datum.message()!!))

        }, {errorMessage.postValue(ErrorMessage("fail", 500, it.message!!))})
    }

    fun answerVote(holder: View, _id: String, vote: InputVote) {
        saveVoteAnswer?.cancel()
        saveVoteAnswer = apolloClient.mutate(SaveAnswerVoteMutation.builder()._id(_id).vote(vote.get()).build())
        saveVoteAnswer?.enqueue({
            val error = it.data()?.createAnswerVote()?.error();
            if(error != null) {
                errorMessage.postValue(error.status()?.let { it1 -> error.statusCode()?.toLong()?.let { it2 -> ErrorMessage(it1, it2, error.message()!!) } })
            } else {
                val datum = it.data()?.createAnswerVote()?.command()
                result.postValue(Command(datum?.id()!!, datum?.status()!!, datum.modifyFlag()!!, datum.statusCode()!!, datum.message()!!))
                var totalCount: Int;
               if(vote.status == VoteStatus.UP) {
                   var helper = holder.up_vote_helper
                   if (helper.text == "0") {
                       holder.up_vote.icon.color(ContextCompat.getColor(holder.context, R.color.colorPrimary))
                       holder.down_vote.icon.color(ContextCompat.getColor(holder.context, R.color.pf_grey))
                       totalCount =  Integer.parseInt(holder.txt_vote_count.text as String) + 1
                       helper.text = "1";
                   } else {
                       totalCount = Integer.parseInt(holder.txt_vote_count.text as String) - 1
                       holder.up_vote.icon.color(ContextCompat.getColor(holder.context, R.color.pf_grey))
                       holder.down_vote.icon.color(ContextCompat.getColor(holder.context, R.color.pf_grey))
                       helper.text = "0";
                   }
               } else {
                   var helper = holder.down_vote_helper
                   if (helper.text == "0") {
                       holder.down_vote.icon.color(ContextCompat.getColor(holder.context, R.color.colorPrimary))
                       holder.up_vote.icon.color(ContextCompat.getColor(holder.context, R.color.pf_grey))
                       totalCount =  Integer.parseInt(holder.txt_vote_count.text as String) - 1
                       helper.text = "1";
                   } else {
                       holder.down_vote.icon.color(ContextCompat.getColor(holder.context, R.color.pf_grey))
                       holder.up_vote.icon.color(ContextCompat.getColor(holder.context, R.color.pf_grey))
                       totalCount =  Integer.parseInt(holder.txt_vote_count.text as String) + 1
                       helper.text = "0";
                   }
               }
                holder.txt_vote_count.text = totalCount.toString()
            }
        }, {errorMessage.postValue(ErrorMessage("fail", 500, it.message!!))})
    }

    override fun onCleared() {
        super.onCleared()
        saveVoteQuestion?.cancel()
        saveVoteAnswer?.cancel()
        saveFavoriteQuestion?.cancel()
    }
}