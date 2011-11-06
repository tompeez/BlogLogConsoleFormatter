package de.hahn.blog.consoleformatter.logger;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;


/**
 * Format message for hte console
 *
 * @projekt BlogConsoleFormatter
 * @desc This class extends the normal log formatter to print out additional information:<br>
 *       level: date time [- threadId] [- class] [- method] [- message] {- throwable.message}<br>
 *       Information in [] are selectable via method {@link
 *       #setOptionen(String aOptionen) setOptionen} Information in {} are printed if available
 * @author Timo Hahn
 */
public class DebugFormatter
    extends SimpleFormatter
{
    //~ Instanzenvariablen -----------------------------------------------------

    /**
     * system calender
     */
    private Calendar mCalendar = Calendar.getInstance();

    /**
     * date formatter
     */
    private SimpleDateFormat mDateFromatter = null;

    /**
     * DOCUMENT ME!
     */
    private String mOptionen = "";

    /**
     * DOCUMENT ME!
     */
    private boolean mShowClass = false;

    /**
     * DOCUMENT ME!
     */
    private boolean mShowLine = false;

    /**
     * DOCUMENT ME!
     */
    private boolean mShowMethod = false;

    /**
     * DOCUMENT ME!
     */
    private boolean mShowName = false;

    /**
     * DOCUMENT ME!
     */
    private boolean mShowThreadID = false;

    //~ Konstruktoren ----------------------------------------------------------

    /**
     * C'tor
     */
    public DebugFormatter()
    {
        super();
        mDateFromatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    }

    //~ Methoden ---------------------------------------------------------------

    /**
     * Generate a log record in the selected format<br>
     * Fromat is:<br>
     * level: date time - threadId - class - method - message [-
     * throwable.message]<br>
     *
     * @param record log record
     *
     * @return log record formatted as String
     */
    public String format(LogRecord record)
    {

        StringBuffer sb = new StringBuffer();

        sb.append(record.getLevel());
        sb.append(": ");

        long time = record.getMillis();
        Date d = new Date(time);
        mCalendar.setTime(d);
        sb.append(mDateFromatter.format(mCalendar.getTime()));
        if (mShowLine)
        {
            String stack = null;
            StackTraceElement ste = null;
            if (record.getThrown() != null)
            {
                ste = getStackTraceElement(new Exception("test"), 5);
            }
            else
            {
                ste = getStackTraceElement(new Exception("test"), 6);
            }

            sb.append(" - ");
            sb.append(ste.toString());
        }

        if (mShowThreadID)
        {
            sb.append(" - ");
            sb.append(record.getThreadID());
        }

        if (mShowName)
        {
            sb.append(" - ");
            sb.append(record.getLoggerName());
        }


        if (mShowClass && !mShowLine)
        {
            sb.append(" - ");
            sb.append(record.getSourceClassName());
        }

        if (mShowMethod && !mShowLine)
        {
            sb.append(" - ");
            sb.append(record.getSourceMethodName());
        }
        if (false)
        {
            if (mShowLine)
            {
                String stack = null;
                StackTraceElement ste = null;
                if (record.getThrown() != null)
                {
                    ste = getStackTraceElement(new Exception("test"), 5);
                }
                else
                {
                    ste = getStackTraceElement(new Exception("test"), 6);
                }
                //            sb.append(" - (Line:");
                //            sb.append(ste.getLineNumber());
                //            sb.append(")");
                sb.append(" - ");
                sb.append(ste.toString());
            }
        }
        sb.append("\n  ");
        //        sb.append(" - ");
        sb.append(record.getMessage());

        Throwable t = record.getThrown();
        if (t != null)
        {
            sb.append(" - Thrown: ");
            sb.append(t.getMessage());
        }

        sb.append("\n");

        return sb.toString();
    }

    /**
     * Returns the fiel and line number of the log message
     *
     * @param stack String representation of a stack trace
     *
     * @return Java-file and  linenumber of the caller
     */
    private String getCallingLine(String stack)
    {
        String aCaller = "";
        try
        {
            String lines[] = stack.split("\n", 10);
            aCaller = lines[8].substring(lines[8].indexOf("("));
        }
        catch (Exception e)
        {
            aCaller = "(---)";
            ;
        }

        return aCaller;
    }

    /**
     * Returns a stack trace as string
     *
     * @param t Exception
     *
     * @return String of hte stack trace
     */
    public static String getStackTrace(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();

        return sw.toString();
    }

    /**
     * Return a stack strace element
     * 
     * @param t Exception, aNum detpth of the element
     *
     * @return StackTraceElement of depth aNum
     */
    public static StackTraceElement getStackTraceElement(Throwable t, int aNum)
    {
        StackTraceElement[] stack = t.getStackTrace();
        if (stack.length >= aNum)
            return stack[aNum];
        else
            return null;
    }

    /**
     * Set the options for the Formatter:<br>
     *
     * <ul>
     * <li>
     * t = ThreadID
     * </li>
     * <li>
     * n = Name dof the logger
     * </li>
     * <li>
     * l = file:line of the log message
     * </li>
     * <li>
     * c = class of the log message
     * </li>
     * <li>
     * m = methode name
     * </li>
     * </ul>
     *
     *
     * @param aOptionen options to print for each log message
     */
    public void setOptionen(String aOptionen)
    {
        if (aOptionen == null)
        {
            return;
        }

        mOptionen = aOptionen;
        mShowClass = (mOptionen.toLowerCase().indexOf("c") >= 0);
        mShowLine = (mOptionen.toLowerCase().indexOf("l") >= 0);
        mShowMethod = (mOptionen.toLowerCase().indexOf("m") >= 0);
        mShowName = (mOptionen.toLowerCase().indexOf("n") >= 0);
        mShowThreadID = (mOptionen.toLowerCase().indexOf("t") >= 0);
    }
}
