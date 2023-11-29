package com.xcmg.jacocoservice.jacococlient;

import org.jacoco.core.tools.ExecFileLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MergeDump {

/*    private final String path ;
    private final File destFile ;

    public MergeDump(String path){
        this.path = path;
        this.destFile = new File(path + "/jacoco.exec");
    }*/

    private static List<File> fileSets(String dir){
        System.out.println(dir);
        List<File> fileSetList = new ArrayList<File>();
        File path = new File(dir);
        if ( ! path.exists() ){
            System.out.println("No path name is :" + dir);
            return null;
        }
        File[] files = path.listFiles();
        try {
            if (files == null || files.length == 0) {
                return null;
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        for (File file : files) {
            if (file.getName().contains(".exec")) {
                System.out.println("文件:" + file.getAbsolutePath());
                fileSetList.add(file);
            } else {
                System.out.println("非exec文件:" + file.getAbsolutePath());
            }
        }
        return fileSetList;
    }

    public static void executeMerge(String path) throws Exception {

        final ExecFileLoader loader = new ExecFileLoader();
        load(loader,path);
        save(loader,path);
        // 执行完成后，删除非必须的dump文件
        for (final File fileSet : fileSets(path)) {
            if ( ! fileSet.getName().equals("jacoco.exec") ) {
                fileSet.delete();
            }
        }
    }

    /**
     * 加载dump文件
     * @param loader
     * @param path
     * @throws Exception
     */
    public static void load(final ExecFileLoader loader, String path) throws Exception {
        for (final File fileSet : fileSets(path)) {
//            System.out.println(fileSet.getAbsoluteFile());
            final File inputFile = new File(path, fileSet.getName());
            if (inputFile.isDirectory()) {
                continue;
            }
            try {
                System.out.println("Loading execution data file " + inputFile.getAbsolutePath());
                loader.load(inputFile);
//                System.out.println(loader.getExecutionDataStore().getContents());
            } catch (final IOException e) {
                throw new Exception("Unable to read "
                        + inputFile.getAbsolutePath(), e);
            }
        }
    }

    /**
     * 执行合并文件
     * @param loader
     * @param path
     * @throws Exception
     */
    public static void save(final ExecFileLoader loader, String path) throws Exception {
        File destFile = new File(path + "/jacoco.exec");

        if (loader.getExecutionDataStore().getContents().isEmpty()) {
            System.out.println("Skipping JaCoCo merge execution due to missing execution data files");
            return;
        }

        System.out.println("Writing merged execution data to " + destFile.getAbsolutePath());
        try {
            loader.save(destFile, false);
        } catch (final IOException e) {
            throw new Exception("Unable to write merged file "
                    + destFile.getAbsolutePath(), e);
        }
    }
}

