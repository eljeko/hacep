/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.redhat.hacep.cluster;

import it.redhat.hacep.HACEP;
import it.redhat.hacep.HACEPImpl;
import it.redhat.hacep.camel.CamelRouter;
import it.redhat.hacep.configuration.JmsConfiguration;
import it.redhat.hacep.configuration.Router;
import it.redhat.hacep.configuration.RulesConfiguration;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.Channel;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@RunWith(Arquillian.class)
public class TestDistributedSuspend {

    private final Logger LOGGER = LoggerFactory.getLogger(TestDistributedSuspend.class);

    private ZonedDateTime now = ZonedDateTime.now();

    private Channel additionsChannel1;
    private Channel additionsChannel2;
    private Channel replayChannel1;
    private Channel replayChannel2;

    private HACEPImpl hacep2;
    private HACEPImpl hacep1;

    @Before
    public void setup() throws InterruptedException {
        System.setProperty("jgroups.configuration", "jgroups-test-tcp.xml");

        additionsChannel1 = mock(Channel.class);
        additionsChannel2 = mock(Channel.class);
        replayChannel1 = mock(Channel.class);
        replayChannel2 = mock(Channel.class);

        hacep1 = new HACEPImpl("node1");
        hacep2 = new HACEPImpl("node2");


        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(hacep1::start);
        executorService.submit(hacep2::start);
        executorService.shutdown();
        executorService.awaitTermination(2, TimeUnit.MINUTES);
    }

    @After
    public void cleanup() {
        hacep1.stop();
        hacep2.stop();
    }

    @Test
    public void testSuspend() throws InterruptedException {
        reset(additionsChannel1, replayChannel1, additionsChannel2, replayChannel2);

        Router router1 = hacep1.getRouter();
        Router router2 = hacep2.getRouter();


        hacep2.suspend();


//        verify(router1, times(1)).suspend();
//        verify(router2, times(1)).suspend();
    }


    @Deployment
    public static JavaArchive createDeployment() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                .addClasses(
                        HACEP.class,
                        Router.class,
                        JmsConfiguration.class,
                        RulesConfiguration.class
                )
                .addAsManifestResource(
                        new StringAsset(
                                "<alternatives><class>it.redhat.hacep.cluster.TestDistributedSuspend</class></alternatives>"),
                        "beans.xml");
        System.out.println(jar.toString(true));
        return jar;
    }

    @Produces
    @Alternative
    public Router createRouter() {
        Router router = mock(Router.class);
        LOGGER.info("==== createRouter Called");
        return router;
    }

    @Produces
    @Alternative
    public HACEP createHACEP() {
        return new HACEPImpl();
    }


    @Produces
    @Alternative
    public JmsConfiguration createJmsConfiguration() {
        JmsConfiguration jmsConfiguration = mock(JmsConfiguration.class);
        LOGGER.info("==== JmsConfiguration Called");
        return jmsConfiguration;
    }

    @Produces
    @Alternative
    public RulesConfiguration createRulesConfiguration() {
        RulesConfiguration rulesConfiguration = mock(RulesConfiguration.class);
        LOGGER.info("==== RulesConfiguration Called");
        return rulesConfiguration;
    }
}
