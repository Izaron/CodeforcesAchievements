package com.izaron.cf.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
public class CompressionService {

    public byte[] zip(String source) {
        byte[] dataToCompress = source.getBytes(StandardCharsets.ISO_8859_1);
        try {
            try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(dataToCompress.length)) {
                try (GZIPOutputStream zipStream = new GZIPOutputStream(byteStream)) {
                    zipStream.write(dataToCompress);
                }
                return byteStream.toByteArray();
            }
        } catch (Exception e) {
            return null;
        }
    }

    public String unzip(byte[] source) {
        ByteArrayInputStream stream = new ByteArrayInputStream(source);
        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(stream);
            InputStreamReader reader = new InputStreamReader(gzipInputStream);
            BufferedReader in = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String readed;
            while ((readed = in.readLine()) != null) {
                builder.append(readed);
                builder.append(StringUtils.LF);
            }
            return StringUtils.chomp(builder.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
