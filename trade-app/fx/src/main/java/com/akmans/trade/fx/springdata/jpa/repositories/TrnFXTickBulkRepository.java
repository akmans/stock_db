package com.akmans.trade.fx.springdata.jpa.repositories;

import java.time.LocalDateTime;
import java.util.List;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXTickBulk;

public interface TrnFXTickBulkRepository extends BaseRepository<TrnFXTickBulk, Long> {
	// Count tick data in period.
	int countFXTickInPeriod(String currencyPair, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo);
	// Find tick data in period.
	List<TrnFXTickBulk> findFirstFXTickInPeriod(String currencyPair, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo);
	// Find tick data in period.
	List<TrnFXTickBulk> findLastFXTickInPeriod(String currencyPair, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo);
	// Find tick data in period.
	List<TrnFXTickBulk> findHighestFXTickInPeriod(String currencyPair, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo);
	// Find tick data in period.
	List<TrnFXTickBulk> findLowestFXTickInPeriod(String currencyPair, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo);
}