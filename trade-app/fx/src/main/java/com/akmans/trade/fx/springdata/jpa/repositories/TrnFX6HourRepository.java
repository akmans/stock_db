package com.akmans.trade.fx.springdata.jpa.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFX6Hour;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;

public interface TrnFX6HourRepository extends BaseRepository<TrnFX6Hour, FXTickKey> {
	// Find previous data by key.
	Optional<TrnFX6Hour> findPrevious(String currencyPair, LocalDateTime dateTime);
}