package com.akmans.trade.fx.springdata.jpa.repositories;

import java.time.ZonedDateTime;
import java.util.List;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXTick;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;

public interface TrnFXTickRepository extends BaseRepository<TrnFXTick, FXTickKey> {
	// Find tick data in period.
	List<TrnFXTick> findFXTickInPeriod(String currencyPair, ZonedDateTime dateTimeFrom, ZonedDateTime dateTimeTo);
}