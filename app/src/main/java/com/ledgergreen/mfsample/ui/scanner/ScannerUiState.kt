package com.ledgergreen.mfsample.ui.scanner

data class ScannerUiState(
    val mfServiceInitialized: Boolean,
    val scannerInProgress: Boolean,
    val scanResult: String?,
)
