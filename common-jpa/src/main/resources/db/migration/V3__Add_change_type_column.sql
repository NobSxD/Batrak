ALTER TABLE node_change  MODIFY COLUMN change_type ENUM('binance', 'bybit', 'mexc', 'test');
ALTER TABLE node_user  MODIFY COLUMN change_type ENUM('binance', 'bybit', 'mexc', 'test');
ALTER TABLE account  MODIFY COLUMN change_type ENUM('binance', 'bybit', 'mexc', 'test');