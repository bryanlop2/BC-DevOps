/* $Id: TestVBox.java 135976 2020-02-04 10:35:17Z bird $ */
/*! file
 * Small sample/testcase which demonstrates that the same source code can
 * be used to connect to the webservice and (XP)COM APIs.
 */

/*
 * Copyright (C) 2010-2020 Oracle Corporation
*/

import org.virtualbox_6_1.*;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.math.BigInteger;

public class TestVBox
{
    static void processEvent(IEvent ev)
    {
        System.out.println("got event: " + ev);
        VBoxEventType type = ev.getType();
        System.out.println("type = " + type);
        switch (type)
        {
            case OnMachineStateChanged:
            {
                IMachineStateChangedEvent mcse = IMachineStateChangedEvent.queryInterface(ev);
                if (mcse == null)
                    System.out.println("Cannot query an interface");
                else
                    System.out.println("mid=" + mcse.getMachineId());
                break;
            }
        }
    }

    static class EventHandler
    {
        EventHandler() {}
        public void handleEvent(IEvent ev)
        {
            try {
                processEvent(ev);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }



    static void start(VirtualBoxManager mgr, IVirtualBox vbox)
    {
        IMachine m = startvm OracleLinux6Test;
        String name = m.getName();
        System.out.println("\nAttempting to start VM '" + name + "'");

        ISession session = mgr.getSessionObject();
        ArrayList<String> env = new ArrayList<String>();
        IProgress p = m.launchVMProcess(session, "gui", env);
        progressBar(mgr, p, 10000);
        session.unlockMachine();
        // process system event queue
        mgr.waitForEvents(0);
    }
    
    static void createVM(VirtualBoxManager mgr, IVirtualBox vbox)
    {
        IMachine m = createvm --name OracleLinux6Test --ostype Oracle_64 --register;
        String name = m.getName();
        System.out.println("\nAttempting to create VM '" + name + "'");

        ISession session = mgr.getSessionObject();
        ArrayList<String> env = new ArrayList<String>();
        IProgress p = m.launchVMProcess(session, "gui", env);
        progressBar(mgr, p, 10000);
        session.unlockMachine();
        // process system event queue
        mgr.waitForEvents(0);
    }
    
    
    static void deleteVM(VirtualBoxManager mgr, IVirtualBox vbox)
    {
        IMachine m = unregistervm –delete OracleLinux6Test;
        String name = m.getName();
        System.out.println("\nAttempting to delete VM '" + name + "'");

        ISession session = mgr.getSessionObject();
        ArrayList<String> env = new ArrayList<String>();
        IProgress p = m.launchVMProcess(session, "gui", env);
        progressBar(mgr, p, 10000);
        session.unlockMachine();
        // process system event queue
        mgr.waitForEvents(0);
    }

    static void printErrorInfo(VBoxException e)
    {
        System.out.println("VBox error: " + e.getMessage());
        System.out.println("Error cause message: " + e.getCause());
        System.out.println("Overall result code: " + Integer.toHexString(e.getResultCode()));
        int i = 1;
        for (IVirtualBoxErrorInfo ei = e.getVirtualBoxErrorInfo(); ei != null; ei = ei.getNext(), i++)
        {
            System.out.println("Detail information #" + i);
            System.out.println("Error mesage: " + ei.getText());
            System.out.println("Result code:  " + Integer.toHexString(ei.getResultCode()));
            // optional, usually provides little additional information:
            System.out.println("Component:    " + ei.getComponent());
            System.out.println("Interface ID: " + ei.getInterfaceID());
        }
    }


    public static void main(String[] args)
    {
        VirtualBoxManager mgr = VirtualBoxManager.createInstance(null);

        boolean ws = false;
        String  url = null;
        String  user = null;
        String  passwd = null;

        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-w"))
                ws = true;
            else if (args[i].equals("-url"))
                url = args[++i];
            else if (args[i].equals("-user"))
                user = args[++i];
            else if (args[i].equals("-passwd"))
                passwd = args[++i];
        }

        if (ws)
        {
            try {
                mgr.connect(url, user, passwd);
            } catch (VBoxException e) {
                e.printStackTrace();
                System.out.println("Cannot connect, start webserver first!");
            }
        }

        try
        {
            IVirtualBox vbox = mgr.getVBox();
            if (vbox != null)
            {
                System.out.println("VirtualBox version: " + vbox.getVersion() + "\n");
                testEnumeration(mgr, vbox);
                testReadLog(mgr, vbox);
                testStart(mgr, vbox);
                testEvents(mgr, vbox.getEventSource());

                System.out.println("done, press Enter...");
                int ch = System.in.read();
            }
        }
        catch (VBoxException e)
        {
            printErrorInfo(e);
            System.out.println("Java stack trace:");
            e.printStackTrace();
        }
        catch (RuntimeException e)
        {
            System.out.println("Runtime error: " + e.getMessage());
            e.printStackTrace();
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
        }

        // process system event queue
        mgr.waitForEvents(0);
        if (ws)
        {
            try {
                mgr.disconnect();
            } catch (VBoxException e) {
                e.printStackTrace();
            }
        }

        mgr.cleanup();

    }

}
