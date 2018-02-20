package it.redhat.hacep.cache.executors;

import it.redhat.hacep.configuration.Router;
import org.infinispan.Cache;
import org.infinispan.distexec.DistributedCallable;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Set;

public class Suspend implements DistributedCallable, Serializable {


    @Override
    public void setEnvironment(Cache cache, Set set) {
        this.cache = cache;
        this.set = set;
    }

    @Override
    public Object call() throws Exception {
        if (router != null) {
            router.suspend();
            return "OK";
        }
        return "KO";
    }


   // @Inject
    private Router router;

    private transient Set set;
    private transient Cache<String, String> cache;

}
