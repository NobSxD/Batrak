ALTER TABLE node_change  MODIFY COLUMN change_type ENUM('Binance', 'Bybit', 'Mexc', 'Test');
ALTER TABLE node_user  MODIFY COLUMN change_type ENUM('Binance', 'Bybit', 'Mexc', 'Test');
ALTER TABLE account  MODIFY COLUMN change_type ENUM('Binance', 'Bybit', 'Mexc', 'Test');