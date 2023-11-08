package br.com.astrosoft.framework.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class IppPrintJob {

    short printDocument(
            URI uri, InputStream documentInputStream
    ) throws IOException {
        HttpURLConnection httpURLConnection =
                (HttpURLConnection) uri.toURL().openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/ipp");
        OutputStream outputStream = httpURLConnection.getOutputStream();
        DataOutputStream dataOutputStream =
                new DataOutputStream(httpURLConnection.getOutputStream());
        dataOutputStream.writeShort(0x0101); // ipp version
        dataOutputStream.writeShort(0x0002); // print job operation
        dataOutputStream.writeInt(0x002A); // request id
        dataOutputStream.writeByte(0x01); // operation group tag
        writeAttribute(dataOutputStream, 0x47, "attributes-charset", "utf-8");
        writeAttribute(dataOutputStream, 0x48, "attributes-natural-language", "en");
        writeAttribute(dataOutputStream, 0x45, "printer-uri", uri.toString());
        dataOutputStream.writeByte(0x03); // end tag
        documentInputStream.transferTo(outputStream);
        dataOutputStream.close();
        outputStream.close();
        if (httpURLConnection.getResponseCode() == 200) {
            DataInputStream dataInputStream =
                    new DataInputStream(httpURLConnection.getInputStream());
            System.out.println(String.format("ipp version %d.%s",
                    dataInputStream.readByte(), dataInputStream.readByte()
            ));
            return dataInputStream.readShort();
        } else {
            throw new IOException(String.format("post to %s failed with http status %d",
                    uri, httpURLConnection.getResponseCode()
            ));
        }
    }

    void writeAttribute(
            DataOutputStream dataOutputStream, int tag, String name, String value
    ) throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        dataOutputStream.writeByte(tag);
        dataOutputStream.writeShort(name.length());
        dataOutputStream.write(name.getBytes(charset));
        dataOutputStream.writeShort(value.length());
        dataOutputStream.write(value.getBytes(charset));
    }

}