package org.reminder.edu.modbusmaster.config;

import org.reminder.edu.model.MasterModel;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class ModbusMasterModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MasterModel.class).in(Singleton.class);
    }
}
