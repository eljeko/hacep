package it.redhat.hacep.cluster;

import it.redhat.hacep.configuration.RulesConfiguration;
import org.kie.api.runtime.Channel;

import java.util.Map;

public class MockRulesConfiguration implements RulesConfiguration {
    @Override
    public String getKieSessionName() {
        return null;
    }

    @Override
    public String getKieBaseName() {
        return null;
    }

    @Override
    public Map<String, Channel> getChannels() {
        return null;
    }

    @Override
    public Map<String, Channel> getReplayChannels() {
        return null;
    }

    @Override
    public String getGroupId() {
        return null;
    }

    @Override
    public String getArtifactId() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }
}
