package com.test.bs.bio.bio3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 这里主要讲解tcp关闭，四次挥手问题
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
