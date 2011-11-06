package de.hahn.blog.consoleformatter.logger;

import java.text.SimpleDateFormat;

import java.util.Calendar;


public class WLSConsoleFormatter
    extends DebugFormatter
{
    private Calendar mCalendar = Calendar.getInstance();


    private SimpleDateFormat mDateFromatter = null;
    
    public WLSConsoleFormatter()
    {
        super();
        setOptionen("tnlcm");
    }
}
