package com.example.rxsensorlauncher

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
data class AppInfo(
    val icon : Drawable,
    var label : String,
    var componentName : ComponentName
){
    fun launch(context: Context): ComponentName? {
        try {
            val intent = Intent(Intent.ACTION_MAIN).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                it.addCategory(Intent.CATEGORY_LAUNCHER)
                it.component = componentName

            }
            //context.startActivity(intent)
            return componentName
        } catch (e: ActivityNotFoundException) {
        }
        return null
    }
}

