package com.akmans.trade.fx.springdata.jpa.repositories;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXHour;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;

public interface TrnFXHourRepository extends BaseRepository<TrnFXHour, FXTickKey> {
	// Find FX Hour data in period.
	List<TrnFXHour> findFXHourInPeriod(String currencyPair, ZonedDateTime dateTimeFrom, ZonedDateTime dateTimeTo);

	// Find previous data by key.
	Optional<TrnFXHour> findPrevious(String currencyPair, ZonedDateTime dateTime);
}