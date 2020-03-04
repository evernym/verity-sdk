/*
 * COPYRIGHT 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.
 */
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

    Context context;
    private static PrintStream err = System.err;
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
    abstract int listenerPort();


    void start() throws IOException, InterruptedException {
        System.setErr(new PrintStream(errBuffer));
        startListening(); // The example app stands up an endpoint to listen for messages from Verity
    }

    void end() {
        listener.stop();
        println("");
        println("******************** STDERR ********************");
        println(new String(errBuffer.toByteArray()));
    }

    // Basic http server listening for messages from Verity
    void startListening() throws IOException, InterruptedException {
        listener = new Listener(listenerPort(), (String encryptedMessageFromVerity) -> {
            try {
                handlers.handleMessage(context, encryptedMessageFromVerity.getBytes());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
        listener.listen();
        println("Listening on port " + listenerPort());
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
        err.println(msg);
        err.flush();

        err.println("******************** STDERR ********************");
        err.println(new String(errBuffer.toByteArray()));

        err.flush();

        listener.stop();
        try { Thread.sleep(250); } catch (InterruptedException ignored) {}
        System.exit(-1);
    }


    static void println(Object out) {
        System.out.println(out.toString());
    }

    static void printlnMessage(String messageName, JSONObject message) {
        try {
            printlnObject(message, "<<<", String.format("Incoming Message -- %s", messageName));
        } catch (IOException ignored) {}
    }

    static void printlnObject(JSONObject obj, String prefix, String preamble) throws IOException {
        println(prefix + "  " + preamble);
        BufferedReader r = new BufferedReader(new StringReader(obj.toString(2)));
        String line;
        while((line = r.readLine()) != null) {
            println(prefix + "  " + line);
        }
        r.close();
        println("");
    }

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    static boolean consoleYesNo(String request, boolean defaultYes) throws IOException {
        String yesNo = defaultYes ? "[y]/n" : "y/n";
        String modifiedRequest = request + "? " + yesNo;
        String response = consoleInput(modifiedRequest).trim().toLowerCase();

        if(defaultYes && "".equals(response)){
            return true;
        }
        else if ("y".equals(response)) {
            return true;
        }
        else if ("n".equals(response)) {
            return false;
        }

        throw new IOException("Did not get a valid response -- '"+response+"' is not y or n");

    }

    static String consoleInput(String request) throws IOException {
        ByteArrayOutputStream recordedOut = new ByteArrayOutputStream();
        PrintStream out = System.out;
        System.setOut(new PrintStream(recordedOut));

        try {

            out.println();
            out.print(request+": ");
            out.flush();

            String rtn = in.readLine();
            return rtn;
        }
        finally {
            out.print(new String(recordedOut.toByteArray()));
            System.setOut(out);
        }
    }

    static void waitFor(String waitMsg) {
        ByteArrayOutputStream recordedOut = new ByteArrayOutputStream();
        PrintStream out = System.out;
        System.setOut(new PrintStream(recordedOut));

        try {
            out.println();
            out.print(waitMsg+" ... ");
            out.flush();
            try {in.readLine();} catch (IOException ignored) {}

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
