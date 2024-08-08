package org.cxq.domain.activity.service;

import org.cxq.domain.activity.repository.IActivityRepository;
import org.springframework.stereotype.Service;


@Service
public class RaffleActivityService extends AbstractRaffleActivity{
    public RaffleActivityService(IActivityRepository iActivityRepository) {
        super(iActivityRepository);
    }

}
