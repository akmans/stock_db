package com.akmans.trade.fx.springdata.jpa.repositories;

import java.time.LocalDateTime;
import java.util.List;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXTick;

public interface TrnFXTickRepository extends BaseRepository<TrnFXTick, Long> {
	// Find tick data in period.
//	List<TrnFXTick> findFXTickInPeriod(String currencyPair, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo);
	// Count tick data in period.
	int countFXTickInPeriod(String currencyPair, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo);
	// Find tick data in period.
	List<TrnFXTick> findFirstFXTickInPeriod(String currencyPair, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo);
	// Find tick data in period.
	List<TrnFXTick> findLastFXTickInPeriod(String currencyPair, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo);
	// Find tick data in period.
	List<TrnFXTick> findHighestFXTickInPeriod(String currencyPair, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo);
	// Find tick data in period.
	List<TrnFXTick> findLowestFXTickInPeriod(String currencyPair, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo);
}