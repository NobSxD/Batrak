package org.example.observer.pair;

import lombok.RequiredArgsConstructor;
import org.example.observer.StockData;
import org.example.observer.Subject;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataBtcUsdtImpl extends StockData implements Subject {
}
