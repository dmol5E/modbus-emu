package org.reminder.edu.controller;

import java.util.Collection;
import java.util.LinkedList;

import org.reminder.edu.modbusslave.comm.CoilSensorMapper;
import org.reminder.edu.modbusslave.entity.Sensor;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import net.wimpi.modbus.procimg.ObservableDigitalOut;

public class DigitalOutRow {

    private ObservableDigitalOut digitalOut;

    private IntegerProperty digOutAdress;
    private IntegerProperty digOutValue;
    private StringProperty digOutTarget;
    private StringProperty digOutComment;

    private DigitalOutRow(Sensor sensor, ObservableDigitalOut digitalOut,
            int adress, Target target) {
        super();
        setSensor(sensor);
        setDigitalOut(digitalOut, adress, target);
    }

    private enum Target {

        ONN_OFF("Включить/Выключить"),
        NORMAL_STATE("Установить нормальное состояние"), 
        GENERATE_ALARM("Сгенерировать треовгу"),
        GENERATE_DEFECT("Сгенерировать неисправность");

        private String targetValue;

        private Target(String targetValue) {
            this.targetValue = targetValue;
        }

        @Override
        public String toString() {
            return targetValue;
        }
    }

    public void setSensor(Sensor sensor) {
        getDigOutComment()
                .setValue(sensor.getName() + ", адрес:" + (sensor.getId() - 1));
    }

    private void setDigitalOut(ObservableDigitalOut digitalOut, int number,
            Target target) {
        this.digitalOut = digitalOut;
        getDigOutAdress().setValue(number);
        getDigOutValue().setValue(digitalOut.isSet() ? 1 : 0);
        getDigOutTarget().setValue(target.toString());
    }

    public IntegerProperty getDigOutAdress() {
        if (digOutAdress == null) {
            digOutAdress = new SimpleIntegerProperty(this, "digOutAdress");
        }
        return digOutAdress;
    }

    public IntegerProperty getDigOutValue() {
        if (digOutValue == null) {
            digOutValue = new SimpleIntegerProperty(this, "digOutValue");
            digOutValue.addListener(new ChangeListener<Number>() {

                @Override
                public void changed(
                        ObservableValue<? extends Number> observable,
                        Number oldValue, Number newValue) {
                    if (!(newValue.equals(1) || newValue.equals(0))) {
                        newValue = oldValue;
                        return;
                    }

                    digitalOut.set(newValue.equals(1));
                }
            });
        }
        return digOutValue;
    }

    public StringProperty getDigOutTarget() {
        if (digOutTarget == null) {
            digOutTarget = new SimpleStringProperty(this, "digOutTarget");
        }
        return digOutTarget;
    }

    public StringProperty getDigOutComment() {
        if (digOutComment == null) {
            digOutComment = new SimpleStringProperty(this, "digOutComment");
        }
        return digOutComment;
    }

    public static Collection<DigitalOutRow> generateDigitalOutRow(
            Collection<CoilSensorMapper> mappers) {

        Collection<DigitalOutRow> result = new LinkedList<DigitalOutRow>();
        int i = 0;
        for (CoilSensorMapper mapper : mappers) {
            int target = 0;
            for (ObservableDigitalOut digOut : mapper.getDigitalOuts()) {
                DigitalOutRow item = new DigitalOutRow(mapper.getSensor(),
                        digOut, i++, Target.values()[target++]);
                result.add(item);
            }
        }

        return result;
    }
}
