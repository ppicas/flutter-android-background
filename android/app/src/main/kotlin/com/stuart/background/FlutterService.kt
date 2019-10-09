package com.stuart.background

import android.app.Service
import android.content.Intent
import android.os.IBinder

class FlutterService : Service() {

    override fun onBind(intent: Intent): IBinder? = null

}
