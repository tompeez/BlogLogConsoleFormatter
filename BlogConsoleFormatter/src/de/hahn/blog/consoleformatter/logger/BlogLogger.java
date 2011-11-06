package de.hahn.blog.consoleformatter.logger;


import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import oracle.adf.share.logging.ADFLogger;


public class BlogLogger
    implements java.io.Serializable
{

    private static final String GLOBAL_DEBUG_FILE = "globalDebugLog";


    private static BlogLogger mBlogLogger;


    private static FileHandler mGlobalDebugLogFileHandler = null;

    //~ Instanzenvariablen -------------------------------------------------------------------------


    private HashMap loggers = null;


    private boolean mUseGlobalLog = false;

    //~ Konstruktoren ------------------------------------------------------------------------------


    private BlogLogger()
    {
        // falls das Property auf true gesetzt ist, den globalen Loghandler initialsieren
        String s = System.getProperty("blog.useGlobalDebugLog");
        if ((s != null) && (s.compareToIgnoreCase("true") == 0))
        {
            mUseGlobalLog = true;
            try
            {
                mGlobalDebugLogFileHandler =
                        new FileHandler(BlogStandardDef.BLOG_LOG_PATH + BlogStandardDef.FILE_SEPARATOR +
                                        GLOBAL_DEBUG_FILE + ".log", BlogStandardDef.LOG_SIZE,
                                        BlogStandardDef.LOG_COUNT, true);

                // Optionen für globalLog holen
                String strOption = System.getProperty("blog.GlobalDebugLogOption");

                DebugFormatter debugFormatter = new DebugFormatter();
                debugFormatter.setOptionen(strOption);
                mGlobalDebugLogFileHandler.setFormatter(debugFormatter);
                System.out.println("BlogLogging: File-Handler for logger  " +
                                   BlogStandardDef.BLOG_LOG_PATH + BlogStandardDef.FILE_SEPARATOR +
                                   GLOBAL_DEBUG_FILE + ".log" + " build");
            }
            catch (IOException eIO)
            {
                System.err.println("BlogLogging: building File-Handler for logger  " +
                                   BlogStandardDef.BLOG_LOG_PATH + BlogStandardDef.FILE_SEPARATOR +
                                   GLOBAL_DEBUG_FILE + ".log" + " failed");
            }
        }

        loggers = new HashMap();
    }


    public static BlogLogger getInstance()
    {
        if (mBlogLogger == null)
        {
            mBlogLogger = new BlogLogger();
        }

        return mBlogLogger;
    }


    public Logger getLogger(String name)
    {
        ADFLogger log = (ADFLogger) loggers.get(name);
        if (log == null)
        {
            log = newLogger(name);
        }

        return log.getLogger();
    }


    private synchronized ADFLogger newLogger(String name)
    {
        ADFLogger log = ADFLogger.createADFLogger(name);

        String s1 = System.getProperty("smc.debug.usefiles");
        Boolean lUseFile = Boolean.TRUE;
        if ((s1 != null) && (s1.compareToIgnoreCase("false") == 0))
        {
            lUseFile = Boolean.FALSE;
        }
        initLogger(log, name, lUseFile);
        loggers.put(name, log);

        String s2 = System.getProperty("smc.debugmode");
        if (s2 == null)
            s2 = System.getProperty("smc.debug.mode");

        if ((s2 != null) && (s2.compareToIgnoreCase("true") == 0))
        {
            String level = System.getProperty("smc.debug.level");
            if (level == null)
                level = "INF0";

            Level lLevel = null;
            if (level.equalsIgnoreCase("FINE"))
                lLevel = Level.FINE;
            else if (level.equalsIgnoreCase("INFO"))
                lLevel = Level.INFO;
            else if (level.equalsIgnoreCase("WARNING"))
                lLevel = Level.WARNING;
            else if (level.equalsIgnoreCase("SEVERE"))
                lLevel = Level.SEVERE;
            else if (level.equalsIgnoreCase("ALL"))
                lLevel = Level.ALL;
            setDebug(name, lLevel);
            log.info("Logger " + name + " mit Level " + level + " eingetragen");
        }

        return log;
    }


    private void initLogger(ADFLogger aLogger, String aLogName, Boolean aUseFile)
    {
        try
        {
            if (aUseFile != null && aUseFile.booleanValue())
            {
                FileHandler fh =
                    new FileHandler(BlogStandardDef.BLOG_LOG_PATH + BlogStandardDef.FILE_SEPARATOR +
                                    aLogName + ".log", BlogStandardDef.LOG_SIZE,
                                    BlogStandardDef.LOG_COUNT, true);
                SimpleFormatter simpleFormatter = new SimpleFormatter();
                fh.setFormatter(simpleFormatter);
                aLogger.addHandler(fh);
            }

            if (mUseGlobalLog)
            {
                aLogger.addHandler(mGlobalDebugLogFileHandler);
            }

            aLogger.info("Logger " + aLogName + " ready.");
        }
        catch (IOException ioe)
        {
            aLogger.severe("Building of File-Handler for logger  " + aLogName +
                           " failed");
        }
    }

    public void setDebug(boolean aDebug)
    {
        Level level = null;
        if (aDebug)
        {
            level = Level.ALL;
        }
        else
        {
            level = Level.INFO;
        }

        // für alle bekannten Logger den Level setzen
        Iterator it = loggers.values().iterator();
        while (it.hasNext())
        {
            ADFLogger l = (ADFLogger) it.next();
            l.setLevel(level);
        }
    }

    public void setDebug(String name, Level aLevel)
    {
        Level level = null;
        if (aLevel != null)
        {
            level = aLevel;
        }
        else
        {
            level = Level.INFO;
        }

        ADFLogger l = (ADFLogger) loggers.get(name);
        if (l != null)
        {
            l.setLevel(level);
        }
    }
}
