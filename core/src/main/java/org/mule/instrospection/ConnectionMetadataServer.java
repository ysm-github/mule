package org.mule.instrospection;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Startable;
import org.mule.util.IOUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 */
public class ConnectionMetadataServer implements MuleContextAware, Startable
{

    private MuleContext muleContext;
    private ApplicationExternalConnectionMetadata applicationConnectionMetadata;

    @Override
    public void setMuleContext(MuleContext context)
    {
        this.muleContext = context;
    }

    @Override
    public void start() throws MuleException
    {
        try
        {
            InetAddress localHost = InetAddress.getLocalHost();
            Collection<ExternalConnection> externalConnections = muleContext.getRegistry().lookupObjects(ExternalConnection.class);

            applicationConnectionMetadata = new ApplicationExternalConnectionMetadata.ApplicationExternalConnectionMetadataBuilder()
                    .applicationName(muleContext.getConfiguration().getId())
                    .serverIp(localHost.getHostAddress())
                    .externalConnections(CollectionUtils.collect(externalConnections, new Transformer()
                    {
                        @Override
                        public Object transform(Object input)
                        {
                            return ((ExternalConnection)input).getConnectionMetadata();
                        }
                    })).build();

            HttpServer httpServer = HttpServer.create(new InetSocketAddress(9090), 0);

            final JSONObject serverJson =  new JSONObject();
            final JSONObject applicationJson =new JSONObject();
            serverJson.put("serverIp", applicationConnectionMetadata.getServerIp());
            JSONArray applications = new JSONArray();
            serverJson.put("apps", applications);
            applications.add(0, applicationJson);

            applicationJson.put("appName", applicationConnectionMetadata.getApplicationName());
            JSONArray externalConnectionsJsonArray = new JSONArray();
            applicationJson.put("externalConnections", externalConnectionsJsonArray);
            for (ExternalConnection externalConnection : externalConnections)
            {
                ConnectionMetadata connectionMetadata = externalConnection.getConnectionMetadata();
                JSONObject connectionMetadataJson = new JSONObject();
                externalConnectionsJsonArray.add(connectionMetadataJson);
                connectionMetadataJson.put("host", connectionMetadata.getHost());
                connectionMetadataJson.put("port", connectionMetadata.getPort());
                connectionMetadataJson.put("type", connectionMetadata.getType().name());
                connectionMetadataJson.put("destination", connectionMetadata.getDestination());
                connectionMetadataJson.put("resourceType", connectionMetadata.getResourceType());
            }

            httpServer.createContext("/metadata", new HttpHandler()
            {
                @Override
                public void handle(HttpExchange httpExchange) throws IOException
                {
                    //String response = serverJson.toJSONString();
                    String response = getFakeJson();
                    System.out.println(getFakeJson());
                    httpExchange.getResponseHeaders().put("Access-Control-Allow-Origin", Arrays.asList("*"));
                    httpExchange.getResponseHeaders().put("Content-Type", Arrays.asList("application/json"));
                    httpExchange.sendResponseHeaders(200, response.length());
                    httpExchange.getResponseBody().write(response.getBytes());
                    httpExchange.getResponseBody().close();
                }
            });
            httpServer.setExecutor(null);
            httpServer.start();

        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }


    }

    public String getFakeJson()
    {
        try
        {
            try (FileInputStream fileInputStream = new FileInputStream(new File("/Users/pablolagreca/Dev/Projects2/mule/3.x/3.x-ce/tests/integration/src/test/resources/fakeServerData.json")))
            {
                return IOUtils.toString(fileInputStream);
            }
            //return IOUtils.getResourceAsString("fakeServerData.json", getClass());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

}
