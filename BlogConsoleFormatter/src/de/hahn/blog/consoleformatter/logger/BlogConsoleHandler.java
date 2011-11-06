package de.hahn.blog.consoleformatter.logger;


import java.util.Properties;

import oracle.core.ojdl.logging.ConsoleHandler;
import oracle.core.ojdl.logging.HandlerFactoryException;


public class BlogConsoleHandler
    extends ConsoleHandler
{
    public BlogConsoleHandler()
    {
        super();
    }

    @Override
    public java.util.logging.Handler create(Properties aProperties)
        throws HandlerFactoryException
    {

        java.util.logging.Handler handler = super.create(aProperties);

        String formatter = aProperties.getProperty("formatter");

        try
        {
            Object classFormatter = Class.forName(formatter).newInstance();
            if (classFormatter instanceof WLSConsoleFormatter)
            {
                WLSConsoleFormatter wlcConsoleFormatter = (WLSConsoleFormatter) classFormatter;
                String style = aProperties.getProperty("formatStyle");
                if (style != null)
                {
                    wlcConsoleFormatter.setOptionen(style);
                }
                this.setFormatter(wlcConsoleFormatter);
                handler.setFormatter(wlcConsoleFormatter);

            }
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (InstantiationException e2)
        {
            e2.printStackTrace();
        }
        catch (IllegalAccessException e3)
        {
            e3.printStackTrace();
        }
        return handler;
    }
}
