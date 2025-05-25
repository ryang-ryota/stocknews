package io.github.ryang_ryota.stocknews.service;

import io.github.ryang_ryota.stocknews.model.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 株価情報を取得・加工するサービスクラス
 */
@Service
@RequiredArgsConstructor
public class StockService {

    private final WebClient webClient;
    private final String apiKey;

    /**
     * 指定された銘柄の2時間分の1分足データを取得する
     *
     * @param symbol 銘柄コード
     * @return 2時間分の株価データのFlux
     */
    public Flux<List<Stock>> getTwoHoursStockData(String symbol) {
        return Flux.interval(Duration.ZERO, Duration.ofMinutes(2))
                .flatMap(tick -> fetchIntradayData(symbol)
                        .map(this::extractLast2Hours));
    }

    /**
     * AlphaVantage APIから1分足データを取得する
     *
     * @param symbol 銘柄コード
     * @return 株価データのリストを含むMono
     */
    private Mono<List<Stock>> fetchIntradayData(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("function", "TIME_SERIES_INTRADAY")
                        .queryParam("symbol", symbol)
                        .queryParam("interval", "1min")
                        .queryParam("outputsize", "compact")
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .<List<Stock>>map(this::parseResponse)
                .onErrorResume(e -> Mono.just(new ArrayList<Stock>()));
    }

    /**
     * APIレスポンスをStock型のリストに変換する
     *
     * @param response APIレスポンス
     * @return 株価データのリスト
     */
    private List<Stock> parseResponse(Map<String, Object> response) {
        Map<String, Object> metaData = (Map<String, Object>) response.get("Meta Data");
        Map<String, Map<String, String>> timeSeries =
                (Map<String, Map<String, String>>) response.get("Time Series (1min)");
        if (timeSeries == null) return Collections.emptyList();

        List<Stock> stocks = timeSeries.entrySet().stream()
                .map(entry -> {
                    String timestamp = entry.getKey();
                    Map<String, String> values = entry.getValue();
                    Stock stock = new Stock();
                    stock.setSymbol(metaData.get("2. Symbol").toString());
                    stock.setTimestamp(timestamp);
                    stock.setOpen(Double.parseDouble(values.get("1. open")));
                    stock.setHigh(Double.parseDouble(values.get("2. high")));
                    stock.setLow(Double.parseDouble(values.get("3. low")));
                    stock.setClose(Double.parseDouble(values.get("4. close")));
                    stock.setVolume(Long.parseLong(values.get("5. volume")));
                    return stock;
                })
                .sorted(Comparator.comparing(Stock::getTimestamp))
                .collect(Collectors.toList());
        return stocks;
    }

    /**
     * 直近2時間分(120件)のデータのみを抽出する
     * 取引時間外の場合は直近取引分を含む
     *
     * @param stocks 株価データのリスト
     * @return フィルタリング後の株価データのリスト
     */
    private List<Stock> extractLast2Hours(List<Stock> stocks) {
        if (stocks.isEmpty()) return stocks;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/New_York"));
        LocalDateTime twoHoursAgo = now.minusHours(2);

        List<Stock> filtered = stocks.stream()
                .filter(s -> {
                    LocalDateTime ts = LocalDateTime.parse(s.getTimestamp(), formatter);
                    return !ts.isBefore(twoHoursAgo);
                })
                .collect(Collectors.toList());

        if (filtered.size() < 120) {
            int from = Math.max(stocks.size() - 120, 0);
            return stocks.subList(from, stocks.size());
        } else {
            return filtered;
        }
    }
}