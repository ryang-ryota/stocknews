package io.github.ryang_ryota.stocknews.router;

import io.github.ryang_ryota.stocknews.handler.StockHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * 株式情報に関するルーティング設定を行うクラス
 */
@Configuration
public class StockRouter {

    /**
     * 株式情報のストリーミングに関するルーティングを設定する
     *
     * @param stockHandler 株式情報を処理するハンドラー
     * @return ルーティング設定
     */
    @Bean
    public RouterFunction<ServerResponse> stockRoutes(StockHandler stockHandler) {
        return route(GET("/stocks/stream")
                        .and(accept(MediaType.TEXT_EVENT_STREAM)),
                stockHandler::streamStockData);
    }
}