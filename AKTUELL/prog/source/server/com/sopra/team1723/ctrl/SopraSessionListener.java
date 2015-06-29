package com.sopra.team1723.ctrl;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Erkennt wenn eine Session abgelaufen bzw. zerst�rt wurde. Und pr�ft
 * daraufhin, ob noch files vom Export auf dem Server liegen. Wenn ja, dann
 * werden diese gel�scht.
 * 
 * @author Andreas
 *
 */
public class SopraSessionListener implements HttpSessionListener
{

    @Override
    public void sessionCreated(HttpSessionEvent arg0)
    {
        System.out.println("[SessionListener] Created Session");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent arg0)
    {
        System.out.println("[SessionListener] Destroyed Session");
        // Pr�fen, ob noch alte dateien erzeugt wurden
        PDFExporter pexp = (PDFExporter) arg0.getSession().getAttribute(ServletController.sessionAttributePDFExporter);
        if (pexp != null)
        {
            pexp.deleteFiles();
        }
        arg0.getSession().setAttribute(ServletController.sessionAttributePDFExporter, null);
    }

}
