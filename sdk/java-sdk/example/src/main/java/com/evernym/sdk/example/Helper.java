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
    public static final String ANSII_GREEN = "\u001B[32m";
    public static final String ANSII_RESET = "\u001B[0m";

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
        handlers = new Handlers();
        startListening(); // The example app stands up an endpoint to listen for messages from Verity
    }

    void end() {
        listener.stop();
        String stdErrOutput = new String(errBuffer.toByteArray());
        if (!stdErrOutput.isEmpty()) {
            println("");
            println("******************** STDERR ********************");
            println(new String(errBuffer.toByteArray()));
        }
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

    static BufferedInputStream in = new BufferedInputStream(System.in);//new BufferedReader(new InputStreamReader(System.in));

    static boolean consoleYesNo(String request, boolean defaultYes) throws IOException {
        String yesNo = defaultYes ? "[y]/n" : "y/n";
        String modifiedRequest = request + "? " + yesNo;
        String response = consoleInput(modifiedRequest, null).trim().toLowerCase();

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

    static String readline(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return reader.readLine();
    }

    static void flushIn(InputStream in) throws IOException {
        if(in.available() > 0)
            in.read(new byte[in.available()]);
    }

    static String consoleInput(String request, String defaultValue) throws IOException {
        ByteArrayOutputStream recordedOut = new ByteArrayOutputStream();
        PrintStream out = System.out;
        System.setOut(new PrintStream(recordedOut));

        try {
            if (defaultValue != null) {
                out.println();
                out.print(request + ": ");
                out.print(ANSII_GREEN + defaultValue + ANSII_RESET + " is set via environment variable");
                out.println();
                out.print("Press any key to continue");
                out.flush();
                readline(in);
                return defaultValue;
            } else {
                flushIn(in);
                out.println();
                out.print(request + ": ");
                out.flush();
                String rtn = readline(in);
                return rtn;
            }
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
            try {flushIn(in);} catch (IOException ignored) {}

            out.println();
            out.print(waitMsg+" ... ");
            out.flush();
            try {readline(in);} catch (IOException ignored) {}

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
