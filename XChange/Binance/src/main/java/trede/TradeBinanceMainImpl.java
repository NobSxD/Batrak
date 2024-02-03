package trede;

import ches.filter.Change;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.binance.BinanceSubscriptionType;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.TradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Log4j
@RequiredArgsConstructor
public class TradeBinanceMainImpl implements Change {
    private static final Logger LOG = LoggerFactory.getLogger(BinanceStreamingExchange.class);
    public void setTradesEth() throws InterruptedException {
        ExchangeSpecification spec = StreamingExchangeFactory.INSTANCE.createExchange(BinanceStreamingExchange.class)
                .getDefaultExchangeSpecification();
        BinanceStreamingExchange exchange = (BinanceStreamingExchange) StreamingExchangeFactory.INSTANCE.createExchange(spec);

        // First, we need to subscribe to at least one currency pair at connection time
// Note: at connection time, the live subscription is disabled
        ProductSubscription subscription =
                ProductSubscription.create().addTrades(CurrencyPair.BTC_USDT).addOrderbook(CurrencyPair.BTC_USDT).build();
        exchange.connect(subscription).blockingAwait();

        // We subscribe to trades update for the currency pair subscribed at connection time (BTC)
// For live unsubscription, you need to add a doOnDispose that will call the method unsubscribe in BinanceStreamingMarketDataService
        Disposable tradesBtc = exchange.getStreamingMarketDataService()
                .getTrades(CurrencyPair.BTC_USDT)
                .doOnDispose(
                        () -> exchange.getStreamingMarketDataService().unsubscribe(CurrencyPair.BTC_USDT, BinanceSubscriptionType.TRADE))
                .subscribe(trade -> {
                    LOG.info("Trade: {}", trade);
                    File file = new File("D:/Проекты/new/treadeBot/XChange/Binance/src/main/resources/json.txt");
                    FileOutputStream writer = new FileOutputStream(file, true);
                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                                    .create();
                    String json = gson.toJson(trade);

                    writer.write(json.getBytes());
                    writer.close();
                });
        TradeService tradeService = exchange.getTradeService();
        String s = "stop";
        try {
            tradeService.cancelOrder(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }




// Now we enable the live subscription/unsubscription to add new currencies to the streams
        exchange.enableLiveSubscription();

        // We live subscribe a new currency pair to the trades update
//        Disposable tradesEth = exchange.getStreamingMarketDataService()
//                .getTrades(CurrencyPair.ETH_USDT)
//                .doOnDispose(
//                        () -> exchange.getStreamingMarketDataService().unsubscribe(CurrencyPair.ETH_USDT, BinanceSubscriptionType.TRADE))
//                .subscribe(trade -> {
//                    LOG.info("Trade: {}", trade);
//                });

        Thread.sleep(30000);

// We unsubscribe from the streams
        tradesBtc.dispose();
     //   tradesEth.dispose();
    }

    @Override
    public String infoAccount(String publicKey, String secretKey, String userName) {
        ExchangeSpecification exSpec = new BinanceExchange().getDefaultExchangeSpecification();
        exSpec.setUserName(userName);
        exSpec.setApiKey(publicKey);
        exSpec.setSecretKey(secretKey);
        Exchange binance = ExchangeFactory.INSTANCE.createExchange(exSpec);

        AccountService accountService = binance.getAccountService();
        AccountInfo accountInfo;
        try {
            accountInfo = accountService.getAccountInfo();
            System.out.println(accountInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return accountInfo.getUsername();

    }

    @Override
    public void startTread(String pair) {

    }

    @Override
    public void stopTread() {

    }

    @Override
    public List<String> pair() {
        
        return null;
    }

}
