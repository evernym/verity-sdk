package com.evernym.sdk.example;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.handlers.Handlers;
import com.evernym.verity.sdk.handlers.MessageHandler;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Helper {
    Listener listener;
    Handlers handlers;
    Integer port = 4000;
    Context context;
    private static ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();

    void execute() {
        try {
            start();
            example();
        } catch (InterruptedException | IOException | VerityException e) {
            e.printStackTrace();
        } finally {
            end();
        }
    }

    abstract void example() throws IOException, VerityException, InterruptedException;


    void start() throws IOException, InterruptedException {
        System.setErr(new PrintStream(errBuffer));
        startListening(); // The example app stands up an endpoint to listen for messages from Verity
    }

    void end() {
        listener.stop();
    }

    // Basic http server listening for messages from Verity
    void startListening() throws IOException, InterruptedException {
        listener = new Listener(port, (String encryptedMessageFromVerity) -> {
            try {
                handlers.handleMessage(context, encryptedMessageFromVerity.getBytes());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
        listener.listen();
        println("Listening on port " + port);
    }

    void handle(MessageFamily messageFamily, MessageHandler.Handler messageHandler) {
        if(handlers == null) {
            handlers = new Handlers();
        }

        handlers.addHandler(messageFamily, (String msgName, JSONObject message) -> {
            try {
                messageHandler.handle(msgName, message);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    void nonHandled(String msg) {
        System.err.println(msg);
        System.err.flush();
        listener.stop();
        try { Thread.sleep(250); } catch (InterruptedException ignored) {}
        System.exit(-1);
    }


    static void println(Object out) {
        System.out.println(out.toString());
    }

    static void waitFor(String waitMsg) {
        ByteArrayOutputStream recordedOut = new ByteArrayOutputStream();
        PrintStream out = System.out;
        System.setOut(new PrintStream(recordedOut));

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            out.println();
            out.print(waitMsg+" ... ");
            out.flush();
            try {br.readLine();} catch (IOException ignored) {}

            out.print("Done\n");
            out.flush();
        }
        finally {
            out.print(new String(recordedOut.toByteArray()));
            System.setOut(out);
        }

    }

    static void waitFor(AtomicBoolean canContinue, String waitMsg) {
        ByteArrayOutputStream recordedOut = new ByteArrayOutputStream();
        PrintStream out = System.out;
        System.setOut(new PrintStream(recordedOut));

        try {
            String[] spinner = new String[] {"\u0008/", "\u0008-", "\u0008\\", "\u0008|" };
            out.println();
            out.print(waitMsg + " ... ");
            out.print("|");
            int pos = 0;
            while(!canContinue.get()) {
                try { Thread.sleep(450); } catch (InterruptedException ignored) {}
                out.printf("%s", spinner[pos % spinner.length]);
                out.flush();
                pos++;
            }
            out.print("\u0008");
            out.print("Done\n");
            out.flush();
        }
        finally {
            out.print(new String(recordedOut.toByteArray()));
            System.setOut(out);
        }
    }

}
