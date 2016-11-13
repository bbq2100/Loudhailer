package github.qabbasi.loudhailer

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/* https://github.com/firebase/quickstart-android */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        // Check if the message contains a data payload.
        if (remoteMessage != null && remoteMessage.data.size > 0) {
            val message: String? = remoteMessage.data["message"]
            if (message != null)
                sendNotification(message)
        }

    }

    private fun sendNotification(message: String) {
        val intent = Intent(this, AlarmActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(AlarmActivity.MESSAGE, message)
        startActivity(intent)
    }

}
