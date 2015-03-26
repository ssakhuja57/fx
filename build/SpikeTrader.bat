@echo off
cd /d "C:\Program Files\Candleworks\ForexConnectAPIx64\bin"
java -Djava.library.path=.;./java -jar %~dp0\SpikeTrader.jar