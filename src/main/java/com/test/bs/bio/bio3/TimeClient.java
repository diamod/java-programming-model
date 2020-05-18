package com.test.bs.bio.bio3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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

            /**
             * out 写9遍nihao到服务端
             */
            OutputStream finalOut = out;
            Thread thread1 = new Thread() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < 9; i++) {
                            finalOut.write("client sended nihao".concat(i + "").getBytes());
                        }
                        finalOut.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            /**
             * in 连续读9遍服务端信息
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
