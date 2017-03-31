package ObserverClass;

import java.util.Observable;

/**
 * Created by ivanmorandi on 08/03/2017.
 */

public class SMSObserver extends Observable {
    private static SMSObserver instance;

    public static SMSObserver getInstance(){
        if(instance == null)
            instance = new SMSObserver();
        return instance;
    }

    private SMSObserver(){

    }

    public void updateValue(Object data){
        synchronized (this){
            setChanged();
            notifyObservers(data);
        }
    }
}
