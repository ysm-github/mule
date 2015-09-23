package org.mule.instrospection;

/**
 * An external inbound connection represents a connection with the outside world
 * used by mule to receive information from external resources.
 */
public interface ExternalConnection
{

    ConnectionMetadata getConnectionMetadata();

}
