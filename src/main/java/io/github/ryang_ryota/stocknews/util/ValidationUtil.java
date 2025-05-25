package io.github.ryang_ryota.stocknews.util;

/**
 * 入力値のバリデーションを行うユーティリティクラス
 */
public class ValidationUtil {

    /**
     * 株式銘柄コードの形式が正しいかを検証します
     *
     * @param symbol 検証対象の株式銘柄コード
     * @return 銘柄コードが1-5文字の英大文字のみで構成されている場合true、それ以外の場合false
     */
    public static boolean isValidStockSymbol(String symbol) {
        return symbol != null &&
                symbol.matches("[A-Z]{1,5}") &&
                symbol.length() <= 5;
    }
}