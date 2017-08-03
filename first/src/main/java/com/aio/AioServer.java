package com.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;

public class AioServer {

    public AioServer(int port) throws IOException {
        AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open()
                .bind(new InetSocketAddress(port));
        listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {
                listener.accept(null, this);/*接收下一个链接*/
                try {
                    handler(result);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("异步IO失败....");
            }
        });

    }

    /*业务逻辑*/
    public void handler(AsynchronousSocketChannel channel) throws ExecutionException, InterruptedException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(32);
        channel.read(byteBuffer).get();
        byteBuffer.flip();
        System.out.println("服务端接收到：" + byteBuffer.get());
        channel.write(ByteBuffer.allocateDirect(1));
    }

    public static void main(String[] args) {
        try {
            AioServer server = new AioServer(7080);
            System.out.println("服务器监听端口：7080");
            Thread.sleep(1000000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
