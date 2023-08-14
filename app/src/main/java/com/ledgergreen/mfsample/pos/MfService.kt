package com.ledgergreen.mfsample.pos

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.os.bundleOf
import com.morefun.yapi.device.scanner.InnerScanner
import com.morefun.yapi.engine.DeviceServiceEngine
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

private enum class ServiceConnectionState {
    DISCONNECTED, CONNECTING, CONNECTED
}

@Singleton
class MfService @Inject constructor(@ApplicationContext private val context: Context) {

    private var deviceServiceEngine: DeviceServiceEngine? = null

    val scanner: InnerScanner get() = deviceServiceEngine().innerScanner

    private var state: ServiceConnectionState = ServiceConnectionState.DISCONNECTED
    private var attempt = 1

    fun initialize() {
        if (state == ServiceConnectionState.DISCONNECTED) {
            Timber.v("bind MF service. attempt ${attempt++}")
            state = ServiceConnectionState.CONNECTING

            context.bindService(
                Intent().apply {
                    action = ACTION_MOREFUN_SERVICE
                    setPackage(PACKAGE_MOREFUN)
                },
                serviceConnection,
                Application.BIND_AUTO_CREATE,
            )
        }
    }

    private fun deviceServiceEngine(): DeviceServiceEngine {
        if (deviceServiceEngine == null) {
            initialize()
        }
        return deviceServiceEngine ?: run {
            // go into recursion
            Timber.w("deviceServiceEngine() returned null. Reconnect")
            deviceServiceEngine()
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Timber.d("MoreFun service connected")
            state = ServiceConnectionState.CONNECTED

            deviceServiceEngine = DeviceServiceEngine.Stub.asInterface(service)

            deviceServiceEngine!!.login(
                bundleOf(), /* businessId */ "00000000",
            )

            service?.let(::linkToDeath)
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            Timber.i("MoreFun service disconnected")
            state = ServiceConnectionState.DISCONNECTED
            initialize()
        }

        private fun linkToDeath(service: IBinder) {
            service.linkToDeath(
                {
                    Timber.d("MoreFun service linkToDeath")
                    state = ServiceConnectionState.DISCONNECTED

                    deviceServiceEngine = null
                    initialize()
                },
                0,
            )
        }
    }

    private fun teardown() {
        context.unbindService(serviceConnection)
    }

    companion object {
        private const val ACTION_MOREFUN_SERVICE = "com.morefun.ysdk.service"
        private const val PACKAGE_MOREFUN = "com.morefun.ysdk"
    }
}
