package paa.locker.util;

import paa.locker.model.Locker;


import java.util.ArrayList;
import java.util.Collection;

public class ExampleLockers {
    public static Collection<Locker> getLockers() {
        ArrayList<Locker> lockers = new ArrayList<>();
        lockers.add(new Locker(null, "Vallecas", "Nikola Tesla, S/N", -3.6286138, 40.3901617, 4, 8));
        lockers.add(new Locker(null, "Atocha", "Glorieta del Emperador Carlos V", -3.679583, 40.3965354, 10, 20));
        lockers.add(new Locker(null, "Moncloa", "Plaza de la Moncloa", -3.7116853, 40.4317665, 4, 16));
        lockers.add(new Locker(null, "América", "Avenida de América, 1", -3.6770431, 40.4390217, 5, 4));
        lockers.add(new Locker(null, "Chamartín", "Agustín de Foxá, 40", -3.681379, 40.4715852, 20, 10));
        return lockers;
    }
    
    
}
