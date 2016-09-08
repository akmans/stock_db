package com.akmans.trade.fx.springdata.jpa.repositories;

import com.akmans.trade.core.springdata.jpa.repositories.BaseRepository;
import com.akmans.trade.fx.springdata.jpa.entities.TrnFX6Hour;
import com.akmans.trade.fx.springdata.jpa.keys.FXTickKey;

public interface TrnFX6HourRepository extends BaseRepository<TrnFX6Hour, FXTickKey> {
}