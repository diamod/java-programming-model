package com.test.bs.bio.bio1;

import java.io.*;
import java.net.Socket;

/**
 * 这里主要讲解 粘包、拆包是tcp固有特性，与bio、nio没有关系
 * 下面通过TimerServerHandler接收20M的数据包可以明显看出，socket进行了拆包，同理如果应用层一次性数据包较小可能发生粘包
 * <p>
 * tcp 粘包、拆包主要讲述tcp作为传输层，其没办法知道上层协议制定的数据包大小，必然发生粘包、拆包特性，进而带来读写半包问题
 * * 写半包：由于滑动窗口这个很容易发生
 * * 读半包：写了半包，读自然会产生半包问题
 */
public class TimerServerHandler implements Runnable {
    private final Socket socket;

    public TimerServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = this.socket.getInputStream();
            out = this.socket.getOutputStream();
            //开辟20m的字节缓冲区
            byte[] bytes = new byte[1024 * 1024 * 20];
            while (true) {
                /**
                 *由于tcp内置滑动窗口，实际没有一次性读取到20m的字节到bytes中，而是分多次读取完成
                 * read(bytes,0,bytes.length)底层调用SocketInputStream socketRead0(fd, b, off, len, timeout);
                 */
                int actualBytes = in.read(bytes, 0, bytes.length);
                String str = new String(bytes, 0, 1);
                System.out.println("actual read bytes: " + actualBytes);
                System.out.println("actual read bytes),first alpha:" + str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }
}
