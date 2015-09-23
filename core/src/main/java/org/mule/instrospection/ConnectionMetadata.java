package org.mule.instrospection;

/**
 *  Represents an external connection
 */
public class ConnectionMetadata
{

    public enum ConnectionType {CONSUMER, LISTENER, OUTBOUND}

    private String host;
    private int port;
    private String destination;
    private String resourceType;
    private ConnectionType type;

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public String getDestination()
    {
        return destination;
    }

    public ConnectionType getType()
    {
        return type;
    }

    public String getResourceType()
    {
        return resourceType;
    }

    public static class ConnectionMetadataBuilder
    {
        private ConnectionMetadata connectionMetadata = new ConnectionMetadata();

        public ConnectionMetadataBuilder setHost(String host)
        {
            this.connectionMetadata.host = host;
            return this;
        }

        public ConnectionMetadataBuilder setPort(int port)
        {
            this.connectionMetadata.port = port;
            return this;
        }

        public ConnectionMetadataBuilder setDestination(String destination)
        {
            this.connectionMetadata.destination = destination;
            return this;
        }

        public ConnectionMetadataBuilder setType(ConnectionType type)
        {
            this.connectionMetadata.type = type;
            return this;
        }

        public ConnectionMetadataBuilder setResourceType(String resourceType)
        {
            this.connectionMetadata.resourceType = resourceType;
            return this;
        }

        public ConnectionMetadata build()
        {
            return connectionMetadata;
        }
    }

}
