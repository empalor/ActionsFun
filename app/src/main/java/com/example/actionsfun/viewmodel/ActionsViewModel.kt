package com.example.actionsfun.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.actionsfun.data.repository.BaseRepository
import com.example.actionsfun.model.Action
import com.example.actionsfun.model.ActionResponse
import com.example.actionsfun.model.ResultState
import com.example.actionsfun.util.ActionsDecider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.annotation.Nullable
import javax.inject.Inject

@HiltViewModel
class ActionsViewModel @Inject constructor(
    private val repository: BaseRepository<ActionResponse>
) : ViewModel() {

    companion object {
        private val TAG = ActionsViewModel::class.qualifiedName
    }

    private val _actionList = MutableLiveData<ResultState<ActionResponse>>()
    val actionList = _actionList // expose reference for the actions live data

    private var actionsRaw: List<Action>? = null

    init {
        fetchActions()
    }

    private fun fetchActions() {
        viewModelScope.launch {
            repository.fetchAll().collect {
                _actionList.value = it
                actionsRaw = it?.data?.results as? ArrayList<Action>
            }
        }
    }

    /**
     * An event triggered upon "click me" button click.
     * get decided action and update it's last triggered time
     */
    fun onActionButtonClicked(): Action? {
        val decidedAction: Action? = ActionsDecider.decideAction(actionsRaw!!)
        if (decidedAction == null) {
            Log.i(
                TAG, "@onActionButtonClicked(): couldn't find any actions, " +
                        "perhaps all of possible ones are in cooldown, let's spin :P"
            )
            return null
        }
        Log.i(TAG, "decided action: $decidedAction, updating last trigger time")
        decidedAction.last_triggered = System.currentTimeMillis()
        viewModelScope.launch(Dispatchers.IO) {
            decidedAction.id.let { repository.update(it) }
        }
        return decidedAction
    }
}