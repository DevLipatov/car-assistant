package com.main.carassistant.db;

import com.main.carassistant.model.BusinessCard;
import com.main.carassistant.model.Cathegory;
import com.main.carassistant.model.Costs;
import com.main.carassistant.model.Stats;

public final class Contract {

    public static final Class<?>[] MODELS =
            {
                    Stats.class,
                    BusinessCard.class,
                    Costs.class,
                    Cathegory.class
            };
}
