package com.worldpay.sdk.wpg.connection.factory;

import com.worldpay.sdk.wpg.connection.GatewayContext;

import java.io.IOException;
import java.net.Socket;

public interface ConnectionFactory
{

    Socket get(GatewayContext connection, String hostName, int port) throws IOException;

    void release(Socket socket);

}
