package com.example;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Set;

public class NonBlockingFileServer {
    public static void main( String[] args ) throws IOException {

        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(4321));
            serverSocketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("server is run on the port 4321");

            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
            while (selectionKeyIterator.hasNext()){
                SelectionKey key = selectionKeyIterator.next();


            if (key.isAcceptable()){
                ServerSocketChannel server = (ServerSocketChannel) key.channel();
                SocketChannel clientChannel = server.accept();
                clientChannel.configureBlocking(false);
                clientChannel.register(selector,SelectionKey.OP_READ);
                System.out.println("New client is running...");
            } else if (key.isReadable()) {
                SocketChannel clientChannel = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int bytesRead = clientChannel.read(buffer);

                if (bytesRead > 0 ){
                    buffer.flip();
                    System.out.println("Received data from client..." + new String(buffer.array(),0,bytesRead));
                    sendFileToClient(clientChannel,"C:\\Users\\Parse\\Desktop\\MiladTask\\JavaCoreTask\\Non-blocking-IN-OUT-Channels-in-java\\src\\main\\resources\\clientfile.txt");
                } else if (bytesRead == -1) {
                    clientChannel.close();
                    System.out.println("Client disconnected...");
                }
            }
            }
            selectionKeyIterator.remove();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    private static void sendFileToClient(SocketChannel clientChannel, String clientFile) throws IOException {
        FileChannel fileChannel = FileChannel.open(Paths.get(clientFile), StandardOpenOption.READ);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead ;
        while ((bytesRead = fileChannel.read(buffer))>0){
            buffer.flip();
            fileChannel.write(buffer);
            buffer.clear();
        }
        fileChannel.close();
        System.out.println("File send to Client .."  + clientFile);
    }
}
