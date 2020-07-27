package com.example.rxsensorlauncher

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager

import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager

import android.util.Log
import androidx.preference.PreferenceManager

import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent
import com.github.pwittchen.reactivesensors.library.ReactiveSensors

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlin.collections.ArrayDeque
import kotlin.math.abs
import kotlin.math.sign


@ExperimentalStdlibApi
class SensorService : Service(){

    lateinit var cameraTool : CameraTool
    @ExperimentalStdlibApi
    lateinit var xQueue : ArrayDeque<String>
    @ExperimentalStdlibApi
    lateinit var yQueue : ArrayDeque<String>
    @ExperimentalStdlibApi
    lateinit var zQueue : ArrayDeque<String>

    private var xSwicth : Boolean = false


    override fun onCreate() {
        super.onCreate()
        Log.i("DEBUG","Service onCreate")

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("DEBUG", "onStartCommand")

        val requestCode: Int = intent!!.getIntExtra("REQUEST_CODE", 0)
        val context: Context = applicationContext
        var channelId = "default"
        var title = context.getString(R.string.app_name)


        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(applicationContext, requestCode, intent, FLAG_UPDATE_CURRENT)

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel: NotificationChannel =
            NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT)

        notificationManager.createNotificationChannel(channel)



        if(notificationManager != null){
            val notification: Notification = Notification.Builder(context, channelId)
                //   .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentIntent(pendingIntent)
                .build()

            startForeground(R.string.foreground_service_notification_id,notification)

            start(context)

        }


        start(context);

        return START_NOT_STICKY
    }

    @SuppressLint("CheckResult")
    protected fun start(context : Context){
        val cameraManager : CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        xSwicth=prefs.getBoolean("xSwitch",false)

        xQueue = ArrayDeque()
        yQueue = ArrayDeque()
        zQueue = ArrayDeque()

        cameraTool  = CameraTool(cameraManager)

        var bundle : Bundle = Bundle()
        bundle.putString("Message","Fragment Test")

        ReactiveSensors(context).observeSensor(Sensor.TYPE_GYROSCOPE)
            .subscribeOn(Schedulers.computation())
            .filter(ReactiveSensorEvent::sensorChanged)
            .observeOn(AndroidSchedulers.mainThread())
            .filter { abs(it.sensorValues()[0]) >=2 ||abs(it.sensorValues()[1])>=1 || abs(it.sensorValues()[2])>=1}
            .map{RSE : ReactiveSensorEvent? ->
                RSE?.sensorValues()
            }
            .subscribe {
                var x : Float = (it?.get(0)?.minus(it?.get(0)%1)!!)
                var y : Float = (it?.get(1)?.minus(it?.get(1)%1))
                var z : Float = (it?.get(2)?.minus(it?.get(2)%1))

                xQueue.addLast(sign(x).toInt().toString())
                yQueue.addLast(sign(x).toInt().toString())
                zQueue.addLast(sign(x).toInt().toString())

                println("x:${x},y:${y},z:${z}")

                QueueChecker(xQueue,prefs)
            }
    }

    fun stop(){
        stopForeground(true)
        stopSelf()
        Log.i("DEBUG","stop")
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }




    @ExperimentalStdlibApi
    protected fun QueueChecker(iQueue : ArrayDeque<String>,prefs: SharedPreferences){
        println("Queue Check is launched")
        val list = iQueue.reversed()
        var iterator = list.iterator()
        var i = 0
        var s = arrayOf("","")

        while(i<2 && iterator.hasNext()){
            s[1-i] = iterator.next().toString()
            i++
        }

        if(s[0] != null && s[1] != null)
        {
            when(s[0]){
                "-1" ->{
                    when(s[1]){
                        "1" ->{
                            xSwicth=prefs.getBoolean("xSwitch",false)
                            if(xSwicth) {
                                when(prefs.getString("xList","")){
                                    "torch" ->{
                                        cameraTool.cameraAction()
                                        xQueue.clear()
                                    }

                                    "application" ->{
                                        val packageName:String = prefs.getString("xApp","")!!
                                        val className:String = prefs.getString("xAppClass","")!!
                                        //val mIntent = Intent().
                                        val mIntent = Intent(Intent.ACTION_MAIN).also {
                                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                                            it.addCategory(Intent.CATEGORY_LAUNCHER)
                                        }

                                        mIntent.setClassName(packageName,className)
                                        wakeFromSleep()
                                        //TODO バックグラウンドからだと実行できないので、スリープの解除を待ったあとに、このアプリを起動
                                        //TODO 起動後、アクティブにしたあとに実行する
                                        startActivity(mIntent)

                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    class CameraTool(private val cameraManager: CameraManager) {
        private var cameraID : String = ""
        private var SW : Boolean = false

        init {
            cameraManager.registerTorchCallback(object : CameraManager.TorchCallback(){
                override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                    super.onTorchModeChanged(cameraId, enabled)
                    cameraID = cameraId
                    SW = enabled

                }
            }, Handler())
        }

        fun cameraAction(){
            println("カメラアクションが動いたよ　SW:${SW} ")

            if(cameraID == null){
                println{"cameraID is null"}
                return;
            }
            try{
                if(!SW){
                    cameraManager.setTorchMode(cameraID,true)
                }else{
                    cameraManager.setTorchMode(cameraID,false)
                }
            }catch(e : CameraAccessException){
                e.printStackTrace()
            }
        }
    }

    private lateinit var keyguard : KeyguardManager
    private lateinit var keyguardLock : KeyguardManager.KeyguardLock
    private lateinit var wakeLock: PowerManager.WakeLock

    @SuppressLint("InvalidWakeLockTag", "MissingPermission")
    private fun wakeFromSleep(){
        wakeLock = (getSystemService(android.content.Context.POWER_SERVICE) as PowerManager)
            .newWakeLock(
                PowerManager.FULL_WAKE_LOCK
                        or PowerManager.ACQUIRE_CAUSES_WAKEUP
                        or PowerManager.ON_AFTER_RELEASE, "disableLock"
            )
            wakeLock.acquire(20000)

            keyguard= getSystemService(android.content.Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardLock = keyguard.newKeyguardLock("disableLock")
            keyguardLock.disableKeyguard()
        }


}

