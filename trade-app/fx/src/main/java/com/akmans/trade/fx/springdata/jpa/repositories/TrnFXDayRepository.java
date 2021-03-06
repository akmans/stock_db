package com.akmans.trade.fx.springdata.jpa.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXDay;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;

public interface TrnFXDayRepository extends BaseRepository<TrnFXDay, FXTickKey> {
	// Find tick data in period.
	List<TrnFXDay> findFXDayInPeriod(String currencyPair, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo);

	// Find previous data by key.
	Optional<TrnFXDay> findPrevious(String currencyPair, LocalDateTime dateTime);
}