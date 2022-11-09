package com.github.mdvinyaninov.tools;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Handler implements ResponseHandler<Integer> {
    private static Logger LOGGER = LogManager.getLogger(Handler.class);
    private String storePath;
    private String folder;
    private String filename;
    private int result;

    public Handler(String storePath, String filename) {
        this.storePath = storePath;
        this.filename = filename;
    }

    public Handler(String storePath, String folder, String filename) {
        this.storePath = storePath;
        this.folder = folder;
        this.filename = filename;
    }

    public Integer handleResponse(HttpResponse response) {
        InputStream in = null;
        OutputStream out = null;
        result = -1;

        try {
            LOGGER.debug("Response Status line: " + response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();

            if (status == 200) {
                LOGGER.info("Success.");
                HttpEntity e = response.getEntity();
                long l = e.getContentLength();
                LOGGER.debug("Response Content length: " + l);

                if (e != null && l != 0L) {
                    in = e.getContent();
                    byte[] content = IOUtils.toByteArray(in);

                    final String destination = (folder != null) ? storePath + File.separator + folder : storePath;
                    final String filePath = destination + File.separator + filename;

                    File f = new File(destination);
                    f.mkdirs();

                    File file = new File(filePath);
                    if (file.exists()) {
                        LOGGER.info("File exists.");
                        file.delete();
                        file.createNewFile();
                    }

                    out = new FileOutputStream(file);
                    LOGGER.info("Writing content to file '{}'...", filePath);
                    IOUtils.write(content, out);
                    LOGGER.info("Success.");
                    result = 0;
                }
            }
            else {
                LOGGER.info("Failed to download graph.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null)
                    in.close();

                if (out != null)
                    out.close();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }
        }

        return result;
    }

    public int getResult() {
        return result;
    }
}
