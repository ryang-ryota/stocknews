package io.github.ryang_ryota.stocknews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Bootアプリケーションのメインクラス
 * <p>
 * このクラスは株価ニュースアプリケーションのエントリーポイントとして機能し、
 * Spring Boot の自動設定とコンポーネントスキャンを有効化します。
 */
@SpringBootApplication
public class Stocknews {

    /**
     * アプリケーションのメインメソッド
     *
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(Stocknews.class, args);
    }

}