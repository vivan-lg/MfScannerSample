package com.ledgergreen.mfsample.pos.scanner

import androidx.core.os.bundleOf
import com.ledgergreen.mfsample.pos.MfService
import com.morefun.yapi.ServiceResult
import com.morefun.yapi.device.scanner.OnScannedListener
import com.morefun.yapi.device.scanner.ScannerConfig
import com.morefun.yapi.device.scanner.ZebraParam
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber

class IdScanner @Inject constructor(
    private val mfService: MfService,
) {
    suspend fun scan(): String? = suspendCancellableCoroutine { continuation ->
        val scannerCallback = object : ScannerCallback {
            override fun onResult(result: String) {
                continuation.resume(result)
            }

            override fun onError(error: String) {
                continuation.resumeWithException(Exception(error))
            }

            override fun onCancel() {
                continuation.resume(null)
            }
        }

        start(scannerCallback)

        continuation.invokeOnCancellation {
            stop()
        }
    }

    private fun start(callback: ScannerCallback) {
        Timber.d("MfScanner start scanner")
        mfService.scanner.initScanner(
            bundleOf(
                ScannerConfig.ZEBRA_PARAM to
                    arrayListOf(
                        ZebraParam(
                            ScannerConfig.ZerbaParamNum.PICKLIST_MODE,
                            ScannerConfig.ZebraParamVal.SUPP_AUTOD,
                        ),
                        // enables MRZ for MRD documents
                        ZebraParam(681.toShort(), 1.toByte()),
                        ZebraParam(685.toShort(), 11.toByte()),
                        // enables PDF417 for Driver license
                        ZebraParam(15.toShort(), 1.toByte()),
                        ZebraParam(277.toShort(), 1.toByte()),
                    ),
                ScannerConfig.COMM_SCANNER_TYPE to 0,
            ),
        )

        mfService.scanner.startScan(
            TIMEOUT,
            object : OnScannedListener.Stub() {
                override fun onScanResult(retCode: Int, data: ByteArray?) {
                    when (retCode) {
                        ServiceResult.Success -> {
                            Timber.d("MfScanner success")
                            data?.let {
                                callback.onResult(String(data))
                            }
                        }

                        ServiceResult.Scanner_CALLBACK_FAIL -> {
                            Timber.d("MfScanner failed Scanner_CALLBACK_FAIL")
                            callback.onCancel()
                        }

                        else -> {
                            Timber.w("MfScanner retCode $retCode")
                        }
                    }
                }
            },
        )
    }

    private fun stop() {
        Timber.d("MfScanner stop")
        mfService.scanner.stopScan()
    }

    companion object {
        private const val TIMEOUT = 70_000
    }
}

private interface ScannerCallback {
    fun onResult(result: String)
    fun onError(error: String)
    fun onCancel()
}
