package com.wiokru.library.utils;

import com.wiokru.library.entity.Reserved;
import com.wiokru.library.repositories.ReservedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ScheduledTasks {

    @Autowired
    ReservedRepository reservedRepository;

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    //fired daily
    @Scheduled(initialDelay = 1000, fixedRate = 86400000)
    public void overtimeReservations() {

        LOGGER.setLevel(Level.INFO);

        for (Reserved reserved : reservedRepository.findAll()){
            if (reserved.getDueDate().isAfter(LocalDate.now())){
                reservedRepository.delete(reserved);
                LOGGER.info(Const.RESERVATION_CANCELED_OVERTIME);
            }
        }
    }
}
