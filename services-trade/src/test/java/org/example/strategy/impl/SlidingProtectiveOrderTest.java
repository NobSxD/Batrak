package org.example.strategy.impl;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class SlidingProtectiveOrderTest {

	@Test
	void tt() throws InterruptedException {
		Observer<BigDecimal> observer = new Observer<BigDecimal>() {
			Disposable disposable;
			@Override
			public void onSubscribe(@NonNull Disposable d) {
				System.out.println("Получил подписку");
				this.disposable = d;
			}

			@Override
			public void onNext(@NonNull BigDecimal bigDecimal) {

			}

			@Override
			public void onError(@NonNull Throwable e) {

			}

			@Override
			public void onComplete() {

			}
		};



	}

}