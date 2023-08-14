package com.ledgergreen.mfsample.ui.scanner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState().value

    ScannerScreen(
        state,
        onScanClick = viewModel::startScan,
        onStopScanClick = viewModel::stopScan,
    )
}

@Composable
fun ScannerScreen(
    state: ScannerUiState,
    onScanClick: () -> Unit,
    onStopScanClick: () -> Unit,
) {
    Scaffold { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            Row(Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        if (!state.scannerInProgress) {
                            onScanClick()
                        } else {
                            onStopScanClick()
                        }
                    },
                    enabled = !state.scannerInProgress && state.mfServiceInitialized,
                    content = { Text("Start scanner") },
                )
                Spacer(Modifier.width(8.dp))
                val mfServiceConnectionStatus = if (state.mfServiceInitialized) {
                    "connected"
                } else {
                    "not connected"
                }
                Text("MF service $mfServiceConnectionStatus")
            }
            Spacer(Modifier.height(8.dp))
            LazyColumn(Modifier.weight(1f)) {
                state.scanResult?.let { scanResult ->
                    item("scan_result") {
                        Text(scanResult)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ScannerScreenIdle() {
    MaterialTheme {
        ScannerScreen(
            state = ScannerUiState(
                mfServiceInitialized = true,
                scannerInProgress = false,
                scanResult = null,
            ),
            onScanClick = { },
            onStopScanClick = { },
        )
    }
}
