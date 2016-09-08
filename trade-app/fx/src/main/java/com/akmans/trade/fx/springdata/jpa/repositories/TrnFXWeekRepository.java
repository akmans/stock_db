package com.akmans.trade.fx.springdata.jpa.repositories;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFXWeek;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;

public interface TrnFXWeekRepository extends BaseRepository<TrnFXWeek, FXTickKey> {
}