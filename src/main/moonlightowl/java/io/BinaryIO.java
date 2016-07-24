package main.moonlightowl.java.io;

import main.moonlightowl.java.Logger;

import java.io.*;

/**
 <P>Reads binary data into memory, and writes it back to disk.
 <P>Buffering is used when reading and writing files, to minimize the number 
 of interactions with the disk.
 (http://www.javapractices.com/topic/TopicAction.do?Id=245)
 */

public final class BinaryIO {
    /** Read the given binary file, and return its contents as a byte array.*/
    public static byte[] read(String aInputFileName) throws IOException {
        File file = new File(aInputFileName);
        byte[] result = new byte[(int)file.length()];

        InputStream input = null;
        try {
            int totalBytesRead = 0;
            input = new BufferedInputStream(new FileInputStream(file));
            while(totalBytesRead < result.length){
                int bytesRemaining = result.length - totalBytesRead;
                //input.read() returns -1, 0, or more:
                int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
                if (bytesRead > 0){
                    totalBytesRead = totalBytesRead + bytesRead;
                }
            }
            /*
             the above style is a bit tricky: it places bytes into the 'result' array;
             'result' is an output parameter;
             the while loop usually has a single iteration only.
            */
        }
        finally {
            if(input != null) input.close();
        }

        return result;
    }

    /**
     Write a byte array to the given file.
     Writing binary data is significantly simpler than reading it.
     */
    public static void write(byte[] aInput, String aOutputFileName) throws IOException {
        OutputStream output = null;
        try {
            output = new BufferedOutputStream(new FileOutputStream(aOutputFileName));
            output.write(aInput);
        }
        finally {
            if(output != null) output.close();
        }
    }

    /** Read the given binary file, and return its contents as a byte array.*/
    public static byte[] readAlternateImpl(String aInputFileName){
        File file = new File(aInputFileName);
        byte[] result = null;
        try {
            InputStream input =  new BufferedInputStream(new FileInputStream(file));
            result = readAndClose(input);
        }
        catch (FileNotFoundException ex){
            Logger.trace(ex);
        }
        return result;
    }

    /**
     Read an input stream, and return it as a byte array.
     Sometimes the source of bytes is an input stream instead of a file.
     This implementation closes aInput after it's read.
     */
    private static byte[] readAndClose(InputStream aInput){
        //carries the data from input to output :
        byte[] bucket = new byte[32*1024];
        ByteArrayOutputStream result = null;
        try  {
            try {
                //Use buffering? No. Buffering avoids costly access to disk or network;
                //buffering to an in-memory stream makes no sense.
                result = new ByteArrayOutputStream(bucket.length);
                int bytesRead = 0;
                while(bytesRead != -1){
                    //aInput.read() returns -1, 0, or more :
                    bytesRead = aInput.read(bucket);
                    if(bytesRead > 0){
                        result.write(bucket, 0, bytesRead);
                    }
                }
            }
            finally {
                aInput.close();
                //result.close(); this is a no-operation for ByteArrayOutputStream
            }
        }
        catch (IOException ex){
            Logger.trace(ex);
        }
        finally {
            if(result == null) result = new ByteArrayOutputStream();
        }
        return result.toByteArray();
    }
}