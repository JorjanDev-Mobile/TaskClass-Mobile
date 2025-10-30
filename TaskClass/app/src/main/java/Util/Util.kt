package Util

import android.content.Context
import android.content.Intent

class Util {

    companion object{
        fun OpenActivity(context: Context, objClassActivity: Class<*>){
            val objIntent = Intent(context, objClassActivity)
            context.startActivity(objIntent)
        }
    }
}