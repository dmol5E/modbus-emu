package org.reminder.edu.modbusslave.config;

import org.reminder.edu.modbusslave.ModBusSecondary;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class ModbusSecondaryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ModBusSecondary.class).in(Singleton.class);
    }
}