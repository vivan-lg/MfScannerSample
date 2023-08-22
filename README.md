This branch shows how scanner callback called multiple times.

How to reproduce.
1. Hit on Scan button. Read pdf417 or mrz
2. Repeat 1

Logger will print
```
startScan
ScanResult called 1 times
ScanResult called 2 times
ScanResult called 3 times
ScanResult called 4 times
ScanResult called 5 times
ScanResult called 6 times
ScanResult called 7 times
ScanResult called 8 times
ScanResult called 9 times
ScanResult called 10 times
ScanResult called 11 times
```
and every time innerScanner.startScan() method called the more callbacks will be called
