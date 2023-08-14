package com.ledgergreen.mfsample.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledgergreen.mfsample.pos.MfService
import com.ledgergreen.mfsample.pos.scanner.IdScanner
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val mfService: MfService,
    private val scanner: IdScanner,
) : ViewModel() {

    private var scanJob: Job? = null

    val state = MutableStateFlow(
        ScannerUiState(
            mfServiceInitialized = false,
            scannerInProgress = false,
            scanResult = null,
        ),
    )

    init {
        viewModelScope.launch {
            mfService.initialize()
            state.value = state.value.copy(
                mfServiceInitialized = true,
            )
        }
    }

    fun startScan() {
        scanJob?.cancel()

        scanJob = viewModelScope.launch {
            state.value = state.value.copy(
                scannerInProgress = true,
                scanResult = null,
            )

            runCatching {
                val scanResult = scanner.scan()
                if (scanResult != null) {
                    state.value = state.value.copy(
                        scanResult = scanResult,
                        scannerInProgress = false,
                    )
                } else {
                    state.value = state.value.copy(
                        scanResult = null,
                        scannerInProgress = false,
                    )
                }
            }
        }
    }

    fun stopScan() {
        scanJob?.cancel()
    }
}
