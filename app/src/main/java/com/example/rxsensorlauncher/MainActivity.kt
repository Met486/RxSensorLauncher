package com.example.rxsensorlauncher

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent
import com.github.pwittchen.reactivesensors.library.ReactiveSensors
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.internal.operators.flowable.FlowableTakeUntilPredicate
import io.reactivex.schedulers.Schedulers


class MainActivity : AppCompatActivity() {

    lateinit var cameraTool : CameraTool
    @ExperimentalStdlibApi
    lateinit var queue : ArrayDeque<String>

    @SuppressLint("CheckResult")
    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context: Context = applicationContext;
        val cameraManager : CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        queue = ArrayDeque()
        cameraTool  = CameraTool(cameraManager)


        //TODO 改修中
        ReactiveSensors(context).observeSensor(Sensor.TYPE_GYROSCOPE)
            .subscribeOn(Schedulers.computation())
            .filter(ReactiveSensorEvent::sensorChanged)
            .observeOn(AndroidSchedulers.mainThread())
            .filter {Math.abs(it.sensorValues()[0])>=1 ||Math.abs(it.sensorValues()[1])>=1 || Math.abs(it.sensorValues()[2])>=1}
            .map{RSE : ReactiveSensorEvent? ->
                RSE?.sensorValues()
            }
//            .map(Function<ReactiveSensorEvent,FloatArray> {
//                it.sensorValues()
//            })
            //.filter{}
            .subscribe {
                var x : Float = (it?.get(0)?.minus(it?.get(0)%1)!!)
                var y : Float = (it?.get(1)?.minus(it?.get(1)%1))
                var z : Float = (it?.get(2)?.minus(it?.get(2)%1))

                queue.addLast(Math.signum(x).toInt().toString())
                println("x:${x},y:${y},z:${z}")

                QueueChecker(queue)
            }


/*            .map( {it : ReactiveSensorEvent ->{
                it.sensorValues()[0] -it.sensorValues()[0]%1;
                it.sensorValues()[1] -it.sensorValues()[1]%1;
                it.sensorValues()[2] -it.sensorValues()[2]%1 }
            }).doOnNext{
                println("number is " + it)
            }.distinctUntilChanged()
            .subscribe()*/

//          {it.sensorValues()[0] -it.sensorValues()[0]%1;
//                it.sensorValues()[1] -it.sensorValues()[1]%1;
//                it.sensorValues()[2] -it.sensorValues()[2]%1
//            }.doOnNext{
//                println("x:" + it)
//            }


    }

    @ExperimentalStdlibApi
    protected fun QueueChecker(iQueue : ArrayDeque<String>){
        println("Queue Check is launched")
        val list = iQueue.reversed()
        var iterator = list.iterator()
        var i = 0
        var s = arrayOf("","")

        while(i<2 && iterator.hasNext()){
            s[1-i] = iterator.next().toString()
            i++
        }
        println("s[0] = ${s[0]} , s[1] = ${s[1]}")

        if(s[0] != null && s[1] != null)
        {
            when(s[0]){
                "-1" ->{
                    when(s[1]){
                        "1" ->{
                            cameraTool.cameraAction()
                            queue.clear()
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

        //TODO Delete
        fun check(){
            println("Check")
        }
}