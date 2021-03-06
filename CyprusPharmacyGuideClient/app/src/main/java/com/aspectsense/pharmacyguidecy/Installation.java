package com.aspectsense.pharmacyguidecy;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * User: Nearchos Paspallis
 * Date: 6/11/12
 * Time: 10:06 AM
 */
class Installation {

    private static String sID = null;
    private static final String INSTALLATION = "INSTALLATION";

    synchronized static String id(Context context) {
        if (sID == null) {
            final File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists()) {
                    writeInstallationFile(installation);
                }
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return sID;
    }

    private static String readInstallationFile(File installation) throws IOException {
        final RandomAccessFile f = new RandomAccessFile(installation, "r");
        final byte [] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        final FileOutputStream out = new FileOutputStream(installation);
        final String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }
}