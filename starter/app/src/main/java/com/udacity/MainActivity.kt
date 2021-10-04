package com.udacity

import android.Manifest
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var notificationManager: NotificationManager
    private lateinit var downloadManager: DownloadManager
    private var filename:String="null"
private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        createChannel(CHANNEL_ID, getString(R.string.notification_channel_name))
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        var custom_button = binding.customButton
       custom_button.setOnClickListener {
           custom_button.buttonState = ButtonState.Loading
            val intSelectButton: Int = binding.radioGroup!!.checkedRadioButtonId
            when(intSelectButton){
                binding.radioGlide.id->{filename = URL_GLIDE  }
                binding.radioLoadApp.id->{filename = URL_LOADAPP}
                binding.radioRetrofit.id->{filename = URL_RETROFIT}
                else->{
                    Log.i("RadioID",": $intSelectButton")
                }
            }
            if (intSelectButton!=-1) {
                download(filename)
            }else{Log.i("IDRADIO"," : $intSelectButton")
                Toast.makeText(baseContext, "Select one of the link above", Toast.LENGTH_SHORT).show()
                custom_button.buttonState = ButtonState.Completed
            }

        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            notificationManager = ContextCompat.getSystemService(this@MainActivity, NotificationManager::class.java) as NotificationManager
            binding.customButton.buttonState = ButtonState.Completed
            val query = DownloadManager.Query()
            query.setFilterById(id!!)
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    if(status == DownloadManager.STATUS_SUCCESSFUL)  {
                        //status_string = "Download Success"
                            Toast.makeText(context,getString(R.string.download_success),Toast.LENGTH_SHORT).show()
                        notificationManager.sendNotification(getString(R.string.download_success), this@MainActivity)
                    }
                    else {
                        Toast.makeText(context,getString(R.string.download_failed),Toast.LENGTH_SHORT).show()
                        notificationManager.sendNotification(getString(R.string.download_failed), this@MainActivity)
                    }
            }
        }
    }
    fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context){
        val contentIntent = Intent(applicationContext, DetailActivity::class.java)
        contentIntent.putExtra("status", messageBody)
        contentIntent.putExtra("filename", filename)
        val pendingIntent = PendingIntent.getActivity(applicationContext,downloadID.toInt(), contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(messageBody)
            .setContentIntent(pendingIntent)
                .addAction(
                        R.drawable.ic_assistant_black_24dp,
                        applicationContext.getString(R.string.notification_button),
                       pendingIntent
                )
            .setAutoCancel(true)
        notify(downloadID.toInt(), builder.build())
    }

    private fun download(URL:String) {
        try {
            val request =
                    DownloadManager.Request(Uri.parse(URL))
                            .setTitle(getString(R.string.app_name))
                            .setDescription(getString(R.string.app_description))
                            .setRequiresCharging(false)
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true)
            //   .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue
        }
        catch (e:Exception){

           // status_string = "Download Failed"
            notificationManager = ContextCompat.getSystemService(this@MainActivity, NotificationManager::class.java) as NotificationManager
            notificationManager.sendNotification(getString(R.string.download_failed), this@MainActivity)
            Toast.makeText(this,getString(R.string.download_failed),Toast.LENGTH_SHORT).show()
            binding.customButton.buttonState = ButtonState.Completed

        }
    }
    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)
          val  notificationManager = this.getSystemService(
                  NotificationManager::class.java
          )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    companion object {
        private const val URL_GLIDE ="https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val URL_LOADAPP ="https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_RETROFIT ="https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "channelId"
    }


}
