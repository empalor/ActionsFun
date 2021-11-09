package com.example.actionsfun.view.ui

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.example.actionsfun.R
import com.example.actionsfun.databinding.ActivityMainBinding
import com.example.actionsfun.model.Action
import com.example.actionsfun.model.ResultState
import com.example.actionsfun.viewmodel.ActionsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.qualifiedName
        private var NOTIFICATION_CHANNEL_ID = 9876
        private var PUSH_NOTIFICATION_REQUEST_CODE = 29
    }

    private val viewModel by viewModels<ActionsViewModel>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        subscribeUi()
    }

    private fun init() {
        binding.buttonAction.setOnClickListener { onActionButtonClicked() }
    }

    private fun subscribeUi() {
        viewModel.actionList.observe(this, Observer { result ->
            when (result.state) {
                ResultState.State.SUCCESS -> {
                    result.data?.results?.let {
                        Log.i(TAG, "actions successfully retrieved!")
                    }
                    binding.buttonAction.isEnabled = true
                    binding.progressBarAction.visibility = View.GONE
                }

                ResultState.State.FAILURE -> {
                    result.message?.let {
                        showErrorToast(it)
                    }
                    binding.buttonAction.isEnabled = false
                    binding.progressBarAction.visibility = View.GONE
                }

                ResultState.State.LOADING -> {
                    binding.buttonAction.isEnabled = false
                    binding.progressBarAction.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun showErrorToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * From the assignment: "* Keep in mind that in the future, the app should be able to support actions that are more
    complex than the actions above."
     *-------------------------------------------------
     * There is no absolute "contract" between the client and the server(remote json),
     * and we can not pre-determine the endless possible android-use-cases we might need in the future,
     * hence, this dummy implementation.
     */
    private fun onActionButtonClicked() {
        binding.buttonAction.isEnabled = false
        val action: Action? = viewModel.onActionButtonClicked()
        if (action == null) {
            handleAnimationAction()
            binding.buttonAction.isEnabled = true
            return
        }
        when (action?.type) {
            "animation" -> {
                handleAnimationAction()
            }
            "toast" -> {
                handleToastAction()
            }
            "call" -> {
                handleContactAction()
            }
            "notification" -> {
                handleNotificationAction()
            }
        }

        binding.buttonAction.isEnabled = true
    }

    private fun handleNotificationAction() {
        Log.w(TAG, "@handleNotificationAction(): android 12 notification trampoline restriction :( I'll show a toast instead")
        val snack = Snackbar.make(binding.root, "I should have been a notification!", Snackbar.LENGTH_LONG);
            snack.setAction("dismiss") { print("dismissing") }
                .show()
        val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(
                applicationContext
                    .getString(R.string.action_notification_string)
            )
            .setContentText(getString(R.string.action_notification_string))
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    PUSH_NOTIFICATION_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_CHANNEL_ID++, builder.build())
    }

    private fun handleAnimationAction() {
        val rotate = RotateAnimation(
            0F,
            360F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 2500
        rotate.interpolator = LinearInterpolator()
        binding.buttonAction.startAnimation(rotate)
    }

    private fun handleToastAction() {
        Toast.makeText(this, "Action is Toast!", Toast.LENGTH_LONG).show()
    }

    private fun handleContactAction() {
        val contactPickerIntent = Intent(
            Intent.ACTION_PICK,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        )
        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    handleContactPicked(result)
                }
            }
        resultLauncher.launch(contactPickerIntent)
    }

    private fun handleContactPicked(result: ActivityResult) {
        val intent = result.data
        val uri: Uri = intent?.data!!
        val cursor: Cursor = contentResolver.query(uri, null, null, null, null)!!
        cursor.moveToFirst()
        val phoneIndex: Int =
            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val phoneNo = cursor.getString(phoneIndex)
        Toast.makeText(this, "Calling $phoneNo", Toast.LENGTH_LONG).show()
        cursor.close()
    }
}