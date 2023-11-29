package com.xcmg.jacocoservice.jacococlient;


/*******************************************************************************
 * Copyright (c) 2009, 2019 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 *******************************************************************************/
//package org.jacoco.examples;

import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

/**
 * This example connects to a coverage agent that run in output mode
 * <code>tcpserver</code> and requests execution data. The collected data is
 * dumped to a local file.
 */
public final class ExecutionDataClient {

    private static final String DESTFILE = "jacoco-client.exec";

    private static final String ADDRESS = "10.237.78.108";

    private static final int PORT = 8080;


    public static void getJacocoExec(String ip, String port, String destFile) {
        try {
            final FileOutputStream localFile = new FileOutputStream(destFile);
            final ExecutionDataWriter localWriter = new ExecutionDataWriter(
                    localFile);

            // Open a socket to the coverage agent:
            final Socket socket = new Socket(InetAddress.getByName(ip), Integer.parseInt(port));
            final RemoteControlWriter writer = new RemoteControlWriter(
                    socket.getOutputStream());
            final RemoteControlReader reader = new RemoteControlReader(
                    socket.getInputStream());
            reader.setSessionInfoVisitor(localWriter);
            reader.setExecutionDataVisitor(localWriter);


            // Send a dump command and read the response:
            writer.visitDumpCommand(true, false);
            if (!reader.read()) {
                throw new IOException("Socket closed unexpectedly.");
            }

            socket.close();
            localFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            socket.close();
        }

    }

    // 相比较getJacocoExec是多个ip和端口配合的获取exec文件 2020-04-08 zmy
    public static String downLoadDump(List<String> ipPort, String destFile) {
//        int flag = 0; // 可能为了比增量
//        String getDirName = getDirNum(destFile); // 可能为了比增量
        String result = "";
        for (String getIPPort : ipPort) {
            String dirName = destFile + "/" + getIPPort.replace(":", "_") + "_jacoco.exec";
            String ip = getIPPort.split(":")[0];
            int port = Integer.parseInt(getIPPort.split(":")[1]);
            try {
                final FileOutputStream localFile = new FileOutputStream(dirName);
                final ExecutionDataWriter localWriter = new ExecutionDataWriter(
                        localFile);

                // Open a socket to the coverage agent:
                final Socket socket = new Socket(InetAddress.getByName(ip), port);
                final RemoteControlWriter writer = new RemoteControlWriter(
                        socket.getOutputStream());
                final RemoteControlReader reader = new RemoteControlReader(
                        socket.getInputStream());
                reader.setSessionInfoVisitor(localWriter);
                reader.setExecutionDataVisitor(localWriter);

                // Send a dump command and read the response:
                writer.visitDumpCommand(true, false);
                if (!reader.read()) {
                    throw new IOException("Socket closed unexpectedly.");
                }
                socket.close();
                localFile.close();
                System.out.println("Download file success....");
                System.out.println("File path is : " + dirName);
//                flag++; // 可能为了比增量
                result = result + dirName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*if (flag != 0) {
            return getDirName;
        } else {
            return "path";
        }*/ // 可能为了比增量
        return result;

    }


    /**
     * @param path
     * @return
     */
    private static String getDirNum(String path) {
        File file = null;
        String newPath = "";
        for (int i = 0; ; i++) {
            newPath = path + "/" + i;
//            newPath = path;
            file = new File(newPath);
            System.out.println("new path = " + newPath);
            if (!file.isDirectory()) {
                System.out.println(newPath);
                file.mkdirs();
                break;
            }
        }
        return newPath;
    }

    /**
     * Starts the execution data request.
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final FileOutputStream localFile = new FileOutputStream(DESTFILE);
        final ExecutionDataWriter localWriter = new ExecutionDataWriter(
                localFile);

        // Open a socket to the coverage agent:
        final Socket socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
        final RemoteControlWriter writer = new RemoteControlWriter(
                socket.getOutputStream());
        final RemoteControlReader reader = new RemoteControlReader(
                socket.getInputStream());
        reader.setSessionInfoVisitor(localWriter);
        reader.setExecutionDataVisitor(localWriter);

        // Send a dump command and read the response:
        writer.visitDumpCommand(true, false);
        if (!reader.read()) {
            throw new IOException("Socket closed unexpectedly.");
        }

        socket.close();
        localFile.close();
    }

    private ExecutionDataClient() {
    }
}

