package org.mule.instrospection;

import java.util.Collection;

/**
 *
 */
public class ApplicationExternalConnectionMetadata
{

    private String serverIp;
    private String applicationName;
    private Collection<ConnectionMetadata> externalConnections;

    public String getServerIp()
    {
        return serverIp;
    }

    public String getApplicationName()
    {
        return applicationName;
    }

    public Collection<ConnectionMetadata> getExternalConnections()
    {
        return externalConnections;
    }

    public static class ApplicationExternalConnectionMetadataBuilder
    {

        private ApplicationExternalConnectionMetadata metadata = new ApplicationExternalConnectionMetadata();

        public ApplicationExternalConnectionMetadataBuilder serverIp(String serverIp)
        {
            metadata.serverIp = serverIp;
            return this;
        }

        public ApplicationExternalConnectionMetadataBuilder applicationName(String applicationName)
        {
            metadata.applicationName = applicationName;
            return this;
        }

        public ApplicationExternalConnectionMetadataBuilder externalConnections(Collection<ConnectionMetadata> externalConnections)
        {
            metadata.externalConnections = externalConnections;
            return this;
        }

        public ApplicationExternalConnectionMetadata build()
        {
            return metadata;
        }
    }
}
