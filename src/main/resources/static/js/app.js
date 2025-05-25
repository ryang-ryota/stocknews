/**
 * 株価チャートを表示・更新するメインスクリプト
 */
document.addEventListener('DOMContentLoaded', function () {
    // DOM要素の参照を取得
    const form = document.getElementById('symbolForm');
    const symbolInput = document.getElementById('symbol');
    const stopBtn = document.getElementById('stopBtn');
    const intervalBar = document.getElementById('intervalBar');
    const intervalText = document.getElementById('intervalText');
    const chartCtx = document.getElementById('stockChart').getContext('2d');

    // グローバル変数の初期化
    let eventSource = null;
    let chart = null; 
    let updateIntervalSec = 120; // 2分間隔で更新
    let intervalTimer = null;
    let intervalValue = 0;

    /**
     * インターバルバーをリセットし、更新までの残り時間を表示する
     * - インターバル値を0に戻す
     * - 1秒ごとにインターバルバーと残り時間を更新
     * - updateIntervalSecに達したら値をリセット
     */
    function resetIntervalBar() {
        intervalValue = 0;
        intervalBar.value = 0;
        intervalText.textContent = '0s';
        if (intervalTimer) clearInterval(intervalTimer);
        intervalTimer = setInterval(() => {
            intervalValue += 1;
            intervalBar.value = (intervalValue / updateIntervalSec) * 100;
            intervalText.textContent = `${intervalValue}s`;
            if (intervalValue >= updateIntervalSec) {
                intervalValue = 0;
                intervalBar.value = 0;
                intervalText.textContent = '0s';
            }
        }, 1000);
    }

    /**
     * Server-Sent Eventsの接続を停止する
     * - EventSourceを閉じる
     * - インターバルタイマーをクリア
     */
    function stopSSE() {
        if (eventSource) {
            eventSource.close();
            eventSource = null;
        }
        if (intervalTimer) clearInterval(intervalTimer);
    }

    // フォーム送信時の処理
    form.addEventListener('submit', function (e) {
        e.preventDefault();
        // 銘柄コードを取得して大文字に変換
        const symbol = symbolInput.value.trim().toUpperCase();
        if (!symbol) return;

        // 既存の接続を停止
        stopSSE();

        // 2時間分のデータを保持する配列
        let stockData = [];

        // SSE接続を開始
        eventSource = new EventSource(`/stocks/stream?symbol=${symbol}`);

        // データ受信時の処理
        eventSource.onmessage = function (event) {
            const data = JSON.parse(event.data);

            // 最新120件のみを保持
            stockData = data.slice(-120);

            // チャートの描画とインターバルバーのリセット
            drawChart(stockData);
            resetIntervalBar();
        };

        // エラー発生時は接続を停止
        eventSource.onerror = function () {
            stopSSE();
        };
    });

    // 停止ボタンクリック時の処理
    stopBtn.addEventListener('click', function () {
        stopSSE();
    });

    /**
     * 株価データをチャートに描画する
     * @param {Array} data - 株価データの配列
     */
    function drawChart(data) {
        // 時刻ラベルをHH:mm形式で生成
        const labels = data.map(item => {
            const dt = new Date(item.timestamp);
            return dt.getHours().toString().padStart(2, '0') + ':' +
                   dt.getMinutes().toString().padStart(2, '0');
        });
        // 終値データを抽出
        const closes = data.map(item => item.close);

        // 既存のチャートがあれば破棄
        if (chart) chart.destroy();

        // 新しいチャートを生成
        chart = new Chart(chartCtx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: '終値',
                    data: closes,
                    borderColor: '#6c7a89',
                    backgroundColor: 'rgba(186,190,204,0.2)',
                    fill: true,
                    tension: 0.2
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { display: true }
                },
                scales: {
                    x: { display: true, title: { display: false } },
                    y: { display: true, title: { display: false } }
                }
            }
        });
    }
});