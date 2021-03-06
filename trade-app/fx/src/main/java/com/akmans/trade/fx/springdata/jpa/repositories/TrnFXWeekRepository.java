package com.akmans.trade.fx.springdata.jpa.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;

public interface TrnFXWeekRepository extends BaseRepository<TrnFXWeek, FXTickKey> {
	// Find previous data by key.
	Optional<TrnFXWeek> findPrevious(String currencyPair, LocalDateTime dateTime);
}