package io.github.ryang_ryota.stocknews.model;

import lombok.Data;

/**
 * 株式情報を保持するモデルクラス
 */
@Data
public class Stock {
    /**
     * 銘柄コード
     */
    private String symbol;
    /**
     * タイムスタンプ (YYYY-MM-DD形式)
     */
    private String timestamp;
    /**
     * 始値
     */
    private Double open;
    /**
     * 高値
     */
    private Double high;
    /**
     * 安値
     */
    private Double low;
    /**
     * 終値
     */
    private Double close;
    /**
     * 出来高
     */
    private Long volume;
}