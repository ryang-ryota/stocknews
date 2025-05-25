# 株価リアルタイム1分足チャート表示システム

![デモ](img/demo.gif)

## 概要

Alpha Vantage APIを利用し、指定した株式シンボルの直近2時間分の1分足データをリアルタイム（2分ごと更新）でグラフ表示するWebアプリです。  
Spring Boot（WebFlux, SSE）＋ JavaScript（Chart.js）で構成されています。

## 主な機能

- 株式シンボル指定による1分足チャート表示
- 2分ごとに自動更新（SSE）
- 直近2時間分のデータを常に表示

## 必要なもの

- Java 21 以上
- Alpha Vantage APIキー

## 使い方

1. `application.properties` にAlpha VantageのAPIキーを設定
2. プロジェクトをビルド・起動
3. ブラウザで `http://localhost:8080/` にアクセス
4. シンボル（例: IBM, AAPL）を入力し、「表示」ボタンを押す

## 注意事項

- Alpha Vantageの無料APIは1分あたり5リクエストまでです
- データは最大15分遅延です
- 本システムは学習・個人利用目的です

