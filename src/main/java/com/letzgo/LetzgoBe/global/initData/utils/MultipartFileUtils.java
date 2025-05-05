package com.letzgo.LetzgoBe.global.initData.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MultipartFileUtils {
    public static MultipartFile getMultipartFileFromResource(String filePath, String fileName) {
        try {
            Resource resource = new ClassPathResource(filePath);
            byte[] content = StreamUtils.copyToByteArray(resource.getInputStream());

            return new MultipartFile() {
                @Override
                public String getName() {
                    return fileName;
                }

                @Override
                public String getOriginalFilename() {
                    return fileName;
                }

                @Override
                public String getContentType() {
                    try {
                        return Files.probeContentType(Paths.get(filePath));
                    } catch (IOException e) {
                        return null;
                    }
                }

                @Override
                public boolean isEmpty() {
                    return content.length == 0;
                }

                @Override
                public long getSize() {
                    return content.length;
                }

                @Override
                public byte[] getBytes() {
                    return content;
                }

                @Override
                public InputStream getInputStream() {
                    return new ByteArrayInputStream(content);
                }

                @Override
                public void transferTo(File dest) throws IOException {
                    Files.write(dest.toPath(), content);
                }
            };
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to get MultipartFile from resource: " + filePath, e);
        }
    }
}
