package spring.apo.demotest.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.*;

public class InMemoryMultipartFile implements MultipartFile {

    private final String name;
    private final String originalFileName;
    private final String contentType;
    private final byte[] bytes;

    public InMemoryMultipartFile(String name, String originalFileName, String contentType, byte[] bytes) {
        this.name = name;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.bytes = bytes;
    }

    @Override public String getName() { return name; }
    @Override public String getOriginalFilename() { return originalFileName; }
    @Override public String getContentType() { return contentType; }
    @Override public boolean isEmpty() { return bytes.length == 0; }
    @Override public long getSize() { return bytes.length; }
    @Override public byte[] getBytes() { return bytes; }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void transferTo(File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
        }
    }
}
