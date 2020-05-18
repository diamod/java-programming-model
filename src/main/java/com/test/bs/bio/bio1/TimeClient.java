package com.test.bs.bio.bio1;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.LockSupport;

public class TimeClient {
    public static void main(String[] args) {
        int port = 8080;
        Socket socket = null;
        InputStream in = null;
        OutputStream out = null;
        String resp = null;
        try {
            socket = new Socket("127.0.0.1", port);
            in = socket.getInputStream();
            out = socket.getOutputStream();

            //构造一个20m字节数组，一般会超出tcp缓冲窗口
            byte[] bytes = new byte[1024 * 1024 * 20];
            //初始化
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = 'a';
            }
            /**
             *out write是阻塞的，直到bytes全部写完线程才往下走，底层调用的是SocketOutputStream
             * native void socketWrite0(FileDescriptor fd, byte[] b, int off,int len)方法
             * socketWrite0 方法内部涉及tcp底层通讯，逻辑应该是循环直到数组b发送完成，但每次实际发送多少字节
             * 受到tcp滑动窗口影响，不可能一次性把20m bytes一次性发送过去，这就是tcp的拆包，
             * 这个从timerServerHandler实际运行结果可以看出，timerServerHandler 需要多次读取才能把20m读完，说明
             * tcp通道内根本没有那么多数据，说明socketWrite0一次性不可能写太多数据
             *
             */
            out.write(bytes, 0, bytes.length);
            out.flush();

            LockSupport.park();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
