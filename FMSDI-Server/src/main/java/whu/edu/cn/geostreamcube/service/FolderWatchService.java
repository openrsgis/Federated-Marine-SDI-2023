//package whu.edu.cn.geostreamcube.service;
//
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import whu.edu.cn.geostreamcube.producer.Producer;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.channels.FileChannel;
//import java.nio.channels.FileLock;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//import java.util.ArrayList;
//
//@Service
//@EnableScheduling
//class FolderWatchService {
//    private static final String FOLDER_PATH = "D:/组内项目/实时Cube/data/era5/soil_type/";
//    private final ArrayList<String> processedFiles = new ArrayList<>();
//
//
//    @Scheduled(fixedDelay = 1000) // 每秒钟执行一次
//    public void watchFolder() {
//        File folder = new File(FOLDER_PATH);
//        File[] files = folder.listFiles();
//
//        if (files != null) {
//            for (File file : files) {
//                if (file.isFile()) {
//                    String filePath = file.getAbsolutePath();
//                    String fileName = file.getName();
//                    if (!processedFiles.contains(filePath) && isFileAvailable(filePath)) {
//                        System.out.println("New file created: " + filePath);
//                        try {
//                            Thread.sleep(5000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        Producer.meteorologyProducer(filePath, fileName);
//                        processedFiles.add(filePath);
//                    }
//                }
//            }
//        }
//    }
//
//    private boolean isFileAvailable(String filePath) {
//        try {
//            Path path = Paths.get(filePath);
//            // 检查文件是否可读取
//            if (!Files.isReadable(path)) {
//                return false;
//            }
//            // 检查文件是否可写入
//            if (!Files.isWritable(path)) {
//                return false;
//            }
//            // 检查文件是否被其他进程锁定
//            try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE);
//                 FileLock lock = channel.tryLock()) {
//                return lock != null;
//            } catch (IOException e) {
//                return false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//}
