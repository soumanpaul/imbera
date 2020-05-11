package com.imebrademoproject;

import com.imebra.MutableMemory;
import com.imebra.PipeStream;
import com.imebra.StreamWriter;

import java.io.IOException;
import java.io.InputStream;

public class PushToImebraPipe implements Runnable {
    private PipeStream mImebraPipe;
    private InputStream mStream;

    public PushToImebraPipe(com.imebra.PipeStream pipe, InputStream stream) {
        mImebraPipe = pipe;
        mStream = stream;
    }
    @Override
    public void run(){
        try {


            byte[] buffer = new byte[128000];
            MutableMemory memory = new MutableMemory();

            StreamWriter pipeWriter = new StreamWriter(mImebraPipe.getStreamOutput());


            for (int readBytes = mStream.read(buffer); readBytes >= 0; readBytes = mStream.read(buffer)) {


                if(readBytes > 0) {
                    memory.assign(buffer);
                    memory.resize(readBytes);
                    pipeWriter.write(memory);
                }
            }
        }
        catch(IOException e) {
        }
        finally {
            mImebraPipe.close(50000);
        }
    }
}

