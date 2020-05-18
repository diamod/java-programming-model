package com.test.bs.bio.bio2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.locks.LockSupport;

/**
 * 这里主要讲解tcp本身就是全双工的，与bio、nio也没有关系
 * 1、关于全双工、半双工、单工自行度娘
 * 2、这里以双发线程同时运行，互写，互读各9次
 * 3、从实际运行效果看，双方互写9次没问题，互读基本2次就完了，原因是写发生了粘包，某些方面也验证了
 * 粘包、拆包是tcp固有特性
 * 4、bio2 中的这个代码展现了tcp 全双工、半包读写，传统bio read／write 阻塞，承载server socket、client socket对话的thread
 * 基本没办法抽身
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

            /**
             * out 写9遍nihao到客户端
             */
            OutputStream finalOut = out;
            Thread thread1 = new Thread() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < 9; i++) {
                            finalOut.write("server sended nihao".concat(i + "").getBytes());
                        }
                        finalOut.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            /**
             * in 连续读9遍客户端信息
             */
            InputStream finalIn = in;
            Thread thread2 = new Thread() {
                @Override
                public void run() {
                    try {
                        byte[] bytes = new byte[1024];
                        for (int i = 0; i < 9; i++) {
                            finalIn.read(bytes);
                            System.out.println(new String(bytes) + "-timer:" + System.currentTimeMillis());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            //双发线程运行
            thread1.start();
            thread2.start();

            //实际运行挂在thread2 read上了
            thread1.join();
            thread2.join();

//            LockSupport.park();
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
