package com.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NonBlockingFileClient {

    public static void main(String[] args) throws IOException {
        SocketChannel clientChannel = null;
        try {
            clientChannel = SocketChannel.open(new InetSocketAddress("localhost",4321));
            clientChannel.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put("Hello from client ...".getBytes());
            buffer.flip();
            clientChannel.write(buffer);
            buffer.clear();

            while (clientChannel.read(buffer) > 0){
                buffer.flip();
                System.out.println("Received from server.." + new String(buffer.array(),0,buffer.limit()));
                buffer.clear();
            }
            clientChannel.close();

        } catch (IOException e) {
            throw new IOException(e);
        }


    }
}
