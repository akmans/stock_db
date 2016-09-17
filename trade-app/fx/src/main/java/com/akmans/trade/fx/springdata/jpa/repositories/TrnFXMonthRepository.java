package com.akmans.trade.fx.springdata.jpa.repositories;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXMonth;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;

public interface TrnFXMonthRepository extends BaseRepository<TrnFXMonth, FXTickKey> {
	// Find previous data by key.
	Optional<TrnFXMonth> findPrevious(String currencyPair, ZonedDateTime dateTime);
}