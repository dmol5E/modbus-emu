package org.reminder.edu.modbusslave.config;

import org.reminder.edu.modbuscommon.entity.repository.SensorRepository;
import org.reminder.edu.modbuscommon.entity.service.SensorBehaviorService;
import org.reminder.edu.modbusslave.ModBusSecondary;
import org.reminder.edu.modbusslave.comm.ModbusProcessImage;
import org.reminder.edu.modbusslave.repository.ProcessImageRepository;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class ModbusSecondaryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ModbusProcessImage.class).in(Singleton.class);
        bind(SensorRepository.class).to(ProcessImageRepository.class).in(Singleton.class);
        bind(ModBusSecondary.class).in(Singleton.class);
    }

    @Provides
    SensorBehaviorService providSensorBehaviorService(SensorRepository repository) {
        return new SensorBehaviorService(repository);
    }
}
