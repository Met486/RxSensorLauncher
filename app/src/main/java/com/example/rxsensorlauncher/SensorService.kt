package com.example.rxsensorlauncher

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.preference.PreferenceManager
import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent
import com.github.pwittchen.reactivesensors.library.ReactiveSensors
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


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

    val sensorServiceNotification = SensorServiceNotification()

    //TODO test

    override fun onCreate() {
        super.onCreate()
        Log.i("DEBUG","Service onCreate")


        //TODO なぜか実行できない
        sensorServiceNotification.createNotificationChannel(applicationContext)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        start()

        Thread(Runnable {
            fun run(){
            Log.i("DEBUG","run")
            stop();
        }
        })

        return super.onStartCommand(intent, flags, startId)
    }

    fun stop(){
        stopForeground(true)
        stopSelf()
        Log.i("DEBUG","stop")
    }

    @ExperimentalStdlibApi
    fun start(){
        val context: Context = applicationContext;
        val cameraManager : CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        xSwicth=prefs.getBoolean("xSwitch",false)

        xQueue = ArrayDeque()
        yQueue = ArrayDeque()
        zQueue = ArrayDeque()

        cameraTool  = CameraTool(cameraManager)

        var bundle : Bundle = Bundle()
        bundle.putString("Message","Fragment Test")

        //TODO PreferenceChangeのイベントをRxで受け取ろう


        //TODO 改修中
        ReactiveSensors(context).observeSensor(Sensor.TYPE_GYROSCOPE)
            .subscribeOn(Schedulers.computation())
            .filter(ReactiveSensorEvent::sensorChanged)
            .observeOn(AndroidSchedulers.mainThread())
            .filter {Math.abs(it.sensorValues()[0])>=1 ||Math.abs(it.sensorValues()[1])>=1 || Math.abs(it.sensorValues()[2])>=1}
            .map{RSE : ReactiveSensorEvent? ->
                RSE?.sensorValues()
            }

            .subscribe {
                var x : Float = (it?.get(0)?.minus(it?.get(0)%1)!!)
                var y : Float = (it?.get(1)?.minus(it?.get(1)%1))
                var z : Float = (it?.get(2)?.minus(it?.get(2)%1))

                xQueue.addLast(Math.signum(x).toInt().toString())
                yQueue.addLast(Math.signum(x).toInt().toString())
                zQueue.addLast(Math.signum(x).toInt().toString())

                println("x:${x},y:${y},z:${z}")

                QueueChecker(xQueue,prefs)
            }

    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
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
                            //TODO リスナーが完成したら消しましょう
                            xSwicth=prefs.getBoolean("xSwitch",false)
                            if(xSwicth) {
                                cameraTool.cameraAction()
                                xQueue.clear()
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
}

