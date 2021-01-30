package social.amadeus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import social.amadeus.repository.ObserverRepo;

@Service
public class ObserverService {

    @Autowired
    ObserverRepo observerRepo;


}
