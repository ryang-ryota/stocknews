package io.github.ryang_ryota.stocknews.handler;

import io.github.ryang_ryota.stocknews.service.StockService;
import io.github.ryang_ryota.stocknews.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 株価情報のストリーム配信を行うハンドラクラス
 */
@Component
@RequiredArgsConstructor
public class StockHandler {

    private final StockService stockService;

    /**
     * 指定された株式銘柄の2時間分の株価データをSSEでストリーム配信します
     *
     * @param request クライアントからのリクエスト。クエリパラメータ"symbol"に株式銘柄コードを含める必要があります
     * @return 株価データのストリームを含むServerResponseのMono。銘柄コードが不正な場合は400 Bad Requestを返します
     */
    public Mono<ServerResponse> streamStockData(ServerRequest request) {
        String symbol = request.queryParam("symbol").orElse("");
        if (!ValidationUtil.isValidStockSymbol(symbol)) {
            return ServerResponse.badRequest().bodyValue("Invalid stock symbol");
        }

        // SSEで2時間分のデータを配信
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(stockService.getTwoHoursStockData(symbol), List.class);
    }
}