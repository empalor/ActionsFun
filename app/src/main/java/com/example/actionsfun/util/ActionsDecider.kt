package com.example.actionsfun.util

import com.example.actionsfun.model.Action
import java.time.LocalDate
import java.util.concurrent.TimeUnit

object ActionsDecider {

    /**
     * return the suitable, highest prioritized action
     */
    fun decideAction(actions: List<Action?>): Action? {
        val result: Action? = null

        for (action in actions) {
            if (action != null && isActionSuitable(action)) {
                return action
            } else {
                continue
            }
        }

        return result
    }

    /**
     * return true if action is suitable, false otherwise. this is done by:
     * -having fetched actions from db(ordered by priority),
     * -take and return the first action (if suitable),
     * -update it's last triggered time, and persist to db
     */
    private fun isActionSuitable(action: Action): Boolean {
        val isEnabled = action.enabled
        val hasCooldownPassed = hasCooldownPassed(action.last_triggered, action.cool_down)
        val todayNumeric: Int = LocalDate.now().dayOfWeek.value
        val isDayValid = action.valid_days.contains(todayNumeric)

        return isEnabled!! && hasCooldownPassed && isDayValid
    }

    private fun hasCooldownPassed(lastTriggeredMillis: Long, cooldown: Long): Boolean {
        val nowMillis = System.currentTimeMillis()
        val deltaSinceLastTriggerMillis = nowMillis - lastTriggeredMillis
        return lastTriggeredMillis == -1L || lastTriggeredMillis == 0L || (TimeUnit.MILLISECONDS.toDays(
            deltaSinceLastTriggerMillis
        ) >= cooldown)
    }
}